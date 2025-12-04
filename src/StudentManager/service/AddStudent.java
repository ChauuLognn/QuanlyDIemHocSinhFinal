package StudentManager.service;


import ClassManager.Classes;
import ClassManager.data.ClassDatabase;
import Exception.InvalidIDException;
import Exception.InvalidNameException;
import Exception.Validator;
import StudentManager.Student;
import StudentManager.data.StudentDatabase;

import java.util.Scanner;



public class AddStudent {
    private StudentDatabase studentDB = StudentDatabase.getStudentDB();
    private ClassDatabase classesDB = ClassDatabase.getClassDB();
    Scanner sc = new Scanner(System.in);


    //Add student
    public void add(){
        String studentName = "";
        try {
            System.out.print("Nhập tên học sinh: ");
            studentName = sc.nextLine();
            Validator.validateName(studentName);
        } catch (InvalidNameException e){
            System.out.println("Lỗi: " + e.getMessage());
        }

        String studentID = "";
        String studentClassID = "";
        boolean isExit;
        do {
            isExit = false;
            try {
                System.out.print("Nhập mã học sinh: ");
                studentID = sc.nextLine();
                Validator.validateID(studentID);
                isExit = true;
            } catch (InvalidIDException e) {
                System.out.println("Lỗi: " + e.getMessage());
            }


            //Kiểm tra trùng ID
            if (studentDB.findByID(studentID) != null){
                System.out.println("Mã học sinh đã tồn tại!");
                System.out.print("Nhập lại mã học sinh: ");
                isExit = true;
            }
        } while (isExit);


        Classes targetClass = null;

        //Dùng vòng lặp để nhập mã lớp hoặc tạo lớp mới để thêm học sinh vào lớp
        while (targetClass == null){
            System.out.print("Nhập mã lớp: ");
            studentClassID = sc.nextLine();
            //Tìm kiếm mã lớp, nếu chưa có thì tạo mới
            targetClass = classesDB.findClassByID(studentClassID);

            if (targetClass == null){
                System.out.print("Chưa có lớp " + studentClassID + ", bạn có muốn tạo lớp mới không? Y/N: ");
                String ans = sc.nextLine();
                if (ans.equalsIgnoreCase("y") || ans.equalsIgnoreCase("yes")){
                    System.out.print("Nhập tên lớp mới: ");
                    String newClassName = sc.nextLine();

                    targetClass = new Classes(newClassName, studentClassID);
                    classesDB.addNewClass(targetClass);
                    System.out.println("Đã tạo lớp " + studentClassID + " thành công!");
                    break;
                }
                else if (ans.equalsIgnoreCase("n") || ans.equalsIgnoreCase("no")){
                    System.out.print("Chưa có lớp " + studentClassID +". Thêm học sinh mà chưa có lớp? Y/N: ");
                    String ans2 = sc.nextLine();
                    if (ans2.equalsIgnoreCase("y") || ans2.equalsIgnoreCase("yes")){
                        studentClassID = "Chưa có lớp";
                        break;
                    }
                    else {
                        System.out.print("Bạn có muốn nhập lại mã lớp? Y/N: ");
                        String ans3 = sc.nextLine();

                        if (ans3.equalsIgnoreCase("n") || ans3.equalsIgnoreCase("no")){
                            studentName = "";
                            break;
                        }
                    }
                    //Nếu câu trả lời là Y/ Yes (Có), sẽ phải nhập lại mã lớp
                }
            }

        }
        Student newStudent = new Student(studentName, studentID, studentClassID);
        if (targetClass != null && !studentClassID.equalsIgnoreCase("Chưa có lớp")) {
            targetClass.getStudents().add(newStudent);
            //Cập nhật sĩ số lớp
            targetClass.setStudentNumber(targetClass.getStudents().size());

        }

        if (!studentName.equalsIgnoreCase("")){
            studentDB.addStudent(newStudent);
            System.out.println("Đã thêm học sinh thành công!");
        }else{
            System.out.println("Hủy thêm mới học sinh!");
        }

    }
}