package ClassManager.service;

import ClassManager.Classes;
import ClassManager.data.ClassDatabase;
import StudentManager.data.StudentDatabase;

import java.util.Scanner;

public class AddClass {
    private ClassDatabase classDB = ClassDatabase.getClassDB();
    private StudentDatabase studentDB = StudentDatabase.getStudentDB();
    Scanner sc = new Scanner(System.in);

    //Thêm lớp mới
    public void add(){
        System.out.print("Nhập tên lớp mới: ");
        String newClassName = sc.nextLine();
        System.out.print("Nhập mã lớp mới: ");
        String newClassID = sc.nextLine();

        Classes newClass = new Classes(newClassName, newClassID);
        classDB.addNewClass(newClass);
        System.out.println("Thêm lớp mới thành công!");
    }



}
