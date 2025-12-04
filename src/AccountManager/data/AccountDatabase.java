package AccountManager.data;

import AccountManager.Account;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;

public class AccountDatabase {
    private static AccountDatabase accountDB = new AccountDatabase();

    private HashMap<String, Account> accounts = new HashMap<>();

    Scanner sc = new Scanner(System.in);

    private AccountDatabase(){}

    public static AccountDatabase getAccountDB(){ return accountDB;}

    //Hiển thị tất cả accounts
    public void showAllAccounts(){
        if (accounts.size() == 0){
            System.out.println("Danh sách tài khoản trống!");
            return;
        }
        accounts.forEach((username, account) -> {
            System.out.println("Tên đăng nhập: " + account.getUsername() +
                                "Mật khẩu: " + account.getPassword() +
                                "Mã học sinh/ Giáo viên: " + account.getID());
        });
    }

    //Thêm tài khoản
    public void addAccount(String username, String password, String ID){
        int oldSize = accounts.size();
        Account newAccount = new Account(username, password, ID);
        accounts.put(username, newAccount);
        if (oldSize < accounts.size()){
            System.out.println("Đã thêm tài khoản thành công!");
        } else {
            System.out.println("Lỗi! Thêm tài khoản không thành công!");
        }
    }

    //Tìm kiếm tài khoản theo tên đăng nhập
    public Account findAccountByUsername (String username){
        Account a = accounts.get(username);

        return a;
    }

    //Xóa tài khoản
    public boolean deleteAccount(String username){
        return accounts.remove(username) != null;
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
        Account a = accountDB.findAccountByUsername(username);
        if (a == null){
            System.out.println("Sai tên đăng nhập!");
            return false;
        }
        String savedPassword = a.getPassword();
        if (!password.equals(savedPassword)){
            System.out.println("Sai mật khẩu!");
            return false;
        }
        return true;
    }

    //Cập nhật tài khoản
    public void updateUsername (String oldUsername, String newUsername){
        Account a = accounts.get(oldUsername);

        if (a == null){
            System.out.println("Không tìm thấy tài khoản!");
            return;
        }

        //Xóa username cũ
        accounts.remove(oldUsername);

        //Cập nhat username mới
        a.setUsername(newUsername);

        //Lưu lại với username mới
        accounts.put(newUsername, a);
    }
}
