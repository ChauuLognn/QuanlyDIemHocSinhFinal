package ReportManager;

import Database.DatabaseConnection;
import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class ReportService {

    // xuất danh sách lớp
    public void exportClassList(String classID) {
        String fileName = "DanhSach_Lop_" + classID + ".csv";
        saveToFile(fileName, getClassListData(classID));
    }

    // xuất bảng điểm
    public void exportScoreBoard(String classID, String subjectID, int semester) {
        String fileName = "BangDiem_" + classID + "_" + subjectID + "_HK" + semester + ".csv";
        saveToFile(fileName, getScoreData(classID, subjectID, semester));
    }

    // lấy dữ liệu danh sách lớp
    private String getClassListData(String classID) {
        StringBuilder sb = new StringBuilder();
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
                sb.append("\"").append(rs.getString("studentName")).append("\",");
                sb.append(rs.getString("gender")).append(",");
                sb.append(rs.getString("studentClass")).append("\n");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // lấy dữ liệu bảng điểm
    private String getScoreData(String classID, String subjectID, int semester) {
        StringBuilder sb = new StringBuilder();
        sb.append("BẢNG ĐIỂM MÔN: ").append(subjectID).append(" - HỌC KỲ: ").append(semester).append("\n");
        sb.append("LỚP: ").append(classID).append("\n\n");
        sb.append("STT,Mã HS,Họ Tên,Đ.TX,Đ.GK,Đ.CK,ĐTB Môn,Xếp loại\n");

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT s.studentID, s.studentName, " +
                    "COALESCE(g.regularScore, 0) as regular, " +
                    "COALESCE(g.midtermScore, 0) as mid, " +
                    "COALESCE(g.finalScore, 0) as finalScore " +
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
                double m = rs.getDouble("mid");
                double f = rs.getDouble("finalScore");
                double avg = (r + m * 2 + f * 3) / 6.0;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // xếp loại
    private String classify(double score) {
        if (score >= 8.0) return "Giỏi";
        if (score >= 6.5) return "Khá";
        if (score >= 5.0) return "Trung bình";
        return "Yếu";
    }

    // ghi file
    private void saveToFile(String defaultName, String content) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(defaultName));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(fileToSave);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                 BufferedWriter writer = new BufferedWriter(osw)) {

                writer.write("\uFEFF");
                writer.write(content);

                JOptionPane.showMessageDialog(null, "Xuất file thành công:\n" + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Lỗi khi ghi file: " + e.getMessage());
            }
        }
    }
}