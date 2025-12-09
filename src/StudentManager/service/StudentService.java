package StudentManager.service;

import GradeManager.Grade;
import Database.GradeDatabase;
import StudentManager.Student;
import Database.StudentDatabase;
import java.util.*;

public class StudentService {
    private StudentDatabase stuDb = StudentDatabase.getInstance();
    private GradeDatabase gradeDb = GradeDatabase.getInstance();

    // lấy danh sách lớp unique
    public List<String> getClassList() {
        Set<String> uniqueClasses = new HashSet<>();
        for (Student s : stuDb.getAllStudents()) {
            if (s.getStudentClass() != null && !s.getStudentClass().trim().isEmpty()) {
                uniqueClasses.add(s.getStudentClass().trim());
            }
        }
        List<String> sortedList = new ArrayList<>(uniqueClasses);
        Collections.sort(sortedList);
        return sortedList;
    }

    // tính điểm trung bình
    public double calculateAvg(double r, double m, double f) {
        return (r + m * 2 + f * 3) / 6.0;
    }

    // xếp loại
    public String classify(double avg) {
        if (avg == 0) return "-";
        if (avg >= 8.0) return "Giỏi";
        if (avg >= 6.5) return "Khá";
        if (avg >= 5.0) return "Trung bình";
        return "Yếu";
    }

    // lấy điểm học sinh
    public Grade getGrade(String studentID, String subjectID, int semester) {
        return gradeDb.getGrade(studentID, subjectID, semester);
    }

    // lấy toàn bộ học sinh
    public ArrayList<Student> getAllStudents() {
        return stuDb.getAllStudents();
    }
}