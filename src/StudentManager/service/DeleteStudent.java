package StudentManager.service;


import StudentManager.Student;
import StudentManager.data.StudentDatabase;

import java.util.Scanner;

public class DeleteStudent {
    private StudentDatabase studentDB = StudentDatabase.getStudentDB();
    String id;
    Scanner sc = new Scanner(System.in);


    //Xóa sinh viên
    public void delete(){
        //Hiển thị danh sách học sinh trước khi xóa
        studentDB.showAllStudents();

        //Nhập ID học sinh muốn xóa
        System.out.print("Nhập ID học sinh muốn xóa: ");
        id = sc.nextLine();
        Student s = studentDB.findByID(id);
        if (s != null){
            System.out.print("Chắc chắn muốn xóa học sinh "+ s.toString() + "?\nY/N: " );
            String answer = sc.nextLine();
            while (true){
                if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                    studentDB.deleteStudent(id);
                    System.out.println("Xóa học sinh thành công!");
                    break;
                }
                else if (answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no")){
                    System.out.println("Hủy xóa học sinh thành công!");
                    break;
                }
                else {
                    System.out.print("Lựa chọn không hợp lệ! Chọn lại. Y/N: ");

                }
            }
        }
        else {
            System.out.println("Không tồn tại học sinh có ID " + id );
        }
    }
}
