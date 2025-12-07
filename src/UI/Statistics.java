package UI;

import AccountManager.Account;
import Database.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.sql.*;
import java.text.DecimalFormat;

public class Statistics extends JFrame {
    private Account currentAccount;

    // --- COLORS ---
    private final Color primaryColor = Color.decode("#1E40AF");
    private final Color bgColor      = Color.decode("#F3F4F6");
    private final Color cardColor    = Color.WHITE;
    private final Color textColor    = Color.decode("#111827");
    private final Color grayText     = Color.decode("#6B7280");
    private final Color lineColor    = Color.decode("#E5E7EB");

    // Colors for Charts
    private final Color[] chartColors = {
            Color.decode("#10B981"), // Giỏi (Xanh lá)
            Color.decode("#3B82F6"), // Khá (Xanh dương)
            Color.decode("#F59E0B"), // TB (Vàng)
            Color.decode("#EF4444")  // Yếu (Đỏ)
    };

    private int totalHS = 0, totalLop = 0;
    private int countGioi = 0, countKha = 0, countTB = 0, countYeu = 0;

    public Statistics(Account account) {
        this.currentAccount = account;
        loadDataFromDB(); // Tải dữ liệu

        setTitle("Thống kê số liệu");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. TOP BAR
        add(createTopBar(), BorderLayout.NORTH);

        // 2. MAIN CONTENT (Chia bố cục Grid cho chắc chắn)
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Phần trên: 4 Thẻ KPI
        mainPanel.add(createKPIGrid(), BorderLayout.NORTH);

        // Phần giữa: 2 Biểu đồ
        mainPanel.add(createChartsPanel(), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    // ================= TOP BAR =================
    private JPanel createTopBar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBackground(cardColor);
        navbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, lineColor));

