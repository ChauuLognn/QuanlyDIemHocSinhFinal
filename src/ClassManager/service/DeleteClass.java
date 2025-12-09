package ClassManager.service;

import Database.ClassDatabase;

public class DeleteClass {
    private ClassDatabase db = ClassDatabase.getInstance();

    public void delete(String classID) throws Exception {
        if (classID.isEmpty()) {
            throw new Exception("Chưa chọn lớp để xóa!");
        }
        db.deleteClass(classID);
    }
}