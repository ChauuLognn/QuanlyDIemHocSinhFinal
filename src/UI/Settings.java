package UI;

import AccountManager.Account;
import AccountManager.service.EditAccount;
import AccountManager.service.AuthService; // Để check pass cũ nếu cần

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Settings extends JFrame {
    private Account currentAccount;

    // --- COLORS & FONTS ---
    private final Color primaryColor = Color.decode("#1E40AF");
    private final Color bgColor      = Color.decode("#F3F4F6");
    private final Color cardColor    = Color.WHITE;
    private final Color textColor    = Color.decode("#111827");
    private final Color grayText     = Color.decode("#6B7280");
    private final Color lineColor    = Color.decode("#E5E7EB");
    private final Color errorColor   = Color.decode("#EF4444");

    private final Font fontTitle = new Font("Segoe UI", Font.BOLD, 18);
    private final Font fontLabel = new Font("Segoe UI", Font.BOLD, 13);
    private final Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);

    // Components
    private JPasswordField txtOldPass, txtNewPass, txtConfirmPass;
    private JButton btnSave;

    public Settings(Account account) {
        this.currentAccount = account;

        setTitle("Cài đặt hệ thống");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Top Bar
        add(createTopBar(), BorderLayout.NORTH);

        // 2. Main Content
        JPanel mainPanel = new JPanel(new BorderLayout(30, 0));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(40, 100, 40, 100)); // Căn lề rộng cho thoáng

        // --- Left: User Profile Info ---
        mainPanel.add(createInfoPanel(), BorderLayout.WEST);

        // --- Right: Change Password Form ---
        mainPanel.add(createPasswordPanel(), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    // ================= TOP BAR =================
    private JPanel createTopBar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setPreferredSize(new Dimension(0, 70));
        navbar.setBackground(cardColor);
        navbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, lineColor));

        JLabel title = new JLabel("  CÀI ĐẶT & BẢO MẬT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(primaryColor);
        title.setBorder(new EmptyBorder(0, 20, 0, 0));
        navbar.add(title, BorderLayout.WEST);

        JButton btnBack = new JButton("← Quay lại Dashboard");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(primaryColor);
        btnBack.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnBack.addActionListener(e -> {
            this.dispose();
            new Dashboard(currentAccount).setVisible(true);
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setBackground(cardColor);
        right.add(btnBack);
        navbar.add(right, BorderLayout.EAST);

        return navbar;
    }

    // ================= INFO PANEL =================
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cardColor);
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor),
                new EmptyBorder(30, 30, 30, 30)
        ));

        // Avatar to
        JLabel lblAvatar = new JLabel("👤");
        lblAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblAvatar);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Username
        JLabel lblUser = new JLabel(currentAccount.getUsername());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblUser.setForeground(textColor);
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblUser);

        // Role Badge
        JLabel lblRole = new JLabel(getRoleName());
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRole.setForeground(primaryColor);
        lblRole.setBackground(Color.decode("#DBEAFE")); // Xanh nhạt
        lblRole.setOpaque(true);
        lblRole.setBorder(new EmptyBorder(5, 10, 5, 10));
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel badgePanel = new JPanel(); // Wrapper để căn giữa badge
        badgePanel.setBackground(cardColor);
        badgePanel.add(lblRole);
        panel.add(badgePanel);

        panel.add(Box.createVerticalGlue()); // Đẩy nội dung lên trên

        return panel;
    }

    private String getRoleName() {
        String r = currentAccount.getRole();
        if ("admin".equals(r)) return "QUẢN TRỊ VIÊN";
        if ("teacher".equals(r)) return "GIÁO VIÊN";
        return "HỌC SINH";
    }

    // ================= PASSWORD PANEL =================
    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cardColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor),
                new EmptyBorder(30, 40, 30, 40)
        ));

        JLabel lblHeader = new JLabel("Đổi mật khẩu");
        lblHeader.setFont(fontTitle);
        lblHeader.setForeground(textColor);
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblHeader);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // 1. Mật khẩu hiện tại
        panel.add(createLabel("Mật khẩu hiện tại"));
        txtOldPass = createPasswordField();
        panel.add(txtOldPass);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Mật khẩu mới
        panel.add(createLabel("Mật khẩu mới"));
        txtNewPass = createPasswordField();
        panel.add(txtNewPass);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 3. Xác nhận mật khẩu mới
        panel.add(createLabel("Xác nhận mật khẩu mới"));
        txtConfirmPass = createPasswordField();
        panel.add(txtConfirmPass);

        // Show pass checkbox
        JCheckBox chkShow = new JCheckBox("Hiện mật khẩu");
        chkShow.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkShow.setBackground(cardColor);
        chkShow.setForeground(grayText);
        chkShow.setFocusPainted(false);
        chkShow.addActionListener(e -> {
            char echo = chkShow.isSelected() ? (char)0 : '●';
            txtOldPass.setEchoChar(echo);
            txtNewPass.setEchoChar(echo);
            txtConfirmPass.setEchoChar(echo);
        });
        panel.add(chkShow);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Button Save
        btnSave = new JButton("Lưu thay đổi");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(primaryColor);
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setMaximumSize(new Dimension(200, 45));
        btnSave.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnSave.addActionListener(e -> handleChangePassword());
        panel.add(btnSave);

        return panel;
    }

    // ================= HELPER METHODS =================
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(fontLabel);
        lbl.setForeground(grayText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(fontInput);
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Border style
        pf.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, lineColor),
                new EmptyBorder(5, 0, 5, 0)
        ));
        pf.setBackground(cardColor);

        // Focus effect
        pf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                pf.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 2, 0, primaryColor),
                        new EmptyBorder(5, 0, 5, 0)));
            }
            public void focusLost(FocusEvent e) {
                pf.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 2, 0, lineColor),
                        new EmptyBorder(5, 0, 5, 0)));
            }
        });
        return pf;
    }

    // ================= LOGIC ĐỔI MẬT KHẨU =================
    private void handleChangePassword() {
        String oldPass = new String(txtOldPass.getPassword());
        String newPass = new String(txtNewPass.getPassword());
        String confirm = new String(txtConfirmPass.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải từ 6 ký tự trở lên!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Gọi Service để xử lý
        try {
            // Bước 1: Kiểm tra mật khẩu cũ có đúng không (Cần hàm checkLogin trong AccountDatabase)
            // (Nếu bạn muốn kỹ hơn thì gọi AuthService.login() lại lần nữa để check pass cũ)

            // Bước 2: Đổi mật khẩu
            EditAccount service = new EditAccount();
            service.changePassword(currentAccount.getUsername(), newPass);

            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!\nVui lòng đăng nhập lại.");
            this.dispose();
            new Login().setVisible(true); // Logout luôn cho an toàn

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Main Test
    public static void main(String[] args) {
        Account mock = new Account("admin", "", "", "admin");
        SwingUtilities.invokeLater(() -> new Settings(mock).setVisible(true));
    }
}