package StudentManager.service;

import StudentManager.Student;
import StudentManager.data.StudentDatabase;

public class EditStudent {
    private StudentDatabase studentDB = StudentDatabase.getStudentDB();

    public void edit(String currentId, String newId, String newName, String newClass, String newGender) throws Exception {
        // Tạo một đối tượng tạm chứa thông tin mới
        Student newInfo = new Student(newName, newId, newClass, newGender);

        // Gọi database update
        studentDB.updateStudent(currentId, newInfo);
    }
}