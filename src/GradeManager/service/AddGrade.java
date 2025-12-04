package GradeManager.service;

import Exception.InvalidScoreException;
import Exception.Validator;
import GradeManager.Grade;
import GradeManager.data.GradeDatabase;


public class AddGrade {
    private GradeDatabase gradeDB = GradeDatabase.getGradeDB();

    public void addScore(String studentId, double regular, double midterm, double fin) throws Exception {
        // 1. Validate điểm hợp lệ
        if (regular < 0 || regular > 10 || midterm < 0 || midterm > 10 || fin < 0 || fin > 10) {
            throw new Exception("Điểm số phải nằm trong khoảng 0 - 10!");
        }

        // 2. Lưu vào DB
        Grade grade = new Grade(studentId, regular, midterm, fin);
        gradeDB.addOrUpdateGrade(grade);
    }
}
