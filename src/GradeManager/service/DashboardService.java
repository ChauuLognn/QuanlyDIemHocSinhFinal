package GradeManager.service;

import GradeManager.Grade;
import GradeManager.data.GradeDatabase;
import StudentManager.Student;
import StudentManager.data.StudentDatabase;
import java.util.*;

public class DashboardService {
    private final StudentDatabase studentDB = StudentDatabase.getStudentDB();
    private final GradeDatabase gradeDB = GradeDatabase.getGradeDB();

    // Logic tính phân loại học lực
    public String[] calculatePerformanceStats() {
        ArrayList<Student> list = studentDB.getAllStudents();
        if (list.isEmpty()) return new String[]{"Chưa có dữ liệu học sinh"};

        int gio = 0, kha = 0, tb = 0, yeu = 0;
        int countWithScore = 0;

        for (Student s : list) {
            Grade g = gradeDB.getGradeByStudentID(s.getStudentID());
            if (g != null) {
                double avg = g.getAverage();
                if (avg >= 8.0) gio++;
                else if (avg >= 6.5) kha++;
                else if (avg >= 5.0) tb++;
                else yeu++;
                countWithScore++;
            }
        }

        if (countWithScore == 0) return new String[]{"Chưa có dữ liệu điểm số"};

        return new String[]{
                String.format("Giỏi: %d (%d%%)", gio, (int)((gio * 100.0) / countWithScore)),
                String.format("Khá: %d (%d%%)", kha, (int)((kha * 100.0) / countWithScore)),
                String.format("Trung bình: %d (%d%%)", tb, (int)((tb * 100.0) / countWithScore)),
                String.format("Yếu: %d (%d%%)", yeu, (int)((yeu * 100.0) / countWithScore))
        };
    }

    // Logic tìm lớp nổi bật
    public String[] calculateTopClasses() {
        ArrayList<Student> students = studentDB.getAllStudents();
        if (students.isEmpty()) return new String[]{"Chưa có dữ liệu"};

        Map<String, List<Double>> classScores = new HashMap<>();

        for (Student s : students) {
            String className = s.getStudentClass();
            if (className == null || className.isEmpty()) continue;
            Grade g = gradeDB.getGradeByStudentID(s.getStudentID());
            if (g != null) {
                classScores.putIfAbsent(className, new ArrayList<>());
                classScores.get(className).add(g.getAverage());
            }
        }

        if (classScores.isEmpty()) return new String[]{"Chưa có dữ liệu điểm theo lớp"};

        Map<String, Double> classAvgMap = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : classScores.entrySet()) {
            double sum = 0;
            for (double score : entry.getValue()) sum += score;
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