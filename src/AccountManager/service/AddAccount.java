package AccountManager.service;

import AccountManager.Account;
import AccountManager.data.AccountDatabase;

import java.util.Scanner;

public class AddAccount {
    Scanner sc = new Scanner(System.in);
    private AccountDatabase accountDB = AccountDatabase.getAccountDB();
    String username;
    String password;
    String ID;
    boolean isExit;
    public void add(){
        do {
            isExit = false;
            System.out.print("Nhập tên đăng nhập: ");
            username = sc.nextLine();
            Account a = accountDB.findAccountByUsername(username);
            if (a != null){
                System.out.println("Tên đăng nhập đã được sử dụng. Bạn muốn: \n" +
                        "1. Nhập lại tên khác\n" +
                        "2. Thoát");
                int choose = sc.nextInt();
                sc.nextLine();
                if (choose == 2) break;
                isExit = true;
            }
        } while (!isExit);

        System.out.print("Nhập mật khẩu: ");
        password = sc.nextLine();
        password = accountDB.hashSHA256(password);
        System.out.print("Nhập mã học sinh/ giáo viên: ");
        ID = sc.nextLine();

        accountDB.addAccount(username, password, ID);
        System.out.println("Thêm tài khoản thành công!");
    }
}
