package AccountManager.service;

import AccountManager.data.AccountDatabase;

public class AuthService {
    private AccountDatabase accountDB = AccountDatabase.getAccountDB();

    public boolean login(String username, String password) {
        // Sau này có thể thêm logic kiểm tra: tài khoản bị khóa, sai quá 5 lần...
        return accountDB.checkLogin(username, password);
    }
}