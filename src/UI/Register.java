package UI;

import AccountManager.service.AddAccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Register extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JButton btnRegister;
    private JLabel lblLoginLink;

    public Register() {
        setTitle("Student Management System - Register");
        setSize(400, 550); // Thu nhỏ chiều cao lại cho vừa vặn
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 1. MAIN PANEL: Gradient Xám Porsche
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

        // Nút đóng (X)
        JButton btnClose = new JButton("×");
        btnClose.setBounds(350, 10, 30, 30);
        btnClose.setFont(new Font("Arial", Font.BOLD, 24));
        btnClose.setForeground(Color.WHITE);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorder(null);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> System.exit(0));
        mainPanel.add(btnClose);

        // Tiêu đề REGISTER
        JLabel lblTitle = new JLabel("REGISTER");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(30, 50, 200, 40);
        mainPanel.add(lblTitle);

        // --- 1. USERNAME (Đã đẩy lên vị trí cũ của Full Name) ---
        JLabel lblUser = createLabel("Username", 110);
        mainPanel.add(lblUser);
        JPanel pnlUser = createInputPanel(135);
        JLabel iconUser = createIcon("👤", pnlUser);
        txtUsername = createTextField(pnlUser);
        mainPanel.add(pnlUser);

        // --- 2. PASSWORD ---
        JLabel lblPass = createLabel("Password", 200);
        mainPanel.add(lblPass);
        JPanel pnlPass = createInputPanel(225);
        JLabel iconPass = createIcon("🔒", pnlPass);
        txtPassword = createPasswordField(pnlPass);
        mainPanel.add(pnlPass);

        // --- 3. CONFIRM PASSWORD ---
        JLabel lblConfirm = createLabel("Confirm Password", 290);
        mainPanel.add(lblConfirm);
        JPanel pnlConfirm = createInputPanel(315);
        JLabel iconConfirm = createIcon("🔑", pnlConfirm);
        txtConfirmPassword = createPasswordField(pnlConfirm);
        mainPanel.add(pnlConfirm);

        // --- BUTTON REGISTER ---
        btnRegister = new JButton("CREATE ACCOUNT") {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setBackground(new Color(255, 255, 255, 40));
        btnRegister.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        // Đẩy nút lên vị trí mới
        btnRegister.setBounds(85, 400, 230, 45);
        btnRegister.setFocusPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setOpaque(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover
        btnRegister.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnRegister.setBackground(new Color(255, 255, 255, 80)); btnRegister.repaint(); }
            public void mouseExited(MouseEvent e) { btnRegister.setBackground(new Color(255, 255, 255, 40)); btnRegister.repaint(); }
        });

        // SỰ KIỆN BẤM NÚT ĐĂNG KÝ
        btnRegister.addActionListener(e -> handleRegister());

        mainPanel.add(btnRegister);

        // --- LINK BACK TO LOGIN ---
        lblLoginLink = new JLabel("<html>Already have an account? <u>Login here</u></html>");
        lblLoginLink.setFont(new Font("Arial", Font.PLAIN, 12));
        lblLoginLink.setForeground(Color.WHITE);
        // Đẩy link lên vị trí mới
        lblLoginLink.setBounds(0, 470, 400, 30);
        lblLoginLink.setHorizontalAlignment(SwingConstants.CENTER);
        lblLoginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblLoginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Register.this.dispose();
                new Login().setVisible(true);
            }
        });
        mainPanel.add(lblLoginLink);

        add(mainPanel);
    }

    // ================= HELPER METHODS =================
    private JLabel createLabel(String text, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(30, y, 150, 20);
        return lbl;
    }

    private JPanel createInputPanel(int y) {
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
        panel.setBounds(30, y, 340, 45);
        return panel;
    }

    private JLabel createIcon(String icon, JPanel parent) {
        JLabel lbl = new JLabel(icon);
        lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(10, 10, 30, 25);
        parent.add(lbl);
        return lbl;
    }

    private JTextField createTextField(JPanel parent) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Arial", Font.PLAIN, 14));
        tf.setForeground(Color.WHITE);
        tf.setBackground(new Color(0,0,0,0));
        tf.setOpaque(false);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createEmptyBorder());
        tf.setBounds(45, 10, 280, 25);
        parent.add(tf);
        return tf;
    }

    private JPasswordField createPasswordField(JPanel parent) {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Arial", Font.PLAIN, 14));
        pf.setForeground(Color.WHITE);
        pf.setBackground(new Color(0,0,0,0));
        pf.setOpaque(false);
        pf.setCaretColor(Color.WHITE);
        pf.setBorder(BorderFactory.createEmptyBorder());
        pf.setBounds(45, 10, 280, 25);
        parent.add(pf);
        return pf;
    }

    // ================= LOGIC XỬ LÝ =================
    private void handleRegister() {
        try {
            // 1. Lấy dữ liệu từ giao diện
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword());
            String confirm = new String(txtConfirmPassword.getPassword());

            // TỰ ĐỘNG GÁN ID = USERNAME (Vì code backend AddAccount vẫn cần ID)
            String id = user;

            // 2. Gọi Service để xử lý
            AddAccount service = new AddAccount();
            service.addNewAccount(user, pass, confirm, id);

            // 3. Nếu thành công
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