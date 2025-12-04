package StudentManager.service;

import GradeManager.Grade;
import GradeManager.data.GradeDatabase;
import StudentManager.Student;
import StudentManager.data.StudentDatabase;
import java.util.*;

public class StudentService {
    private StudentDatabase studentDB = StudentDatabase.getStudentDB();
    private GradeDatabase gradeDB = GradeDatabase.getGradeDB();

    // 1. Logic lấy danh sách lớp (để đổ vào ComboBox lọc)
    public List<String> getUniqueClassList() {
        Set<String> uniqueClasses = new HashSet<>();
        for (Student s : studentDB.getAllStudents()) {
            if (s.getStudentClass() != null && !s.getStudentClass().trim().isEmpty()) {
                uniqueClasses.add(s.getStudentClass().trim());
            }
        }
        List<String> sortedList = new ArrayList<>(uniqueClasses);
        Collections.sort(sortedList);
        return sortedList;
    }

    // 2. Logic tính điểm trung bình
    public double calculateAvg(double r, double m, double f) {
        return (r + m * 2 + f * 3) / 6.0;
    }

    // 3. Logic xếp loại
    public String classify(double avg) {
        if (avg == 0) return "-";
        if (avg >= 8.0) return "Giỏi";
        if (avg >= 6.5) return "Khá";
        if (avg >= 5.0) return "Trung bình";
        return "Yếu";
    }

    // 4. Lấy điểm của học sinh (để đỡ phải gọi GradeDB ở UI)
    public Grade getStudentGrade(String studentID) {
        return gradeDB.getGradeByStudentID(studentID);
    }

    // 5. Lấy toàn bộ học sinh
    public ArrayList<Student> getAllStudents() {
        return studentDB.getAllStudents();
    }
}