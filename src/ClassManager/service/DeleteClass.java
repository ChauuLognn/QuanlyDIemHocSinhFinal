package ClassManager.service;

import ClassManager.data.ClassDatabase;

public class DeleteClass {
    private ClassDatabase classDB = ClassDatabase.getClassDB();

    public void delete(String classID) throws Exception {
        if (classID.isEmpty()) throw new Exception("Chưa chọn lớp để xóa!");
        classDB.deleteClass(classID);
    }
}