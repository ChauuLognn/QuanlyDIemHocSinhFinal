package GradeManager.service;

import Database.GradeDatabase;

public class DeleteGrade {
    private GradeDatabase gradeDB = GradeDatabase.getGradeDB();

    public void delete(String studentId) {
        // Xóa luôn
        gradeDB.deleteGrade(studentId);
    }
}