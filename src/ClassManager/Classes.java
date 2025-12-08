package ClassManager;

import StudentManager.Student;
import java.io.Serializable;
import java.util.ArrayList;

public class Classes {
    private String classID;
    private String className;
    private String schoolYear; // <--- Thêm thuộc tính này
    private int studentNumber; // Sĩ số (không lưu DB, chỉ để hiển thị)

    public Classes(String classID, String className, String schoolYear) {
        this.classID = classID;
        this.className = className;
        this.schoolYear = schoolYear;
        this.studentNumber = 0;
    }

    public Classes(String className, String classID) {
        this(classID, className, "2024-2025");
    }

    public String getClassID() { return classID; }
    public void setClassID(String classID) { this.classID = classID; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    // Đây là hàm bạn đang thiếu -> gây lỗi c.getSchoolYear()
    public String getSchoolYear() { return schoolYear; }
    public void setSchoolYear(String schoolYear) { this.schoolYear = schoolYear; }

    public int getStudentNumber() { return studentNumber; }
    public void setStudentNumber(int studentNumber) { this.studentNumber = studentNumber; }

    @Override
    public String toString() {
        return className; // Để hiển thị đẹp trong ComboBox nếu cần
    }
}