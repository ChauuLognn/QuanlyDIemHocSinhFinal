package GradeManager.service;

import Exception.InvalidScoreException;
import Exception.Validator;
import Database.GradeDatabase;

public class AddGrade {
    private GradeDatabase gradeDB = GradeDatabase.getGradeDB();

    public void addScore(String studentId, double regular, double midterm, double fin) throws Exception {
        // 1. Kiểm tra studentId
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new Exception("Mã học sinh không được để trống!");
        }

        // 2. VALIDATOR
        try {
            Validator.validateScore(regular);
            Validator.validateScore(midterm);
            Validator.validateScore(fin);
        } catch (InvalidScoreException e) {
            throw new Exception("Lỗi nhập điểm: " + e.getMessage());
        }

        // 3. LOGIC NGHIỆP VỤ
        if (regular == 0 && midterm == 0 && fin == 0) {
            System.out.println("⚠️ Cảnh báo: Học sinh " + studentId + " có cả 3 loại điểm = 0");
        }

        // 4. LÀM TRÒN
        regular = Math.round(regular * 10.0) / 10.0;
        midterm = Math.round(midterm * 10.0) / 10.0;
        fin = Math.round(fin * 10.0) / 10.0;

        gradeDB.addOrUpdateGrade(studentId.trim().toUpperCase(), 1, regular, midterm, fin);
    }
}