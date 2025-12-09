package Database;

import AccountManager.Account;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDatabase {
    private static AccountDatabase instance = new AccountDatabase();

    private AccountDatabase() {}

    public static AccountDatabase getInstance() {
        return instance;
    }

    // lấy tất cả tài khoản
    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM account";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new Account(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("studentID"),
                        rs.getString("role")
                ));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // thêm tài khoản
    public void addAccount(Account acc) throws Exception {
        if (findByUsername(acc.getUsername()) != null) {
            throw new Exception("Tên đăng nhập '" + acc.getUsername() + "' đã tồn tại!");
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO account(username, password, studentID, role) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getPassword());
            ps.setString(3, acc.getID());
            ps.setString(4, "student");
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new Exception("Lỗi thêm tài khoản: " + e.getMessage());
        }
    }

    // tìm tài khoản theo username
    public Account findByUsername(String username) {
        Account acc = null;
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM account WHERE username = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                acc = new Account(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("studentID"),
                        rs.getString("role")
                );
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return acc;
    }

    // xóa tài khoản
    public void deleteAccount(String username) throws Exception {
        if (findByUsername(username) == null) {
            throw new Exception("Tài khoản không tồn tại!");
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "DELETE FROM account WHERE username = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new Exception("Lỗi xóa tài khoản: " + e.getMessage());
        }
    }

    // kiểm tra đăng nhập
    public Account login(String username, String password) {
        Account acc = findByUsername(username);
        if (acc == null) return null;

        String hashPass = hashPassword(password);
        if (acc.getPassword().equals(hashPass)) {
            return acc;
        }
        return null;
    }

    // đổi username
    public void updateUsername(String oldUsername, String newUsername) throws Exception {
        if (findByUsername(oldUsername) == null) {
            throw new Exception("Tài khoản cũ không tồn tại!");
        }
        if (findByUsername(newUsername) != null) {
            throw new Exception("Tên mới đã bị trùng!");
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE account SET username = ? WHERE username = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newUsername);
            ps.setString(2, oldUsername);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new Exception("Lỗi đổi tên: " + e.getMessage());
        }
    }

    // đổi mật khẩu
    public void updatePassword(String username, String newPass) throws Exception {
        if (findByUsername(username) == null) {
            throw new Exception("Không tìm thấy tài khoản!");
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE account SET password = ? WHERE username = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, hashPassword(newPass));
            ps.setString(2, username);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new Exception("Lỗi đổi mật khẩu: " + e.getMessage());
        }
    }

    // mã hóa mật khẩu
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}