package UI;

import Database.DatabaseConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.sql.*;
import java.text.DecimalFormat;

public class Statistics extends JFrame {
    // ✅ ĐÃ SỬA: Bỏ cấu hình SQL Server, dùng DatabaseConnection chung

    // Colors
    private final Color primaryColor = new Color(70, 70, 70);
    private final Color secondaryColor = new Color(245, 245, 245);
    private final Color[] chartColors = {
            new Color(46, 204, 113), // Giỏi (Xanh lá)
            new Color(52, 152, 219), // Khá (Xanh dương)
            new Color(241, 196, 15), // TB (Vàng)
            new Color(231, 76, 60)   // Yếu (Đỏ)
    };

    // Biến lưu dữ liệu thống kê
    private int totalHS = 0;
    private int totalLop = 0;
    private int countGioi = 0;
    private int countKha = 0;
    private int countTB = 0;
    private int countYeu = 0;

    public Statistics() {
        // 1. Tải dữ liệu từ SQL trước khi vẽ giao diện
        loadDataFromDB();

        setTitle("Thống kê & Báo cáo");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 2. Top Bar
        add(createTopBar(), BorderLayout.NORTH);

        // 3. Main Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(secondaryColor);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Phần 1: Các thẻ chỉ số (KPIs) ---
        content.add(createKPIGrid());
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Phần 2: Biểu đồ ---
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setBackground(secondaryColor);
        chartsPanel.setPreferredSize(new Dimension(1100, 400));

        // Truyền dữ liệu thật vào biểu đồ
        chartsPanel.add(createChartCard("Phổ điểm học sinh", new BarChartPanel(countGioi, countKha, countTB, countYeu)));
        chartsPanel.add(createChartCard("Tỉ lệ học lực", new PieChartPanel(countGioi, countKha, countTB, countYeu)));

        content.add(chartsPanel);
        add(content, BorderLayout.CENTER);
    }

