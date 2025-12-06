package GradeManager.data;

import Database.DatabaseConnection;
import GradeManager.Grade;
import java.sql.*;
import java.util.HashMap;

public class GradeDatabase {
    private static GradeDatabase gradeDB = new GradeDatabase();
    private GradeDatabase(){}
    public static GradeDatabase getGradeDB(){ return gradeDB; }

    // 1. Lấy điểm của 1 học sinh
    public Grade getGradeByStudentID (String studentID) {
        Grade grade = null;
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM grade WHERE studentID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, studentID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                grade = new Grade(
                        rs.getString("studentID"),
                        rs.getDouble("regularScore"),
                        rs.getDouble("midtermScore"),
                        rs.getDouble("finalScore")
                );
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return grade;
    }

    // 2. Thêm hoặc cập nhật điểm (Dùng cú pháp MySQL)
    public void addOrUpdateGrade(Grade grade) {
        Connection conn = DatabaseConnection.getConnection();

        // Cú pháp dành riêng cho MySQL: Nếu trùng khoá chính (studentID) thì Update, chưa có thì Insert
        String sql = "INSERT INTO grade(studentID, regularScore, midtermScore, finalScore) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "regularScore = VALUES(regularScore), " +
                "midtermScore = VALUES(midtermScore), " +
                "finalScore = VALUES(finalScore)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, grade.getStudentID());
            ps.setDouble(2, grade.getRegularScore());
            ps.setDouble(3, grade.getMidtermScore());
            ps.setDouble(4, grade.getFinalScore());

            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. Xóa điểm
    public void deleteGrade(String studentID) {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "DELETE FROM grade WHERE studentID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, studentID);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. Lấy toàn bộ điểm (Hàm này giúp tối ưu tốc độ load bảng)
    public HashMap<String, Grade> getAllGradesAsMap() {
        HashMap<String, Grade> map = new HashMap<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM grade";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Grade g = new Grade(
                        rs.getString("studentID"),
                        rs.getDouble("regularScore"),
                        rs.getDouble("midtermScore"),
                        rs.getDouble("finalScore")
                );
                map.put(g.getStudentID(), g);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}