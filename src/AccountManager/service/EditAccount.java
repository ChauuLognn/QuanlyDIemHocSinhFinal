package AccountManager.service;

import Database.AccountDatabase;

public class EditAccount {
    private AccountDatabase db = AccountDatabase.getInstance();

    // đổi username
    public void changeUsername(String currentUsername, String newUsername) throws Exception {
        if (newUsername.trim().isEmpty()) {
            throw new Exception("Tên đăng nhập mới không được để trống!");
        }
        db.updateUsername(currentUsername, newUsername);
    }

    // đổi mật khẩu
    public void changePassword(String username, String newPass) throws Exception {
        if (newPass.length() < 6) {
            throw new Exception("Mật khẩu mới quá ngắn!");
        }
        db.updatePassword(username, newPass);
    }
}