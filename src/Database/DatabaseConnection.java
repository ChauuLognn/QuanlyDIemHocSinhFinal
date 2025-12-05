package Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    // Thông tin lấy từ Neon Dashboard của bạn
    private static final String HOST = "ep-soft-meadow-ahjkvp1k-pooler.c-3.us-east-1.aws.neon.tech";
    private static final String DB_NAME = "neondb";
    private static final String USER = "neondb_owner";
    private static final String PASS = "npg_XnZbt35gjwsA"; // <--- Thay mật khẩu

    // URL kết nối chuẩn cho Neon (Bắt buộc có sslmode=require)
    private static final String URL = "jdbc:postgresql://" + HOST + "/" + DB_NAME + "?sslmode=require&user=" + USER + "&password=" + PASS;

    public static Connection getConnection() {
        Connection cons = null;
        try {
            Class.forName("org.postgresql.Driver");
            cons = DriverManager.getConnection(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cons;
    }

    // Hàm main để test kết nối
    public static void main(String[] args) {
        System.out.println(getConnection() != null ? "Kết nối thành công!" : "Kết nối thất bại!");
    }
}