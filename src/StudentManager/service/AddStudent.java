package StudentManager.service;

import StudentManager.Student;
import StudentManager.data.StudentDatabase;
// import ClassManager.data.ClassDatabase; // Tạm thời comment để giảm sự phụ thuộc, tránh lỗi

public class AddStudent {
    private StudentDatabase studentDB = StudentDatabase.getStudentDB();

    public void add(String id, String name, String className, String gender) throws Exception {
        // 1. Validate
        if (id.isEmpty() || name.isEmpty() || className.isEmpty()) {
            throw new Exception("Vui lòng nhập đủ thông tin!");
        }

        // 2. Tạo đối tượng
        Student s = new Student(name, id, className, gender);

        // 3. Gọi Database lưu
        studentDB.addStudent(s);

        // Lưu ý: Logic thêm vào ClassManager tạm bỏ qua để đơn giản hóa luồng đi.
        // Sau này muốn chuẩn chỉ thì uncomment dòng dưới:
        // ClassManager.data.ClassDatabase.getClassDB().addStudentToClass(id, className);
    }
}