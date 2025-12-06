package UI;

import AccountManager.Account;
import AccountManager.service.AuthService;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login extends JFrame {
    // Components
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cboRole;
    private JButton btnLogin;

    // Colors (Màu sắc)
    private final Color primaryColor = Color.decode("#1E40AF"); // Xanh đậm
    private final Color accentColor  = Color.decode("#3B82F6"); // Xanh sáng (Hover)
    private final Color grayText     = Color.decode("#6B7280"); // Xám chữ

    public Login() {
        setTitle("Đăng nhập hệ thống");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel nền trắng toàn bộ
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(null);
        setContentPane(mainPanel);

        // --- TIÊU ĐỀ (Màu Xanh Đậm) ---
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(primaryColor);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, 50, 435, 40);
        mainPanel.add(lblTitle);

        JLabel lblSub = new JLabel("Hệ thống Quản lý Học sinh");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(grayText);
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        lblSub.setBounds(0, 90, 435, 20);
        mainPanel.add(lblSub);

        // --- 1. CHỌN VAI TRÒ ---
        JLabel lblRole = new JLabel("Vai trò");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRole.setForeground(grayText);
        lblRole.setBounds(40, 140, 100, 20);
        mainPanel.add(lblRole);

        cboRole = new JComboBox<>(new String[]{"Học sinh", "Giáo viên", "Quản trị viên"});
        cboRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboRole.setBackground(Color.WHITE);
        cboRole.setBounds(40, 165, 355, 40);
        cboRole.setFocusable(false);
        mainPanel.add(cboRole);

        // --- 2. USERNAME ---
        JLabel lblUser = new JLabel("Tên đăng nhập / Mã SV");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(grayText);
        lblUser.setBounds(40, 220, 200, 20);
        mainPanel.add(lblUser);

        txtUsername = new JTextField();
        styleTextField(txtUsername);
        txtUsername.setBounds(40, 245, 355, 40);
        mainPanel.add(txtUsername);

        // --- 3. PASSWORD ---
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(grayText);
        lblPass.setBounds(40, 305, 100, 20);
        mainPanel.add(lblPass);

        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        txtPassword.setBounds(40, 330, 355, 40);
        mainPanel.add(txtPassword);

        // Hiện mật khẩu
        JLabel lblShowPass = new JLabel("Hiện mật khẩu");
        lblShowPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblShowPass.setForeground(accentColor);
        lblShowPass.setBounds(310, 375, 90, 20);
        lblShowPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblShowPass.addMouseListener(new MouseAdapter() {
            boolean showing = false;
            public void mouseClicked(MouseEvent e) {
                showing = !showing;
                txtPassword.setEchoChar(showing ? (char)0 : '●');
                lblShowPass.setText(showing ? "Ẩn mật khẩu" : "Hiện mật khẩu");
            }
        });
        mainPanel.add(lblShowPass);

        // --- 4. NÚT ĐĂNG NHẬP (CUSTOM PAINT) ---
        // Tự vẽ nút để đảm bảo màu nền hiện đúng trên mọi Windows
        btnLogin = new JButton("ĐĂNG NHẬP") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Đổi màu khi di chuột
                if (getModel().isRollover()) {
                    g2.setColor(accentColor);
                } else {
                    g2.setColor(primaryColor);
                }

                // Vẽ hình chữ nhật bo tròn (Radius 40)
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.dispose();

                super.paintComponent(g);
            }
        };
        btnLogin.setBounds(40, 420, 355, 45);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE); // Chữ màu trắng

        // Xóa các hiệu ứng mặc định gây lỗi hiển thị
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin.addActionListener(e -> handleLogin());
        mainPanel.add(btnLogin);

        // Link Đăng ký
        JLabel lblReg = new JLabel("Chưa có tài khoản?");
        lblReg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblReg.setForeground(grayText);
        lblReg.setBounds(110, 480, 120, 20);
        mainPanel.add(lblReg);

        JLabel lblRegLink = new JLabel("Đăng ký ngay");
        lblRegLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRegLink.setForeground(primaryColor);
        lblRegLink.setBounds(220, 480, 100, 20);
        lblRegLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblRegLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Login.this.dispose();
                new Register().setVisible(true);
            }
        });
        mainPanel.add(lblRegLink);

        getRootPane().setDefaultButton(btnLogin);
    }

    // ================= HELPER: LÀM ĐẸP TEXT FIELD =================
    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setForeground(Color.BLACK);
        // Viền dưới màu xám nhạt
        tf.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        tf.setBackground(Color.WHITE);

        // Khi click vào thì viền đổi màu xanh
        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 2, 0, accentColor),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 2, 0, new Color(220, 220, 220)),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        });
    }

    // ================= LOGIC ĐĂNG NHẬP =================
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role = cboRole.getSelectedItem().toString();

        AuthService authService = new AuthService();

        try {
            Account acc = authService.login(username, password, role);

            this.dispose();
            JOptionPane.showMessageDialog(null, "Đăng nhập thành công!");

            // Mở Dashboard
            new Dashboard().setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Sử dụng giao diện hệ thống cho đẹp
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}