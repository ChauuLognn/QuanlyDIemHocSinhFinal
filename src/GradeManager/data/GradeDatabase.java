package GradeManager.data;

import Exception.InvalidScoreException;
import Exception.NotFoundException;
import Exception.Validator;
import GradeManager.Grade;

import java.util.HashMap;

public class GradeDatabase {
    private static HashMap<String, Grade> grades = new HashMap<>();
    private static GradeDatabase gradeDB = new GradeDatabase();

    private GradeDatabase(){}

    public static GradeDatabase getGradeDB(){ return gradeDB; }
    public HashMap<String, Grade> getGrades(){
        return grades;
    }
    //Hiển thị điểm
    public void showAllGrades(){
        if (grades.isEmpty()){
            System.out.println("Chưa có điểm!");
            return;
        }
        for (Grade g : grades.values()){
            System.out.println("Mã học sinh: " + g.getStudentID());
            System.out.println("Điểm thường xuyên: " + g.getRegularScore() );
            System.out.println("Điểm giữa kì: " + g.getMidtermScore());
            System.out.println("Điểm cuối kì: " + g.getFinalScore());
        }

    }

    //Cập nhật điểm cho học sinh theo ID
    public void addOrUpdateGrade(Grade grade) throws InvalidScoreException {
        Validator.validateScore(grade.getRegularScore());
        Validator.validateScore(grade.getMidtermScore());
        Validator.validateScore(grade.getFinalScore());

        grades.put(grade.getStudentID(), grade);
    }


    //Lấy điểm của học sinh theo ID
    public Grade getGradeByStudentID (String studentID) throws NotFoundException{
        if (!grades.containsKey(studentID)){
            throw new NotFoundException("Không tìm thấy điểm của học sinh " + studentID);
        }
        return grades.get(studentID);
    }

    //Xóa điểm của học sinh theo ID
    public void deleteGrade(String studentID) throws NotFoundException {
        if (!grades.containsKey(studentID)){
            throw new NotFoundException("Không tìm thấy học sinh có mã " + studentID);
        }
        grades.remove(studentID);
    }
}
