package GradeManager;

public class Grade {
    private String studentID;
    private double regularScore;
    private double midtermScore;
    private double finalScore;

    public Grade(String studentID, double regularScore, double midtermScore, double finalScore){
        this.studentID = studentID;
        this.regularScore = regularScore;
        this.midtermScore = midtermScore;
        this.finalScore = finalScore;
    }

    public void setStudentID(String studentID){ this.studentID = studentID;}
    public String getStudentID(){ return studentID; }

    public void setRegularScore(double regularScore){ this.regularScore = regularScore;}
    public double getRegularScore() {return regularScore;}

    public void setMidtermScore(double midtermScore) {this.midtermScore = midtermScore;}
    public double getMidtermScore() {return midtermScore;}

    public void setFinalScore(double finalScore) {this.finalScore = finalScore;}
    public double getFinalScore() {return finalScore;}
}
