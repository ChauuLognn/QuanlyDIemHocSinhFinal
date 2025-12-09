package ReportManager;

import Database.DatabaseConnection;
import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportService {

    // --- 1. XUẤT DANH SÁCH LỚP ---
    public void exportClassList(String classID) {
        String fileName = "DanhSach_Lop_" + classID + ".csv";
        saveToFile(fileName, getClassListData(classID));
    }

    // --- 2. XUẤT BẢNG ĐIỂM (Theo Lớp + Môn + Kỳ) ---
    public void exportScoreBoard(String classID, String subjectID, int semester) {
        String fileName = "BangDiem_" + classID + "_" + subjectID + "_HK" + semester + ".csv";
        saveToFile(fileName, getScoreData(classID, subjectID, semester));
    }

    // --- HÀM HỖ TRỢ LẤY DỮ LIỆU ---

    private String getClassListData(String classID) {
        StringBuilder sb = new StringBuilder();
        // Header của file Excel
        sb.append("STT,Mã Học Sinh,Họ và Tên,Giới tính,Lớp\n");

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM student WHERE studentClass = ? ORDER BY studentName";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, classID);
            ResultSet rs = ps.executeQuery();

            int stt = 1;
            while (rs.next()) {
                sb.append(stt++).append(",");
                sb.append(rs.getString("studentID")).append(",");
                sb.append("\"").append(rs.getString("studentName")).append("\","); // Bọc tên trong ngoặc kép để tránh lỗi nếu tên có dấu phẩy
                sb.append(rs.getString("gender")).append(",");
                sb.append(rs.getString("studentClass")).append("\n");
            }
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
        return sb.toString();
    }

    private String getScoreData(String classID, String subjectID, int semester) {
        StringBuilder sb = new StringBuilder();
        // Header
        sb.append("BẢNG ĐIỂM MÔN: ").append(subjectID).append(" - HỌC KỲ: ").append(semester).append("\n");
        sb.append("LỚP: ").append(classID).append("\n\n");
        sb.append("STT,Mã HS,Họ Tên,Đ.TX (HS1),Đ.GK (HS2),Đ.CK (HS3),ĐTB Môn,Xếp loại\n");

        try {
            Connection conn = DatabaseConnection.getConnection();
            // Join Student với Grade (LEFT JOIN để lấy cả HS chưa có điểm)
            String sql = "SELECT s.studentID, s.studentName, " +
                    "COALESCE(g.regularScore, 0) as regular, " +
                    "COALESCE(g.midtermScore, 0) as midterm, " +
                    "COALESCE(g.finalScore, 0) as final " +
                    "FROM student s " +
                    "LEFT JOIN grade g ON s.studentID = g.studentID AND g.subjectID = ? AND g.semester = ? " +
                    "WHERE s.studentClass = ? " +
                    "ORDER BY s.studentName";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, subjectID);
            ps.setInt(2, semester);
            ps.setString(3, classID);
            ResultSet rs = ps.executeQuery();

            int stt = 1;
            while (rs.next()) {
                double r = rs.getDouble("regular");
                double m = rs.getDouble("midterm");
                double f = rs.getDouble("final");
                double avg = (r + m*2 + f*3) / 6.0;

                sb.append(stt++).append(",");
                sb.append(rs.getString("studentID")).append(",");
                sb.append("\"").append(rs.getString("studentName")).append("\",");
                sb.append(r).append(",");
                sb.append(m).append(",");
                sb.append(f).append(",");
                sb.append(String.format("%.2f", avg)).append(",");
                sb.append(classify(avg)).append("\n");
            }
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
        return sb.toString();
    }

    // Hàm xếp loại
    private String classify(double score) {
        if (score >= 8.0) return "Giỏi";
        if (score >= 6.5) return "Khá";
        if (score >= 5.0) return "Trung bình";
        return "Yếu";
    }

    // --- HÀM GHI FILE ---
    private void saveToFile(String defaultName, String content) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(defaultName));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(fileToSave);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                 BufferedWriter writer = new BufferedWriter(osw)) {

                // Thêm BOM để Excel nhận diện tiếng Việt UTF-8
                writer.write("\uFEFF");
                writer.write(content);

                JOptionPane.showMessageDialog(null, "Xuất file thành công:\n" + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Lỗi khi ghi file: " + e.getMessage());
            }
        }
    }
}