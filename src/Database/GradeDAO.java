package Database;

import GradeManager.Grade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {

    private static final String DB_URL =
            "jdbc:postgresql://ep-soft-meadow-ahjkvp1k-pooler.c-3.us-east-1.aws.neon.tech/neondb?sslmode=require";

    private static final String USER = "neondb_owner";
    private static final String PASS = "npg_RF5EiglMo7KL";

    private static GradeDAO instance;

    public static GradeDAO getInstance() {
        if (instance == null) instance = new GradeDAO();
        return instance;
    }

    // Phương thức connect giống ClassDAO
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // Lấy điểm của 1 học sinh
    public Grade getGrade(String studentID) {
        String sql = "SELECT * FROM Grade WHERE studentID = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Grade(
                            rs.getString("studentID"),
                            rs.getDouble("regularScore"),
                            rs.getDouble("midtermScore"),
                            rs.getDouble("finalScore")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi getGrade: " + e.getMessage());
        }
        return null;
    }

    // Thêm hoặc cập nhật điểm
    public boolean saveGrade(Grade g) {
        String sql = """
            INSERT INTO Grade(studentID, regularScore, midtermScore, finalScore)
            VALUES (?, ?, ?, ?)
            ON CONFLICT(studentID) DO UPDATE SET
            regularScore = excluded.regularScore,
            midtermScore = excluded.midtermScore,
            finalScore   = excluded.finalScore
            """;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, g.getStudentID());
            ps.setDouble(2, g.getRegularScore());
            ps.setDouble(3, g.getMidtermScore());
            ps.setDouble(4, g.getFinalScore());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi saveGrade: " + e.getMessage());
            return false;
        }
    }

    // Xóa điểm
    public boolean deleteGrade(String studentID) {
        String sql = "DELETE FROM Grade WHERE studentID = ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentID);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi deleteGrade: " + e.getMessage());
            return false;
        }
    }

    // Lấy toàn bộ bảng Grade
    public List<Grade> getAllGrades() {
        List<Grade> list = new ArrayList<>();

        String sql = "SELECT * FROM Grade";

        try (Connection conn = connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Grade(
                        rs.getString("studentID"),
                        rs.getDouble("regularScore"),
                        rs.getDouble("midtermScore"),
                        rs.getDouble("finalScore")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Lỗi getAllGrades: " + e.getMessage());
        }
        return list;
    }
}
