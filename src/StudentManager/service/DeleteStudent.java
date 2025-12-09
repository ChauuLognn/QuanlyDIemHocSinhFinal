package StudentManager.service;

import StudentManager.Student;
import Database.StudentDatabase;

public class DeleteStudent {
    private StudentDatabase db = StudentDatabase.getInstance();

    public void delete(String id) throws Exception {
        if (id.isEmpty()) {
            throw new Exception("Chưa chọn học sinh để xóa!");
        }

        Student s = db.findByID(id);
        if (s == null) {
            throw new Exception("Không tìm thấy học sinh!");
        }

        db.deleteStudent(id);
    }
}