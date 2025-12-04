package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StudentManagement extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> rowSorter; // Dùng để lọc

    // Input fields
    private JTextField txtId, txtName, txtClass;
    private JComboBox<String> cboGender;
    private JTextField txtMath, txtLiterature, txtEnglish, txtPhysics, txtChemistry,
            txtBiology, txtHistory, txtGeography, txtCivics;

    // Colors
    private Color primaryColor = new Color(70, 70, 70);
    private Color secondaryColor = new Color(245, 245, 245);

    public StudentManagement() {
        setTitle("Quản lý điểm học sinh THCS");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main layout
        setLayout(new BorderLayout(0, 0));

        // 1. Top bar (Header)
        add(createTopBar(), BorderLayout.NORTH);

        // 2. Left - Input form (Đã tối ưu giao diện)
        add(createInputPanel(), BorderLayout.WEST);

        // 3. Center - Table (Đã thêm tô màu điểm & lọc)
        add(createTablePanel(), BorderLayout.CENTER);

        // 4. Bottom - Buttons (Đã fix lỗi hiển thị màu)
        add(createButtonPanel(), BorderLayout.SOUTH);

        // Thêm dữ liệu mẫu
        addSampleData();
    }

    // ============================================================
    // 1. TOP BAR (Đã fix nút Quay lại)
    // ============================================================
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(primaryColor);
        topBar.setPreferredSize(new Dimension(0, 60));
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("QUẢN LÝ ĐIỂM HỌC SINH THCS");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        topBar.add(title, BorderLayout.WEST);

        // Nút Quay lại (Solid style để dễ nhìn)
        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Arial", Font.BOLD, 12));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(100, 100, 100)); // Màu nền nhạt hơn header xíu

        // Fix hiển thị trên Windows
        btnBack.setOpaque(true);
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnBack.setBackground(new Color(120, 120, 120)); }
            @Override
            public void mouseExited(MouseEvent e) { btnBack.setBackground(new Color(100, 100, 100)); }
        });

        btnBack.addActionListener(e -> backToDashboard());
        topBar.add(btnBack, BorderLayout.EAST);

        return topBar;
    }

    // ============================================================
    // 2. INPUT PANEL (Đã tối ưu Grid Layout 2 cột)
    // ============================================================
    private JPanel createInputPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(340, 0));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- PHẦN 1: THÔNG TIN CÁ NHÂN ---
        JLabel lblInfo = new JLabel("1. Thông tin học sinh");
        lblInfo.setFont(new Font("Arial", Font.BOLD, 14));
        lblInfo.setForeground(primaryColor);
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblInfo);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Hàng 1: Mã HS + Giới tính (Gom cùng dòng)
        JPanel row1 = new JPanel(new GridLayout(1, 2, 10, 0));
        row1.setBackground(Color.WHITE);
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        row1.add(createFieldPanel("Mã HS:", txtId = createTextField()));
        cboGender = new JComboBox<>(new String[]{"Nam", "Nữ"});
        row1.add(createFieldPanel("Giới tính:", cboGender));
        contentPanel.add(row1);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Họ tên & Lớp
        contentPanel.add(createFieldPanel("Họ và tên:", txtName = createTextField()));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(createFieldPanel("Lớp:", txtClass = createTextField()));

        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        contentPanel.add(sep);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- PHẦN 2: ĐIỂM SỐ (GRID 2 CỘT) ---
        JLabel lblScore = new JLabel("2. Nhập điểm chi tiết");
        lblScore.setFont(new Font("Arial", Font.BOLD, 14));
        lblScore.setForeground(primaryColor);
        lblScore.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblScore);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel scorePanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Lưới 2 cột
        scorePanel.setBackground(Color.WHITE);
        scorePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        scorePanel.add(createFieldPanel("Toán:", txtMath = createTextField()));
        scorePanel.add(createFieldPanel("Văn:", txtLiterature = createTextField()));
        scorePanel.add(createFieldPanel("Anh:", txtEnglish = createTextField()));
        scorePanel.add(createFieldPanel("Lý:", txtPhysics = createTextField()));
        scorePanel.add(createFieldPanel("Hóa:", txtChemistry = createTextField()));
        scorePanel.add(createFieldPanel("Sinh:", txtBiology = createTextField()));
        scorePanel.add(createFieldPanel("Sử:", txtHistory = createTextField()));
        scorePanel.add(createFieldPanel("Địa:", txtGeography = createTextField()));
        scorePanel.add(createFieldPanel("GDCD:", txtCivics = createTextField()));

        contentPanel.add(scorePanel);

        // ScrollPane bao bọc
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    // Helper tạo label + input
    private JPanel createFieldPanel(String labelText, JComponent component) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        p.add(lbl, BorderLayout.NORTH);
        p.add(component, BorderLayout.CENTER);
        return p;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Arial", Font.PLAIN, 13));
        return tf;
    }

    // ============================================================
    // 3. TABLE PANEL (Đã thêm Lọc và Tô màu)
    // ============================================================
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(secondaryColor);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Search & Filter Bar ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(secondaryColor);

        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(20);
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { searchStudent(); }
        });
        searchPanel.add(txtSearch);

        searchPanel.add(new JLabel("  Lọc lớp:"));
        JComboBox<String> cboFilter = new JComboBox<>(new String[]{"Tất cả", "6A1", "7A2", "8A1", "9A3"});

        // Logic lọc lớp
        cboFilter.addActionListener(e -> {
            String selected = cboFilter.getSelectedItem().toString();
            if (selected.equals("Tất cả")) rowSorter.setRowFilter(null);
            else rowSorter.setRowFilter(RowFilter.regexFilter(selected, 2)); // Cột 2 là Lớp
        });
        searchPanel.add(cboFilter);

        panel.add(searchPanel, BorderLayout.NORTH);

        // --- Table Setup ---
        String[] columns = {"Mã HS", "Họ tên", "Lớp", "GT", "Toán", "Văn", "Anh", "Lý", "Hóa", "Sinh", "Sử", "Địa", "GDCD", "ĐTB", "Xếp loại"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setGridColor(new Color(220, 220, 220));

        // Kích hoạt bộ lọc
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Fix Header Color
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 35));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(primaryColor);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Arial", Font.BOLD, 12));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // --- Conditional Formatting (Tô màu điểm) ---
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (!isSelected) {
                    try {
                        String text = value.toString();
                        // Cột 4->12 là điểm, 13 là ĐTB
                        if (column >= 4 && column <= 13) {
                            double score = Double.parseDouble(text);
                            if (score < 5.0) {
                                c.setForeground(Color.RED);
                                c.setFont(new Font("Arial", Font.BOLD, 12));
                            } else if (score >= 8.0) {
                                c.setForeground(new Color(0, 100, 0)); // Xanh đậm
                                c.setFont(new Font("Arial", Font.BOLD, 12));
                            } else {
                                c.setForeground(Color.BLACK);
                                c.setFont(new Font("Arial", Font.PLAIN, 12));
                            }
                        } else if (column == 14) { // Cột Xếp loại
                            if (text.equals("Giỏi")) c.setForeground(new Color(0, 100, 0));
                            else if (text.equals("Yếu")) c.setForeground(Color.RED);
                            else c.setForeground(Color.BLACK);
                        } else {
                            c.setForeground(Color.BLACK);
                        }
                    } catch (Exception e) { c.setForeground(Color.BLACK); }
                }
                return c;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);

        // Click to load
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    // Phải convert row index vì đang dùng filter/sorter
                    int modelRow = table.convertRowIndexToModel(row);
                    loadDataToForm(modelRow);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ============================================================
    // 4. BUTTON PANEL (Đã fix lỗi hiển thị màu nút)
    // ============================================================
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton btnAdd = createButton("Thêm học sinh", new Color(40, 167, 69));
        JButton btnUpdate = createButton("Cập nhật", new Color(0, 123, 255));
        JButton btnDelete = createButton("Xóa", new Color(220, 53, 69));
        JButton btnClear = createButton("Làm mới", new Color(108, 117, 125));
        JButton btnExport = createButton("Xuất Excel", new Color(23, 162, 184));

        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearFields());
        btnExport.addActionListener(e -> exportToExcel());

        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnClear);
        panel.add(btnExport);

        return panel;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);

        // --- FIX QUAN TRỌNG: Hiển thị đúng màu nền ---
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bgColor.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bgColor); }
        });
        return btn;
    }

    // ============================================================
    // BUSINESS LOGIC
    // ============================================================
    private void addStudent() {
        try {
            if (!validateInput()) return;
            double[] scores = getScores();
            double avg = calculateAverage(scores);

            Object[] row = {
                    txtId.getText().trim(), txtName.getText().trim(), txtClass.getText().trim(),
                    cboGender.getSelectedItem(),
                    scores[0], scores[1], scores[2], scores[3], scores[4], scores[5], scores[6], scores[7], scores[8],
                    String.format("%.2f", avg), classifyStudent(avg)
            };
            tableModel.addRow(row);
            clearFields();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + e.getMessage());
        }
    }

    private void updateStudent() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn dòng để sửa!"); return; }

        // Convert row index khi dùng filter
        int modelRow = table.convertRowIndexToModel(row);

        try {
            if (!validateInput()) return;
            double[] scores = getScores();
            double avg = calculateAverage(scores);

            tableModel.setValueAt(txtId.getText(), modelRow, 0);
            tableModel.setValueAt(txtName.getText(), modelRow, 1);
            tableModel.setValueAt(txtClass.getText(), modelRow, 2);
            tableModel.setValueAt(cboGender.getSelectedItem(), modelRow, 3);
            for(int i=0; i<9; i++) tableModel.setValueAt(scores[i], modelRow, i+4);
            tableModel.setValueAt(String.format("%.2f", avg), modelRow, 13);
            tableModel.setValueAt(classifyStudent(avg), modelRow, 14);

            clearFields();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage()); }
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn dòng để xóa!"); return; }

        if (JOptionPane.showConfirmDialog(this, "Xóa học sinh này?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            int modelRow = table.convertRowIndexToModel(row);
            tableModel.removeRow(modelRow);
            clearFields();
        }
    }

    private void clearFields() {
        txtId.setText(""); txtName.setText(""); txtClass.setText(""); cboGender.setSelectedIndex(0);
        txtMath.setText(""); txtLiterature.setText(""); txtEnglish.setText(""); txtPhysics.setText("");
        txtChemistry.setText(""); txtBiology.setText(""); txtHistory.setText(""); txtGeography.setText(""); txtCivics.setText("");
        table.clearSelection();
    }

    private void loadDataToForm(int row) {
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtName.setText(tableModel.getValueAt(row, 1).toString());
        txtClass.setText(tableModel.getValueAt(row, 2).toString());
        cboGender.setSelectedItem(tableModel.getValueAt(row, 3).toString());
        txtMath.setText(tableModel.getValueAt(row, 4).toString());
        txtLiterature.setText(tableModel.getValueAt(row, 5).toString());
        txtEnglish.setText(tableModel.getValueAt(row, 6).toString());
        txtPhysics.setText(tableModel.getValueAt(row, 7).toString());
        txtChemistry.setText(tableModel.getValueAt(row, 8).toString());
        txtBiology.setText(tableModel.getValueAt(row, 9).toString());
        txtHistory.setText(tableModel.getValueAt(row, 10).toString());
        txtGeography.setText(tableModel.getValueAt(row, 11).toString());
        txtCivics.setText(tableModel.getValueAt(row, 12).toString());
    }

    private boolean validateInput() {
        if(txtId.getText().isEmpty() || txtName.getText().isEmpty() || txtClass.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập thiếu thông tin!"); return false;
        }
        return true;
    }

    private double[] getScores() {
        return new double[] {
                pScore(txtMath), pScore(txtLiterature), pScore(txtEnglish), pScore(txtPhysics),
                pScore(txtChemistry), pScore(txtBiology), pScore(txtHistory), pScore(txtGeography), pScore(txtCivics)
        };
    }

    private double pScore(JTextField tf) {
        double s = Double.parseDouble(tf.getText().trim());
        if(s<0 || s>10) throw new NumberFormatException();
        return s;
    }

    private double calculateAverage(double[] s) {
        double sum = 0; for(double d : s) sum+=d; return sum/s.length;
    }

    private String classifyStudent(double avg) {
        if(avg>=8) return "Giỏi"; if(avg>=6.5) return "Khá"; if(avg>=5) return "Trung bình"; return "Yếu";
    }

    private void searchStudent() {
        String text = txtSearch.getText();
        if (text.trim().length() == 0) rowSorter.setRowFilter(null);
        else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
    }

    private void backToDashboard() {
        this.dispose();
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }

    private void exportToExcel() {
        JOptionPane.showMessageDialog(this, "Chức năng đang phát triển!");
    }

    private void addSampleData() {
        tableModel.addRow(new Object[]{"HS001", "Nguyễn Văn An", "6A1", "Nam", 8.5, 7.5, 8.0, 7.0, 7.5, 8.0, 7.5, 8.0, 9.0, "7.89", "Khá"});
        tableModel.addRow(new Object[]{"HS002", "Trần Thị Bình", "7A2", "Nữ", 9.0, 8.5, 9.0, 8.0, 8.5, 9.0, 8.5, 8.0, 9.5, "8.67", "Giỏi"});
        tableModel.addRow(new Object[]{"HS003", "Lê Văn Cường", "8A1", "Nam", 4.0, 5.0, 4.5, 3.0, 5.0, 4.0, 5.0, 4.0, 6.0, "4.50", "Yếu"});
        tableModel.addRow(new Object[]{"HS004", "Phạm Thị Dung", "9A3", "Nữ", 7.0, 7.5, 7.0, 7.5, 7.0, 7.5, 8.0, 7.5, 8.5, "7.50", "Khá"});
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagement().setVisible(true));
    }


}