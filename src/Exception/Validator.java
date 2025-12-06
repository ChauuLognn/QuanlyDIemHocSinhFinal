package Exception;

public class Validator {

    // ✅ CẢI THIỆN: Kiểm tra ID chặt chẽ hơn
    public static void validateID(String id) throws InvalidIDException {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidIDException("ID không được để trống!");
        }

        // Loại bỏ khoảng trắng đầu cuối
        id = id.trim();

        // Kiểm tra độ dài (Ví dụ: 3-15 ký tự)
        if (id.length() < 3 || id.length() > 15) {
            throw new InvalidIDException("ID phải từ 3-15 ký tự!");
        }

        // Chỉ cho phép chữ, số (không có ký tự đặc biệt)
        if (!id.matches("^[A-Za-z0-9]+$")) {
            throw new InvalidIDException("ID chỉ được chứa chữ cái và số, không có ký tự đặc biệt hoặc khoảng trắng!");
        }
    }

    // ✅ CẢI THIỆN: Kiểm tra tên chặt chẽ hơn
    public static void validateName(String name) throws InvalidNameException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidNameException("Tên không được để trống!");
        }

        // Loại bỏ khoảng trắng đầu cuối
        name = name.trim();

        // Kiểm tra độ dài (Ví dụ: 2-50 ký tự)
        if (name.length() < 2 || name.length() > 50) {
            throw new InvalidNameException("Tên phải từ 2-50 ký tự!");
        }

        // Cho phép chữ cái tiếng Việt có dấu + khoảng trắng
        if (!name.matches("^[A-Za-zÀ-ỹ\\s]+$")) {
            throw new InvalidNameException("Tên chỉ được chứa chữ cái, không có số hoặc ký tự đặc biệt!");
        }

        // Kiểm tra không có nhiều khoảng trắng liên tiếp
        if (name.contains("  ")) {
            throw new InvalidNameException("Tên không được chứa nhiều khoảng trắng liên tiếp!");
        }
    }

    // ✅ CẢI THIỆN: Kiểm tra điểm số chặt chẽ hơn
    public static void validateScore(double score) throws InvalidScoreException {
        // Kiểm tra NaN hoặc Infinity
        if (Double.isNaN(score) || Double.isInfinite(score)) {
            throw new InvalidScoreException("Điểm số không hợp lệ!");
        }

        // Kiểm tra khoảng 0-10
        if (score < 0 || score > 10) {
            throw new InvalidScoreException("Điểm phải nằm trong khoảng 0-10!");
        }

        // ✅ THÊM: Kiểm tra số thập phân (chỉ cho phép 1 chữ số sau dấu phẩy)
        // Ví dụ: 8.5 ✅, 8.55 ❌
        String scoreStr = String.format("%.2f", score);
        String[] parts = scoreStr.split("\\.");
        if (parts.length > 1 && parts[1].length() > 1) {
            // Làm tròn đến 1 chữ số thập phân
            double rounded = Math.round(score * 10.0) / 10.0;
            if (Math.abs(score - rounded) > 0.01) {
                throw new InvalidScoreException("Điểm chỉ được phép có tối đa 1 chữ số thập phân (Ví dụ: 8.5)");
            }
        }
    }

    // ✅ HÀM MỚI: Kiểm tra email (Dự phòng cho tương lai)
    public static void validateEmail(String email) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email không được để trống!");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            throw new Exception("Email không đúng định dạng! (Ví dụ: example@gmail.com)");
        }
    }

    // ✅ HÀM MỚI: Kiểm tra số điện thoại (Dự phòng cho tương lai)
    public static void validatePhone(String phone) throws Exception {
        if (phone == null || phone.trim().isEmpty()) {
            throw new Exception("Số điện thoại không được để trống!");
        }

        // Loại bỏ khoảng trắng và dấu gạch ngang
        phone = phone.replaceAll("[\\s-]", "");

        // Kiểm tra định dạng Việt Nam (10-11 số, bắt đầu bằng 0)
        if (!phone.matches("^0[0-9]{9,10}$")) {
            throw new Exception("Số điện thoại phải có 10-11 số và bắt đầu bằng 0!");
        }
    }

    // ✅ HÀM MỚI: Kiểm tra mật khẩu
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

        // ✅ THÊM: Kiểm tra mật khẩu mạnh (Tùy chọn - Comment lại nếu không cần)
        /*
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");

        if (!hasUpper || !hasLower || !hasDigit) {
            throw new Exception("Mật khẩu phải chứa ít nhất 1 chữ hoa, 1 chữ thường và 1 số!");
        }
        */
    }

    // ✅ HÀM MỚI: Kiểm tra username
    public static void validateUsername(String username) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Username không được để trống!");
        }

        username = username.trim();

        if (username.length() < 3 || username.length() > 20) {
            throw new Exception("Username phải từ 3-20 ký tự!");
        }

        // Chỉ cho phép chữ, số, dấu gạch dưới
        if (!username.matches("^[A-Za-z0-9_]+$")) {
            throw new Exception("Username chỉ được chứa chữ, số và dấu gạch dưới (_)!");
        }

        // Không được bắt đầu bằng số
        if (username.matches("^[0-9].*")) {
            throw new Exception("Username không được bắt đầu bằng số!");
        }
    }
}