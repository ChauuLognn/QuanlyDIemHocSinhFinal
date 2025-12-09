package UI;

import AccountManager.Account;
import Database.ClassDatabase;
import GradeManager.Grade;
import Database.GradeDatabase;
import GradeManager.service.DashboardService;
import StudentManager.Student;
import Database.StudentDatabase;

import javax.swing.border.CompoundBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Dashboard extends JFrame {
    private JPanel mainPanel;
    private Account currentAccount;

    private final Color primaryColor = Color.decode("#1E40AF");
    private final Color bgColor = Color.decode("#F3F4F6");
    private final Color cardColor = Color.WHITE;
    private final Color textColor = Color.decode("#111827");
    private final Color grayText = Color.decode("#6B7280");
    private final Color lineColor = Color.decode("#E5E7EB");
    private final Color hoverColor = Color.decode("#EFF6FF");

    private final Font fontTitle = new Font("Segoe UI", Font.BOLD, 24);
    private final Font fontHead = new Font("Segoe UI", Font.BOLD, 16);
    private final Font fontText = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font fontMenu = new Font("Segoe UI", Font.BOLD, 14);

    private final StudentDatabase stuDb = StudentDatabase.getInstance();
    private final GradeDatabase gradeDb = GradeDatabase.getInstance();
    private final ClassDatabase classDb = ClassDatabase.getInstance();

    public Dashboard(Account account) {
        this.currentAccount = account;
        setTitle("Hệ thống Quản lý - " + getRoleName());
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);

        if ("student".equals(currentAccount.getRole())) {
            mainPanel = createStudentDashboard();
        } else {
            mainPanel = createAdminDashboard();
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

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(null);
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBackground(cardColor);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, lineColor));

        JLabel lblBrand = new JLabel("SCHOOL ADMIN");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblBrand.setForeground(primaryColor);
        lblBrand.setBounds(30, 30, 220, 40);
        sidebar.add(lblBrand);

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

        int y = 200;
        String role = currentAccount.getRole();

        sidebar.add(createMenuBtn("Trang chủ", y, true));
        y += 50;

        if ("student".equals(role)) {
            sidebar.add(createMenuBtn("Cài đặt hệ thống", y, false));
            y += 50;
        }

        if ("admin".equals(role)) {
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

    private JPanel createAdminDashboard() {
        JPanel content = new JPanel(null);
        content.setBackground(bgColor);

        JLabel lblTitle = new JLabel("Tổng quan hệ thống");
        lblTitle.setFont(fontTitle);
        lblTitle.setForeground(textColor);
        lblTitle.setBounds(40, 40, 400, 40);
        content.add(lblTitle);

        int totalStudents = stuDb.getAllStudents().size();
        int totalClasses = classDb.getAllClasses().size();

        content.add(createCard("Tổng học sinh", String.valueOf(totalStudents), 40, 100));
        content.add(createCard("Tổng số lớp", String.valueOf(totalClasses), 300, 100));
        content.add(createCard("Năm Học", "2025-2026", 560, 100));

        DashboardService service = new DashboardService();
        content.add(createListCard("Phân loại học lực", service.getPerformanceStats(), 40, 260, 400));
        content.add(createListCard("Lớp học tiêu biểu (Top ĐTB)", service.getTopClasses(), 480, 260, 500));

        return content;
    }

// phần 2 tiếp tục...

// tiếp phần 1

    private JPanel createStudentDashboard() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(bgColor);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(cardColor);
        headerPanel.setBorder(new CompoundBorder(
                new LineBorder(lineColor),
                new EmptyBorder(15, 20, 15, 20)
        ));

        String studentID = currentAccount.getID();
        Student s = stuDb.findByID(studentID);

        if (s != null) {
            JLabel lblHello = new JLabel("Xin chào, " + s.getStudentName());
            lblHello.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblHello.setForeground(primaryColor);

            JLabel lblInfo = new JLabel("Mã số: " + s.getStudentID() + "  |  Lớp: " + s.getStudentClass());
            lblInfo.setFont(fontText);
            lblInfo.setForeground(grayText);

            headerPanel.add(lblHello, BorderLayout.NORTH);
            headerPanel.add(lblInfo, BorderLayout.CENTER);
        }

        content.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(fontHead);
        tabbedPane.setBackground(bgColor);

        tabbedPane.addTab("Học kỳ 1", createSemesterPanel(studentID, 1));
        tabbedPane.addTab("Học kỳ 2", createSemesterPanel(studentID, 2));

        content.add(tabbedPane, BorderLayout.CENTER);

        return content;
    }

    private JPanel createSemesterPanel(String studentID, int semester) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        ArrayList<Object[]> transcript = gradeDb.getTranscript(studentID, semester);

        String[] columns = {"Môn học", "Hệ số", "Đ.TX", "Đ.GK", "Đ.CK", "ĐTB Môn", "Xếp loại"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        double totalScore = 0;
        int totalCoeff = 0;

        for (Object[] row : transcript) {
            String subName = (String) row[0];
            int coeff = (int) row[1];
            double r = (double) row[2];
            double m = (double) row[3];
            double f = (double) row[4];

            double avgSub = (r + m * 2 + f * 3) / 6.0;

            totalScore += avgSub * coeff;
            totalCoeff += coeff;

            model.addRow(new Object[]{
                    subName, coeff, r, m, f,
                    String.format("%.2f", avgSub),
                    classify(avgSub)
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(fontText);
        table.getTableHeader().setFont(fontHead);
        table.getTableHeader().setBackground(Color.decode("#E0E7FF"));
        table.setEnabled(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(lineColor));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(cardColor);
        footer.setBorder(new LineBorder(lineColor));

        double semesterAvg = (totalCoeff > 0) ? (totalScore / totalCoeff) : 0;

        JLabel lblAvg = new JLabel("ĐIỂM TRUNG BÌNH HỌC KỲ " + semester + ":  " + String.format("%.2f", semesterAvg));
        lblAvg.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAvg.setForeground(primaryColor);

        JLabel lblRank = new JLabel("  [" + classify(semesterAvg) + "]  ");
        lblRank.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRank.setForeground(Color.decode("#EF4444"));

        footer.add(lblAvg);
        footer.add(lblRank);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private String classify(double score) {
        if (score >= 8.0) return "Giỏi";
        if (score >= 6.5) return "Khá";
        if (score >= 5.0) return "Trung bình";
        return "Yếu";
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

    private void handleMenuClick(String menuName) {
        if ("Quản lý học sinh".equals(menuName)) {
            this.dispose();
            new StudentManagement(currentAccount).setVisible(true);
        } else if ("Quản lý bảng điểm".equals(menuName)) {
            this.dispose();
            new GradeManagement(currentAccount).setVisible(true);
        } else if ("Lớp học".equals(menuName)) {
            this.dispose();
            new ClassManagement(currentAccount).setVisible(true);
        } else if ("Xuất báo cáo".equals(menuName)) {
            this.dispose();
            new ReportManagement(currentAccount).setVisible(true);
        } else if ("Cài đặt hệ thống".equals(menuName)) {
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