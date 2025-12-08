package UI;

import AccountManager.Account;
import Database.ClassDatabase;
import GradeManager.Grade;
import Database.GradeDatabase;
import GradeManager.service.DashboardService;
import StudentManager.Student;
import Database.StudentDatabase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Dashboard extends JFrame {
    private JPanel mainPanel;
    private Account currentAccount;

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

        // QUYẾT ĐỊNH GIAO DIỆN DỰA TRÊN VAI TRÒ
        if ("student".equals(currentAccount.getRole())) {
            mainPanel = createStudentDashboard(); // Giao diện riêng cho HS
        } else {
            mainPanel = createAdminDashboard();   // Giao diện cho Admin/GV
        }

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

        String firstLetter = currentAccount.getUsername().isEmpty() ? "?" : currentAccount.getUsername().substring(0, 1).toUpperCase();
        JLabel lblAvatar = new JLabel(firstLetter);
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

        if ("student".equals(role)) {
            sidebar.add(createMenuBtn("Cài đặt hệ thống", y, false));
            y += 50;
        }

        if ("admin".equals(role)){
            sidebar.add(createMenuBtn("Quản lý học sinh", y, false));
            y += 50;
            sidebar.add(createMenuBtn("Quản lý bảng điểm", y, false));
            y += 50;
            sidebar.add(createMenuBtn("Lớp học", y, false));
            y += 50;
            sidebar.add(createMenuBtn("Xuất báo cáo", y, false));
            y += 50;
            sidebar.add(createMenuBtn("Cài đặt hệ thống", y, false));
            y += 50;
        }

        if ("teacher".equals(role)) {
            sidebar.add(createMenuBtn("Quản lý bảng điểm", y, false));
            y += 50;
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

    // ================= 1. GIAO DIỆN ADMIN / GIÁO VIÊN =================
    private JPanel createAdminDashboard() {
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
        content.add(createCard("Năm Học", "2025-2026", 560, 100));

        DashboardService service = new DashboardService();
        content.add(createListCard("Phân loại học lực", service.calculatePerformanceStats(), 40, 260, 400));
        content.add(createListCard("Lớp học tiêu biểu (Top ĐTB)", service.calculateTopClasses(), 480, 260, 500));

        return content;
    }

    // ================= 2. GIAO DIỆN HỌC SINH (MỚI) =================
    private JPanel createStudentDashboard() {
        JPanel content = new JPanel(null);
        content.setBackground(bgColor);

        JLabel lblTitle = new JLabel("Hồ sơ cá nhân");
        lblTitle.setFont(fontTitle);
        lblTitle.setForeground(textColor);
        lblTitle.setBounds(40, 40, 400, 40);
        content.add(lblTitle);

        // Lấy thông tin học sinh
        String studentID = currentAccount.getID();
        Student s = studentDB.findByID(studentID);
        Grade g = gradeDB.getGradeByStudentID(studentID);

        if (s == null) {
            JLabel lblError = new JLabel("Chưa tìm thấy thông tin hồ sơ cho tài khoản này!");
            lblError.setFont(fontHead);
            lblError.setForeground(Color.RED);
            lblError.setBounds(40, 100, 500, 30);
            content.add(lblError);
            return content;
        }

        // --- THẺ THÔNG TIN CÁ NHÂN ---
        JPanel infoCard = new JPanel(null);
        infoCard.setBounds(40, 100, 400, 250);
        infoCard.setBackground(cardColor);
        infoCard.setBorder(BorderFactory.createMatteBorder(4, 0, 0, 0, primaryColor)); // Viền trên màu xanh

        JLabel lblName = new JLabel(s.getStudentName());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblName.setForeground(primaryColor);
        lblName.setBounds(30, 20, 350, 40);
        infoCard.add(lblName);

        addInfoRow(infoCard, "Mã học sinh:", s.getStudentID(), 70);
        addInfoRow(infoCard, "Lớp:", s.getStudentClass(), 110);
        addInfoRow(infoCard, "Giới tính:", s.getGender(), 150);
        addInfoRow(infoCard, "Trạng thái:", "Đang học", 190);

        content.add(infoCard);

        // --- THẺ ĐIỂM SỐ ---
        JPanel scoreCard = new JPanel(null);
        scoreCard.setBounds(480, 100, 500, 250);
        scoreCard.setBackground(cardColor);
        scoreCard.setBorder(BorderFactory.createMatteBorder(4, 0, 0, 0, Color.decode("#10B981"))); // Viền trên màu xanh lá

        JLabel lblScoreTitle = new JLabel("Kết quả học tập");
        lblScoreTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblScoreTitle.setForeground(textColor);
        lblScoreTitle.setBounds(30, 20, 300, 40);
        scoreCard.add(lblScoreTitle);

        if (g != null) {
            // Hiển thị 3 cột điểm
            addScoreBox(scoreCard, "Thường xuyên", String.valueOf(g.getRegularScore()), 30, 80);
            addScoreBox(scoreCard, "Giữa kỳ", String.valueOf(g.getMidtermScore()), 180, 80);
            addScoreBox(scoreCard, "Cuối kỳ", String.valueOf(g.getFinalScore()), 330, 80);

            // Điểm tổng kết to đùng
            JLabel lblAvgTitle = new JLabel("Điểm Trung Bình:");
            lblAvgTitle.setFont(fontText);
            lblAvgTitle.setForeground(grayText);
            lblAvgTitle.setBounds(30, 180, 150, 30);
            scoreCard.add(lblAvgTitle);

            JLabel lblAvg = new JLabel(String.format("%.2f", g.getAverage()));
            lblAvg.setFont(new Font("Segoe UI", Font.BOLD, 36));
            lblAvg.setForeground(primaryColor);
            lblAvg.setBounds(150, 170, 150, 45);
            scoreCard.add(lblAvg);

            // Xếp loại
            DashboardService service = new DashboardService(); // Mượn logic xếp loại nếu có, hoặc tự tính
            // Ở đây mình tạm dùng logic đơn giản để demo
            String rank = (g.getAverage() >= 8) ? "Giỏi" : (g.getAverage() >= 6.5) ? "Khá" : "Trung bình";
            JLabel lblRank = new JLabel(rank);
            lblRank.setOpaque(true);
            lblRank.setBackground(rank.equals("Giỏi") ? Color.decode("#D1FAE5") : Color.decode("#FEF3C7"));
            lblRank.setForeground(rank.equals("Giỏi") ? Color.decode("#065F46") : Color.decode("#92400E"));
            lblRank.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblRank.setHorizontalAlignment(SwingConstants.CENTER);
            lblRank.setBounds(330, 180, 100, 30);
            scoreCard.add(lblRank);

        } else {
            JLabel lblNoScore = new JLabel("Chưa có dữ liệu điểm số.");
            lblNoScore.setFont(fontText);
            lblNoScore.setBounds(30, 80, 300, 30);
            scoreCard.add(lblNoScore);
        }

        content.add(scoreCard);

        return content;
    }

    private void addInfoRow(JPanel p, String label, String value, int y) {
        JLabel lblL = new JLabel(label);
        lblL.setFont(fontText);
        lblL.setForeground(grayText);
        lblL.setBounds(30, y, 100, 25);
        p.add(lblL);

        JLabel lblV = new JLabel(value);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblV.setForeground(textColor);
        lblV.setBounds(140, y, 200, 25);
        p.add(lblV);
    }

    private void addScoreBox(JPanel p, String title, String score, int x, int y) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBounds(x, y, 120, 70);
        box.setBackground(bgColor);
        box.setBorder(BorderFactory.createLineBorder(lineColor));

        JLabel lblScore = new JLabel(score);
        lblScore.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblScore.setHorizontalAlignment(SwingConstants.CENTER);
        lblScore.setForeground(textColor);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(grayText);
        lblTitle.setBorder(new EmptyBorder(5,0,5,0));

        box.add(lblScore, BorderLayout.CENTER);
        box.add(lblTitle, BorderLayout.SOUTH);
        p.add(box);
    }

    // --- CÁC HÀM CŨ (CreateCard,...) GIỮ NGUYÊN ĐỂ DÙNG CHO ADMIN ---
    private JPanel createCard(String title, String value, int x, int y) {
        JPanel card = new JPanel(null);
        card.setBounds(x, y, 240, 130);
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createMatteBorder(1, 1, 4, 1, lineColor));
        JLabel lblVal = new JLabel(value); lblVal.setFont(new Font("Segoe UI", Font.BOLD, 36)); lblVal.setForeground(primaryColor); lblVal.setBounds(25, 25, 200, 45); card.add(lblVal);
        JLabel lblTitle = new JLabel(title); lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14)); lblTitle.setForeground(grayText); lblTitle.setBounds(25, 80, 200, 20); card.add(lblTitle);
        return card;
    }
    private JPanel createListCard(String title, String[] items, int x, int y, int width) {
        JPanel card = new JPanel(null);
        card.setBounds(x, y, width, 450);
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createLineBorder(lineColor));
        JLabel lblHeader = new JLabel(title); lblHeader.setFont(fontHead); lblHeader.setForeground(textColor); lblHeader.setBounds(30, 25, 300, 30);
        JSeparator sep = new JSeparator(); sep.setBounds(30, 65, width - 60, 1); sep.setForeground(lineColor); card.add(lblHeader); card.add(sep);
        if (items != null) { int itemY = 85; for (String item : items) { JLabel lblItem = new JLabel(item); lblItem.setFont(fontText); lblItem.setForeground(textColor); lblItem.setBounds(30, itemY, width - 60, 25); card.add(lblItem); itemY += 40; } }
        return card;
    }

    // --- LOGIC ĐIỀU HƯỚNG ---
    private void handleMenuClick(String menuName) {
        if ("Quản lý học sinh".equals(menuName)) {
            this.dispose();
            new StudentManagement(currentAccount).setVisible(true);
        }
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
        else if ("Cài đặt hệ thống".equals(menuName)) {
            this.dispose();
            new Settings(currentAccount).setVisible(true);
        }
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