package Exception;

public class Validator {
    public static void validateID(String id) throws InvalidIDException{
        if (id == null || id.isEmpty())
            throw new InvalidIDException("ID không được để trống!");
        if (!id.matches("[A-Za-z0-9]+"))
            throw new InvalidIDException("ID chỉ được chứa kí tự và số!");
    }

    public static void validateName(String name) throws InvalidNameException{
        if (name == null || name.trim().isEmpty())
            throw new InvalidNameException("Tên không được để trống!");
        if(!name.matches("[A-Za-zÀ-Ỹà-ỹ\\s]+"))
            throw new InvalidNameException("Tên chỉ được chứa chữ cái!");
    }

    public static void validateScore(double score) throws InvalidScoreException{
        if (score < 0 || score > 10)
            throw new InvalidScoreException("Điểm phải nằm trong khoảng 0-10!");
    }
}
