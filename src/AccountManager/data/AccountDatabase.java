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

    //lấy tài khoản
    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM account";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while(rs.next()){
                list.add(new Account(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("studentID")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // thêm tài khoản
    public void addAccount(Account acc) throws Exception {
        if (findAccountByUsername(acc.getUsername()) != null) {
            throw new Exception("Tên đăng nhập '" + acc.getUsername() + "' đã tồn tại!");
        }

        String sql = "INSERT INTO account(username, password, studentID, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getPassword());
            ps.setString(3, acc.getID());
            ps.setString(4, "user");

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Lỗi thêm tài khoản: " + e.getMessage());
        }
    }

    // tìm tài khoản
    public Account findAccountByUsername (String username){
        Account acc = null;
        String sql = "SELECT * FROM account WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                acc = new Account(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("studentID")
                );
            }
            rs.close(); // Đóng ResultSet
        } catch (Exception e) {
            e.printStackTrace();
        }
        return acc;
    }

    // xóa tk
    public void deleteAccount(String username) throws Exception {
        if (findAccountByUsername(username) == null) {
            throw new Exception("Tài khoản không tồn tại để xóa!");
        }

        String sql = "DELETE FROM account WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Lỗi xóa tài khoản: " + e.getMessage());
        }
    }

    // ktra đăng nhập
    public boolean checkLogin(String username, String password){
        Account acc = findAccountByUsername(username);
        if (acc == null) return false;

        String inputHash = hashSHA256(password);
        return acc.getPassword().equals(inputHash);
    }

    // đổi usename
    public void updateUsername(String oldUsername, String newUsername) throws Exception {
        if (findAccountByUsername(oldUsername) == null) throw new Exception("TK cũ không tồn tại!");
        if (findAccountByUsername(newUsername) != null) throw new Exception("TK mới đã bị trùng!");

        String sql = "UPDATE account SET username = ? WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newUsername);
            ps.setString(2, oldUsername);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Lỗi đổi tên: " + e.getMessage());
        }
    }

    // 7. đổi pass
    public void updatePassword(String username, String newPass) throws Exception {
        if (findAccountByUsername(username) == null) throw new Exception("Không tìm thấy tài khoản!");

        String sql = "UPDATE account SET password = ? WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hashSHA256(newPass));
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Lỗi đổi mật khẩu: " + e.getMessage());
        }
    }

    // mã hóa mật khẩu
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