package GradeManager.service;

import GradeManager.data.GradeDatabase;

public class DeleteGrade {
    private GradeDatabase gradeDB = GradeDatabase.getGradeDB();

    public void delete(String studentId) {
        // Xóa luôn
        gradeDB.deleteGrade(studentId);
    }
}