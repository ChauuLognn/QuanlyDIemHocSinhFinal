package StudentManager.data;

import StudentManager.Student;

import java.util.ArrayList;

public class StudentDatabase {
    private static StudentDatabase studentDB = new StudentDatabase();
    private ArrayList<Student> students = new ArrayList<>();

    private StudentDatabase(){}     //Ngăn tạo nhiều bản sao

    public static StudentDatabase getStudentDB(){
        return studentDB;
    }

    public ArrayList<Student> getStudents(){
        return students;
    }

    //Hiển thị danh sách học sinh hiện tại
    public void showAllStudents(){
        if (students.isEmpty()){
            System.out.println("Danh sách học sinh trống!");
            return;
        }else{
            System.out.println("DANH SÁCH HỌC SINH:");
            for (int i=0; i<students.size(); i++){
                System.out.println( (i+1) + ". " + students.get(i).getStudentName() + " - " + students.get(i).getStudentID() + " - "
                        + students.get(i).getStudentClass());
            }
        }
    }
    //Thêm học sinh
    public void addStudent(Student s){
        students.add(s);
    }

    //Tìm kiếm học sinh theo ID
    public Student findByID (String id){
        for (Student s : students){
            if (s.getStudentID().equals(id)){
                return s;
            }
        }
        return null;
    }

    //Xóa học sinh theo ID
    public boolean deleteStudent(String id){
        for (Student s : students){
            if (s.getStudentID().equals(id)){
                students.remove(s);
                return true;
            }
        }
        return false;
    }

}
