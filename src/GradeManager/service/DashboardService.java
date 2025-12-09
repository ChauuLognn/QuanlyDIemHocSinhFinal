package GradeManager.service;

import GradeManager.Grade;
import Database.GradeDatabase;
import StudentManager.Student;
import Database.StudentDatabase;
import java.util.*;

public class DashboardService {
    private StudentDatabase stuDb = StudentDatabase.getInstance();
    private GradeDatabase gradeDb = GradeDatabase.getInstance();

    // tính phân loại học lực
    public String[] getPerformanceStats() {
        ArrayList<Student> list = stuDb.getAllStudents();
        if (list.isEmpty()) {
            return new String[]{"Chưa có dữ liệu học sinh"};
        }

        int excellent = 0, good = 0, average = 0, weak = 0;
        int countWithScore = 0;

        for (Student s : list) {
            Grade g = gradeDb.getGrade(s.getStudentID(), "TOAN", 1);
            if (g != null) {
                double avg = g.getAverage();
                if (avg >= 8.0) excellent++;
                else if (avg >= 6.5) good++;
                else if (avg >= 5.0) average++;
                else weak++;
                countWithScore++;
            }
        }

        if (countWithScore == 0) {
            return new String[]{"Chưa có dữ liệu điểm số"};
        }

        return new String[]{
                String.format("Giỏi: %d (%d%%)", excellent, (excellent * 100) / countWithScore),
                String.format("Khá: %d (%d%%)", good, (good * 100) / countWithScore),
                String.format("Trung bình: %d (%d%%)", average, (average * 100) / countWithScore),
                String.format("Yếu: %d (%d%%)", weak, (weak * 100) / countWithScore)
        };
    }

    // tìm lớp nổi bật
    public String[] getTopClasses() {
        ArrayList<Student> students = stuDb.getAllStudents();
        if (students.isEmpty()) {
            return new String[]{"Chưa có dữ liệu"};
        }

        Map<String, List<Double>> classScores = new HashMap<>();

        for (Student s : students) {
            String className = s.getStudentClass();
            if (className == null || className.isEmpty()) continue;

            Grade g = gradeDb.getGrade(s.getStudentID(), "TOAN", 1);
            if (g != null) {
                classScores.putIfAbsent(className, new ArrayList<>());
                classScores.get(className).add(g.getAverage());
            }
        }

        if (classScores.isEmpty()) {
            return new String[]{"Chưa có dữ liệu điểm theo lớp"};
        }

        Map<String, Double> classAvgMap = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : classScores.entrySet()) {
            double sum = 0;
            for (double score : entry.getValue()) {
                sum += score;
            }
            classAvgMap.put(entry.getKey(), sum / entry.getValue().size());
        }

        List<Map.Entry<String, Double>> sortedClasses = new ArrayList<>(classAvgMap.entrySet());
        sortedClasses.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(4, sortedClasses.size()); i++) {
            Map.Entry<String, Double> entry = sortedClasses.get(i);
            int count = classScores.get(entry.getKey()).size();
            result.add(String.format("%s: %d HS - ĐTB: %.2f", entry.getKey(), count, entry.getValue()));
        }
        return result.toArray(new String[0]);
    }
}