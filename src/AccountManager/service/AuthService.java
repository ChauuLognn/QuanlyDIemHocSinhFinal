package AccountManager.service;

import AccountManager.Account;
import Database.AccountDatabase;

public class AuthService {
    private AccountDatabase accountDB = AccountDatabase.getAccountDB();

    // Hàm login mới: Nhận thêm tham số roleSelected từ giao diện
    public Account login(String username, String password, String roleSelected) throws Exception {
        // 1. Kiểm tra user/pass
        Account acc = accountDB.login(username, password);

        if (acc == null) {
            throw new Exception("Sai tên đăng nhập hoặc mật khẩu!");
        }

        // 2. Kiểm tra vai trò (Role)
        // roleSelected: Là cái người dùng chọn (Admin, Teacher, Student)
        // acc.getRole(): Là cái lưu trong DB (admin, teacher, student)

        String dbRole = acc.getRole();

        // Map từ giao diện tiếng Việt sang DB tiếng Anh (hoặc ngược lại tùy bạn quy ước)
        // Giả sử DB lưu: admin, teacher, student
        String roleCode = "";
        if (roleSelected.equals("Quản trị viên")) roleCode = "admin";
        else if (roleSelected.equals("Giáo viên")) roleCode = "teacher";
        else if (roleSelected.equals("Học sinh")) roleCode = "student";

        if (!dbRole.equalsIgnoreCase(roleCode)) {
            throw new Exception("Bạn không có quyền đăng nhập với vai trò " + roleSelected + "!");
        }

        return acc; // Đăng nhập thành công, trả về acc để UI xử lý tiếp
    }
}