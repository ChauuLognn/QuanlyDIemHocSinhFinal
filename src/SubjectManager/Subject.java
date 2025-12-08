package SubjectManager;

public class Subject {
    private String id;
    private String name;
    private int coefficient; // Hệ số môn

    public Subject(String id, String name, int coefficient) {
        this.id = id;
        this.name = name;
        this.coefficient = coefficient;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getCoefficient() { return coefficient; }

    // Hàm này để hiển thị tên trong ComboBox
    @Override
    public String toString() { return name; }
}