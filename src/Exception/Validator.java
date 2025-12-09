package Exception;

public class Validator {

    // kiểm tra id
    public static void validateID(String id) throws InvalidIDException {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidIDException("ID không được để trống!");
        }

        id = id.trim();

        if (id.length() < 3 || id.length() > 15) {
            throw new InvalidIDException("ID phải từ 3-15 ký tự!");
        }

        if (!id.matches("^[A-Za-z0-9]+$")) {
            throw new InvalidIDException("ID chỉ được chứa chữ và số!");
        }
    }

    // kiểm tra tên
    public static void validateName(String name) throws InvalidNameException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidNameException("Tên không được để trống!");
        }

        name = name.trim();

        if (name.length() < 2 || name.length() > 50) {
            throw new InvalidNameException("Tên phải từ 2-50 ký tự!");
        }

        if (!name.matches("^[A-Za-zÀ-ỹ\\s]+$")) {
            throw new InvalidNameException("Tên chỉ được chứa chữ cái!");
        }

        if (name.contains("  ")) {
            throw new InvalidNameException("Tên không được có nhiều khoảng trắng liên tiếp!");
        }
    }

    // kiểm tra điểm
    public static void validateScore(double score) throws InvalidScoreException {
        if (Double.isNaN(score) || Double.isInfinite(score)) {
            throw new InvalidScoreException("Điểm số không hợp lệ!");
        }

        if (score < 0 || score > 10) {
            throw new InvalidScoreException("Điểm phải từ 0-10!");
        }

        // làm tròn 1 chữ số thập phân
        double rounded = Math.round(score * 10.0) / 10.0;
        if (Math.abs(score - rounded) > 0.01) {
            throw new InvalidScoreException("Điểm chỉ được có tối đa 1 chữ số thập phân");
        }
    }

    // kiểm tra username
    public static void validateUsername(String username) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Username không được để trống!");
        }

        username = username.trim();

        if (username.length() < 3 || username.length() > 20) {
            throw new Exception("Username phải từ 3-20 ký tự!");
        }

        if (!username.matches("^[A-Za-z0-9_]+$")) {
            throw new Exception("Username chỉ được chứa chữ, số và dấu gạch dưới!");
        }

        if (username.matches("^[0-9].*")) {
            throw new Exception("Username không được bắt đầu bằng số!");
        }
    }

    // kiểm tra mật khẩu
    public static void validatePassword(String password) throws Exception {
        if (password == null || password.isEmpty()) {
            throw new Exception("Mật khẩu không được để trống!");
        }

        if (password.length() < 6) {
            throw new Exception("Mật khẩu phải có ít nhất 6 ký tự!");
        }

        if (password.length() > 30) {
            throw new Exception("Mật khẩu không được quá 30 ký tự!");
        }
    }
}