package GradeManager.service;

import Exception.InvalidScoreException;
import Exception.Validator;
import GradeManager.Grade;
import GradeManager.data.GradeDatabase;

import java.util.Scanner;

public class AddGrade {
    private GradeDatabase gradeDB = GradeDatabase.getGradeDB();
    String studentID;
    private double regularScore;
    private double midtermScore;
    private double finalScore;

    Scanner sc = new Scanner(System.in);

    public void addOrUpdate() {
        System.out.print("Nhập mã học sinh: ");
        studentID = sc.nextLine();

        //Kiểm tra xem học sinh có mã studentID đã có điểm hay chưa
        if (gradeDB.getGrades().containsKey(studentID)){
            System.out.println("Học sinh có mã " + studentID + " đã có điểm trên hệ thống!");
            return;
        }

        try {
            System.out.print("Nhập điểm thường xuyên: ");
            regularScore = sc.nextDouble();
            Validator.validateScore(regularScore);

            System.out.print("Nhập điểm giữa kì: ");
            midtermScore = sc.nextDouble();
            Validator.validateScore(midtermScore);

            System.out.print("Nhập điểm cuối kì: ");
            finalScore = sc.nextDouble();
            Validator.validateScore(finalScore);
            sc.nextLine();

            Grade newGrade = new Grade(studentID, regularScore, midtermScore, finalScore);
            gradeDB.addOrUpdateGrade(newGrade);

            System.out.println("Cập nhật điểm thành công!");
        } catch (InvalidScoreException e){
            System.out.println("Lỗi: " + e.getMessage());
        }

    }
}
