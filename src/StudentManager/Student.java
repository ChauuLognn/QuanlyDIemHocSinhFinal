package StudentManager;

public class Student {
    private String studentName;
    private String studentID;
    private String studentClass;


    public Student (String studentName, String studentID, String studentClass){
        this.studentName = studentName;
        this.studentID = studentID;
        this.studentClass = studentClass;

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

    @Override
    public String toString(){
        return studentName + " - " + studentID + " - Lớp " + studentClass;
    }
}
