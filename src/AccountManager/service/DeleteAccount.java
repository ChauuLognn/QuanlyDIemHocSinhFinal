package AccountManager.service;

import Database.AccountDatabase;

public class DeleteAccount {
    private AccountDatabase db = AccountDatabase.getInstance();

    public void delete(String username) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Chưa chọn tài khoản để xóa!");
        }

        db.deleteAccount(username);
    }
}