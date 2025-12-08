package UI;

import AccountManager.Account;
import AccountManager.service.EditAccount;
import AccountManager.service.DeleteAccount; // Import service xóa

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class Settings extends JFrame {
    private Account currentAccount;

    // --- COLORS & FONTS ---
    private final Color primaryColor = Color.decode("#1E40AF");
    private final Color bgColor      = Color.decode("#F3F4F6");
    private final Color cardColor    = Color.WHITE;
    private final Color textColor    = Color.decode("#111827");
    private final Color grayText     = Color.decode("#6B7280");
    private final Color lineColor    = Color.decode("#E5E7EB");
    private final Color dangerColor  = Color.decode("#EF4444"); // Màu đỏ cho nút xóa

    private final Font fontTitle = new Font("Segoe UI", Font.BOLD, 18);
    private final Font fontLabel = new Font("Segoe UI", Font.BOLD, 13);
    private final Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);

    // Components
    private JPasswordField txtOldPass, txtNewPass, txtConfirmPass;
    private JButton btnSave, btnDeleteAccount;

    public Settings(Account account) {
        this.currentAccount = account;

        setTitle("Cài đặt hệ thống");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Top Bar
        add(createTopBar(), BorderLayout.NORTH);

        // 2. Main Content
        JPanel mainPanel = new JPanel(new BorderLayout(30, 0));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Trái: Thông tin
        mainPanel.add(createInfoPanel(), BorderLayout.WEST);

        // Phải: Form đổi mật khẩu & Xóa tài khoản
        mainPanel.add(createActionPanel(), BorderLayout.CENTER);

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

    // ================= LEFT: INFO PANEL =================
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cardColor);
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(lineColor),
                new EmptyBorder(30, 30, 30, 30)
        ));

        // Avatar
        JLabel lblAvatar = new JLabel("👤");
        lblAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblAvatar);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Username
        JLabel lblUser = new JLabel(currentAccount.getUsername());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblUser.setForeground(textColor);
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblUser);

        // Role Badge
        JLabel lblRole = new JLabel(getRoleName());
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRole.setForeground(primaryColor);
        lblRole.setBackground(Color.decode("#DBEAFE"));
        lblRole.setOpaque(true);
        lblRole.setBorder(new EmptyBorder(5, 15, 5, 15));
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel badgePanel = new JPanel();
        badgePanel.setBackground(cardColor);
        badgePanel.add(lblRole);
        panel.add(badgePanel);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private String getRoleName() {
        String r = currentAccount.getRole();
        if ("admin".equals(r)) return "QUẢN TRỊ VIÊN";
        if ("teacher".equals(r)) return "GIÁO VIÊN";
        return "HỌC SINH";
    }

    // ================= RIGHT: ACTION PANEL (PASS + DELETE) =================
    private JPanel createActionPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(bgColor);

        // 1. Panel Đổi Mật Khẩu
        JPanel passPanel = new JPanel();
        passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.Y_AXIS));
        passPanel.setBackground(cardColor);
        passPanel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(lineColor),
                new EmptyBorder(30, 40, 30, 40)
        ));

        JLabel lblHeader = new JLabel("Đổi mật khẩu");
        lblHeader.setFont(fontTitle);
        lblHeader.setForeground(textColor);
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        passPanel.add(lblHeader);
        passPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        passPanel.add(createLabel("Mật khẩu hiện tại"));
        txtOldPass = createPasswordField();
        passPanel.add(txtOldPass);
        passPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        passPanel.add(createLabel("Mật khẩu mới"));
        txtNewPass = createPasswordField();
        passPanel.add(txtNewPass);
        passPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        passPanel.add(createLabel("Xác nhận mật khẩu mới"));
        txtConfirmPass = createPasswordField();
        passPanel.add(txtConfirmPass);

        JCheckBox chkShow = new JCheckBox("Hiện mật khẩu");
        chkShow.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkShow.setBackground(cardColor);
        chkShow.setFocusPainted(false);
        chkShow.addActionListener(e -> {
            char echo = chkShow.isSelected() ? (char)0 : '●';
            txtOldPass.setEchoChar(echo);
            txtNewPass.setEchoChar(echo);
            txtConfirmPass.setEchoChar(echo);
        });
        passPanel.add(chkShow);
        passPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        btnSave = new JButton("Lưu thay đổi");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(primaryColor);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setMaximumSize(new Dimension(150, 40));
        btnSave.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSave.addActionListener(e -> handleChangePassword());
        passPanel.add(btnSave);

        container.add(passPanel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Panel Xóa Tài Khoản (Danger Zone)
        JPanel deletePanel = new JPanel(new BorderLayout());
        deletePanel.setBackground(new Color(254, 242, 242)); // Đỏ rất nhạt
        deletePanel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(252, 165, 165)), // Viền đỏ nhạt
                new EmptyBorder(20, 25, 20, 25)
        ));
        deletePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel lblDanger = new JLabel("Xóa tài khoản");
        lblDanger.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblDanger.setForeground(dangerColor);


        btnDeleteAccount = new JButton("Xóa tài khoản");
        btnDeleteAccount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDeleteAccount.setForeground(Color.WHITE);
        btnDeleteAccount.setBackground(dangerColor);
        btnDeleteAccount.setFocusPainted(false);
        btnDeleteAccount.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDeleteAccount.addActionListener(e -> handleDeleteAccount());

        deletePanel.add(btnDeleteAccount, BorderLayout.EAST);

        container.add(deletePanel);

        return container;
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
        pf.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, lineColor),
                new EmptyBorder(5, 0, 5, 0)
        ));
        pf.setBackground(cardColor);

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

    // ================= LOGIC XỬ LÝ =================

    // 1. Đổi mật khẩu
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
        if (newPass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu phải từ 6 ký tự trở lên!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            new EditAccount().changePassword(currentAccount.getUsername(), newPass);
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!\nVui lòng đăng nhập lại.");
            this.dispose();
            new Login().setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 2. Xóa tài khoản
    private void handleDeleteAccount() {
        // Cảnh báo lần 1
        int confirm1 = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa tài khoản này không?\nHành động này KHÔNG THỂ hoàn tác!",
                "Cảnh báo nguy hiểm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm1 == JOptionPane.YES_OPTION) {
            // Cảnh báo lần 2 (cho chắc chắn)
            String input = JOptionPane.showInputDialog(this,
                    "Nhập chữ 'DELETE' để xác nhận xóa:");

            if (input != null && input.equals("DELETE")) {
                try {
                    new DeleteAccount().delete(currentAccount.getUsername());
                    JOptionPane.showMessageDialog(this, "Tài khoản đã bị xóa vĩnh viễn.");
                    this.dispose();
                    new Login().setVisible(true);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else if (input != null) {
                JOptionPane.showMessageDialog(this, "Mã xác nhận không đúng. Hủy bỏ xóa.");
            }
        }
    }

    public static void main(String[] args) {
        Account mock = new Account("admin", "", "", "admin");
        SwingUtilities.invokeLater(() -> new Settings(mock).setVisible(true));
    }
}