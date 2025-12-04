package StudentManager;

import java.io.Serializable;

public class Student {
    private static final long serialVersionUID = 1L;

    private String studentName;
    private String studentID;
    private String studentClass;
    private String gender;


    public Student(String studentName, String studentID, String studentClass, String gender){
        this.studentName = studentName;
        this.studentID = studentID;
        this.studentClass = studentClass;
        this.gender = gender;
    }

    public Student(String studentName, String studentID, String studentClass){
        this(studentName, studentID, studentClass, "Nam");
    }

    public void setStudentName(String studentName){
        this.studentName = studentName;
    }
    public String getStudentName(){
        return studentName;
    }

    public void setStudentID(String studentID){
        this.studentID = studentID;
    }
    public String getStudentID(){
        return studentID;
    }

    public void setStudentClass(String studentClass){
        this.studentClass = studentClass;
    }
    public String getStudentClass(){
        return studentClass;
    }

    public void setGender(String gender) { this.gender = gender; }
    public String getGender() { return gender; }

    @Override
    public String toString(){
        return studentName + " - " + studentID + " - Lớp " + studentClass;
    }
}
