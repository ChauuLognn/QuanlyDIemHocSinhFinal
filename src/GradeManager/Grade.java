package GradeManager;

import java.io.Serializable;

public class Grade implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentID;
    private int semester;
    private double regularScore;
    private double midtermScore;
    private double finalScore;

    public Grade(String studentID, int semester, double regularScore, double midtermScore, double finalScore) {        this.studentID = studentID;
        this.studentID = studentID;
        this.semester = semester;
        this.regularScore = regularScore;
        this.midtermScore = midtermScore;
        this.finalScore = finalScore;
    }

    public Grade(String studentID, double regularScore, double midtermScore, double finalScore) {
        this(studentID, 1, regularScore, midtermScore, finalScore);
    }

    public String getStudentID() {
        return studentID;
    }
    public int getSemester() {
        return semester;
    }
    public double getRegularScore() {
        return regularScore;
    }
    public double getMidtermScore() {
        return midtermScore;
    }
    public double getFinalScore() {
        return finalScore;
    }

    //tính điểm trung bình
    public double getAverage() {
        return (regularScore + midtermScore * 2 + finalScore * 3) / 6.0;
    }
}