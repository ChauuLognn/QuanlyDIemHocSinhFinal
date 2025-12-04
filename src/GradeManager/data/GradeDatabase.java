package GradeManager.data;

import Exception.InvalidScoreException;
import Exception.NotFoundException;
import Exception.Validator;
import GradeManager.Grade;
import java.util.ArrayList;
import java.util.HashMap;

public class GradeDatabase {
    private static GradeDatabase gradeDB = new GradeDatabase();
    // Dùng HashMap để tìm điểm theo StudentID cực nhanh
    private HashMap<String, Grade> grades = new HashMap<>();

    private GradeDatabase(){}

    public static GradeDatabase getGradeDB(){ return gradeDB; }

    public HashMap<String, Grade> getGrades(){
        return grades;
    }

    // Lấy điểm của 1 học sinh
    public Grade getGradeByStudentID (String studentID) {
        return grades.get(studentID);
    }

    // Thêm hoặc cập nhật điểm
    public void addOrUpdateGrade(Grade grade) {
        grades.put(grade.getStudentID(), grade);
    }

    // Xóa điểm
    public void deleteGrade(String studentID) {
        grades.remove(studentID);
    }
}
