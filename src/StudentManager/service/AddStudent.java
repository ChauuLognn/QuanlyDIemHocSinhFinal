package StudentManager.service;

import StudentManager.Student;
import Database.StudentDatabase;
import Database.ClassDatabase;
import Exception.Validator;

public class AddStudent {
    private StudentDatabase stuDb = StudentDatabase.getInstance();
    private ClassDatabase classDb = ClassDatabase.getInstance();

    public void add(String id, String name, String className, String gender) throws Exception {
        // kiểm tra rỗng
        if (id == null || id.trim().isEmpty()) {
            throw new Exception("Mã học sinh không được để trống!");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Tên học sinh không được để trống!");
        }
        if (className == null || className.trim().isEmpty()) {
            throw new Exception("Lớp học không được để trống!");
        }
        if (gender == null || gender.trim().isEmpty()) {
            throw new Exception("Giới tính không được để trống!");
        }

        // validate
        Validator.validateID(id.trim());
        Validator.validateName(name.trim());

        // kiểm tra giới tính
        if (!gender.equalsIgnoreCase("Nam") && !gender.equalsIgnoreCase("Nữ")) {
            throw new Exception("Giới tính chỉ được là 'Nam' hoặc 'Nữ'!");
        }

        // kiểm tra lớp tồn tại
        if (classDb.findByID(className.trim()) == null) {
            throw new Exception("Lớp " + className + " không tồn tại!");
        }

        // kiểm tra trùng mã
        if (stuDb.findByID(id.trim()) != null) {
            throw new Exception("Mã học sinh " + id.trim() + " đã tồn tại!");
        }

        // tạo đối tượng
        Student s = new Student(
                name.trim(),
                id.trim().toUpperCase(),
                className.trim().toUpperCase(),
                gender.trim()
        );

        // lưu vào db
        stuDb.addStudent(s);
    }
}