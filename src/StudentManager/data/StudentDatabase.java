package StudentManager.data;

import Database.DatabaseConnection;
import StudentManager.Student;
import java.sql.*;
import java.util.ArrayList;

public class StudentDatabase {
    private static StudentDatabase studentDB = new StudentDatabase();

    private StudentDatabase(){}

    public static StudentDatabase getStudentDB(){
        return studentDB;
    }

    // 1. Lấy danh sách tất cả học sinh từ MySQL
    public ArrayList<Student> getAllStudents(){
        ArrayList<Student> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM student";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                // Đảm bảo thứ tự cột khớp với Database: studentName, studentID, studentClass, gender
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

    // 2. Tìm học sinh theo ID
    public Student findByID (String id){
        Student s = null;
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM student WHERE studentID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
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

    // 3. Thêm học sinh
    public void addStudent(Student s) throws Exception {
        if (findByID(s.getStudentID()) != null) {
            throw new Exception("Mã học sinh " + s.getStudentID() + " đã tồn tại!");
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
            e.printStackTrace();
            throw new Exception("Lỗi thêm học sinh: " + e.getMessage());
        }
    }

    // 4. Xóa học sinh
    public void deleteStudent(String id) throws Exception {
        if (findByID(id) == null) {
            throw new Exception("Không tìm thấy học sinh để xóa!");
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

    // 5. Cập nhật thông tin học sinh
    public void updateStudent(String originalId, Student newInfo) throws Exception {
        if (findByID(originalId) == null) throw new Exception("Không tìm thấy học sinh gốc!");

        // Nếu người dùng đổi ID, cần kiểm tra xem ID mới có trùng với ai khác không
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
            ps.setString(5, originalId); // ID cũ dùng để tìm dòng cần sửa

            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new Exception("Lỗi cập nhật: " + e.getMessage());
        }
    }
}