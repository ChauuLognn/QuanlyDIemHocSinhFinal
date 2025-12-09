package UI;

import AccountManager.service.AddAccount;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Register extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JButton btnRegister;

    private final Color primaryColor = Color.decode("#1E40AF");
    private final Color accentColor = Color.decode("#3B82F6");
    private final Color grayText = Color.decode("#6B7280");

    public Register() {
        setTitle("Đăng ký tài khoản");
        setSize(450, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(null);
        setContentPane(mainPanel);

        // title
        JLabel lblTitle = new JLabel("ĐĂNG KÝ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(primaryColor);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, 40, 435, 40);
        mainPanel.add(lblTitle);

        JLabel lblSub = new JLabel("Tạo tài khoản mới để sử dụng hệ thống");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(grayText);
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        lblSub.setBounds(0, 80, 435, 20);
        mainPanel.add(lblSub);

        // username
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(grayText);
        lblUser.setBounds(40, 130, 200, 20);
        mainPanel.add(lblUser);

        txtUsername = new JTextField();
        styleTextField(txtUsername);
        txtUsername.setBounds(40, 155, 355, 35);
        mainPanel.add(txtUsername);

        // password
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(grayText);
        lblPass.setBounds(40, 210, 100, 20);
        mainPanel.add(lblPass);

        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        txtPassword.setBounds(40, 235, 355, 35);
        mainPanel.add(txtPassword);

        // confirm password
        JLabel lblConfirm = new JLabel("Xác nhận mật khẩu");
        lblConfirm.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblConfirm.setForeground(grayText);
        lblConfirm.setBounds(40, 290, 200, 20);
        mainPanel.add(lblConfirm);

        txtConfirmPassword = new JPasswordField();
        styleTextField(txtConfirmPassword);
        txtConfirmPassword.setBounds(40, 315, 355, 35);
        mainPanel.add(txtConfirmPassword);

        // show password
        JLabel lblShowPass = new JLabel("Hiện mật khẩu");
        lblShowPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblShowPass.setForeground(accentColor);
        lblShowPass.setBounds(310, 360, 90, 20);
        lblShowPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblShowPass.addMouseListener(new MouseAdapter() {
            boolean showing = false;
            public void mouseClicked(MouseEvent e) {
                showing = !showing;
                char echo = showing ? (char)0 : '●';
                txtPassword.setEchoChar(echo);
                txtConfirmPassword.setEchoChar(echo);
                lblShowPass.setText(showing ? "Ẩn mật khẩu" : "Hiện mật khẩu");
            }
        });
        mainPanel.add(lblShowPass);

        // button register
        btnRegister = new JButton("ĐĂNG KÝ TÀI KHOẢN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(accentColor);
                } else {
                    g2.setColor(primaryColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnRegister.setBounds(40, 410, 355, 45);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.addActionListener(e -> handleRegister());
        mainPanel.add(btnRegister);

        // login link
        JLabel lblLogin = new JLabel("Đã có tài khoản?");
        lblLogin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLogin.setForeground(grayText);
        lblLogin.setBounds(110, 470, 100, 20);
        mainPanel.add(lblLogin);

        JLabel lblLoginLink = new JLabel("Đăng nhập ngay");
        lblLoginLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLoginLink.setForeground(primaryColor);
        lblLoginLink.setBounds(210, 470, 120, 20);
        lblLoginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLoginLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Register.this.dispose();
                new Login().setVisible(true);
            }
        });
        mainPanel.add(lblLoginLink);

        getRootPane().setDefaultButton(btnRegister);
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setForeground(Color.BLACK);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        tf.setBackground(Color.WHITE);

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

    private void handleRegister() {
        try {
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword());
            String confirm = new String(txtConfirmPassword.getPassword());
            String id = user;

            AddAccount service = new AddAccount();
            service.addAccount(user, pass, confirm, id);

            JOptionPane.showMessageDialog(this, "Đăng ký thành công! Hãy đăng nhập.");
            this.dispose();
            new Login().setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đăng ký", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Register().setVisible(true));
    }
}