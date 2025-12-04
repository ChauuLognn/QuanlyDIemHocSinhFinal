package AccountManager.data;

import AccountManager.Account;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class AccountDatabase {
    private static AccountDatabase accountDB = new AccountDatabase();
    private HashMap<String, Account> accounts = new HashMap<>();

    private AccountDatabase(){
        // Constructor rỗng
        // Bạn có thể thêm dữ liệu mẫu ở đây để test nếu muốn
        accounts.put("admin", new Account("admin", hashSHA256("1011"), "AD01"));
    }

    public static AccountDatabase getAccountDB(){ return accountDB;}


    //Hiển thị danh sách tài khoản
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    //Thêm tài khoản
    public void addAccount(Account acc) throws Exception {
        if (accounts.containsKey(acc.getUsername())) {
            throw new Exception("Tên đăng nhập '" + acc.getUsername() + "' đã tồn tại!");
        }
        accounts.put(acc.getUsername(), acc);
    }

    //Tìm kiếm tài khoản theo tên đăng nhập
    public Account findAccountByUsername (String username){
        return accounts.get(username);
    }

    //Xóa tài khoản
    public void deleteAccount(String username) throws Exception {
        if (!accounts.containsKey(username)) {
            throw new Exception("Tài khoản không tồn tại để xóa!");
        }
        accounts.remove(username);
    }

    //Dùng hashing mã hóa mật khẩu
    public String hashSHA256(String password){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());

            //Chuyển đổi byte sang hex
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash){
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1){
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

    //Kiểm tra đăng nhập
    public boolean checkLogin(String username, String password){
        Account a = accounts.get(username);
        if (a == null) {
            return false;
        }
        String inputHash = hashSHA256(password);
        return a.getPassword().equals(inputHash);
    }

    //Cập nhật tài khoản
    public void updateUsername(String oldUsername, String newUsername) throws Exception {
        if (!accounts.containsKey(oldUsername)) {
            throw new Exception("Tài khoản cũ không tồn tại!");
        }
        if (accounts.containsKey(newUsername)) {
            throw new Exception("Tên đăng nhập mới đã bị trùng!");
        }

        Account acc = accounts.remove(oldUsername);
        acc.setUsername(newUsername);
        accounts.put(newUsername, acc);
    }

    // Cập nhật Password
    public void updatePassword(String username, String newPass) throws Exception {
        Account acc = accounts.get(username);
        if (acc == null) throw new Exception("Không tìm thấy tài khoản!");
        acc.setPassword(hashSHA256(newPass));
    }
}

