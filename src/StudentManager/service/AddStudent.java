package StudentManager.service;

import StudentManager.Student;
import StudentManager.data.StudentDatabase;
import ClassManager.data.ClassDatabase;

public class AddStudent {
    private StudentDatabase studentDB = StudentDatabase.getStudentDB();
    private ClassDatabase classDB = ClassDatabase.getClassDB();


    public void add(String id, String name, String className, String gender) throws Exception {
        // 1. Validate
        if (id.isEmpty() || name.isEmpty() || className.isEmpty()) {
            throw new Exception("Vui lòng nhập đủ thông tin!");
        }

        // 2. Tạo đối tượng
        if (classDB.findClassByID(className) == null) {
            throw new Exception("Lớp " + className + " không tồn tại trong hệ thống!\n" +
                    "Vui lòng tạo lớp trước hoặc kiểm tra lại mã lớp.");
        }
        // 3. Tạo đối tượng Student
        Student s = new Student(name, id, className, gender);

        // 4. Lưu vào StudentDatabase
        studentDB.addStudent(s);

        // 5. ✅ THÊM VÀO LỚP HỌC
        classDB.addStudentToClass(id, className);
    }
}