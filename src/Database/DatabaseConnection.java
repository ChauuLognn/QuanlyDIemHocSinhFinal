package Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    // Cấu hình cho MySQL Localhost
    private static final String URL = "jdbc:mysql://localhost:3306/QuanLyHocSinhDB";
    private static final String USER = "root";
    private static final String PASS = "1011";

    public static Connection getConnection() {
        Connection cons = null;
        try {
            // Đổi driver sang MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            cons = DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cons;
    }

    public static void main(String[] args) {
        System.out.println(getConnection() != null ? "Kết nối MySQL thành công!" : "Kết nối thất bại!");
    }
}