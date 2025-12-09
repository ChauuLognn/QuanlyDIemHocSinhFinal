package Database;

import ClassManager.Classes;
import java.sql.*;
import java.util.ArrayList;

public class ClassDatabase {
    private static ClassDatabase instance = new ClassDatabase();

    private ClassDatabase() {}

    public static ClassDatabase getInstance() {
        return instance;
    }

    // lấy danh sách lớp (tự động đếm sĩ số)
    public ArrayList<Classes> getAllClasses() {
        ArrayList<Classes> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT c.classID, c.className, c.schoolYear, " +
                    "(SELECT COUNT(*) FROM student s WHERE s.studentClass = c.classID) AS studentNumber " +
                    "FROM classes c";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                Classes c = new Classes(
                        rs.getString("classID"),
                        rs.getString("className"),
                        rs.getString("schoolYear")
                );
                c.setStudentNumber(rs.getInt("studentNumber"));
                list.add(c);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // tìm lớp theo id
    public Classes findByID(String classID) {
        Classes c = null;
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM classes WHERE classID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, classID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                c = new Classes(
                        rs.getString("classID"),
                        rs.getString("className"),
                        rs.getString("schoolYear")
                );
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    // thêm lớp
    public void addClass(Classes c) throws Exception {
        if (findByID(c.getClassID()) != null) {
            throw new Exception("Mã lớp đã tồn tại!");
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO classes(classID, className, schoolYear) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, c.getClassID());
            ps.setString(2, c.getClassName());
            ps.setString(3, c.getSchoolYear());
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new Exception("Lỗi thêm lớp: " + e.getMessage());
        }
    }

    // sửa lớp
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

            // cập nhật mã lớp cho học sinh nếu đổi mã
            if(!oldID.equalsIgnoreCase(newID)) {
                updateStudentClass(oldID, newID);
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi sửa lớp: " + e.getMessage());
        }
    }

    // xóa lớp
    public void deleteClass(String classID) throws Exception {
        Connection conn = DatabaseConnection.getConnection();

        // kiểm tra có học sinh không
        PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM student WHERE studentClass = ?");
        check.setString(1, classID);
        ResultSet rs = check.executeQuery();
        if(rs.next() && rs.getInt(1) > 0) {
            throw new Exception("Không thể xóa lớp đang có học sinh!");
        }

        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM classes WHERE classID = ?");
            ps.setString(1, classID);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new Exception("Lỗi xóa lớp: " + e.getMessage());
        }
    }

    // cập nhật mã lớp cho học sinh
    private void updateStudentClass(String oldClassID, String newClassID) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE student SET studentClass = ? WHERE studentClass = ?");
            ps.setString(1, newClassID);
            ps.setString(2, oldClassID);
            ps.executeUpdate();
            conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}