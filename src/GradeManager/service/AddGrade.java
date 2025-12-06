package GradeManager.service;

import Exception.InvalidScoreException;
import Exception.Validator;
import GradeManager.Grade;
import GradeManager.data.GradeDatabase;

public class AddGrade {
    private GradeDatabase gradeDB = GradeDatabase.getGradeDB();

    // ✅ ĐÃ SỬA: Thêm validation đầy đủ
    public void addScore(String studentId, double regular, double midterm, double fin) throws Exception {
        // 1. Kiểm tra studentId
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new Exception("Mã học sinh không được để trống!");
        }

        // 2. ✅ SỬ DỤNG VALIDATOR CHO TỪNG ĐIỂM
        try {
            Validator.validateScore(regular);
            Validator.validateScore(midterm);
            Validator.validateScore(fin);
        } catch (InvalidScoreException e) {
            throw new Exception("Lỗi nhập điểm: " + e.getMessage());
        }

        // 3. ✅ KIỂM TRA LOGIC NGHIỆP VỤ
        // Cảnh báo nếu cả 3 điểm đều = 0 (có thể chưa nhập)
        if (regular == 0 && midterm == 0 && fin == 0) {
            System.out.println("⚠️ Cảnh báo: Học sinh " + studentId + " có cả 3 loại điểm = 0");
        }

        // 4. ✅ LÀM TRÒN ĐIỂM ĐẾN 1 CHỮ SỐ THẬP PHÂN
        regular = Math.round(regular * 10.0) / 10.0;
        midterm = Math.round(midterm * 10.0) / 10.0;
        fin = Math.round(fin * 10.0) / 10.0;

        // 5. Lưu vào DB
        Grade grade = new Grade(studentId.trim().toUpperCase(), regular, midterm, fin);
        gradeDB.addOrUpdateGrade(grade);
    }
}