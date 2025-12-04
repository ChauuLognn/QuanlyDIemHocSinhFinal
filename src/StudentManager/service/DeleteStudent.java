package StudentManager.service;

import StudentManager.data.StudentDatabase;

public class DeleteStudent {
    private StudentDatabase studentDB = StudentDatabase.getStudentDB();

    public void delete(String id) throws Exception {
        if (id.isEmpty()) throw new Exception("Chưa chọn học sinh để xóa!");
        studentDB.deleteStudent(id);
    }
}