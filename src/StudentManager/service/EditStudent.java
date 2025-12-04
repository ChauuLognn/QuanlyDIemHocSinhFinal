package StudentManager.service;

import StudentManager.Student;
import StudentManager.data.StudentDatabase;

import java.util.Scanner;

public class EditStudent {
    private StudentDatabase studentDB = StudentDatabase.getStudentDB();

    String id;
    Scanner sc = new Scanner(System.in);

    public void edit(){
        //Hiển thị danh sách trước khi sửa
        studentDB.showAllStudents();

        System.out.print("Nhập ID sinh viên muốn sửa đổi: ");
        id = sc.nextLine();
        Student s = studentDB.findByID(id);

        if (s != null){
            int choice;
            do{
                System.out.println("Thông tin muốn sửa:\n" +
                        "1. Họ tên\n" +
                        "2. Mã sinh viên\n" +
                        "3. Lớp\n" +
                        "4. Thoát");

                choice = sc.nextInt();
                switch (choice){
                    case 1 -> {
                        System.out.print("Nhập họ tên mới: ");
                        String newName = sc.nextLine();
                        s.setStudentName(newName);
                        System.out.print("Cập nhật họ tên thành công!");
                    }

                    case 2 -> {
                        System.out.print("Nhập mã sinh viên mới: ");
                        String newID = sc.nextLine();
                        s.setStudentID(newID);
                        System.out.print("Cập nhật mã sinh viên thành công!");
                    }

                    case 3 -> {
                        System.out.print("Nhập lớp mới: ");
                        String newClass = sc.nextLine();
                        s.setStudentClass(newClass);
                        System.out.print("Cập nhật lớp thành công!");
                    }


                    case 4 -> {
                        System.out.print("Thoát sửa thông tin!");
                    }

                    default -> System.out.print("Thông tin không hợp lệ! Vui lòng nhập lại.");
                }
            } while (choice != 4);
        }
    }



}
