package AccountManager.service;

import AccountManager.Account;
import AccountManager.data.AccountDatabase;

import java.util.Scanner;

public class EditAccount {
    private AccountDatabase accountDB = AccountDatabase.getAccountDB();
    String username;
    String password;
    Scanner sc = new Scanner(System.in);


    public void edit(){

        //Kiểm tra thông tin đăng nhập trước khi cho sửa đổi
        System.out.print("Nhập tên đăng nhập: ");
        username = sc.nextLine();
        System.out.print("Nhập mật khẩu: ");
        password = sc.nextLine();

        boolean check = accountDB.checkLogin(username, password);


        if (!check) {
            return;
        }

        Account a = accountDB.findAccountByUsername(username);
        System.out.print("  Chọn thông tin muốn sửa:\n" +
                "1. Tên đăng nhập\n" +
                "2. Mật khẩu\n" +
                "Chọn: ");
        int choose = sc.nextInt();
        sc.nextLine();
        switch (choose){
            case 1 -> {
                System.out.print("Nhập tên đăng nhập mới: ");
                String newUsername = sc.nextLine();

                accountDB.updateUsername(username, newUsername);
                System.out.println("Đổi tên đăng nhập thành công!");
            }
            case 2 -> {
                System.out.print("Nhập mật khẩu mới: ");
                String newPassword = sc.nextLine();

                newPassword = accountDB.hashSHA256(newPassword);
                a.setPassword(newPassword);
                System.out.println("Đổi mật khẩu thành công!");
            }

            default -> System.out.println("Lựa chọn không hợp lệ!");
        }

    }
}
