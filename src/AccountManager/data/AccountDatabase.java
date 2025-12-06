package AccountManager.data;

import AccountManager.Account;
import Database.DatabaseConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDatabase {
    private static AccountDatabase accountDB = new AccountDatabase();

    private AccountDatabase(){}

    public static AccountDatabase getAccountDB(){ return accountDB;}

    // hiện thị all tk
    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        // MySQL: tên bảng thường là chữ thường 'account'
        String sql = "SELECT * FROM account";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                list.add(new Account(
                        rs.getString("username"),
                        rs.getString("password"),
                        // Quan trọng: Tên cột trong MySQL là studentID
                        rs.getString("studentID")
                ));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // thêm tk
    public void addAccount(Account acc) throws Exception {
        if (findAccountByUsername(acc.getUsername()) != null) {
            throw new Exception("Tên đăng nhập '" + acc.getUsername() + "' đã tồn tại!");
        }

        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO account(username, password, studentID, role) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getPassword()); // Pass đã hash từ Service
            ps.setString(3, acc.getID());       // ID này sẽ lưu vào cột studentID
            ps.setString(4, "studen");            // Mặc định role là student

            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Lỗi thêm tài khoản: " + e.getMessage());
        }
    }

    // tìm tk
    public Account findAccountByUsername (String username){
        Account acc = null;
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM account WHERE username = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
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
        if (findAccountByUsername(username) == null) {
            throw new Exception("Tài khoản không tồn tại để xóa!");
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

    // ktra đăng nhập
    public Account login(String username, String password) {
        Account acc = findAccountByUsername(username);
        if (acc == null) return null;

        String inputHash = hashSHA256(password);
        if (acc.getPassword().equals(inputHash)) {
            return acc; // Trả về toàn bộ thông tin tài khoản nếu đúng pass
        }
        return null; // Sai pass
    }

    // đổi usename
    public void updateUsername(String oldUsername, String newUsername) throws Exception {
        if (findAccountByUsername(oldUsername) == null) throw new Exception("TK cũ không tồn tại!");
        if (findAccountByUsername(newUsername) != null) throw new Exception("TK mới đã bị trùng!");

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
        if (findAccountByUsername(username) == null) throw new Exception("Không tìm thấy tài khoản!");

        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE account SET password = ? WHERE username = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, hashSHA256(newPass)); // Nhớ hash trước khi lưu
            ps.setString(2, username);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new Exception("Lỗi đổi mật khẩu: " + e.getMessage());
        }
    }

    // hàm mã hóa
    public String hashSHA256(String password){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash){
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }
}