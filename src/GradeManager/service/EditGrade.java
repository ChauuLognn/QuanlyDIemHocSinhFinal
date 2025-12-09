package GradeManager.service;

import Database.GradeDatabase;

public class EditGrade {
    private GradeDatabase db = GradeDatabase.getInstance();

    public void editScore(String studentId, String subjectId, int semester, double regular, double mid, double finalScore) throws Exception {
        // kiểm tra rỗng
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new Exception("Mã học sinh không được để trống!");
        }

        // kiểm tra điểm hợp lệ
        if (regular < 0 || regular > 10 || mid < 0 || mid > 10 || finalScore < 0 || finalScore > 10) {
            throw new Exception("Điểm số phải từ 0-10!");
        }

        // làm tròn
        regular = Math.round(regular * 10.0) / 10.0;
        mid = Math.round(mid * 10.0) / 10.0;
        finalScore = Math.round(finalScore * 10.0) / 10.0;

        // lưu vào db
        db.saveGrade(studentId, subjectId, semester, regular, mid, finalScore);
    }
}