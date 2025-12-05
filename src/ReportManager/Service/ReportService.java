package ReportManager.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportService {

    // --- 1. HÀM TẠO NỘI DUNG XEM TRƯỚC (TEXT PREVIEW) ---
    // Hàm này giữ nguyên định dạng cũ để hiển thị đẹp trên JTextArea
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
            sb.append(String.format("%-5s | %-15s | %-5s | %-5s | %-5s | %-5s\n", "02", "Trần Thị Bình", "9.0", "8.5", "9.0", "8.8"));
        } else {
            sb.append("\n[Dữ liệu mẫu cho báo cáo này đang được cập nhật...]\n");
        }

        sb.append("============================================================\n");
        sb.append("\n\n\n");
        sb.append("          Người lập biểu                  Ban Giám Hiệu\n");
        sb.append("            (Ký tên)                        (Ký tên)\n");

        return sb.toString();
    }

    // --- 2. HÀM TẠO NỘI DUNG CHO EXCEL (CSV FORMAT) ---
    // Hàm này tạo chuỗi cách nhau bởi dấu phẩy để Excel hiểu là các cột
    public String generateCSVContent(String type, String className, String semester) {
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        StringBuilder sb = new StringBuilder();

        // Header chung (Tiêu đề báo cáo)
        sb.append("TRƯỜNG THCS ABC\n");
        sb.append("BÁO CÁO:,").append(type).append("\n"); // Dấu phẩy để sang cột B
        sb.append("Lớp:,").append(className).append("\n");
        sb.append("Học kỳ:,").append(semester).append("\n");
        sb.append("Ngày xuất:,").append(date).append("\n");
        sb.append("\n"); // Dòng trống ngăn cách

        // Body (Dữ liệu bảng)
        if (type.contains("Danh sách lớp")) {
            // Tiêu đề cột
            sb.append("STT,Mã Học Sinh,Họ và Tên,Giới Tính,Ghi Chú\n");

            // Dữ liệu giả lập
            sb.append("1,HS001,Nguyễn Văn An,Nam,\n");
            sb.append("2,HS002,Trần Thị Bình,Nữ,Lớp trưởng\n");
            sb.append("3,HS003,Lê Văn Cường,Nam,\n");
        }
        else if (type.contains("Bảng điểm")) {
            sb.append("STT,Họ và Tên,Toán,Văn,Anh,Điểm TB,Xếp Loại\n");
            sb.append("1,Nguyễn Văn An,8.5,7.0,8.0,7.8,Khá\n");
            sb.append("2,Trần Thị Bình,9.0,8.5,9.0,8.8,Giỏi\n");
        }
        else {
            sb.append("Thông báo:,Chưa có dữ liệu mẫu cho loại báo cáo này\n");
        }

        return sb.toString();
    }

    // --- 3. HÀM LƯU FILE (HỖ TRỢ TIẾNG VIỆT UTF-8 BOM) ---
    public void saveReportToFile(File file, String content) throws Exception {
        // Sử dụng FileOutputStream + OutputStreamWriter để kiểm soát mã hóa (Encoding)
        // StandardCharsets.UTF_8 đảm bảo tiếng Việt không bị lỗi
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // QUAN TRỌNG: Ghi BOM (Byte Order Mark) để Excel nhận diện được đây là file UTF-8
            // Nếu không có dòng này, Excel mở lên sẽ bị lỗi font tiếng Việt
            writer.write('\ufeff');

            // Ghi nội dung báo cáo
            writer.write(content);
        }
    }
}