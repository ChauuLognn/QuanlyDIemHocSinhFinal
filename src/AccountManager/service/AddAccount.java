package AccountManager.service;

import AccountManager.Account;
import Database.AccountDatabase;
import Exception.Validator;

public class AddAccount {
    private AccountDatabase accountDB = AccountDatabase.getAccountDB();

    public void addNewAccount(String username, String password, String confirmPass, String id) throws Exception {
        // 1. Kiểm tra rỗng
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Username không được để trống!");
        }
        if (password == null || password.isEmpty()) {
            throw new Exception("Mật khẩu không được để trống!");
        }
        if (confirmPass == null || confirmPass.isEmpty()) {
            throw new Exception("Vui lòng xác nhận mật khẩu!");
        }
        if (id == null || id.trim().isEmpty()) {
            throw new Exception("ID không được để trống!");
        }

        try {
            Validator.validateUsername(username.trim());
            Validator.validatePassword(password);
            Validator.validateID(id.trim());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        // 3. Kiểm tra mật khẩu khớp
        if (!password.equals(confirmPass)) {
            throw new Exception("Mật khẩu xác nhận không khớp!");
        }

        // 4. ✅ THÊM: Kiểm tra username không chứa khoảng trắng
        if (username.contains(" ")) {
            throw new Exception("Username không được chứa khoảng trắng!");
        }

        // 6. Mã hóa mật khẩu trước khi lưu
        String hashedPassword = accountDB.hashSHA256(password);

        // 7. Tạo đối tượng và gửi xuống Database
        Account newAcc = new Account(username.trim(), hashedPassword, id.trim());

        // Hàm này bên Database sẽ ném lỗi nếu trùng user
        accountDB.addAccount(newAcc);
    }
}