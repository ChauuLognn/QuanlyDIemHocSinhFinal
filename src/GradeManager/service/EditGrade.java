package GradeManager.service;

import Exception.NotFoundException;
import Exception.InvalidScoreException;
import GradeManager.Grade;
import GradeManager.data.GradeDatabase;

import java.util.Scanner;

public class EditGrade {
    private GradeDatabase gradeDB = GradeDatabase.getGradeDB();

    Scanner sc = new Scanner(System.in);
    String studentID;

    public void edit(){
        System.out.print("Nhập mã học sinh: ");
        studentID = sc.nextLine();


        if (!gradeDB.getGrades().containsKey(studentID)){
            System.out.println("Không tìm thấy học sinh có mã " + studentID);
            return;
        }

        try {
            //Lấy điểm hiện tại của học sinh
            Grade grade = gradeDB.getGradeByStudentID(studentID);

            //Nếu điểm ban đầu là null thì tạo mới
            if (grade == null) {
                grade = new Grade(studentID, 0, 0, 0);
            }
            int choose;
            do{
                System.out.print("Chọn điểm muốn sửa:\n" +
                        "1. Điểm thường xuyên\n" +
                        "2. Điểm giữa kì\n" +
                        "3. Điểm cuối kì\n" +
                        "4. Thoát\n" +
                        "Chọn: ");
                choose = sc.nextInt();
                sc.nextLine();
                switch (choose){
                    case 1 -> {
                        System.out.print("Nhập điểm thường xuyên mới: ");
                        double newRegularScore = sc.nextDouble();
                        sc.nextLine();
                        grade.setRegularScore(newRegularScore);
                        gradeDB.addOrUpdateGrade(grade);
                        System.out.println("Cập nhật điểm thường xuyên thành công!");
                    }
                    case 2 -> {
                        System.out.print("Nhập điểm giữa kì mới: ");
                        double newMidtermScore = sc.nextDouble();
                        sc.nextLine();
                        grade.setMidtermScore(newMidtermScore);
                        gradeDB.addOrUpdateGrade(grade);
                        System.out.println("Cập nhật điểm giữa kì thành công!");
                    }
                    case 3 -> {
                        System.out.print("Nhập điểm cuối kì mới: ");
                        double newFinalScore = sc.nextDouble();
                        sc.nextLine();
                        grade.setFinalScore(newFinalScore);
                        gradeDB.addOrUpdateGrade(grade);
                        System.out.println("Cập nhật điểm cuối kì thành công!");
                    }
                    default -> {
                        System.out.print("Lựa chọn không hợp lệ! Chọn lại: ");
                    }
                }
            }while (choose != 4);

        } catch (NotFoundException e){
            System.out.println("Lỗi: " + e.getMessage());
        } catch (InvalidScoreException e) { // <--- SỬA THÊM: Bắt lỗi điểm sai (ví dụ <0 hoặc >10)
            System.out.println("Lỗi nhập điểm: " + e.getMessage());
        } catch (java.util.InputMismatchException e) { // <--- SỬA THÊM: Bắt lỗi nhập chữ thay vì số
            System.out.println("Lỗi: Vui lòng nhập con số hợp lệ!");
            sc.nextLine(); // Xóa bộ nhớ đệm
        }
    }
}