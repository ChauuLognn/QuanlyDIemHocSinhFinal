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

    // 1. LẤY TẤT CẢ TÀI KHOẢN (SELECT)
    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        // SỬA: Accounts -> account
        String sql = "SELECT * FROM account";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                list.add(new Account(
                        rs.getString("username"),
                        rs.getString("password"),
                        // SỬA: id -> studentID (để khớp với cột trong DB)
                        rs.getString("studentID")
                ));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. THÊM TÀI KHOẢN (INSERT)
    public void addAccount(Account acc) throws Exception {
        if (findAccountByUsername(acc.getUsername()) != null) {
            throw new Exception("Tên đăng nhập '" + acc.getUsername() + "' đã tồn tại!");
        }

        Connection conn = DatabaseConnection.getConnection();
        // SỬA: Accounts -> account, id -> studentID
        String sql = "INSERT INTO account(username, password, studentID, role) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getPassword());
            ps.setString(3, acc.getID());
            ps.setString(4, "user"); // Mặc định role là user nếu constructor không có

            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Lỗi thêm tài khoản: " + e.getMessage());
        }
    }

    // 3. TÌM TÀI KHOẢN (SELECT WHERE)
    public Account findAccountByUsername (String username){
        Account acc = null;
        Connection conn = DatabaseConnection.getConnection();
        // SỬA: Accounts -> account
        String sql = "SELECT * FROM account WHERE username = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                acc = new Account(
                        rs.getString("username"),
                        rs.getString("password"),
                        // SỬA: id -> studentID
                        rs.getString("studentID")
                );
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return acc;
    }

    // 4. XÓA TÀI KHOẢN (DELETE)
    public void deleteAccount(String username) throws Exception {
        if (findAccountByUsername(username) == null) {
            throw new Exception("Tài khoản không tồn tại để xóa!");
        }

        Connection conn = DatabaseConnection.getConnection();
        // SỬA: Accounts -> account
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

    // 5. KIỂM TRA ĐĂNG NHẬP
    public boolean checkLogin(String username, String password){
        Account acc = findAccountByUsername(username);
        if (acc == null) return false;

        String inputHash = hashSHA256(password);
        // Lưu ý: Nếu dữ liệu cũ trong DB chưa hash thì sẽ không login được.
        // Bạn nên đảm bảo password trong DB đã được hash giống cơ chế này.
        return acc.getPassword().equals(inputHash);
    }

    // 6. ĐỔI USERNAME (UPDATE)
    public void updateUsername(String oldUsername, String newUsername) throws Exception {
        if (findAccountByUsername(oldUsername) == null) throw new Exception("TK cũ không tồn tại!");
        if (findAccountByUsername(newUsername) != null) throw new Exception("TK mới đã bị trùng!");

        Connection conn = DatabaseConnection.getConnection();
        // SỬA: Accounts -> account
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

    // 7. ĐỔI MẬT KHẨU (UPDATE)
    public void updatePassword(String username, String newPass) throws Exception {
        if (findAccountByUsername(username) == null) throw new Exception("Không tìm thấy tài khoản!");

        Connection conn = DatabaseConnection.getConnection();
        // SỬA: Accounts -> account
        String sql = "UPDATE account SET password = ? WHERE username = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, hashSHA256(newPass));
            ps.setString(2, username);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new Exception("Lỗi đổi mật khẩu: " + e.getMessage());
        }
    }

    // --- HÀM MÃ HÓA GIỮ NGUYÊN ---
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