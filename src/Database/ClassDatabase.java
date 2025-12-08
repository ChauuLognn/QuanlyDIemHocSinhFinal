package Database;

import ClassManager.Classes;

import java.sql.*;
import java.util.ArrayList;

public class ClassDatabase {
    private static ClassDatabase classBD = new ClassDatabase();
    private ClassDatabase(){}
    public static ClassDatabase getClassDB(){ return classBD; }

    // 1. Lấy danh sách lớp
    public ArrayList<Classes> getAllClasses() {
        ArrayList<Classes> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM classes";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Classes c = new Classes(
                        rs.getString("className"),
                        rs.getString("classID")
                );
                // Lấy sĩ số đang lưu trong DB
                c.setStudentNumber(rs.getInt("studentNumber"));
                list.add(c);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Tìm lớp theo ID
    public Classes findClassByID(String classID){
        Classes c = null;
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM classes WHERE classID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, classID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                c = new Classes(rs.getString("className"), rs.getString("classID"));
                c.setStudentNumber(rs.getInt("studentNumber"));
            }
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
        return c;
    }

    // 3. Thêm lớp mới
    public void addNewClass(Classes c) throws Exception {
        if (findClassByID(c.getClassID()) != null) throw new Exception("Mã lớp đã tồn tại!");

        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO classes(classID, className, studentNumber) VALUES (?, ?, 0)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, c.getClassID());
            ps.setString(2, c.getClassName());
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new Exception("Lỗi thêm lớp: " + e.getMessage());
        }
    }

    // 4. Sửa lớp
    public void updateClass(String oldID, String newID, String newName) throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE classes SET classID = ?, className = ? WHERE classID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newID);
            ps.setString(2, newName);
            ps.setString(3, oldID);
            ps.executeUpdate();
            conn.close();

            // Cập nhật lại cột studentClass bên bảng student nếu mã lớp thay đổi
            if(!oldID.equalsIgnoreCase(newID)) {
                updateStudentsClassName(oldID, newID);
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi sửa lớp: " + e.getMessage());
        }
    }

    // 5. Xóa lớp
    public void deleteClass(String classID) throws Exception {
        Classes c = findClassByID(classID);
        // Kiểm tra xem lớp có học sinh không (dựa vào cột studentNumber)
        if(c != null && c.getStudentNumber() > 0) {
            throw new Exception("Không thể xóa lớp đang có học sinh!");
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "DELETE FROM classes WHERE classID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, classID);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new Exception("Lỗi xóa lớp: " + e.getMessage());
        }
    }

    // --- CÁC HÀM HỖ TRỢ ---

    private void updateStudentsClassName(String oldClassID, String newClassID) {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE student SET studentClass = ? WHERE studentClass = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newClassID);
            ps.setString(2, oldClassID);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Hàm quan trọng: Đếm lại số học sinh từ bảng student và cập nhật vào bảng classes
    public void syncAllClassesStudentCount() {
        Connection conn = DatabaseConnection.getConnection();
        // Câu lệnh SQL update: Đếm từ bảng student rồi update sang classes
        String sql = "UPDATE classes c SET studentNumber = (SELECT COUNT(*) FROM student s WHERE s.studentClass = c.classID)";

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Các hàm này giữ lại để tương thích với code cũ ở Service,
    // nhiệm vụ chính là gọi sync để cập nhật sĩ số.
    public void addStudentToClass(String studentID, String classID) throws Exception {
        syncAllClassesStudentCount();
    }

    public void removeStudentFromClass(String studentID, String classID) {
        syncAllClassesStudentCount();
    }

    public void updateStudentClass(String studentID, String oldClassID, String newClassID) throws Exception {
        syncAllClassesStudentCount();
    }
}