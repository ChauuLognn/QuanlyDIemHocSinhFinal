package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;

public class Statistics extends JFrame {
    // Colors
    private final Color primaryColor = new Color(70, 70, 70);
    private final Color secondaryColor = new Color(245, 245, 245);

    // Chart Colors (Màu cho biểu đồ)
    private final Color[] chartColors = {
            new Color(46, 204, 113), // Xanh lá (Giỏi)
            new Color(52, 152, 219), // Xanh dương (Khá)
            new Color(241, 196, 15), // Vàng (TB)
            new Color(231, 76, 60)   // Đỏ (Yếu)
    };

    public Statistics() {
        setTitle("Thống kê & Báo cáo");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Top Bar
        add(createTopBar(), BorderLayout.NORTH);

        // 2. Main Scroll Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(secondaryColor);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Phần 1: Các thẻ chỉ số (KPIs) ---
        content.add(createKPIGrid());
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Phần 2: Biểu đồ (Cột & Tròn) ---
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setBackground(secondaryColor);
        chartsPanel.setPreferredSize(new Dimension(1100, 400));

        // Biểu đồ cột: Phổ điểm
        chartsPanel.add(createChartCard("Phổ điểm học sinh", new BarChartPanel()));
        // Biểu đồ tròn: Tỉ lệ học lực
        chartsPanel.add(createChartCard("Tỉ lệ học lực", new PieChartPanel()));

        content.add(chartsPanel);

        add(content, BorderLayout.CENTER);
    }

    // ================= TOP BAR =================
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(primaryColor);
        topBar.setPreferredSize(new Dimension(0, 60));
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("THỐNG KÊ DỮ LIỆU");
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

    // ================= KPI CARDS =================
    private JPanel createKPIGrid() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(secondaryColor);
        panel.setPreferredSize(new Dimension(0, 120));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        panel.add(createKPICard("Tổng học sinh", "1,250", new ImageIcon(), new Color(52, 152, 219)));
        panel.add(createKPICard("Học sinh Giỏi", "450", new ImageIcon(), new Color(46, 204, 113)));
        panel.add(createKPICard("Học sinh Yếu", "35", new ImageIcon(), new Color(231, 76, 60)));
        panel.add(createKPICard("Lớp học", "24", new ImageIcon(), new Color(155, 89, 182)));

        return panel;
    }

    private JPanel createKPICard(String title, String value, Icon icon, Color barColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, barColor)); // Viền màu dưới đáy

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Arial", Font.BOLD, 28));
        lblVal.setForeground(primaryColor);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTitle.setForeground(Color.GRAY);

        textPanel.add(lblVal);
        textPanel.add(lblTitle);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    // ================= CHART CONTAINERS =================
    private JPanel createChartCard(String title, JPanel chart) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitle.setForeground(primaryColor);
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(chart, BorderLayout.CENTER);
        return card;
    }

    // ================= CUSTOM BAR CHART (Tự vẽ) =================
    class BarChartPanel extends JPanel {
        // Dữ liệu mẫu: Giỏi, Khá, TB, Yếu
        private final int[] values = {450, 600, 150, 50};
        private final String[] labels = {"Giỏi", "Khá", "TB", "Yếu"};
        private final int maxValue = 650; // Giá trị max để chia tỉ lệ

        public BarChartPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int barWidth = 60;
            int gap = 50;
            int x = (width - (barWidth * 4 + gap * 3)) / 2; // Căn giữa biểu đồ
            int baseY = height - 40; // Đáy biểu đồ

            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((double) values[i] / maxValue * (height - 80));

                // Vẽ cột
                g2.setColor(chartColors[i]);
                g2.fillRoundRect(x, baseY - barHeight, barWidth, barHeight, 10, 10);

                // Vẽ số liệu trên đầu cột
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                String valStr = String.valueOf(values[i]);
                int strWidth = g2.getFontMetrics().stringWidth(valStr);
                g2.drawString(valStr, x + (barWidth - strWidth) / 2, baseY - barHeight - 5);

                // Vẽ nhãn dưới chân (Giỏi, Khá...)
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                int lblWidth = g2.getFontMetrics().stringWidth(labels[i]);
                g2.drawString(labels[i], x + (barWidth - lblWidth) / 2, baseY + 20);

                x += barWidth + gap;
            }
        }
    }

    // ================= CUSTOM PIE CHART (Tự vẽ) =================
    class PieChartPanel extends JPanel {
        // Dữ liệu %: 36%, 48%, 12%, 4%
        private final double[] values = {36, 48, 12, 4};
        private final String[] labels = {"Giỏi (36%)", "Khá (48%)", "TB (12%)", "Yếu (4%)"};

        public PieChartPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int diameter = Math.min(width, height) - 60;
            int x = (width - diameter) / 2 - 60; // Dịch trái xíu để vẽ chú thích bên phải
            int y = (height - diameter) / 2;

            double currentAngle = 90; // Bắt đầu từ 12h

            // Vẽ hình tròn
            for (int i = 0; i < values.length; i++) {
                double angle = values[i] * 360 / 100;
                g2.setColor(chartColors[i]);
                g2.fill(new Arc2D.Double(x, y, diameter, diameter, currentAngle, -angle, Arc2D.PIE));
                currentAngle -= angle;
            }

            // Vẽ chú thích (Legend) bên phải
            drawLegend(g2, x + diameter + 40, y + 20);
        }

        private void drawLegend(Graphics2D g2, int x, int y) {
            for (int i = 0; i < labels.length; i++) {
                g2.setColor(chartColors[i]);
                g2.fillRoundRect(x, y + i * 30, 15, 15, 4, 4); // Ô màu

                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Arial", Font.PLAIN, 13));
                g2.drawString(labels[i], x + 25, y + i * 30 + 12);
            }
        }
    }

    private void backToDashboard() {
        this.dispose();
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Statistics().setVisible(true));
    }
}