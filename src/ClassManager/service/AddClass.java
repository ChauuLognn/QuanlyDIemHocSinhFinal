package ClassManager.service;

import ClassManager.Classes;
import ClassManager.data.ClassDatabase;

public class AddClass {
    private ClassDatabase classDB = ClassDatabase.getClassDB();

    public void add(String id, String name) throws Exception {
        if (id.isEmpty() || name.isEmpty()) {
            throw new Exception("Mã lớp và Tên lớp là bắt buộc!");
        }

        Classes newClass = new Classes(name, id);
        classDB.addNewClass(newClass);
    }
}