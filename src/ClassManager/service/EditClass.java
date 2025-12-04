package ClassManager.service;

import ClassManager.Classes;
import ClassManager.data.ClassDatabase;

import java.util.Scanner;

public class EditClass {
    private ClassDatabase classDB = ClassDatabase.getClassDB();
    String classInfo;
    Scanner sc = new Scanner(System.in);

    public void edit(){
        System.out.print("Nhập tên/ mã lớp muốn sửa: ");
        classInfo = sc.nextLine();

        //Tìm kiếm lớp
        Classes findedClassByID = classDB.findClassByID(classInfo); //tìm theo mã lớp
        Classes findedClassByName = classDB.findClassByName(classInfo);  //tìm theo tên lớp

        Classes targetClass = null;
        if (findedClassByID != null){
            targetClass = findedClassByID;
        }else {
            targetClass = findedClassByName;
        }

        if (targetClass == null){
            System.out.println("Không tồn tại lớp " + targetClass.getClassID());
            return;
        }

        int choose;
        do{
            System.out.print("  Chọn thông tin muốn sửa: \n" +
                            "1. Tên lớp\n" +
                            "2. Mã lớp\n" +
                            "3. Thoát\n" +
                            "Chọn: ");
            choose = sc.nextInt();
            sc.nextLine();

            switch (choose){
                case 1 -> {
                    System.out.print("Nhập tên lớp mới: ");
                    String newClassName = sc.nextLine();
                    targetClass.setClassName(newClassName);
                    if(targetClass.getClassName().equalsIgnoreCase(newClassName)){
                        System.out.println("Cập nhật tên lớp mới thành công!");
                    }else{
                        System.out.println("Lỗi! Cập nhật tên lớp mới không thành công!");
                    }
                }
                case 2 -> {
                    System.out.print("Nhập mã lớp mới: ");
                    String newClassID = sc.nextLine();
                    targetClass.setClassID(newClassID);
                    if(targetClass.getClassID().equalsIgnoreCase(newClassID)){
                        System.out.println("Cập nhật mã lớp mới thành công!");
                    }else{
                        System.out.println("Lỗi! Cập nhật mã lớp mới không thành công!");
                    }
                }
                case 3 -> {
                    System.out.println("Thoát sửa thông tin!");

                }
                default -> System.out.print("Lựa chọn không hợp lệ! Vui lòng chọn lại: ");
            }
        }while (choose != 3);
    }

}