    // ================= DATABASE LOGIC - ✅ ĐÃ SỬA =================
    private void loadDataFromDB() {
        // ✅ SỬA: Dùng DatabaseConnection thay vì hardcode SQL Server
        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                JOptionPane.showMessageDialog(this,
                        "Không thể kết nối Database!\nVui lòng kiểm tra MySQL đang chạy.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 1. Lấy tổng số học sinh - ✅ Kiểm tra tên bảng
            String sqlHS = "SELECT COUNT(*) AS Tong FROM Student"; // Hoặc HocSinh tùy DB của bạn
            try (PreparedStatement psHS = con.prepareStatement(sqlHS);
                 ResultSet rsHS = psHS.executeQuery()) {
                if (rsHS.next()) totalHS = rsHS.getInt("Tong");
            } catch (SQLException e) {
                System.err.println("⚠️ Lỗi đếm học sinh (Bảng có thể chưa tồn tại): " + e.getMessage());
            }

            // 2. Lấy tổng số lớp - ✅ Kiểm tra tên bảng
            String sqlLop = "SELECT COUNT(*) AS Tong FROM Classes"; // Hoặc LopHoc
            try (PreparedStatement psLop = con.prepareStatement(sqlLop);
                 ResultSet rsLop = psLop.executeQuery()) {
                if (rsLop.next()) totalLop = rsLop.getInt("Tong");
            } catch (SQLException e) {
                System.err.println("⚠️ Lỗi đếm lớp (Bảng có thể chưa tồn tại): " + e.getMessage());
            }

            // 3. Lấy thống kê điểm - ✅ Kiểm tra cột DiemTrungBinh
            String sqlScore = "SELECT " +
                    "SUM(CASE WHEN (regularScore + midtermScore*2 + finalScore*3)/6 >= 8.0 THEN 1 ELSE 0 END) as Gioi, " +
                    "SUM(CASE WHEN (regularScore + midtermScore*2 + finalScore*3)/6 >= 6.5 AND (regularScore + midtermScore*2 + finalScore*3)/6 < 8.0 THEN 1 ELSE 0 END) as Kha, " +
                    "SUM(CASE WHEN (regularScore + midtermScore*2 + finalScore*3)/6 >= 5.0 AND (regularScore + midtermScore*2 + finalScore*3)/6 < 6.5 THEN 1 ELSE 0 END) as TB, " +
                    "SUM(CASE WHEN (regularScore + midtermScore*2 + finalScore*3)/6 < 5.0 THEN 1 ELSE 0 END) as Yeu " +
                    "FROM Grade"; // Hoặc Diem

            try (PreparedStatement psScore = con.prepareStatement(sqlScore);
                 ResultSet rsScore = psScore.executeQuery()) {
                if (rsScore.next()) {
                    countGioi = rsScore.getInt("Gioi");
                    countKha = rsScore.getInt("Kha");
                    countTB = rsScore.getInt("TB");
                    countYeu = rsScore.getInt("Yeu");
                }
            } catch (SQLException e) {
                System.err.println("⚠️ Lỗi thống kê điểm (Bảng Grade có thể chưa tồn tại): " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải dữ liệu: " + e.getMessage() +
                            "\n\nGợi ý: Kiểm tra tên bảng trong MySQL phải khớp với code",
                    "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
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

        panel.add(createKPICard("Tổng học sinh",String.valueOf(totalHS), new Color(52, 152, 219)));
        panel.add(createKPICard("Học sinh Giỏi", String.valueOf(countGioi), new Color(46, 204, 113)));
        panel.add(createKPICard("Học sinh Yếu", String.valueOf(countYeu), new Color(231, 76, 60)));
        panel.add(createKPICard("Tổng số Lớp", String.valueOf(totalLop), new Color(155, 89, 182)));

        return panel;
    }

    private JPanel createKPICard(String title, String value, Color barColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, barColor));

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

    // ================= BAR CHART =================
    class BarChartPanel extends JPanel {
        private int[] values;
        private final String[] labels = {"Giỏi", "Khá", "TB", "Yếu"};
        private int maxValue = 10;

        public BarChartPanel(int g, int k, int tb, int y) {
            setBackground(Color.WHITE);
            this.values = new int[]{g, k, tb, y};

            for(int val : values) {
                if(val > maxValue) maxValue = val;
            }
            maxValue = (int)(maxValue * 1.2);
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
            int x = (width - (barWidth * 4 + gap * 3)) / 2;
            int baseY = height - 40;

            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((double) values[i] / maxValue * (height - 80));
                if (barHeight < 5 && values[i] > 0) barHeight = 5;

                g2.setColor(chartColors[i]);
                g2.fillRoundRect(x, baseY - barHeight, barWidth, barHeight, 10, 10);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                String valStr = String.valueOf(values[i]);
                int strWidth = g2.getFontMetrics().stringWidth(valStr);
                g2.drawString(valStr, x + (barWidth - strWidth) / 2, baseY - barHeight - 5);

                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                int lblWidth = g2.getFontMetrics().stringWidth(labels[i]);
                g2.drawString(labels[i], x + (barWidth - lblWidth) / 2, baseY + 20);

                x += barWidth + gap;
            }
        }
    }

    // ================= PIE CHART =================
    class PieChartPanel extends JPanel {
        private double[] values;
        private String[] labels;

        public PieChartPanel(int g, int k, int tb, int y) {
            setBackground(Color.WHITE);
            double total = g + k + tb + y;
            if (total == 0) total = 1;

            double pG = (g / total) * 100;
            double pK = (k / total) * 100;
            double pTB = (tb / total) * 100;
            double pY = (y / total) * 100;

            this.values = new double[]{pG, pK, pTB, pY};

            DecimalFormat df = new DecimalFormat("##0.0");
            this.labels = new String[]{
                    "Giỏi (" + df.format(pG) + "%)",
                    "Khá (" + df.format(pK) + "%)",
                    "TB (" + df.format(pTB) + "%)",
                    "Yếu (" + df.format(pY) + "%)"
            };
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int diameter = Math.min(width, height) - 60;
            int x = (width - diameter) / 2 - 60;
            int y = (height - diameter) / 2;

            double currentAngle = 90;

            for (int i = 0; i < values.length; i++) {
                double angle = values[i] * 360 / 100;
                g2.setColor(chartColors[i]);
                g2.fill(new Arc2D.Double(x, y, diameter, diameter, currentAngle, -angle, Arc2D.PIE));
                currentAngle -= angle;
            }

            drawLegend(g2, x + diameter + 40, y + 20);
        }

        private void drawLegend(Graphics2D g2, int x, int y) {
            for (int i = 0; i < labels.length; i++) {
                g2.setColor(chartColors[i]);
                g2.fillRoundRect(x, y + i * 30, 15, 15, 4, 4);

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