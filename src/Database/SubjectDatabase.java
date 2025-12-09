package Database;

import SubjectManager.Subject;
import java.sql.*;
import java.util.ArrayList;

public class SubjectDatabase {

    // lấy tất cả môn học
    public static ArrayList<Subject> getAllSubjects() {
        ArrayList<Subject> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM subject");
            while (rs.next()) {
                list.add(new Subject(
                        rs.getString("subjectID"),
                        rs.getString("subjectName"),
                        rs.getInt("coefficient")
                ));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}