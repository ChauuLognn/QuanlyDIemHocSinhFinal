package GradeManager;

import java.io.Serializable;

public class Grade implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentID;
    private String subjectID; // Môn học (Chuẩn bị cho tính năng môn)
    private int semester;     // Học kỳ
    private double regularScore;
    private double midtermScore;
    private double finalScore;

    public Grade(String studentID, int semester, double regularScore, double midtermScore, double finalScore) {
        this.studentID = studentID;
        this.subjectID = subjectID;
        this.semester = semester;
        this.regularScore = regularScore;
        this.midtermScore = midtermScore;
        this.finalScore = finalScore;
    }

    public Grade(String id, String studentID, int semester, double regularScore, double midtermScore, double finalScore) {
        this(studentID, 1, regularScore, midtermScore, finalScore);
    }

    public String getStudentID() { return studentID; }

    public String getSubjectID() { return subjectID; } // <--- Getter mới
    public void setSubjectID(String subjectID) { this.subjectID = subjectID; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public double getRegularScore() { return regularScore; }
    public void setRegularScore(double regularScore) { this.regularScore = regularScore; }

    public double getMidtermScore() { return midtermScore; }
    public void setMidtermScore(double midtermScore) { this.midtermScore = midtermScore; }

    public double getFinalScore() { return finalScore; }
    public void setFinalScore(double finalScore) { this.finalScore = finalScore; }

    // Tính điểm trung bình (Công thức cấp 2)
    public double getAverage() {
        return (regularScore + midtermScore * 2 + finalScore * 3) / 6.0;
    }
}