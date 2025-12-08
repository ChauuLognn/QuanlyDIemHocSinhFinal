package Database;

import GradeManager.Grade;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GradeDatabase {
    private static GradeDatabase gradeDB = new GradeDatabase();
    private GradeDatabase(){}
    public static GradeDatabase getGradeDB(){ return gradeDB; }

    // =================================================================================
    // PHẦN 1: CÁC HÀM MỚI (HỖ TRỢ MÔN HỌC & HỌC KỲ) - Dùng cho GradeManagement mới
    // =================================================================================

    // 1. Lấy điểm chi tiết (HS + Môn + Kỳ)
    public Grade getGrade(String studentID, String subjectID, int semester) {
        Grade grade = null;
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM grade WHERE studentID = ? AND subjectID = ? AND semester = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, studentID);
            ps.setString(2, subjectID);
            ps.setInt(3, semester);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                // Gọi Constructor 6 tham số (đảm bảo Grade.java đã cập nhật)
                grade = new Grade(
                        rs.getString("studentID"),
                        rs.getString("subjectID"),
                        rs.getInt("semester"),
                        rs.getDouble("regularScore"),
                        rs.getDouble("midtermScore"),
                        rs.getDouble("finalScore")
                );
            }
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
        return grade;
    }

    // 2. Lưu hoặc Cập nhật điểm (HS + Môn + Kỳ)
    public void saveGrade(String studentID, String subjectID, int semester, double r, double m, double f) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            // Cú pháp chuẩn MySQL mới để tránh cảnh báo Deprecated
            String sql = "INSERT INTO grade (studentID, subjectID, semester, regularScore, midtermScore, finalScore) " +
                    "VALUES (?, ?, ?, ?, ?, ?) AS new " +
                    "ON DUPLICATE KEY UPDATE " +
                    "regularScore = new.regularScore, " +
                    "midtermScore = new.midtermScore, " +
                    "finalScore = new.finalScore";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, studentID);
            ps.setString(2, subjectID);
            ps.setInt(3, semester);
            ps.setDouble(4, r);
            ps.setDouble(5, m);
            ps.setDouble(6, f);

            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // =================================================================================
    // PHẦN 2: CÁC HÀM CŨ (ĐỂ TƯƠNG THÍCH NGƯỢC) - Tránh lỗi đỏ ở các file Service cũ
    // =================================================================================

    // Hàm cũ 1: Lấy điểm chỉ theo kỳ (Mặc định lấy môn TOAN để không lỗi, hoặc trả về null)
    public Grade getGrade(String studentID, int semester) {
        return getGrade(studentID, "TOAN", semester);
    }

    // Hàm cũ 2: Lưu điểm không có môn (Mặc định lưu vào TOAN)
    public void addOrUpdateGrade(String studentID, int semester, double r, double m, double f) {
        saveGrade(studentID, "TOAN", semester, r, m, f);
    }

    // Hàm cũ 3: Nhận đối tượng Grade (Dùng cho AddGrade cũ)
    public void addOrUpdateGrade(Grade g) {
        // Nếu object Grade có subjectID thì dùng, không thì mặc định TOAN
        String subID = (g.getSubjectID() == null || g.getSubjectID().isEmpty()) ? "TOAN" : g.getSubjectID();
        saveGrade(g.getStudentID(), subID, g.getSemester(), g.getRegularScore(), g.getMidtermScore(), g.getFinalScore());
    }

    // Hàm cũ 4: Dashboard Service hay dùng
    public Grade getGradeByStudentID(String id) {
        return getGrade(id, "TOAN", 1); // Mặc định lấy Toán HK1
    }

    // Hàm cũ 5: Lấy map (Cẩn thận: HashMap key là studentID nên sẽ bị ghi đè nếu có nhiều môn)
    // Chỉ dùng để test hoặc load dữ liệu sơ bộ
    public HashMap<String, Grade> getGradesBySemester(int semester) {
        HashMap<String, Grade> map = new HashMap<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            // Lấy môn TOAN làm đại diện để load danh sách
            String sql = "SELECT * FROM grade WHERE semester = ? AND subjectID = 'TOAN'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, semester);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Grade g = new Grade(
                        rs.getString("studentID"),
                        rs.getString("subjectID"),
                        rs.getInt("semester"),
                        rs.getDouble("regularScore"),
                        rs.getDouble("midtermScore"),
                        rs.getDouble("finalScore")
                );
                map.put(g.getStudentID(), g);
            }
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }

    public HashMap<String, Grade> getAllGradesAsMap() {
        return getGradesBySemester(1);
    }

    public void deleteGrade(String id) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.createStatement().executeUpdate("DELETE FROM grade WHERE studentID='" + id + "'");
            conn.close();
        } catch(Exception e) {}
    }


    // Lấy bảng điểm đầy đủ của 1 học sinh trong 1 kỳ (Kèm tên môn và hệ số)
    // Trả về danh sách Object[] gồm: {Tên Môn, Hệ Số, TX, GK, CK}
    public ArrayList<Object[]> getStudentTranscript(String studentID, int semester) {
        ArrayList<Object[]> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            // Join bảng Grade và Subject để lấy tên môn
            String sql = "SELECT s.subjectName, s.coefficient, g.regularScore, g.midtermScore, g.finalScore " +
                    "FROM grade g " +
                    "JOIN subject s ON g.subjectID = s.subjectID " +
                    "WHERE g.studentID = ? AND g.semester = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, studentID);
            ps.setInt(2, semester);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                list.add(new Object[]{
                        rs.getString("subjectName"),
                        rs.getInt("coefficient"),
                        rs.getDouble("regularScore"),
                        rs.getDouble("midtermScore"),
                        rs.getDouble("finalScore")
                });
            }
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }


}