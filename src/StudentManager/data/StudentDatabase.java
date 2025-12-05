package StudentManager.data;

import StudentManager.Student;
import java.util.ArrayList;

public class StudentDatabase {
    private static StudentDatabase studentDB = new StudentDatabase();
    private ArrayList<Student> students = new ArrayList<>();

    private StudentDatabase(){}

    public static StudentDatabase getStudentDB(){
        return studentDB;
    }

    public ArrayList<Student> getAllStudents(){
        return students;
    }

    // Thêm học sinh
    public void addStudent(Student s) throws Exception {
        if (findByID(s.getStudentID()) != null) {
            throw new Exception("Mã học sinh " + s.getStudentID() + " đã tồn tại!");
        }
        students.add(s);
    }

    public Student findByID (String id){
        for (Student s : students){
            if (s.getStudentID().equalsIgnoreCase(id)){ // Dùng ignoreCase cho chuẩn
                return s;
            }
        }
        return null;
    }

    public void deleteStudent(String id) throws Exception {
        Student s = findByID(id);
        if (s == null) {
            throw new Exception("Không tìm thấy học sinh để xóa!");
        }
        students.remove(s);
    }

    public void updateStudent(String originalId, Student newInfo) throws Exception {
        Student old = findByID(originalId);
        if (old == null) throw new Exception("Không tìm thấy học sinh gốc!");

        if (!originalId.equalsIgnoreCase(newInfo.getStudentID())) {
            if (findByID(newInfo.getStudentID()) != null) {
                throw new Exception("Mã học sinh mới bị trùng với người khác!");
            }
        }


        old.setStudentID(newInfo.getStudentID());
        old.setStudentName(newInfo.getStudentName());
        old.setStudentClass(newInfo.getStudentClass());
        old.setGender(newInfo.getGender());
    }
}