package AccountManager.service;

import AccountManager.Account;
import AccountManager.data.AccountDatabase;


public class DeleteAccount {
    private AccountDatabase accountDB = AccountDatabase.getAccountDB();

    public void delete(String username) throws Exception {
        // Kiểm tra tồn tại
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Chưa chọn tài khoản để xóa!");
        }

        // Gọi DB xóa
        accountDB.deleteAccount(username);
    }
}
