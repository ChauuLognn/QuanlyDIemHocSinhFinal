package GradeManager.data;

import Database.DatabaseConnection;
import GradeManager.Grade;
import java.sql.*;
import java.util.HashMap;

public class GradeDatabase {
    private static GradeDatabase gradeDB = new GradeDatabase();
    private GradeDatabase(){}
    public static GradeDatabase getGradeDB(){ return gradeDB; }

    // --- 1. HÀM MỚI (CÓ HỌC KỲ) - Dùng cho GradeManagement ---
    public Grade getGrade(String studentID, int semester) {
        Grade grade = null;
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM grade WHERE studentID = ? AND semester = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, studentID);
            ps.setInt(2, semester);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                grade = new Grade(
                        rs.getString("studentID"),
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

    public void addOrUpdateGrade(String studentID, int semester, double r, double m, double f) {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO grade(studentID, semester, regularScore, midtermScore, finalScore) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE regularScore=VALUES(regularScore), midtermScore=VALUES(midtermScore), finalScore=VALUES(finalScore)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, studentID);
            ps.setInt(2, semester);
            ps.setDouble(3, r);
            ps.setDouble(4, m);
            ps.setDouble(5, f);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public HashMap<String, Grade> getGradesBySemester(int semester) {
        HashMap<String, Grade> map = new HashMap<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM grade WHERE semester = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, semester);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Grade g = new Grade(
                        rs.getString("studentID"),
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

    // --- 2. HÀM CŨ (MẶC ĐỊNH KỲ 1) - Dùng cho AddGrade, EditGrade cũ ---
    // Giúp sửa lỗi "no suitable method found"

    public void addOrUpdateGrade(Grade g) {
        // Mặc định lưu vào kỳ 1 nếu gọi hàm cũ
        addOrUpdateGrade(g.getStudentID(), 1, g.getRegularScore(), g.getMidtermScore(), g.getFinalScore());
    }

    public Grade getGradeByStudentID(String id) {
        return getGrade(id, 1); // Mặc định lấy kỳ 1
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
}