package ClassManager.data;

import ClassManager.Classes;
import StudentManager.Student;
import StudentManager.data.StudentDatabase;
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

        if (!oldID.equalsIgnoreCase(newID)) {
            updateStudentsClassName(oldID, newID);
        }
    }

    public void deleteClass(String classID) throws Exception {
        Classes c = findClassByID(classID);
        if (c == null){
            throw new Exception("Không tìm thấy lớp " + classID);
        }

        if (c.getStudentNumber() > 0 || !c.getStudents().isEmpty()) {
            throw new Exception("Không thể xóa lớp còn " + c.getStudentNumber() +
                    " học sinh! Hãy chuyển họ sang lớp khác hoặc xóa học sinh trước.");
        }

        classes.remove(c);
    }

    // ==================== CÁC HÀM MỚI ====================

    /**
     * Thêm học sinh vào lớp (Gọi khi AddStudent)
     */
    public void addStudentToClass(String studentID, String classID) throws Exception {
        Classes c = findClassByID(classID);
        if (c == null) {
            throw new Exception("Lớp " + classID + " không tồn tại!");
        }

        Student s = StudentDatabase.getStudentDB().findByID(studentID);
        if (s == null) {
            throw new Exception("Không tìm thấy học sinh " + studentID);
        }

        // Kiểm tra xem học sinh đã có trong danh sách chưa
        boolean exists = false;
        for (Student student : c.getStudents()) {
            if (student.getStudentID().equalsIgnoreCase(studentID)) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            c.getStudents().add(s);
            c.setStudentNumber(c.getStudents().size());
        }
    }

    /**
     * Xóa học sinh khỏi lớp (Gọi khi DeleteStudent)
     */
    public void removeStudentFromClass(String studentID, String classID) {
        if (classID == null || classID.trim().isEmpty()) return;

        Classes c = findClassByID(classID);
        if (c == null) return;

        // Xóa học sinh khỏi danh sách
        c.getStudents().removeIf(s -> s.getStudentID().equalsIgnoreCase(studentID));
        c.setStudentNumber(c.getStudents().size());
    }

    /**
     * Cập nhật lớp khi học sinh chuyển lớp (Gọi khi EditStudent)
     */
    public void updateStudentClass(String studentID, String oldClassID, String newClassID) throws Exception {
        // Nếu không đổi lớp thì thôi
        if (oldClassID.equalsIgnoreCase(newClassID)) return;

        // Xóa khỏi lớp cũ
        removeStudentFromClass(studentID, oldClassID);

        // Thêm vào lớp mới
        addStudentToClass(studentID, newClassID);
    }

    /**
     * Cập nhật tên lớp cho tất cả học sinh khi đổi mã lớp
     */
    private void updateStudentsClassName(String oldClassID, String newClassID) {
        StudentDatabase studentDB = StudentDatabase.getStudentDB();
        for (Student s : studentDB.getAllStudents()) {
            if (s.getStudentClass().equalsIgnoreCase(oldClassID)) {
                s.setStudentClass(newClassID);
            }
        }
    }

    /**
     * Đồng bộ lại số lượng học sinh cho TẤT CẢ các lớp
     * (Gọi khi khởi động app hoặc sau khi import data)
     */
    public void syncAllClassesStudentCount() {
        StudentDatabase studentDB = StudentDatabase.getStudentDB();

        // Reset tất cả về 0
        for (Classes c : classes) {
            c.getStudents().clear();
            c.setStudentNumber(0);
        }

        // Đếm lại từ đầu
        for (Student s : studentDB.getAllStudents()) {
            String className = s.getStudentClass();
            if (className == null || className.trim().isEmpty()) continue;

            Classes c = findClassByID(className);
            if (c != null) {
                // Kiểm tra trùng trước khi thêm
                boolean exists = false;
                for (Student student : c.getStudents()) {
                    if (student.getStudentID().equalsIgnoreCase(s.getStudentID())) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    c.getStudents().add(s);
                    c.setStudentNumber(c.getStudents().size());
                }
            }
        }
    }
}