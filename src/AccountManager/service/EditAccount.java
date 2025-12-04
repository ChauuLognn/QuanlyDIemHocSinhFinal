package AccountManager.service;

import AccountManager.data.AccountDatabase;

public class EditAccount {
    private AccountDatabase accountDB = AccountDatabase.getAccountDB();

    // Hàm đổi tên đăng nhập
    public void changeUsername(String currentUsername, String newUsername) throws Exception {
        if (newUsername.trim().isEmpty()) {
            throw new Exception("Tên đăng nhập mới không được để trống!");
        }
        accountDB.updateUsername(currentUsername, newUsername);
    }

    // Hàm đổi mật khẩu
    public void changePassword(String username, String newPass) throws Exception {
        if (newPass.length() < 6) {
            throw new Exception("Mật khẩu mới quá ngắn (tối thiểu 6 ký tự)!");
        }
        accountDB.updatePassword(username, newPass);
    }
}