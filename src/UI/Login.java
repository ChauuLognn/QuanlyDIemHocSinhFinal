package UI;

import AccountManager.data.AccountDatabase; // <--- Import Database

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkRemember;
    private JButton btnLogin;
    private JLabel lblForgotPassword;
    private boolean passwordVisible = false;

    public Login() {
        setTitle("Student Management System");
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 1. MAIN PANEL
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                Color color1 = new Color(70, 70, 70);
                Color color2 = new Color(130, 130, 130);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);

        // Nút đóng
        JButton btnClose = createIconButton("×", 350, 10, 30, 30);
        btnClose.setFont(new Font("Arial", Font.BOLD, 24));
        btnClose.addActionListener(e -> System.exit(0));
        mainPanel.add(btnClose);

        // Tiêu đề
        JLabel lblTitle = new JLabel("LOGIN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(30, 70, 200, 40);
        mainPanel.add(lblTitle);

        // Input Username
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        lblUsername.setForeground(Color.WHITE);
        lblUsername.setBounds(30, 160, 100, 20);
        mainPanel.add(lblUsername);

        JPanel usernamePanel = createInputPanel();
        usernamePanel.setBounds(30, 185, 340, 45);
        JLabel userIcon = new JLabel("👤");
        userIcon.setForeground(Color.WHITE);
        userIcon.setBounds(10, 10, 30, 25);
        usernamePanel.add(userIcon);

        txtUsername = new JTextField("");
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsername.setForeground(Color.WHITE);
        txtUsername.setBackground(new Color(0,0,0,0));
        txtUsername.setOpaque(false);
        txtUsername.setCaretColor(Color.WHITE);
        txtUsername.setBorder(BorderFactory.createEmptyBorder());
        txtUsername.setBounds(45, 10, 280, 25);
        usernamePanel.add(txtUsername);
        mainPanel.add(usernamePanel);

        // Input Password
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setBounds(30, 250, 100, 20);
        mainPanel.add(lblPassword);

        JPanel passwordPanel = createInputPanel();
        passwordPanel.setBounds(30, 275, 340, 45);
        JLabel lockIcon = new JLabel("🔒");
        lockIcon.setForeground(Color.WHITE);
        lockIcon.setBounds(10, 10, 30, 25);
        passwordPanel.add(lockIcon);

        txtPassword = new JPasswordField("");
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setBackground(new Color(0,0,0,0));
        txtPassword.setOpaque(false);
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createEmptyBorder());
        txtPassword.setEchoChar('●');
        txtPassword.setBounds(45, 10, 245, 25);
        passwordPanel.add(txtPassword);

        JLabel eyeIcon = new JLabel("👁");
        eyeIcon.setForeground(Color.WHITE);
        eyeIcon.setBounds(300, 10, 30, 25);
        eyeIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eyeIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                passwordVisible = !passwordVisible;
                txtPassword.setEchoChar(passwordVisible ? (char) 0 : '●');
            }
        });
        passwordPanel.add(eyeIcon);
        mainPanel.add(passwordPanel);

        // Checkbox & Forgot Pass
        chkRemember = new JCheckBox("Save Login");
        chkRemember.setFont(new Font("Arial", Font.PLAIN, 12));
        chkRemember.setForeground(Color.WHITE);
        chkRemember.setOpaque(false);
        chkRemember.setBounds(30, 335, 120, 25);
        chkRemember.setFocusPainted(false);
        mainPanel.add(chkRemember);

        lblForgotPassword = new JLabel("Forgot Password?");
        lblForgotPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        lblForgotPassword.setForeground(Color.WHITE);
        lblForgotPassword.setBounds(245, 335, 120, 25);
        lblForgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(lblForgotPassword);

        // Button Login
        btnLogin = new JButton("LOGIN") {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(new Color(255, 255, 255, 40));
        btnLogin.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        btnLogin.setBounds(105, 385, 180, 45);
        btnLogin.setFocusPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setOpaque(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> handleLogin()); // Gọi hàm xử lý
        mainPanel.add(btnLogin);

        // Link Register
        JLabel lblRegister = new JLabel("<html>Don't have an account? <u>Sign Up</u></html>");
        lblRegister.setFont(new Font("Arial", Font.PLAIN, 12));
        lblRegister.setForeground(Color.WHITE);
        lblRegister.setBounds(0, 450, 400, 30);
        lblRegister.setHorizontalAlignment(SwingConstants.CENTER);
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Login.this.dispose();
                new Register().setVisible(true);
            }
        });
        mainPanel.add(lblRegister);

        add(mainPanel);
        getRootPane().setDefaultButton(btnLogin);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(null);
        panel.setBackground(new Color(255, 255, 255, 30));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return panel;
    }

    private JButton createIconButton(String text, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0,0,0,0));
        btn.setBorder(null);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ================= LOGIC ĐĂNG NHẬP MỚI =================
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        // 1. Gọi Database kiểm tra
        AccountDatabase db = AccountDatabase.getAccountDB();
        boolean isValid = db.checkLogin(username, password);

        if (isValid) {
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
            this.dispose();
            SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
        } else {
            JOptionPane.showMessageDialog(this,
                    "Sai tên đăng nhập hoặc mật khẩu!\n(Hãy chắc chắn bạn đã Đăng ký trước)",
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}