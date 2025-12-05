package Database;

import ClassManager.Classes;
import StudentManager.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {

    private static final String DB_URL =
            "jdbc:postgresql://ep-soft-meadow-ahjkvp1k-pooler.c-3.us-east-1.aws.neon.tech/neondb?sslmode=require";

    private static final String USER = "neondb_owner";
    private static final String PASS = "npg_RF5EiglMo7KL";

    private static ClassDAO instance;

    public static ClassDAO getInstance() {
        if (instance == null) instance = new ClassDAO();
        return instance;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // Lấy tất cả classes
    public List<Classes> getAllClasses() {
        List<Classes> list = new ArrayList<>();
        String sql = "SELECT * FROM Classes ORDER BY classID";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Classes(
                        rs.getString("classID"),
                        rs.getString("className"),
                        rs.getInt("studentNumber")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Lỗi getAllClasses: " + e.getMessage());
        }

        return list;
    }

    // Tìm class theo ID
    public Classes findByID(String id) {
        String sql = "SELECT * FROM Classes WHERE classID = ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Classes(
                        rs.getString("classID"),
                        rs.getString("className"),
                        rs.getInt("studentNumber")
                );
            }

        } catch (SQLException e) {
            System.out.println("Lỗi findByID: " + e.getMessage());
        }

        return null;
    }

    // Thêm class
    public boolean addNewClass(Classes c) {
        String sql = "INSERT INTO Classes(classID, className, studentNumber) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getClassID());
            ps.setString(2, c.getClassName());
            ps.setInt(3, c.getStudentNumber());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Lỗi addNewClass: " + e.getMessage());
            return false;
        }
    }

    public boolean addStudentToClass(String studentID, String classID) {
        String sql = "UPDATE Student SET className = ? WHERE studentID = ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, classID);
            ps.setString(2, studentID);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                // cập nhật lại studentNumber
                updateStudentNumber(classID, conn);
            }

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi addStudentToClass: " + e.getMessage());
            return false;
        }
    }


    public List<Student> getStudentsOfClass(String classID) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM Student WHERE className = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, classID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Student(
                        rs.getString("studentID"),
                        rs.getString("studentName"),
                        rs.getString("className")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Lỗi getStudentsOfClass: " + e.getMessage());
        }

        return list;
    }


    // Xóa class
    public boolean deleteClass(String classID) {
        String sql = "DELETE FROM Classes WHERE classID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, classID);

            int rowsAffected = ps.executeUpdate();  // Số dòng bị xóa

            return rowsAffected > 0; // true nếu xóa thành công

        } catch (SQLException e) {
            System.out.println("Lỗi deleteClass: " + e.getMessage());
            return false;
        }
    }

    private void updateStudentNumber(String classID, Connection conn) throws SQLException {
        String countSQL = "SELECT COUNT(*) FROM Student WHERE className = ?";
        String updateSQL = "UPDATE Classes SET studentNumber = ? WHERE classID = ?";

        // đếm số SV
        int count = 0;
        try (PreparedStatement ps = conn.prepareStatement(countSQL)) {
            ps.setString(1, classID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) count = rs.getInt(1);
        }

        // update lại vào bảng Classes
        try (PreparedStatement ps2 = conn.prepareStatement(updateSQL)) {
            ps2.setInt(1, count);
            ps2.setString(2, classID);
            ps2.executeUpdate();
        }
    }

    public boolean updateClassName(String classID, String newName) {
        String sql = "UPDATE Classes SET className = ? WHERE classID = ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newName);
            ps.setString(2, classID);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi updateClassName: " + e.getMessage());
            return false;
        }
    }

    public boolean updateClassID(String oldID, String newID) {
        String sql = "UPDATE Classes SET classID = ? WHERE classID = ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newID);
            ps.setString(2, oldID);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi updateClassID: " + e.getMessage());
            return false;
        }
    }

    // Đếm số học sinh trong 1 lớp (dựa vào trường className trong bảng Student)
    public int countStudentsInClass(String classID) {
        String sql = "SELECT COUNT(*) FROM Student WHERE className = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, classID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi countStudentsInClass: " + e.getMessage());
        }
        return 0;
    }

    // Gỡ (remove) học sinh khỏi lớp, sau đó cập nhật studentNumber
    public boolean removeStudentFromClass(String studentID, String classID) {
        String sql = "UPDATE Student SET className = NULL WHERE studentID = ? AND className = ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentID);
            ps.setString(2, classID);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                // cập nhật lại số lượng trong Classes
                updateStudentNumber(classID, conn);
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.out.println("Lỗi removeStudentFromClass: " + e.getMessage());
            return false;
        }
    }

}
