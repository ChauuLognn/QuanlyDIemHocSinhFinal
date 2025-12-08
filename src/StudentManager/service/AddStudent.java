package StudentManager.service;

import StudentManager.Student;
import Database.StudentDatabase;
import Database.ClassDatabase;
import Exception.Validator;
import Exception.InvalidIDException;
import Exception.InvalidNameException;

public class AddStudent {
    private StudentDatabase studentDB = StudentDatabase.getStudentDB();
    private ClassDatabase classDB = ClassDatabase.getClassDB();

    // ✅ ĐÃ SỬA: Thêm validation đầy đủ
    public void add(String id, String name, String className, String gender) throws Exception {
        // 1. Kiểm tra rỗng cơ bản
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

        // 2. ✅ SỬ DỤNG VALIDATOR
        try {
            Validator.validateID(id.trim());
            Validator.validateName(name.trim());
        } catch (InvalidIDException | InvalidNameException e) {
            throw new Exception(e.getMessage());
        }

        // 3. ✅ KIỂM TRA GIỚI TÍNH HỢP LỆ
        if (!gender.equalsIgnoreCase("Nam") && !gender.equalsIgnoreCase("Nữ")) {
            throw new Exception("Giới tính chỉ được là 'Nam' hoặc 'Nữ'!");
        }

        // 4. ✅ KIỂM TRA LỚP TỒN TẠI
        if (classDB.findClassByID(className.trim()) == null) {
            throw new Exception("Lớp " + className + " không tồn tại trong hệ thống!\n" +
                    "Vui lòng tạo lớp trước hoặc kiểm tra lại mã lớp.");
        }

        // 5. ✅ KIỂM TRA TRÙNG MÃ HỌC SINH
        if (studentDB.findByID(id.trim()) != null) {
            throw new Exception("Mã học sinh " + id.trim() + " đã tồn tại!\nVui lòng dùng mã khác.");
        }

        // 6. Tạo đối tượng Student (Chuẩn hóa dữ liệu)
        Student s = new Student(
                name.trim(),           // Tên (đã trim)
                id.trim().toUpperCase(), // Mã HS (viết hoa, trim)
                className.trim().toUpperCase(), // Mã lớp (viết hoa, trim)
                gender.trim()          // Giới tính (trim)
        );

        // 7. Lưu vào StudentDatabase
        studentDB.addStudent(s);

        // 8. Thêm vào lớp học
        classDB.addStudentToClass(s.getStudentID(), s.getStudentClass());
    }
}