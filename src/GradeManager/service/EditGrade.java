package GradeManager.service;

import Exception.NotFoundException;
import Exception.InvalidScoreException;
import GradeManager.Grade;
import GradeManager.data.GradeDatabase;

public class EditGrade {
    private GradeDatabase gradeDB = GradeDatabase.getGradeDB();

    public void editScore(String studentId, double regular, double midterm, double fin) throws Exception {
        if (gradeDB.getGradeByStudentID(studentId) == null) {
            // Nếu chưa có điểm thì coi như thêm mới
            // Hoặc throw Exception("Học sinh này chưa có điểm để sửa") tùy logic bạn muốn
        }

        if (regular < 0 || regular > 10 || midterm < 0 || midterm > 10 || fin < 0 || fin > 10) {
            throw new Exception("Điểm số phải nằm trong khoảng 0 - 10!");
        }

        Grade grade = new Grade(studentId, regular, midterm, fin);
        gradeDB.addOrUpdateGrade(grade);
    }
}