package ClassManager.data;

import ClassManager.Classes;
import java.util.ArrayList;

public class ClassDatabase {
    private static ClassDatabase classBD = new ClassDatabase();
    private ArrayList<Classes> classes = new ArrayList<>();

    private ClassDatabase(){}

    public static ClassDatabase getClassDB(){ return classBD; }

    public ArrayList<Classes> getAllClasses() {
        return classes;
    }

    public Classes findClassByID(String classID){
        for (Classes c : classes){
            if (c.getClassID().equalsIgnoreCase(classID)){
                return c;
            }
        }
        return null;
    }

    public void addNewClass(Classes c) throws Exception {
        if (findClassByID(c.getClassID()) != null) {
            throw new Exception("Mã lớp " + c.getClassID() + " đã tồn tại!");
        }
        classes.add(c);
    }

    public void updateClass(String oldID, String newID, String newName) throws Exception {
        Classes old = findClassByID(oldID);
        if (old == null) throw new Exception("Không tìm thấy lớp để sửa!");

        // Nếu đổi mã lớp, check trùng
        if (!oldID.equalsIgnoreCase(newID)) {
            if (findClassByID(newID) != null) {
                throw new Exception("Mã lớp mới bị trùng!");
            }
        }

        old.setClassID(newID);
        old.setClassName(newName);
    }

    public void deleteClass(String classID) throws Exception {
        Classes c = findClassByID(classID);
        if (c == null){
            throw new Exception("Không tìm thấy lớp " + classID);
        }
        classes.remove(c);
    }
}