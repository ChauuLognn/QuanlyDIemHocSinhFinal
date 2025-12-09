package AccountManager.service;

import AccountManager.Account;
import Database.AccountDatabase;

public class AuthService {
    private AccountDatabase db = AccountDatabase.getInstance();

    // đăng nhập với role
    public Account login(String username, String password, String roleSelected) throws Exception {
        // kiểm tra user/pass
        Account acc = db.login(username, password);

        if (acc == null) {
            throw new Exception("Sai tên đăng nhập hoặc mật khẩu!");
        }

        // kiểm tra vai trò
        String dbRole = acc.getRole();

        // map từ tiếng việt sang db
        String roleCode = "";
        if (roleSelected.equals("Quản trị viên")) roleCode = "admin";
        else if (roleSelected.equals("Giáo viên")) roleCode = "teacher";
        else if (roleSelected.equals("Học sinh")) roleCode = "student";

        if (!dbRole.equalsIgnoreCase(roleCode)) {
            throw new Exception("Bạn không có quyền đăng nhập với vai trò " + roleSelected + "!");
        }

        return acc;
    }
}