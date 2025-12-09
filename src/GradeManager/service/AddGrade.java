package GradeManager.service;

import Exception.Validator;
import Database.GradeDatabase;

public class AddGrade {
    private GradeDatabase db = GradeDatabase.getInstance();

    public void addScore(String studentId, String subjectId, int semester, double regular, double mid, double finalScore) throws Exception {
        // kiểm tra rỗng
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new Exception("Mã học sinh không được để trống!");
        }

        // validate điểm
        Validator.validateScore(regular);
        Validator.validateScore(mid);
        Validator.validateScore(finalScore);

        // cảnh báo nếu cả 3 điểm = 0
        if (regular == 0 && mid == 0 && finalScore == 0) {
            System.out.println("Cảnh báo: Học sinh " + studentId + " có cả 3 loại điểm = 0");
        }

        // làm tròn
        regular = Math.round(regular * 10.0) / 10.0;
        mid = Math.round(mid * 10.0) / 10.0;
        finalScore = Math.round(finalScore * 10.0) / 10.0;

        // lưu vào db
        db.saveGrade(studentId.trim().toUpperCase(), subjectId, semester, regular, mid, finalScore);
    }
}