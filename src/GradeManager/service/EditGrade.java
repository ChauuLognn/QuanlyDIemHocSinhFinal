package GradeManager.service;

import Database.GradeDatabase; // Import đúng package Database

public class EditGrade {
    private GradeDatabase gradeDB = GradeDatabase.getGradeDB();

    // Hàm sửa điểm đầy đủ (Có môn học + học kỳ)
    public void editScore(String studentId, String subjectId, int semester, double regular, double midterm, double fin) throws Exception {

        // 1. Validation cơ bản
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new Exception("Mã học sinh không được để trống!");
        }

        if (regular < 0 || regular > 10 || midterm < 0 || midterm > 10 || fin < 0 || fin > 10) {
            throw new Exception("Điểm số phải nằm trong khoảng 0 - 10!");
        }

        // 2. Làm tròn điểm (1 chữ số thập phân)
        regular = Math.round(regular * 10.0) / 10.0;
        midterm = Math.round(midterm * 10.0) / 10.0;
        fin = Math.round(fin * 10.0) / 10.0;

        // 3. Gọi Database để lưu
        // Không cần tạo object Grade hay dùng rs ở đây
        gradeDB.saveGrade(studentId, subjectId, semester, regular, midterm, fin);
    }
}