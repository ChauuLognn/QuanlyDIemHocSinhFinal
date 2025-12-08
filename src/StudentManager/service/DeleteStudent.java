package StudentManager.service;

import StudentManager.Student;
import Database.StudentDatabase;
import Database.ClassDatabase;

public class DeleteStudent {
    private StudentDatabase studentDB = StudentDatabase.getStudentDB();
    private ClassDatabase classDB = ClassDatabase.getClassDB();

    public void delete(String id) throws Exception {
        if (id.isEmpty()) throw new Exception("Chưa chọn học sinh để xóa!");

        // 1. Lấy thông tin học sinh trước khi xóa
        Student s = studentDB.findByID(id);
        if (s == null) {
            throw new Exception("Không tìm thấy học sinh để xóa!");
        }

        String className = s.getStudentClass();

        // 2. XÓA KHỎI LỚP HỌC TRƯỚC
        if (className != null && !className.trim().isEmpty()) {
            classDB.removeStudentFromClass(id, className);
        }

        // 3. Xóa khỏi StudentDatabase
        studentDB.deleteStudent(id);
    }
}