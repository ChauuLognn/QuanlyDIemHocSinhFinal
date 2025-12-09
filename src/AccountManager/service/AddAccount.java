package AccountManager.service;

import AccountManager.Account;
import Database.AccountDatabase;
import Exception.Validator;

public class AddAccount {
    private AccountDatabase db = AccountDatabase.getInstance();

    public void addAccount(String username, String password, String confirmPass, String id) throws Exception {
        // kiểm tra rỗng
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

        // validate
        Validator.validateUsername(username.trim());
        Validator.validatePassword(password);
        Validator.validateID(id.trim());

        // kiểm tra mật khẩu khớp
        if (!password.equals(confirmPass)) {
            throw new Exception("Mật khẩu xác nhận không khớp!");
        }

        // kiểm tra khoảng trắng
        if (username.contains(" ")) {
            throw new Exception("Username không được chứa khoảng trắng!");
        }

        // mã hóa mật khẩu
        String hashedPassword = db.hashPassword(password);

        // tạo tài khoản
        Account newAcc = new Account(username.trim(), hashedPassword, id.trim());
        db.addAccount(newAcc);
    }
}