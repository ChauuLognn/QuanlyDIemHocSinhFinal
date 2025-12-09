package Database;

import GradeManager.Grade;
import java.sql.*;
import java.util.ArrayList;

public class GradeDatabase {
    private static GradeDatabase instance = new GradeDatabase();

    private GradeDatabase() {}

    public static GradeDatabase getInstance() {
        return instance;
    }

    // lấy điểm theo học sinh + môn + kỳ
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
            if(rs.next()) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return grade;
    }

    // lưu hoặc cập nhật điểm
    public void saveGrade(String studentID, String subjectID, int semester, double regular, double mid, double finalScore) {
        try {
            Connection conn = DatabaseConnection.getConnection();
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
            ps.setDouble(4, regular);
            ps.setDouble(5, mid);
            ps.setDouble(6, finalScore);

            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // xóa điểm học sinh
    public void deleteGrade(String studentID) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.createStatement().executeUpdate("DELETE FROM grade WHERE studentID='" + studentID + "'");
            conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // lấy bảng điểm đầy đủ của học sinh trong 1 kỳ
    public ArrayList<Object[]> getTranscript(String studentID, int semester) {
        ArrayList<Object[]> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT s.subjectName, s.coefficient, g.regularScore, g.midtermScore, g.finalScore " +
                    "FROM grade g " +
                    "JOIN subject s ON g.subjectID = s.subjectID " +
                    "WHERE g.studentID = ? AND g.semester = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, studentID);
            ps.setInt(2, semester);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                list.add(new Object[]{
                        rs.getString("subjectName"),
                        rs.getInt("coefficient"),
                        rs.getDouble("regularScore"),
                        rs.getDouble("midtermScore"),
                        rs.getDouble("finalScore")
                });
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}