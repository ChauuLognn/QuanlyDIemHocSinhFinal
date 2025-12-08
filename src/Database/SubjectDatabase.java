package Database; // <--- Sửa lại dòng này cho đúng thư mục

import Database.DatabaseConnection;
import SubjectManager.Subject;
import java.sql.*;
import java.util.ArrayList;

public class SubjectDatabase {
    public static ArrayList<Subject> getAllSubjects() {
        ArrayList<Subject> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            // Lỗi "Unable to resolve table 'subject'" là do IDE chưa kết nối DB, kệ nó nếu code chạy được
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM subject");
            while (rs.next()) {
                list.add(new Subject(
                        rs.getString("subjectID"),
                        rs.getString("subjectName"),
                        rs.getInt("coefficient")
                ));
            }
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}