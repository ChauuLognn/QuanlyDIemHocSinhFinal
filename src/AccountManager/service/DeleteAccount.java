package AccountManager.service;

import AccountManager.Account;
import AccountManager.data.AccountDatabase;

import java.util.Scanner;

public class DeleteAccount {
    private AccountDatabase accountDB = AccountDatabase.getAccountDB();
    String username;
    Scanner sc = new Scanner(System.in);

    public void delete(){
        System.out.print("Nhập tên đăng nhập tài khoản muốn xóa: ");
        username = sc.nextLine();
        Account a = accountDB.findAccountByUsername(username);
        if(a == null){
            System.out.println("Không tìm thấy tài khoản " + username);
            return;
        }

        boolean success = accountDB.deleteAccount(username);
        if (success)
         System.out.println("Đã xóa tài khoản thành công!");
        else {
            System.out.println("Lỗi! Không thể xóa tài khoản.");
        }
    }
}
