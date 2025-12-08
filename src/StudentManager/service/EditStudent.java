package StudentManager.service;

import StudentManager.Student;
import Database.StudentDatabase;
import Database.ClassDatabase;

public class EditStudent {
    private StudentDatabase studentDB = StudentDatabase.getStudentDB();
    private ClassDatabase classDB = ClassDatabase.getClassDB();

    public void edit(String currentId, String newId, String newName, String newClass, String newGender) throws Exception {
        // 1. Lấy thông tin học sinh cũ
        Student oldStudent = studentDB.findByID(currentId);
        if (oldStudent == null) {
            throw new Exception("Không tìm thấy học sinh gốc!");
        }

        String oldClass = oldStudent.getStudentClass();

        // 2. Kiểm tra lớp mới có tồn tại không
        if (classDB.findClassByID(newClass) == null) {
            throw new Exception("Lớp " + newClass + " không tồn tại trong hệ thống!");
        }

        // 3. Tạo đối tượng thông tin mới
        Student newInfo = new Student(newName, newId, newClass, newGender);

        // 4. Cập nhật trong StudentDatabase
        studentDB.updateStudent(currentId, newInfo);

        // 5. ✅ CẬP NHẬT LẠI THÔNG TIN LỚP
        // Nếu đổi ID hoặc đổi lớp, cần xử lý
        if (!currentId.equalsIgnoreCase(newId)) {
            // Trường hợp đổi cả ID và có thể cả lớp
            classDB.removeStudentFromClass(currentId, oldClass);
            classDB.addStudentToClass(newId, newClass);
        } else if (!oldClass.equalsIgnoreCase(newClass)) {
            // Chỉ đổi lớp, không đổi ID
            classDB.updateStudentClass(newId, oldClass, newClass);
        }
        // Nếu không đổi gì cả thì không cần update
    }
}