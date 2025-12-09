package Database;

import StudentManager.Student;
import java.sql.*;
import java.util.ArrayList;

public class StudentDatabase {
    private static StudentDatabase instance = new StudentDatabase();

    private StudentDatabase() {}

    public static StudentDatabase getInstance() {
        return instance;
    }

    // lấy tất cả học sinh
    public ArrayList<Student> getAllStudents() {
        ArrayList<Student> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM student";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new Student(
                        rs.getString("studentName"),
                        rs.getString("studentID"),
                        rs.getString("studentClass"),
                        rs.getString("gender")
                ));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // tìm học sinh theo id
    public Student findByID(String id) {
        Student s = null;
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM student WHERE studentID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                s = new Student(
                        rs.getString("studentName"),
                        rs.getString("studentID"),
                        rs.getString("studentClass"),
                        rs.getString("gender")
                );
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    // thêm học sinh
    public void addStudent(Student s) throws Exception {
        if (findByID(s.getStudentID()) != null) {
            throw new Exception("Mã học sinh đã tồn tại!");
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO student(studentName, studentID, studentClass, gender) VALUES(?, ?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, s.getStudentName());
            ps.setString(2, s.getStudentID());
            ps.setString(3, s.getStudentClass());
            ps.setString(4, s.getGender());
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new Exception("Lỗi thêm học sinh: " + e.getMessage());
        }
    }

    // xóa học sinh
    public void deleteStudent(String id) throws Exception {
        if (findByID(id) == null) {
            throw new Exception("Không tìm thấy học sinh!");
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "DELETE FROM student WHERE studentID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new Exception("Lỗi xóa học sinh: " + e.getMessage());
        }
    }

    // cập nhật học sinh
    public void updateStudent(String originalId, Student newInfo) throws Exception {
        if (findByID(originalId) == null) {
            throw new Exception("Không tìm thấy học sinh!");
        }

        if (!originalId.equalsIgnoreCase(newInfo.getStudentID())) {
            if (findByID(newInfo.getStudentID()) != null) {
                throw new Exception("Mã học sinh mới bị trùng!");
            }
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE student SET studentID=?, studentName=?, studentClass=?, gender=? WHERE studentID=?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newInfo.getStudentID());
            ps.setString(2, newInfo.getStudentName());
            ps.setString(3, newInfo.getStudentClass());
            ps.setString(4, newInfo.getGender());
            ps.setString(5, originalId);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new Exception("Lỗi cập nhật: " + e.getMessage());
        }
    }
}