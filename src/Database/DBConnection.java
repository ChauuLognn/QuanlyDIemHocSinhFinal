package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL =
            "jdbc:postgresql://ep-soft-meadow-ahjkvp1k-pooler.c-3.us-east-1.aws.neon.tech/neondb?sslmode=require";

    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_RF5EiglMo7KL";

    private static Connection connection;

    // Lấy kết nối
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }

    // Hàm thực hiện kết nối
    private static void connect() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver"); // Load driver
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Kết nối PostgreSQL Neon thành công!");
        } catch (ClassNotFoundException e) {
            System.out.println("Không tìm thấy Driver PostgreSQL!");
            e.printStackTrace();
        }
    }

    // Đóng kết nối khi tắt chương trình
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Đã đóng kết nối!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
