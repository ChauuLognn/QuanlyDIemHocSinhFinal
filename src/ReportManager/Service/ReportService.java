package ReportManager.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportService {

    // Hàm tạo nội dung báo cáo (Logic thuần túy)
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

        // Body (Giả lập logic lấy dữ liệu)
        if (type.contains("Danh sách lớp")) {
            sb.append(String.format("%-5s | %-20s | %-10s | %-10s\n", "STT", "HỌ VÀ TÊN", "GIỚI TÍNH", "GHI CHÚ"));
            sb.append("------------------------------------------------------------\n");
            sb.append(String.format("%-5s | %-20s | %-10s | %-10s\n", "01", "Nguyễn Văn An", "Nam", ""));
            sb.append(String.format("%-5s | %-20s | %-10s | %-10s\n", "02", "Trần Thị Bình", "Nữ", "Lớp trưởng"));
            // ... Bạn có thể kết nối Database ở đây để loop dữ liệu thật ...
        } else if (type.contains("Bảng điểm")) {
            sb.append(String.format("%-5s | %-15s | %-5s | %-5s | %-5s | %-5s\n", "STT", "HỌ TÊN", "TOÁN", "VĂN", "ANH", "ĐTB"));
            sb.append("------------------------------------------------------------\n");
            sb.append(String.format("%-5s | %-15s | %-5s | %-5s | %-5s | %-5s\n", "01", "Nguyễn Văn An", "8.5", "7.0", "8.0", "7.8"));
        } else {
            sb.append("\n[Dữ liệu mẫu cho báo cáo này đang được cập nhật...]\n");
        }

        sb.append("============================================================\n");
        sb.append("\n\n\n");
        sb.append("          Người lập biểu                  Ban Giám Hiệu\n");
        sb.append("            (Ký tên)                        (Ký tên)\n");

        return sb.toString();
    }

    // Hàm xuất file (Logic IO)
    public void saveReportToFile(File file, String content) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }
}