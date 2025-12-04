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

    //Tìm kiếm lớp theo mã lớp
    public Classes findClassByID(String classID){
        for (Classes c : classes){
            if (c.getClassID().equals(classID)){
                return c;
            }
        }
        return null;
    }

    //Tìm kiếm lớp theo tên lớp
    public Classes findClassByName(String className){
        for (Classes c : classes){
            if (c.getClassID().equalsIgnoreCase(className)){
                return c;
            }
        }
        return null;
    }

    //In danh sách tất cả các lớp
    public void showAllClasses(){
        if (classes.isEmpty()){
            System.out.println("Danh sách các lớp trống!");
            return;
        }else{
            for (Classes c : classes){
                System.out.println("Lớp: " + c.getClassName() + " (" + c.getClassID() + ") - " + c.getStudentNumber() + " học sinh");
            }
        }

    }

    //Thêm lớp mới
    public void addNewClass (Classes c){
        classes.add(c);
    }

    //Thêm học sinh vào lớp
    public void addStudentToClass(String studentID, String classID){
        Student student = StudentDatabase.getStudentDB().findByID(studentID);
        Classes findedClass = findClassByID(classID);
        if (findedClass == null){
            System.out.println("Không có lớp " + classID);
        }
        else {
            int currentStudentNumber = findedClass.getStudents().size();  //Sĩ số học sinh trước khi thêm
            findedClass.getStudents().add(student);
            findedClass.setStudentNumber(findedClass.getStudents().size());

            //So sánh sĩ số mới, nếu có tăng lên so với sĩ số cũ thì in ra dòng "thêm thành công"
            if (findedClass.getStudentNumber() > currentStudentNumber){
                System.out.println("Thêm học sinh vào lớp " + classID + " thành công!");
            }else{
                System.out.println("Lỗi, chưa thể thêm học sinh!");
            }

        }
    }

    //Xóa lớp theo mã lớp
    public void deleteClass (String classID){
        Classes c = findClassByID(classID);
        if (c != null){
            classes.remove(c);
        }
    }


}
