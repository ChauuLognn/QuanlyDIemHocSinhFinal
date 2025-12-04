package UI;

import AccountManager.Account;
import AccountManager.data.AccountDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkRemember;
    private JButton btnLogin;

    // Thêm biến để kết nối backend
    private AccountDatabase accountDB = AccountDatabase.getAccountDB();

    public Login() {
        setTitle("Student Management System");
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ... (Code giao diện như cũ)

        JPanel mainPanel = createMainPanel();
        add(mainPanel);
    }

    private JPanel createMainPanel() {
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
        btnClose.addActionListener(e -> System.exit(0));
        mainPanel.add(btnClose);

        // Tiêu đề
        JLabel lblTitle = new JLabel("LOGIN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(30, 70, 200, 40);
        mainPanel.add(lblTitle);

        // Username
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        lblUsername.setForeground(Color.WHITE);
        lblUsername.setBounds(30, 160, 100, 20);
        mainPanel.add(lblUsername);

        JPanel usernamePanel = createInputPanel();
        usernamePanel.setBounds(30, 185, 340, 45);

        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        userIcon.setForeground(Color.WHITE);
        userIcon.setBounds(10, 10, 30, 25);
        usernamePanel.add(userIcon);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsername.setForeground(Color.WHITE);
        txtUsername.setBackground(new Color(0,0,0,0));
        txtUsername.setOpaque(false);
        txtUsername.setCaretColor(Color.WHITE);
        txtUsername.setBorder(BorderFactory.createEmptyBorder());
        txtUsername.setBounds(45, 10, 280, 25);
        usernamePanel.add(txtUsername);
        mainPanel.add(usernamePanel);

        // Password
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setBounds(30, 250, 100, 20);
        mainPanel.add(lblPassword);

        JPanel passwordPanel = createInputPanel();
        passwordPanel.setBounds(30, 275, 340, 45);

        JLabel lockIcon = new JLabel("🔒");
        lockIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lockIcon.setForeground(Color.WHITE);
        lockIcon.setBounds(10, 10, 30, 25);
        passwordPanel.add(lockIcon);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setBackground(new Color(0,0,0,0));
        txtPassword.setOpaque(false);
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createEmptyBorder());
        txtPassword.setEchoChar('●');
        txtPassword.setBounds(45, 10, 280, 25);
        passwordPanel.add(txtPassword);
        mainPanel.add(passwordPanel);

        // Remember checkbox
        chkRemember = new JCheckBox("Save Login");
        chkRemember.setFont(new Font("Arial", Font.PLAIN, 12));
        chkRemember.setForeground(Color.WHITE);
        chkRemember.setBackground(new Color(0, 0, 0, 0));
        chkRemember.setOpaque(false);
        chkRemember.setBounds(30, 335, 120, 25);
        chkRemember.setFocusPainted(false);
        mainPanel.add(chkRemember);

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

        // *** QUAN TRỌNG: Kết nối sự kiện với backend ***
        btnLogin.addActionListener(e -> handleLogin());

        mainPanel.add(btnLogin);

        // Link đăng ký
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

        return mainPanel;
    }

    // *** PHƯƠNG THỨC QUAN TRỌNG: KẾT NỐI VỚI BACKEND ***
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Kiểm tra rỗng
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ thông tin!",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Hash mật khẩu để so sánh (vì password trong DB đã được hash)
        String hashedPassword = accountDB.hashSHA256(password);

        // Kiểm tra đăng nhập với backend
        Account account = accountDB.findAccountByUsername(username);

        if (account == null) {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập không tồn tại!",
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // So sánh mật khẩu đã hash
        if (!account.getPassword().equals(hashedPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu không đúng!",
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Đăng nhập thành công
        JOptionPane.showMessageDialog(this,
                "Đăng nhập thành công!\nChào mừng, " + username,
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);

        // Chuyển sang Dashboard
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            new Dashboard().setVisible(true);
        });
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

    public static void main(String[] args) {
        // Thêm dữ liệu mẫu vào hệ thống
        AccountDatabase accountDB = AccountDatabase.getAccountDB();

        // Tạo tài khoản admin (password: admin123)
        String hashedAdminPass = accountDB.hashSHA256("admin123");
        accountDB.addAccount("admin", hashedAdminPass, "ADMIN001");

        // Tạo tài khoản giáo viên
        String hashedTeacherPass = accountDB.hashSHA256("teacher123");
        accountDB.addAccount("giaovien", hashedTeacherPass, "GV001");

        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}