package ClassManager;

import StudentManager.Student;

import java.util.ArrayList;

public class Classes {
    private String className;
    private String classID;
    private int studentNumber;
    private ArrayList<Student> students;

    public Classes(String className, String classID ){
        this.className = className;
        this.classID = classID;
        this.students = new ArrayList<>();
        this.studentNumber = 0;
    }

    public void setClassName(String className){ this.className = className;}
    public void setClassID(String classID){ this.classID = classID;}
    public void setStudentNumber(int studentNumber){ this.studentNumber = studentNumber;}
    public void setStudents( ArrayList<Student> students) { this.students = students; }

    public String getClassName(){ return className;}
    public String getClassID(){ return classID;}
    public int getStudentNumber(){ return studentNumber;}
    public ArrayList<Student> getStudents() { return students;}
}
