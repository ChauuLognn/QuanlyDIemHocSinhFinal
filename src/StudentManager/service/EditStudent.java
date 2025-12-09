package StudentManager.service;

import StudentManager.Student;
import Database.StudentDatabase;
import Database.ClassDatabase;

public class EditStudent {
    private StudentDatabase stuDb = StudentDatabase.getInstance();
    private ClassDatabase classDb = ClassDatabase.getInstance();

    public void edit(String currentId, String newId, String newName, String newClass, String newGender) throws Exception {
        // kiểm tra học sinh cũ
        Student oldStudent = stuDb.findByID(currentId);
        if (oldStudent == null) {
            throw new Exception("Không tìm thấy học sinh!");
        }

        // kiểm tra lớp mới tồn tại
        if (classDb.findByID(newClass) == null) {
            throw new Exception("Lớp " + newClass + " không tồn tại!");
        }

        // tạo object mới
        Student newInfo = new Student(newName, newId, newClass, newGender);

        // cập nhật
        stuDb.updateStudent(currentId, newInfo);
    }
}