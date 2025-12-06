package UI;

import AccountManager.Account;
import ClassManager.data.ClassDatabase;
import GradeManager.Grade;
import GradeManager.data.GradeDatabase;
import GradeManager.service.DashboardService;
import StudentManager.Student;
import StudentManager.data.StudentDatabase;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Dashboard extends JFrame {
    private JPanel mainPanel;
    private Account currentAccount;

    // Colors
    private final Color primaryColor = Color.decode("#1E40AF");
    private final Color bgColor      = Color.decode("#F3F4F6");
    private final Color cardColor    = Color.WHITE;
    private final Color textColor    = Color.decode("#111827");
    private final Color grayText     = Color.decode("#6B7280");

    private final Font fontTitle = new Font("Segoe UI", Font.BOLD, 24);
    private final Font fontHead  = new Font("Segoe UI", Font.BOLD, 16);
    private final Font fontText  = new Font("Segoe UI", Font.PLAIN, 14);

    // DB
    private final StudentDatabase studentDB = StudentDatabase.getStudentDB();
    private final GradeDatabase gradeDB = GradeDatabase.getGradeDB();
    private final ClassDatabase classDB = ClassDatabase.getClassDB();

    public Dashboard(Account account) {
        this.currentAccount = account;
        setTitle("Hệ thống Quản lý - " + getRoleName());
        setSize(1280, 800);
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

    // --- SIDEBAR ---
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(null);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(cardColor);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(229, 231, 235)));

        JLabel lblBrand = new JLabel("<html><span style='color:#1E40AF'>SCHOOL</span> ADMIN</html>");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrand.setBounds(30, 30, 200, 30);
        sidebar.add(lblBrand);

        JPanel userPanel = new JPanel(null);
        userPanel.setBackground(new Color(239, 246, 255));
        userPanel.setBounds(20, 80, 220, 70);
        userPanel.setBorder(new LineBorder(new Color(219, 234, 254), 1, true));

        JLabel lblAvatar = new JLabel("👤");
        lblAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblAvatar.setBounds(15, 10, 40, 50);
        userPanel.add(lblAvatar);

        JLabel lblUser = new JLabel(currentAccount.getUsername());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(primaryColor);
        lblUser.setBounds(60, 15, 150, 20);
        userPanel.add(lblUser);

        JLabel lblRole = new JLabel(getRoleName());
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(grayText);
        lblRole.setBounds(60, 35, 150, 20);
        userPanel.add(lblRole);
        sidebar.add(userPanel);

        int y = 180;
        String role = currentAccount.getRole();

        sidebar.add(createMenuBtn("Trang chủ", "🏠", y, true));
        y += 55;

        if ("admin".equals(role) || "teacher".equals(role)) {
            sidebar.add(createMenuBtn("Quản lý học sinh", "👨‍🎓", y, false));
            y += 55;
        }

        if ("admin".equals(role)) {
            sidebar.add(createMenuBtn("Lớp học", "🏫", y, false));
            y += 55;
            sidebar.add(createMenuBtn("Báo cáo & Thống kê", "📊", y, false));
            y += 55;
        }

        if ("student".equals(role)) {
            sidebar.add(createMenuBtn("Xem điểm cá nhân", "📝", y, false));
            y += 55;
        }

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setBounds(20, 700, 220, 40);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(new Color(220, 38, 38));
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());
        sidebar.add(btnLogout);

        return sidebar;
    }

    private JButton createMenuBtn(String text, String icon, int y, boolean isActive) {
        JButton btn = new JButton("  " + icon + "   " + text);
        btn.setBounds(20, y, 220, 45);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);

        if (isActive) {
            btn.setBackground(primaryColor);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        } else {
            btn.setBackground(cardColor);
            btn.setForeground(textColor);
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(243, 244, 246)); }
                public void mouseExited(MouseEvent e) { btn.setBackground(cardColor); }
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
        lblTitle.setBounds(40, 30, 400, 40);
        content.add(lblTitle);

        int totalStudents = studentDB.getAllStudents().size();
        int totalClasses = classDB.getAllClasses().size();

        content.add(createCard("Tổng học sinh", String.valueOf(totalStudents), 40, 90));
        content.add(createCard("Tổng số lớp", String.valueOf(totalClasses), 300, 90));
        content.add(createCard("Học kỳ", "HK1 - 2025", 560, 90));

        DashboardService service = new DashboardService();
        content.add(createListCard("Phân loại học lực", service.calculatePerformanceStats(), 40, 240, 400));
        content.add(createListCard("Lớp học tiêu biểu (Top ĐTB)", service.calculateTopClasses(), 480, 240, 480));

        return content;
    }

    private JPanel createCard(String title, String value, int x, int y) {
        JPanel card = new JPanel(null);
        card.setBounds(x, y, 240, 120);
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createMatteBorder(1, 1, 3, 1, new Color(229, 231, 235)));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblVal.setForeground(primaryColor);
        lblVal.setBounds(20, 20, 200, 40);
        card.add(lblVal);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(grayText);
        lblTitle.setBounds(20, 70, 200, 20);
        card.add(lblTitle);
        return card;
    }

    private JPanel createListCard(String title, String[] items, int x, int y, int width) {
        JPanel card = new JPanel(null);
        card.setBounds(x, y, width, 400);
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createMatteBorder(1, 1, 3, 1, new Color(229, 231, 235)));

        JLabel lblHeader = new JLabel(title);
        lblHeader.setFont(fontHead);
        lblHeader.setForeground(textColor);
        lblHeader.setBounds(25, 20, 300, 30);

        JSeparator sep = new JSeparator();
        sep.setBounds(25, 55, width - 50, 1);
        sep.setForeground(new Color(229, 231, 235));
        card.add(lblHeader);
        card.add(sep);

        if (items != null) {
            int itemY = 70;
            for (String item : items) {
                JLabel lblItem = new JLabel("•  " + item);
                lblItem.setFont(fontText);
                lblItem.setForeground(new Color(55, 65, 81));
                lblItem.setBounds(25, itemY, width - 50, 25);
                card.add(lblItem);
                itemY += 35;
            }
        }
        return card;
    }

    // --- LOGIC ---
    private void handleMenuClick(String menuName) {
        if ("Quản lý học sinh".equals(menuName)) {
            this.dispose();
            new StudentManagement(currentAccount).setVisible(true);
        }
        else if ("Lớp học".equals(menuName)) {
            this.dispose();
            new ClassManagement(currentAccount).setVisible(true);
        }
        else if ("Báo cáo & Thống kê".equals(menuName)) {
            this.dispose();
            // ✅ ĐÃ SỬA: Truyền Account vào Statistics
            new Statistics(currentAccount).setVisible(true);
        }
        else if ("Xem điểm cá nhân".equals(menuName)) {
            showStudentPersonalScore();
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
        if (g != null) {
            msg += "Điểm TB: " + String.format("%.2f", g.getAverage());
        } else msg += "Chưa có điểm.";
        JOptionPane.showMessageDialog(this, msg, "Bảng điểm", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        if (JOptionPane.showConfirmDialog(this, "Đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            this.dispose();
            new Login().setVisible(true);
        }
    }

    public static void main(String[] args) {
        Account mock = new Account("admin", "", "", "admin");
        SwingUtilities.invokeLater(() -> new Dashboard(mock).setVisible(true));
    }
}