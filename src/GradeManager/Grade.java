package GradeManager;

import java.io.Serializable;

public class Grade implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentID;
    private String subjectID;
    private int semester;
    private double regularScore;
    private double midtermScore;
    private double finalScore;

    // constructor đầy đủ
    public Grade(String studentID, String subjectID, int semester, double regularScore, double midtermScore, double finalScore) {
        this.studentID = studentID;
        this.subjectID = subjectID;
        this.semester = semester;
        this.regularScore = regularScore;
        this.midtermScore = midtermScore;
        this.finalScore = finalScore;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public double getRegularScore() {
        return regularScore;
    }

    public void setRegularScore(double regularScore) {
        this.regularScore = regularScore;
    }

    public double getMidtermScore() {
        return midtermScore;
    }

    public void setMidtermScore(double midtermScore) {
        this.midtermScore = midtermScore;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }

    // tính điểm trung bình
    public double getAverage() {
        return (regularScore + midtermScore * 2 + finalScore * 3) / 6.0;
    }
}