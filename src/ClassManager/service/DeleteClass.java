package ClassManager.service;

import ClassManager.Classes;
import ClassManager.data.ClassDatabase;

import java.util.Scanner;

public class DeleteClass {
    private ClassDatabase classDB = ClassDatabase.getClassDB();
    String classID;
    Scanner sc = new Scanner(System.in);

    public void delete(){
        //Hiển thị danh sách các lớp trước khi xóa
        classDB.showAllClasses();
        System.out.print("Nhập mã lớp muốn xóa: ");
        classID = sc.nextLine();

        //Tìm kiếm lớp
        Classes findedClass = classDB.findClassByID(classID);
        if (findedClass == null){
            System.out.println("Không tồn tại lớp " + classID);
            return;
        }
        classDB.deleteClass(classID);
        System.out.println("Đã xóa lớp thành công!");
    }

}