        JLabel title = new JLabel("  TỔNG QUAN & THỐNG KÊ");
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

        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnBack.setForeground(primaryColor); }
            public void mouseExited(java.awt.event.MouseEvent e) { btnBack.setForeground(grayText); }
        });

        btnBack.addActionListener(e -> {
            this.dispose();
            new Dashboard(currentAccount).setVisible(true);
        });

        navbar.add(btnBack, BorderLayout.EAST);
        return navbar;
    }

    // ================= KPI CARDS =================
    private JPanel createKPIGrid() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0)); // 1 hàng 4 cột
        panel.setBackground(bgColor);
        panel.setPreferredSize(new Dimension(0, 130)); // Chiều cao cố định

        panel.add(createCard("Tổng học sinh", String.valueOf(totalHS), chartColors[1]));
        panel.add(createCard("Học sinh Giỏi", String.valueOf(countGioi), chartColors[0]));
        panel.add(createCard("Học sinh Yếu", String.valueOf(countYeu), chartColors[3]));
        panel.add(createCard("Tổng số Lớp", String.valueOf(totalLop), Color.decode("#8B5CF6")));

        return panel;
    }

    private JPanel createCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(null);
        card.setBackground(cardColor);
        // Viền dưới màu theo loại
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor),
                BorderFactory.createMatteBorder(0, 0, 4, 0, accentColor)
        ));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblVal.setForeground(textColor);
        lblVal.setBounds(20, 20, 150, 45);
        card.add(lblVal);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(grayText);
        lblTitle.setBounds(20, 70, 150, 20);
        card.add(lblTitle);

        return card;
    }

    // ================= CHARTS PANEL =================
    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0)); // 1 hàng 2 cột
        panel.setBackground(bgColor);

        // Biểu đồ cột
        panel.add(createChartContainer("Phổ điểm học sinh", new BarChartPanel(countGioi, countKha, countTB, countYeu)));

        // Biểu đồ tròn
        panel.add(createChartContainer("Tỉ lệ học lực", new PieChartPanel(countGioi, countKha, countTB, countYeu)));

        return panel;
    }

    private JPanel createChartContainer(String title, JPanel chart) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(cardColor);
        container.setBorder(BorderFactory.createLineBorder(lineColor));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(textColor);
        lblTitle.setBorder(new EmptyBorder(15, 20, 10, 0));

        container.add(lblTitle, BorderLayout.NORTH);
        container.add(chart, BorderLayout.CENTER);
        return container;
    }

    // ================= DATABASE LOGIC =================
    private void loadDataFromDB() {
        try (Connection con = DatabaseConnection.getConnection()) {
            if (con == null) return;
            // 1. Đếm HS
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) AS Tong FROM student"); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) totalHS = rs.getInt("Tong");
            }
            // 2. Đếm Lớp
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) AS Tong FROM classes"); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) totalLop = rs.getInt("Tong");
            }
            // 3. Thống kê điểm
            String sql = "SELECT " +
                    "SUM(CASE WHEN (regularScore+midtermScore*2+finalScore*3)/6 >= 8 THEN 1 ELSE 0 END) as Gioi, " +
                    "SUM(CASE WHEN (regularScore+midtermScore*2+finalScore*3)/6 >= 6.5 AND (regularScore+midtermScore*2+finalScore*3)/6 < 8 THEN 1 ELSE 0 END) as Kha, " +
                    "SUM(CASE WHEN (regularScore+midtermScore*2+finalScore*3)/6 >= 5 AND (regularScore+midtermScore*2+finalScore*3)/6 < 6.5 THEN 1 ELSE 0 END) as TB, " +
                    "SUM(CASE WHEN (regularScore+midtermScore*2+finalScore*3)/6 < 5 THEN 1 ELSE 0 END) as Yeu " +
                    "FROM grade";
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    countGioi = rs.getInt("Gioi");
                    countKha = rs.getInt("Kha");
                    countTB = rs.getInt("TB");
                    countYeu = rs.getInt("Yeu");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ================= CUSTOM CHARTS (VẼ LẠI) =================

    // 1. Biểu đồ cột (Bar Chart)
    class BarChartPanel extends JPanel {
        private int[] values;
        private final String[] labels = {"Giỏi", "Khá", "TB", "Yếu"};
        private int maxValue = 10;

        public BarChartPanel(int g, int k, int tb, int y) {
            setBackground(cardColor);
            this.values = new int[]{g, k, tb, y};
            for(int val : values) if(val > maxValue) maxValue = val;
            maxValue = (maxValue == 0) ? 10 : (int)(maxValue * 1.2); // Tránh chia cho 0
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int barWidth = 60;
            int gap = 80;
            // Tính toán để căn giữa biểu đồ
            int totalChartWidth = (barWidth * 4) + (gap * 3);
            int startX = (width - totalChartWidth) / 2;
            int baseY = height - 50;

            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((double) values[i] / maxValue * (height - 100));
                if (barHeight < 5 && values[i] > 0) barHeight = 5;

                // Vẽ cột
                g2.setColor(chartColors[i]);
                g2.fillRoundRect(startX, baseY - barHeight, barWidth, barHeight, 8, 8);

                // Vẽ số lượng trên đầu cột
                g2.setColor(textColor);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                String valStr = String.valueOf(values[i]);
                int strW = g2.getFontMetrics().stringWidth(valStr);
                g2.drawString(valStr, startX + (barWidth - strW)/2, baseY - barHeight - 10);

                // Vẽ nhãn dưới chân cột
                g2.setColor(grayText);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                String lbl = labels[i];
                int lblW = g2.getFontMetrics().stringWidth(lbl);
                g2.drawString(lbl, startX + (barWidth - lblW)/2, baseY + 25);

                startX += barWidth + gap;
            }
        }
    }

    // 2. Biểu đồ tròn (Pie Chart)
    class PieChartPanel extends JPanel {
        private double[] values;
        private String[] labels;

        public PieChartPanel(int g, int k, int tb, int y) {
            setBackground(cardColor);
            double total = g + k + tb + y;
            if (total == 0) total = 1;

            this.values = new double[]{
                    (g/total)*360, (k/total)*360,
                    (tb/total)*360, (y/total)*360
            };

            DecimalFormat df = new DecimalFormat("##0.0");
            this.labels = new String[]{
                    "Giỏi ("+df.format((g/total)*100)+"%)",
                    "Khá ("+df.format((k/total)*100)+"%)",
                    "TB ("+df.format((tb/total)*100)+"%)",
                    "Yếu ("+df.format((y/total)*100)+"%)"
            };
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int d = Math.min(w, h) - 80; // Đường kính
            int x = (w - d) / 2 - 60;    // Căn trái một chút để nhường chỗ cho chú thích
            int y = (h - d) / 2;

            double angle = 90;
            for (int i = 0; i < values.length; i++) {
                g2.setColor(chartColors[i]);
                g2.fill(new Arc2D.Double(x, y, d, d, angle, -values[i], Arc2D.PIE));
                angle -= values[i];
            }

            // Vẽ chú thích (Legend) bên phải
            int lx = x + d + 40;
            int ly = y + 40;
            for(int i=0; i<labels.length; i++){
                g2.setColor(chartColors[i]);
                g2.fillRoundRect(lx, ly + i*40, 16, 16, 4, 4);

                g2.setColor(textColor);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2.drawString(labels[i], lx + 30, ly + i*40 + 13);
            }
        }
    }

    public static void main(String[] args) {
        Account mock = new Account("admin", "", "", "admin");
        SwingUtilities.invokeLater(() -> new Statistics(mock).setVisible(true));
    }
}