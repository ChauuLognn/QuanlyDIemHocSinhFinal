package UI;

import AccountManager.Account;
import AccountManager.service.EditAccount;
import AccountManager.service.DeleteAccount;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Settings extends JFrame {
    private Account currentAccount;

    // --- COLORS & FONTS ---
    private final Color primaryColor = Color.decode("#1E40AF"); // Xanh đậm
    private final Color bgColor      = Color.decode("#F3F4F6"); // Xám nền
    private final Color cardColor    = Color.WHITE;             // Trắng
    private final Color textColor    = Color.decode("#111827"); // Đen chữ
    private final Color grayText     = Color.decode("#6B7280"); // Xám chữ
    private final Color lineColor    = Color.decode("#E5E7EB"); // Viền
    private final Color dangerColor  = Color.decode("#EF4444"); // Đỏ

    private final Font fontTitle = new Font("Segoe UI", Font.BOLD, 16);
    private final Font fontLabel = new Font("Segoe UI", Font.BOLD, 13);
    private final Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);

    // Components
    private JPasswordField txtOldPass, txtNewPass, txtConfirmPass;

    public Settings(Account account) {
        this.currentAccount = account;

        setTitle("Cài đặt tài khoản");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Top Bar
        add(createTopBar(), BorderLayout.NORTH);

        // 2. Main Content (Chia 2 cột)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 30, 0)); // 1 hàng, 2 cột, cách nhau 30px
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Cột Trái: Profile
        mainPanel.add(createProfilePanel());

        // Cột Phải: Đổi mật khẩu & Xóa
        mainPanel.add(createSecurityPanel());

        add(mainPanel, BorderLayout.CENTER);
    }

    // ================= 1. TOP BAR =================
    private JPanel createTopBar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBackground(cardColor);
        navbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, lineColor));

        JLabel title = new JLabel("  CÀI ĐẶT HỆ THỐNG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(primaryColor);
        title.setBorder(new EmptyBorder(0, 15, 0, 0));
        navbar.add(title, BorderLayout.WEST);

        // Nút Quay lại
        JButton btnBack = new JButton("← Dashboard");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setForeground(grayText);
        btnBack.setBackground(cardColor);
        btnBack.setBorder(new EmptyBorder(0, 15, 0, 20));
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

    // ================= 2. LEFT PANEL (PROFILE) =================
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cardColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(lineColor),
                new EmptyBorder(30, 20, 30, 20)
        ));

        // Avatar Icon
        JLabel lblAvatar = new JLabel("👤", JLabel.CENTER);
        lblAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 100));
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username
        JLabel lblUser = new JLabel(currentAccount.getUsername(), JLabel.CENTER);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblUser.setForeground(textColor);
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Role Badge
        JLabel lblRole = new JLabel(getRoleName(), JLabel.CENTER);
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRole.setForeground(Color.WHITE);
        lblRole.setBackground(primaryColor);
        lblRole.setOpaque(true);
        lblRole.setBorder(new EmptyBorder(5, 15, 5, 15));
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components with spacing
        panel.add(Box.createVerticalGlue());
        panel.add(lblAvatar);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(lblUser);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(lblRole);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // ================= 3. RIGHT PANEL (SECURITY) =================
    private JPanel createSecurityPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cardColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(lineColor),
                new EmptyBorder(25, 30, 25, 30)
        ));

        // Header
        JLabel lblHead = new JLabel("ĐỔI MẬT KHẨU");
        lblHead.setFont(fontTitle);
        lblHead.setForeground(primaryColor);
        lblHead.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblHead);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Fields
        panel.add(createLabel("Mật khẩu hiện tại"));
        txtOldPass = createPasswordField();
        panel.add(txtOldPass);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createLabel("Mật khẩu mới"));
        txtNewPass = createPasswordField();
        panel.add(txtNewPass);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createLabel("Xác nhận mật khẩu"));
        txtConfirmPass = createPasswordField();
        panel.add(txtConfirmPass);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Button Save
        JButton btnSave = createButton("Lưu thay đổi", primaryColor);
        btnSave.addActionListener(e -> handleChangePassword());
        panel.add(btnSave);

        // Divider
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        JSeparator sep = new JSeparator();
        sep.setForeground(lineColor);
        panel.add(sep);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Delete Account Section
        JLabel lblDanger = new JLabel("");
        lblDanger.setFont(fontTitle);
        lblDanger.setForeground(dangerColor);
        lblDanger.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblDanger);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnDelete = createButton("Xóa tài khoản", dangerColor);
        btnDelete.addActionListener(e -> handleDeleteAccount());
        panel.add(btnDelete);

        return panel;
    }

    // ================= HELPER UI METHODS =================
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
        pf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(lineColor),
                new EmptyBorder(5, 10, 5, 10) // Padding trong
        ));
        return pf;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private String getRoleName() {
        String r = currentAccount.getRole();
        if ("admin".equals(r)) return "QUẢN TRỊ VIÊN";
        if ("teacher".equals(r)) return "GIÁO VIÊN";
        return "HỌC SINH";
    }

    // ================= LOGIC (GIỮ NGUYÊN) =================
    private void handleChangePassword() {
        String newPass = new String(txtNewPass.getPassword());
        String confirm = new String(txtConfirmPass.getPassword());

        if (newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu mới!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (newPass.length() < 3) { // Giảm xuống 3 cho dễ test, hoặc để 6 tùy bạn
            JOptionPane.showMessageDialog(this, "Mật khẩu quá ngắn!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            new EditAccount().changePassword(currentAccount.getUsername(), newPass);
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
            this.dispose();
            new Login().setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "CẢNH BÁO: Hành động này sẽ xóa vĩnh viễn tài khoản và không thể hoàn tác.\nBạn có chắc chắn không?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                new DeleteAccount().delete(currentAccount.getUsername());
                JOptionPane.showMessageDialog(this, "Tài khoản đã bị xóa.");
                this.dispose();
                new Login().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}