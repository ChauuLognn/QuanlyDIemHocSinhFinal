package UI;

import AccountManager.Account;
import Database.DatabaseConnection;
import ReportManager.service.ReportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.*;

public class ReportManagement extends JFrame {
    private Account currentAccount;

    private final Color primaryColor = Color.decode("#1E40AF");
    private final Color bgColor      = Color.decode("#F3F4F6");
    private final Color cardColor    = Color.WHITE;
    private final Color textColor    = Color.decode("#111827");
    private final Color grayText     = Color.decode("#6B7280");
    private final Color lineColor    = Color.decode("#E5E7EB");

    private JComboBox<String> cboReportType, cboClass, cboSemester;
    private JTextArea txtPreview;

    public ReportManagement(Account account) {
        this.currentAccount = account;
        setTitle("Xuất Báo Cáo");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createTopBar(), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(25, 0));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        mainPanel.add(createConfigPanel(), BorderLayout.WEST);
        mainPanel.add(createPreviewPanel(), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBackground(cardColor);
        navbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, lineColor));

        JLabel title = new JLabel("  XUẤT BÁO CÁO");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(primaryColor);
        title.setBorder(new EmptyBorder(0, 15, 0, 0));
        navbar.add(title, BorderLayout.WEST);

        JButton btnBack = new JButton("← Dashboard");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setForeground(grayText);
        btnBack.setBackground(cardColor);
        btnBack.setBorder(new EmptyBorder(0, 15, 0, 15));
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnBack.setForeground(primaryColor); }
            public void mouseExited(MouseEvent e) { btnBack.setForeground(grayText); }
        });
        btnBack.addActionListener(e -> {
            this.dispose();
            new Dashboard(currentAccount).setVisible(true);
        });

        navbar.add(btnBack, BorderLayout.EAST);
        return navbar;
    }

    private JPanel createConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cardColor);
        panel.setPreferredSize(new Dimension(350, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor),
                new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel lblTitle = new JLabel("CẤU HÌNH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(primaryColor);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        panel.add(createLabel("Loại báo cáo"));
        cboReportType = createComboBox(new String[]{
                "Danh sách lớp (Class List)", "Bảng điểm tổng kết",
                "Danh sách học sinh giỏi", "Phiếu liên lạc cá nhân"
        });
        panel.add(cboReportType);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(createLabel("Chọn lớp"));
        cboClass = createComboBox(new String[]{"Tất cả"});
        loadClassList();
        panel.add(cboClass);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(createLabel("Học kỳ"));
        cboSemester = createComboBox(new String[]{"Học kỳ 1", "Học kỳ 2", "Cả năm"});
        panel.add(cboSemester);

        panel.add(Box.createVerticalGlue());

        // Buttons
        JButton btnPreview = createButton("Xem trước", Color.decode("#3B82F6"));
        btnPreview.addActionListener(e -> generatePreview());
        panel.add(btnPreview);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnExport = createButton("Xuất Excel (.csv)", Color.decode("#10B981"));
        btnExport.addActionListener(e -> exportToExcel());
        panel.add(btnExport);

        return panel;
    }

    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardColor);
        panel.setBorder(BorderFactory.createLineBorder(lineColor));

        JLabel lblHeader = new JLabel("  BẢN XEM TRƯỚC");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(grayText);
        lblHeader.setPreferredSize(new Dimension(0, 50));
        lblHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, lineColor));
        panel.add(lblHeader, BorderLayout.NORTH);

        txtPreview = new JTextArea();
        txtPreview.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtPreview.setEditable(false);
        txtPreview.setMargin(new Insets(25, 25, 25, 25));
        txtPreview.setText("Vui lòng chọn cấu hình và bấm 'Xem trước'...");

        JScrollPane scroll = new JScrollPane(txtPreview);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(grayText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setBackground(Color.WHITE);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        return box;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadClassList() {
        try (Connection con = DatabaseConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT className FROM classes")) {
            while (rs.next()) cboClass.addItem(rs.getString("className"));
        } catch (Exception ignored) {}
    }

    private void generatePreview() {
        ReportService service = new ReportService();
        String content = service.generateReportContent((String) cboReportType.getSelectedItem(), (String) cboClass.getSelectedItem(), (String) cboSemester.getSelectedItem());
        txtPreview.setText(content);
        txtPreview.setCaretPosition(0);
    }

    private void exportToExcel() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("BaoCao_" + System.currentTimeMillis() + ".csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ReportService service = new ReportService();
                String content = service.generateCSVContent((String) cboReportType.getSelectedItem(), (String) cboClass.getSelectedItem(), (String) cboSemester.getSelectedItem());
                service.saveReportToFile(fc.getSelectedFile(), content);
                JOptionPane.showMessageDialog(this, "Xuất file thành công!");
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage()); }
        }
    }
}