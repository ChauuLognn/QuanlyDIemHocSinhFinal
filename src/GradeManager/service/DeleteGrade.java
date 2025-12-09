package GradeManager.service;

import Database.GradeDatabase;

public class DeleteGrade {
    private GradeDatabase db = GradeDatabase.getInstance();

    public void delete(String studentId) {
        db.deleteGrade(studentId);
    }
}