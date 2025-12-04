package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import ReportManager.service.ReportService;

public class ReportManagement extends JFrame {
    // Colors
    private final Color primaryColor = new Color(70, 70, 70);
    private final Color secondaryColor = new Color(245, 245, 245);

    // Components
    private JComboBox<String> cboReportType, cboClass, cboSemester;
    private JTextArea txtPreview;
    private JButton btnPreview, btnExport;

    public ReportManagement() {
        setTitle("Xuất Báo Cáo & Thống Kê");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Top Bar
        add(createTopBar(), BorderLayout.NORTH);

        // 2. Left Configuration Panel
        add(createConfigPanel(), BorderLayout.WEST);

        // 3. Center Preview Panel
        add(createPreviewPanel(), BorderLayout.CENTER);
    }

    // ================= TOP BAR =================
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(primaryColor);
        topBar.setPreferredSize(new Dimension(0, 60));
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("BÁO CÁO & IN ẤN");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        topBar.add(title, BorderLayout.WEST);

        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Arial", Font.BOLD, 12));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(100, 100, 100));
        btnBack.setOpaque(true);
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> backToDashboard());
        // Hover
        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnBack.setBackground(new Color(120, 120, 120)); }
            public void mouseExited(MouseEvent e) { btnBack.setBackground(new Color(100, 100, 100)); }
        });

        topBar.add(btnBack, BorderLayout.EAST);
        return topBar;
    }

    // ================= LEFT CONFIG PANEL =================
    private JPanel createConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Cấu hình báo cáo");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(primaryColor);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblTitle);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // 1. Loại báo cáo
        content.add(createLabel("Loại báo cáo:"));
        String[] types = {
                "Danh sách lớp (Class List)",
                "Bảng điểm tổng kết (Score Summary)",
                "Danh sách học sinh giỏi",
                "Phiếu liên lạc cá nhân"
        };
        cboReportType = new JComboBox<>(types);
        styleComboBox(cboReportType);
        content.add(cboReportType);
        content.add(Box.createRigidArea(new Dimension(0, 15)));

        // 2. Chọn lớp
        content.add(createLabel("Chọn lớp:"));
        cboClass = new JComboBox<>(new String[]{"Tất cả", "6A1", "7A2", "8A1", "9A3"});
        styleComboBox(cboClass);
        content.add(cboClass);
        content.add(Box.createRigidArea(new Dimension(0, 15)));

        // 3. Học kỳ
        content.add(createLabel("Học kỳ/Năm học:"));
        cboSemester = new JComboBox<>(new String[]{"Học kỳ 1 - 2023", "Học kỳ 2 - 2023", "Cả năm"});
        styleComboBox(cboSemester);
        content.add(cboSemester);
        content.add(Box.createRigidArea(new Dimension(0, 30)));

        // Buttons
        btnPreview = createButton("Xem trước (Preview)", new Color(0, 123, 255));
        btnPreview.addActionListener(e -> generateReportPreview());
        content.add(btnPreview);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        btnExport = createButton("Xuất file (.txt)", new Color(40, 167, 69));
        btnExport.addActionListener(e -> exportToFile());
        content.add(btnExport);

        panel.add(content);
        return panel;
    }

    // ================= CENTER PREVIEW PANEL =================
    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(secondaryColor);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblPreview = new JLabel("Bản xem trước:");
        lblPreview.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblPreview, BorderLayout.NORTH);

        // TextArea giả lập trang giấy
        txtPreview = new JTextArea();
        txtPreview.setFont(new Font("Monospaced", Font.PLAIN, 13)); // Dùng font đơn cách để căn lề đẹp
        txtPreview.setEditable(false);
        txtPreview.setMargin(new Insets(20, 40, 20, 40)); // Lề trang giấy
        txtPreview.setText("Vui lòng chọn cấu hình bên trái và bấm 'Xem trước'...");

        JScrollPane scroll = new JScrollPane(txtPreview);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ================= HELPER METHODS =================
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void styleComboBox(JComboBox box) {
        box.setFont(new Font("Arial", Font.PLAIN, 13));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.setBackground(Color.WHITE);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(color.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(color); }
        });
        return btn;
    }

    // ================= BUSINESS LOGIC =================


    // 1. Tạo nội dung báo cáo
    private void generateReportPreview() {
        String type = (String) cboReportType.getSelectedItem();
        String className = (String) cboClass.getSelectedItem();
        String semester = (String) cboSemester.getSelectedItem();

        ReportService service = new ReportService();
        // UI chỉ việc hiển thị, không cần biết chuỗi được cộng trừ thế nào
        String content = service.generateReportContent(type, className, semester);

        txtPreview.setText(content);
        txtPreview.setCaretPosition(0);
    }

    // 2. Xuất file ra máy tính
    private void exportToFile() {
        if (txtPreview.getText().isEmpty() || txtPreview.getText().startsWith("Vui lòng")) {
            JOptionPane.showMessageDialog(this, "Hãy bấm 'Xem trước' để tạo dữ liệu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file báo cáo");
        fileChooser.setSelectedFile(new File("BaoCao_" + System.currentTimeMillis() + ".txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ReportService service = new ReportService();
                service.saveReportToFile(fileChooser.getSelectedFile(), txtPreview.getText());

                JOptionPane.showMessageDialog(this, "Xuất file thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    private void backToDashboard() {
        this.dispose();
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReportManagement().setVisible(true));
    }
}
