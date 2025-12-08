package ClassManager.service;

import Database.ClassDatabase;

public class EditClass {
    private ClassDatabase classDB = ClassDatabase.getClassDB();

    public void edit(String oldID, String newID, String newName) throws Exception {
        if (newID.isEmpty() || newName.isEmpty()) {
            throw new Exception("Thông tin không được để trống!");
        }

        classDB.updateClass(oldID, newID, newName);
    }
}