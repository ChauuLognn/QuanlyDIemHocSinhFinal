📘 Hệ Thống Quản Lý Điểm Học Sinh
📌 Giới thiệu
Hệ thống Quản Lý Điểm Học Sinh THCS được xây dựng bằng Java theo kiến trúc phân tầng, hỗ trợ quản lý thông tin liên quan đến học sinh, lớp học, môn học, điểm số và báo cáo tổng hợp. Mục tiêu là tạo ra một hệ thống dễ mở rộng, dễ bảo trì và đảm bảo tính rõ ràng, chính xác, tiện lợi.
________________________________________
📁 Cấu trúc thư mục
src/
│
├── AccountManager/      # Quản lý tài khoản đăng nhập
├── ClassManager/        # Quản lý lớp học
├── Database/            # Tương tác cơ sở dữ liệu nội bộ
├── Exception/           # Các ngoại lệ tùy chỉnh
├── GradeManager/        # Quản lý điểm số
├── ReportManager/       # Xuất và xử lý báo cáo
├── StudentManager/      # Quản lý học sinh
├── SubjectManager/      # Quản lý môn học
└── UI/                  # Giao diện hệ thống
________________________________________
🧩 Chức năng chính theo từng vai trò
🔐 Quản trị viên
•	Quản lý tài khoản
•	Phân quyền
•	Quản lý lớp
•	Quản lý thông tin học sinh
🏫Giáo viên
•	Quản lý điểm
•	Xuất báo cáo
👨‍🎓 Học sinh
•	Xem điểm và học lực của mình
________________________________________
⚙️ Yêu cầu hệ thống
•	JDK 17+
•	IDE khuyến nghị: IntelliJ IDEA / Eclipse / VS Code có tích hợp thư viện MySQL Connector/J 
•	MySQL Workbench
________________________________________
▶️ Hướng dẫn cài đặt & chạy chương trình
1️⃣ Cài đặt MySQL
•	Cài MySQL Server và MySQL Workbench.
•	Mở MySQL Workbench → tạo schema mới tên: QuanLyHocSinhDB.
•	Tạo user có quyền truy cập schema (nếu dùng root thì chỉ cần nhớ mật khẩu).
2️⃣ Cấu hình kết nối CSDL trong project
•	Mở file cấu hình kết nối (DatabaseConnection.java ).
•	Tìm các dòng sau và chỉnh sửa thông tin:
private static final String URL = "jdbc:mysql://localhost:3306/QuanLyHocSinhDB";
       private static final String USER = "root";  	//your_username
       private static final String PASS = "1011";	//your_password
3️⃣ Import dữ liệu từ file SQL có sẵn
Dự án đã chuẩn bị sẵn file SQL: Dump20251209.sql.
Thực hiện import như sau:
1.	Mở MySQL Workbench → chọn schema QuanLyHocSinhDB.
2.	Vào menu Server → Data Import.
3.	Chọn mục Import from Self Contained File.
4.	Chọn file: Dump20251209.sql.
5.	Ở mục Default Target Schema, chọn QuanLyHocSinhDB.
6.	Nhấn Start Import để nạp toàn bộ bảng + dữ liệu mẫu.
(Hệ thống sẽ tự động có sẵn học sinh, lớp, môn, điểm… giúp việc test chương trình dễ dàng hơn.)
4️⃣ Build & chạy project
1.	Clone project:
2.	git clone https://github.com/ChauuLognn/QuanlyDIemHocSinhFinal.git
2.	Mở project bằng IntelliJ IDEA hoặc IDE bất kỳ.
3.	Đảm bảo đã cài JDK 17+.
4.	Nếu dùng JDBC, đảm bảo thư viện MySQL Connector đã được import.
5.	Chạy file Login trong thư mục UI.
6.	Hệ thống sẽ xuất hiện để bạn thực hiện các thao tác.
________________________________________
🚀 Định hướng phát triển
•	Tích hợp cơ sở dữ liệu MySQL thay cho lưu trữ thủ công.
•	Giao diện đồ họa (JavaFX / Swing / Web UI).
•	Hệ thống phân quyền nâng cao (Admin / Giáo viên / Học sinh).
•	Xuất báo cáo Excel
