package UI;

import AccountManager.Account;
import ClassManager.data.ClassDatabase;
import GradeManager.Grade;
import GradeManager.data.GradeDatabase;
import GradeManager.service.DashboardService;
import StudentManager.Student;
import StudentManager.data.StudentDatabase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Dashboard extends JFrame {
    private JPanel mainPanel;
    private Account currentAccount;

    // --- COLORS & FONTS ---
    private final Color primaryColor = Color.decode("#1E40AF");
    private final Color bgColor      = Color.decode("#F3F4F6");
    private final Color cardColor    = Color.WHITE;
    private final Color textColor    = Color.decode("#111827");
    private final Color grayText     = Color.decode("#6B7280");
    private final Color lineColor    = Color.decode("#E5E7EB");
    private final Color hoverColor   = Color.decode("#EFF6FF");

    private final Font fontTitle = new Font("Segoe UI", Font.BOLD, 24);
    private final Font fontHead  = new Font("Segoe UI", Font.BOLD, 16);
    private final Font fontText  = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font fontMenu  = new Font("Segoe UI", Font.BOLD, 14);

    // DB
    private final StudentDatabase studentDB = StudentDatabase.getStudentDB();
    private final GradeDatabase gradeDB = GradeDatabase.getGradeDB();
    private final ClassDatabase classDB = ClassDatabase.getClassDB();

    public Dashboard(Account account) {
        this.currentAccount = account;
        setTitle("Hệ thống Quản lý - " + getRoleName());
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        mainPanel = createMainContent();
        add(mainPanel, BorderLayout.CENTER);
    }

    private String getRoleName() {
        if (currentAccount == null) return "Khách";
        String r = currentAccount.getRole();
        if ("admin".equals(r)) return "Quản trị viên";
        if ("teacher".equals(r)) return "Giáo viên";
        return "Học sinh";
    }

    // ================= SIDEBAR =================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(null);
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBackground(cardColor);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, lineColor));

        // Brand
        JLabel lblBrand = new JLabel("SCHOOL ADMIN");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblBrand.setForeground(primaryColor);
        lblBrand.setBounds(30, 30, 220, 40);
        sidebar.add(lblBrand);

        // User Profile
        JPanel userPanel = new JPanel(null);
        userPanel.setBackground(bgColor);
        userPanel.setBounds(20, 90, 240, 70);
        userPanel.setBorder(new LineBorder(lineColor, 1, true));

        JLabel lblAvatar = new JLabel(currentAccount.getUsername().substring(0, 1).toUpperCase());
        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblAvatar.setForeground(Color.WHITE);
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatar.setOpaque(true);
        lblAvatar.setBackground(primaryColor);
        lblAvatar.setBounds(15, 15, 40, 40);
        userPanel.add(lblAvatar);

        JLabel lblUser = new JLabel(currentAccount.getUsername());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(textColor);
        lblUser.setBounds(70, 15, 160, 20);
        userPanel.add(lblUser);

        JLabel lblRole = new JLabel(getRoleName());
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(grayText);
        lblRole.setBounds(70, 35, 160, 20);
        userPanel.add(lblRole);
        sidebar.add(userPanel);

        // --- MENU ITEMS ---
        int y = 200;
        String role = currentAccount.getRole();

        sidebar.add(createMenuBtn("Trang chủ", y, true));
        y += 50;
        if ("admin".equals(role)) {
            sidebar.add(createMenuBtn("Quản lý học sinh", y, false)); // Sửa hồ sơ
            y += 50;
            sidebar.add(createMenuBtn("Quản lý bảng điểm", y, false)); // Nhập điểm
            y += 50;
        }
        if ("teacher".equals(role)) {
            sidebar.add(createMenuBtn("Quản lý bảng điểm", y, false)); // Nhập điểm
            y += 50;
        }
        if ("admin".equals(role)) {
            sidebar.add(createMenuBtn("Lớp học", y, false));
            y += 50;

            // ✅ ĐÃ SỬA: Nút cài đặt
            sidebar.add(createMenuBtn("Cài đặt hệ thống", y, false));
            y += 50;

            sidebar.add(createMenuBtn("Xuất báo cáo", y, false));
            y += 50;
        }

        if ("student".equals(role)) {
            sidebar.add(createMenuBtn("Xem điểm cá nhân", y, false));
            y += 50;
            // Học sinh cũng có thể vào cài đặt để đổi mật khẩu
            sidebar.add(createMenuBtn("Cài đặt hệ thống", y, false));
            y += 50;
        }



        // Logout
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setBounds(20, 750, 240, 45);
        btnLogout.setFont(fontMenu);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(Color.decode("#EF4444"));
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());
        sidebar.add(btnLogout);

        return sidebar;
    }

    private JButton createMenuBtn(String text, int y, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setBounds(20, y, 240, 45);
        btn.setFont(fontMenu);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(0, 20, 0, 0));

        if (isActive) {
            btn.setBackground(primaryColor);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(cardColor);
            btn.setForeground(grayText);
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(hoverColor);
                    btn.setForeground(primaryColor);
                }
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(cardColor);
                    btn.setForeground(grayText);
                }
            });
        }
        btn.addActionListener(e -> handleMenuClick(text));
        return btn;
    }

    // --- MAIN CONTENT ---
    private JPanel createMainContent() {
        JPanel content = new JPanel(null);
        content.setBackground(bgColor);

        JLabel lblTitle = new JLabel("Tổng quan hệ thống");
        lblTitle.setFont(fontTitle);
        lblTitle.setForeground(textColor);
        lblTitle.setBounds(40, 40, 400, 40);
        content.add(lblTitle);

        int totalStudents = studentDB.getAllStudents().size();
        int totalClasses = classDB.getAllClasses().size();

        content.add(createCard("Tổng học sinh", String.valueOf(totalStudents), 40, 100));
        content.add(createCard("Tổng số lớp", String.valueOf(totalClasses), 300, 100));
        content.add(createCard("Học kỳ hiện tại", "HK1 - 2025", 560, 100));

        DashboardService service = new DashboardService();
        content.add(createListCard("Phân loại học lực", service.calculatePerformanceStats(), 40, 260, 400));
        content.add(createListCard("Lớp học tiêu biểu (Top ĐTB)", service.calculateTopClasses(), 480, 260, 500));

        return content;
    }

    private JPanel createCard(String title, String value, int x, int y) {
        JPanel card = new JPanel(null);
        card.setBounds(x, y, 240, 130);
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createMatteBorder(1, 1, 4, 1, lineColor));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblVal.setForeground(primaryColor);
        lblVal.setBounds(25, 25, 200, 45);
        card.add(lblVal);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(grayText);
        lblTitle.setBounds(25, 80, 200, 20);
        card.add(lblTitle);
        return card;
    }

    private JPanel createListCard(String title, String[] items, int x, int y, int width) {
        JPanel card = new JPanel(null);
        card.setBounds(x, y, width, 450);
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createLineBorder(lineColor));

        JLabel lblHeader = new JLabel(title);
        lblHeader.setFont(fontHead);
        lblHeader.setForeground(textColor);
        lblHeader.setBounds(30, 25, 300, 30);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 65, width - 60, 1);
        sep.setForeground(lineColor);
        card.add(lblHeader);
        card.add(sep);

        if (items != null) {
            int itemY = 85;
            for (String item : items) {
                JLabel lblItem = new JLabel(item);
                lblItem.setFont(fontText);
                lblItem.setForeground(textColor);
                lblItem.setBounds(30, itemY, width - 60, 25);
                card.add(lblItem);
                itemY += 40;
            }
        }
        return card;
    }

    // --- LOGIC ĐIỀU HƯỚNG ---
    private void handleMenuClick(String menuName) {
        if ("Quản lý học sinh".equals(menuName)) {
            this.dispose();
            new StudentManagement(currentAccount).setVisible(true);
        }
        // ✅ THÊM DÒNG NÀY
        else if ("Quản lý bảng điểm".equals(menuName)) {
            this.dispose();
            new GradeManagement(currentAccount).setVisible(true);
        }
        else if ("Lớp học".equals(menuName)) {
            this.dispose();
            new ClassManagement(currentAccount).setVisible(true);
        }
        else if ("Xuất báo cáo".equals(menuName)) {
            this.dispose();
            new ReportManagement(currentAccount).setVisible(true);
        }
        else if ("Xem điểm cá nhân".equals(menuName)) {
            showStudentPersonalScore();
        }
        // ✅ ĐÃ SỬA: MỞ TRANG SETTINGS MỚI
        else if ("Cài đặt hệ thống".equals(menuName)) {
            this.dispose();
            new Settings(currentAccount).setVisible(true);
        }
    }

    private void showStudentPersonalScore() {
        String studentID = currentAccount.getID();
        if (studentID == null || studentID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tài khoản này chưa liên kết với mã học sinh nào!");
            return;
        }
        Grade g = gradeDB.getGradeByStudentID(studentID);
        Student s = studentDB.findByID(studentID);

        if (s == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin học sinh!");
            return;
        }
        String msg = "Học sinh: " + s.getStudentName() + "\nLớp: " + s.getStudentClass() + "\n----------------\n";
        if (g != null) msg += "Điểm TB: " + String.format("%.2f", g.getAverage());
        else msg += "Chưa có điểm.";
        JOptionPane.showMessageDialog(this, msg, "Bảng điểm cá nhân", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        if (JOptionPane.showConfirmDialog(this, "Bạn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            this.dispose();
            new Login().setVisible(true);
        }
    }

    public static void main(String[] args) {
        Account mock = new Account("admin", "", "", "admin");
        SwingUtilities.invokeLater(() -> new Dashboard(mock).setVisible(true));
    }
}