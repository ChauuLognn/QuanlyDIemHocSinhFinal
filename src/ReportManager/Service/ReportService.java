package ReportManager.service;

import Database.DatabaseConnection;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportService {

    // ================= 1. XEM TRƯỚC (TEXT PREVIEW) =================
    public String generateReportContent(String type, String className, String semester) {
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("TRƯỜNG THCS ABC\n");
        sb.append("ĐỊA CHỈ: HÀ NỘI, VIỆT NAM\n");
        sb.append("------------------------------------------------------------\n\n");
        sb.append("                  ").append(type.toUpperCase()).append("\n");
        sb.append("                  Năm học: 2023-2024\n\n");
        sb.append("Lớp:      ").append(className).append("\n");
        sb.append("Học kỳ:   ").append(semester).append("\n");
        sb.append("Ngày xuất: ").append(date).append("\n");
        sb.append("Người lập: ADMIN\n");
        sb.append("\n============================================================\n");

        // ✅ KẾT NỐI DATABASE THẬT
        if (type.contains("Danh sách lớp")) {
            sb.append(generateClassListPreview(className));
        } else if (type.contains("Bảng điểm")) {
            sb.append(generateScoreListPreview(className));
        } else {
            sb.append("\n[Loại báo cáo này đang được phát triển...]\n");
        }

        sb.append("============================================================\n");
        sb.append("\n\n\n");
        sb.append("          Người lập biểu                  Ban Giám Hiệu\n");
        sb.append("            (Ký tên)                        (Ký tên)\n");

        return sb.toString();
    }

    // ✅ HÀM MỚI: Lấy danh sách lớp từ Database
    private String generateClassListPreview(String className) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s | %-20s | %-10s | %-10s\n", "STT", "HỌ VÀ TÊN", "GIỚI TÍNH", "GHI CHÚ"));
        sb.append("------------------------------------------------------------\n");

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return "\n[Không thể kết nối Database]\n";
            }

            // Query lấy danh sách học sinh
            String sql;
            PreparedStatement ps;

            if (className.equals("Tất cả")) {
                sql = "SELECT studentID, studentName, studentClass, gender FROM Student ORDER BY studentClass, studentID";
                ps = conn.prepareStatement(sql);
            } else {
                sql = "SELECT studentID, studentName, studentClass, gender FROM Student WHERE studentClass = ? ORDER BY studentID";
                ps = conn.prepareStatement(sql);
                ps.setString(1, className);
            }

            ResultSet rs = ps.executeQuery();
            int count = 1;
            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                String id = rs.getString("studentID");
                String name = rs.getString("studentName");
                String gender = rs.getString("gender");

                sb.append(String.format("%-5s | %-20s | %-10s | %-10s\n",
                        String.format("%02d", count),
                        name,
                        gender,
                        ""
                ));
                count++;
            }

            if (!hasData) {
                sb.append("                   [Không có dữ liệu]\n");
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            sb.append("\n[Lỗi truy vấn: ").append(e.getMessage()).append("]\n");
            e.printStackTrace();
        }

        return sb.toString();
    }

    // ✅ HÀM MỚI: Lấy bảng điểm từ Database
    private String generateScoreListPreview(String className) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s | %-15s | %-5s | %-5s | %-5s | %-5s | %-8s\n",
                "STT", "HỌ TÊN", "ĐTX", "ĐGK", "ĐCK", "ĐTB", "XẾP LOẠI"));
        sb.append("------------------------------------------------------------------------\n");

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return "\n[Không thể kết nối Database]\n";
            }

            // Query JOIN bảng Student và Grade
            String sql;
            PreparedStatement ps;

            if (className.equals("Tất cả")) {
                sql = "SELECT s.studentID, s.studentName, s.studentClass, " +
                        "g.regularScore, g.midtermScore, g.finalScore " +
                        "FROM Student s " +
                        "LEFT JOIN Grade g ON s.studentID = g.studentID " +
                        "ORDER BY s.studentClass, s.studentID";
                ps = conn.prepareStatement(sql);
            } else {
                sql = "SELECT s.studentID, s.studentName, s.studentClass, " +
                        "g.regularScore, g.midtermScore, g.finalScore " +
                        "FROM Student s " +
                        "LEFT JOIN Grade g ON s.studentID = g.studentID " +
                        "WHERE s.studentClass = ? " +
                        "ORDER BY s.studentID";
                ps = conn.prepareStatement(sql);
                ps.setString(1, className);
            }

            ResultSet rs = ps.executeQuery();
            int count = 1;
            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                String name = rs.getString("studentName");
                double reg = rs.getDouble("regularScore");
                double mid = rs.getDouble("midtermScore");
                double fin = rs.getDouble("finalScore");

                double avg = (reg + mid * 2 + fin * 3) / 6.0;
                String rank = classify(avg);

                sb.append(String.format("%-5s | %-15s | %-5.1f | %-5.1f | %-5.1f | %-5.2f | %-8s\n",
                        String.format("%02d", count),
                        name.length() > 15 ? name.substring(0, 15) : name,
                        reg, mid, fin, avg, rank
                ));
                count++;
            }

            if (!hasData) {
                sb.append("                        [Không có dữ liệu]\n");
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            sb.append("\n[Lỗi truy vấn: ").append(e.getMessage()).append("]\n");
            e.printStackTrace();
        }

        return sb.toString();
    }

    // ================= 2. XUẤT EXCEL (CSV FORMAT) =================
    public String generateCSVContent(String type, String className, String semester) {
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        StringBuilder sb = new StringBuilder();

        // Header chung
        sb.append("TRƯỜNG THCS ABC\n");
        sb.append("BÁO CÁO:,").append(type).append("\n");
        sb.append("Lớp:,").append(className).append("\n");
        sb.append("Học kỳ:,").append(semester).append("\n");
        sb.append("Ngày xuất:,").append(date).append("\n");
        sb.append("\n");

        // ✅ KẾT NỐI DATABASE THẬT
        if (type.contains("Danh sách lớp")) {
            sb.append(generateClassListCSV(className));
        } else if (type.contains("Bảng điểm")) {
            sb.append(generateScoreListCSV(className));
        } else {
            sb.append("Thông báo:,Chưa có dữ liệu mẫu cho loại báo cáo này\n");
        }

        return sb.toString();
    }

    // ✅ HÀM MỚI: CSV cho danh sách lớp
    private String generateClassListCSV(String className) {
        StringBuilder sb = new StringBuilder();
        sb.append("STT,Mã Học Sinh,Họ và Tên,Lớp,Giới Tính\n");

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return "Lỗi,Không thể kết nối Database\n";

            String sql;
            PreparedStatement ps;

            if (className.equals("Tất cả")) {
                sql = "SELECT studentID, studentName, studentClass, gender FROM Student ORDER BY studentClass, studentID";
                ps = conn.prepareStatement(sql);
            } else {
                sql = "SELECT studentID, studentName, studentClass, gender FROM Student WHERE studentClass = ? ORDER BY studentID";
                ps = conn.prepareStatement(sql);
                ps.setString(1, className);
            }

            ResultSet rs = ps.executeQuery();
            int count = 1;

            while (rs.next()) {
                sb.append(count).append(",")
                        .append(rs.getString("studentID")).append(",")
                        .append(rs.getString("studentName")).append(",")
                        .append(rs.getString("studentClass")).append(",")
                        .append(rs.getString("gender")).append("\n");
                count++;
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            sb.append("Lỗi,").append(e.getMessage()).append("\n");
        }

        return sb.toString();
    }

    // ✅ HÀM MỚI: CSV cho bảng điểm
    private String generateScoreListCSV(String className) {
        StringBuilder sb = new StringBuilder();
        sb.append("STT,Mã HS,Họ và Tên,Lớp,Điểm TX,Điểm GK,Điểm CK,Điểm TB,Xếp Loại\n");

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return "Lỗi,Không thể kết nối Database\n";

            String sql;
            PreparedStatement ps;

            if (className.equals("Tất cả")) {
                sql = "SELECT s.studentID, s.studentName, s.studentClass, " +
                        "g.regularScore, g.midtermScore, g.finalScore " +
                        "FROM Student s " +
                        "LEFT JOIN Grade g ON s.studentID = g.studentID " +
                        "ORDER BY s.studentClass, s.studentID";
                ps = conn.prepareStatement(sql);
            } else {
                sql = "SELECT s.studentID, s.studentName, s.studentClass, " +
                        "g.regularScore, g.midtermScore, g.finalScore " +
                        "FROM Student s " +
                        "LEFT JOIN Grade g ON s.studentID = g.studentID " +
                        "WHERE s.studentClass = ? " +
                        "ORDER BY s.studentID";
                ps = conn.prepareStatement(sql);
                ps.setString(1, className);
            }

            ResultSet rs = ps.executeQuery();
            int count = 1;

            while (rs.next()) {
                String id = rs.getString("studentID");
                String name = rs.getString("studentName");
                String cls = rs.getString("studentClass");
                double reg = rs.getDouble("regularScore");
                double mid = rs.getDouble("midtermScore");
                double fin = rs.getDouble("finalScore");
                double avg = (reg + mid * 2 + fin * 3) / 6.0;
                String rank = classify(avg);

                sb.append(count).append(",")
                        .append(id).append(",")
                        .append(name).append(",")
                        .append(cls).append(",")
                        .append(String.format("%.1f", reg)).append(",")
                        .append(String.format("%.1f", mid)).append(",")
                        .append(String.format("%.1f", fin)).append(",")
                        .append(String.format("%.2f", avg)).append(",")
                        .append(rank).append("\n");
                count++;
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            sb.append("Lỗi,").append(e.getMessage()).append("\n");
        }

        return sb.toString();
    }

    // ================= 3. LƯU FILE =================
    public void saveReportToFile(File file, String content) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // Ghi BOM để Excel nhận diện UTF-8
            writer.write('\ufeff');
            writer.write(content);
        }
    }

    // ================= HÀM PHỤ TRỢ =================
    private String classify(double avg) {
        if (avg == 0) return "-";
        if (avg >= 8.0) return "Giỏi";
        if (avg >= 6.5) return "Khá";
        if (avg >= 5.0) return "TB";
        return "Yếu";
    }
}