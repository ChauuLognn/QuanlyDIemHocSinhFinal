package UI;

import GradeManager.Grade;
import GradeManager.data.GradeDatabase;
import StudentManager.Student;
import StudentManager.data.StudentDatabase;
import ClassManager.data.ClassDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import GradeManager.service.DashboardService;

public class Dashboard extends JFrame {
    private JPanel mainPanel;
    private String username = "ADMIN"; // Có thể lấy từ Login sau này

    // Colors
    private final Color primaryColor = new Color(70, 70, 70);
    private final Color secondaryColor = new Color(245, 245, 245);

    // Database References
    private final StudentDatabase studentDB = StudentDatabase.getStudentDB();
    private final GradeDatabase gradeDB = GradeDatabase.getGradeDB();
    private final ClassDatabase classDB = ClassDatabase.getClassDB();

    public Dashboard() {
        setTitle("Hệ thống quản lý học sinh");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Top Navigation Bar
        add(createTopNavBar(), BorderLayout.NORTH);

        // 2. Left Sidebar Menu (Đã thêm Cài đặt)
        add(createSidebarMenu(), BorderLayout.WEST);

        // 3. Main Content Area (Dữ liệu thật)
        mainPanel = createMainContent();
        add(mainPanel, BorderLayout.CENTER);
    }

    // ============================================================
    // TOP BAR
    // ============================================================
    private JPanel createTopNavBar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBackground(primaryColor);
        navbar.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel("QUẢN LÝ ĐIỂM HỌC SINH");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        navbar.add(title, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(primaryColor);

        JLabel userName = new JLabel("Xin chào, " + username);
        userName.setFont(new Font("Arial", Font.PLAIN, 13));
        userName.setForeground(Color.WHITE);
        rightPanel.add(userName);

        JLabel separator = new JLabel("|");
        separator.setForeground(new Color(150, 150, 150));
        rightPanel.add(separator);

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Arial", Font.PLAIN, 12));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(primaryColor);
        btnLogout.setBorder(null);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());
        rightPanel.add(btnLogout);

        navbar.add(rightPanel, BorderLayout.EAST);
        return navbar;
    }

    // ============================================================
    // SIDEBAR MENU
    // ============================================================
    private JPanel createSidebarMenu() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] menuItems = {
                "Trang chủ",
                "Quản lý học sinh",
                "Lớp học",
                "Thống kê",
                "Báo cáo",
        };

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuBtn = createMenuButton(menuItems[i], i == 0); // Active mục đầu tiên
            sidebar.add(menuBtn);
        }

        return sidebar;
    }

    private JButton createMenuButton(String text, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setForeground(isActive ? primaryColor : new Color(100, 100, 100));
        btn.setBackground(isActive ? secondaryColor : Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        if (isActive) {
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, primaryColor),
                    BorderFactory.createEmptyBorder(10, 22, 10, 25)
            ));
        }

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (!isActive) btn.setBackground(new Color(250, 250, 250)); }
            public void mouseExited(MouseEvent e) { if (!isActive) btn.setBackground(Color.WHITE); }
        });

        // Xử lý chuyển màn hình
        btn.addActionListener(e -> {
            if (text.equals("Quản lý học sinh")) {
                this.dispose();
                new StudentManagement().setVisible(true);
            } else if (text.equals("Lớp học")) {
                this.dispose();
                new ClassManagement().setVisible(true);
            } else if (text.equals("Thống kê")) {
                this.dispose();
                new Statistics().setVisible(true);
            } else if (text.equals("Báo cáo")) {
                this.dispose();
                new ReportManagement().setVisible(true);
            }
        });

        return btn;
    }

    // ============================================================
    // MAIN CONTENT (DỮ LIỆU THẬT)
    // ============================================================
    private JPanel createMainContent() {
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(secondaryColor);
        content.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // 1. Welcome
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(secondaryColor);
        JLabel welcome = new JLabel("Tổng quan hệ thống");
        welcome.setFont(new Font("Arial", Font.BOLD, 22));
        topSection.add(welcome, BorderLayout.WEST);
        content.add(topSection, BorderLayout.NORTH);

        // 2. Stats Cards (Số liệu thật)
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setBackground(secondaryColor);
        statsPanel.setPreferredSize(new Dimension(0, 150));

        // Lấy số liệu thực tế
        int totalStudents = studentDB.getAllStudents().size();
        int totalClasses = classDB.getAllClasses().size();

        statsPanel.add(createSimpleCard("Tổng học sinh", String.valueOf(totalStudents)));
        statsPanel.add(createSimpleCard("Tổng số lớp", String.valueOf(totalClasses)));

        // 3. Bottom Info: Phân loại & Lớp nổi bật
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomPanel.setBackground(secondaryColor);
        bottomPanel.setPreferredSize(new Dimension(0, 200));

        DashboardService service = new DashboardService();

        // Tính toán dữ liệu thật cho 2 bảng này
        bottomPanel.add(createInfoCard("Phân loại học lực", service.calculatePerformanceStats()));
        bottomPanel.add(createInfoCard("Lớp học nổi bật (Top ĐTB)", service.calculateTopClasses()));


        // Container giữa chứa Stats và Bottom
        JPanel centerContainer = new JPanel(new BorderLayout(0, 20));
        centerContainer.setBackground(secondaryColor);
        centerContainer.add(statsPanel, BorderLayout.NORTH);
        centerContainer.add(bottomPanel, BorderLayout.CENTER);

        content.add(centerContainer, BorderLayout.CENTER);

        return content;
    }

    // ============================================================
    // UI COMPONENTS HELPER
    // ============================================================
    private JPanel createSimpleCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 13));
        lblTitle.setForeground(new Color(120, 120, 120));
        card.add(lblTitle, BorderLayout.NORTH);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 36));
        lblValue.setForeground(primaryColor);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    private JPanel createInfoCard(String title, String[] items) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(primaryColor);
        card.add(lblTitle, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        if (items != null) {
            for (String item : items) {
                JLabel lblItem = new JLabel("• " + item);
                lblItem.setFont(new Font("Arial", Font.PLAIN, 13));
                lblItem.setForeground(new Color(80, 80, 80));
                lblItem.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                listPanel.add(lblItem);
            }
        }

        // Thêm khoảng trống đẩy nội dung lên trên
        listPanel.add(Box.createVerticalGlue());

        card.add(listPanel, BorderLayout.CENTER);
        return card;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new Login().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }
}