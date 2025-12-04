package AccountManager.service;

import AccountManager.Account;
import AccountManager.data.AccountDatabase;

public class AddAccount {
    private AccountDatabase accountDB = AccountDatabase.getAccountDB();

    // Hàm thêm mới (Gọi từ nút Đăng ký hoặc nút Thêm trên giao diện)
    public void addNewAccount(String username, String password, String confirmPass, String id) throws Exception {
        // 1. Kiểm tra rỗng
        if (username.trim().isEmpty() || password.isEmpty() || id.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập đầy đủ thông tin!");
        }

        // 2. Kiểm tra mật khẩu khớp
        if (!password.equals(confirmPass)) {
            throw new Exception("Mật khẩu xác nhận không khớp!");
        }

        // 3. Kiểm tra độ dài mật khẩu (Ví dụ)
        if (password.length() < 6) {
            throw new Exception("Mật khẩu phải từ 6 ký tự trở lên!");
        }

        // 4. Mã hóa mật khẩu trước khi lưu
        String hashedPassword = accountDB.hashSHA256(password);

        // 5. Tạo đối tượng và gửi xuống Database
        Account newAcc = new Account(username, hashedPassword, id);

        // Hàm này bên Database sẽ ném lỗi nếu trùng user
        accountDB.addAccount(newAcc);
    }
}