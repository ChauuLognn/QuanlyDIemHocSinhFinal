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

public class SubjectManagement extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // Input fields
    private JTextField txtSubjectId, txtSubjectName, txtTeacher;
    private JComboBox<String> cboGrade, cboType;
    private JSpinner spinnerWeeklyHours, spinnerCoefficient;
    private JTextArea txtDescription;

    // Colors (Đồng bộ với StudentManagement)
    private Color primaryColor = new Color(70, 70, 70);
    private Color secondaryColor = new Color(245, 245, 245);

    public SubjectManagement() {
        setTitle("Quản lý Môn học THCS");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main layout
        setLayout(new BorderLayout(0, 0));

        // 1. Top bar
        add(createTopBar(), BorderLayout.NORTH);

        // 2. Left - Input form (Đã tối ưu)
        add(createInputPanel(), BorderLayout.WEST);

        // 3. Center - Table (Đã fix màu Header)
        add(createTablePanel(), BorderLayout.CENTER);

        // 4. Bottom - Buttons (Đã fix màu nút)
        add(createButtonPanel(), BorderLayout.SOUTH);

        // Add sample data
        addSampleData();
    }

    // ============================================================
    // 1. TOP BAR
    // ============================================================
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(primaryColor);
        topBar.setPreferredSize(new Dimension(0, 60));
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Title
        JLabel title = new JLabel("QUẢN LÝ MÔN HỌC");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        topBar.add(title, BorderLayout.WEST);

        // Nút Quay lại (Style mới)
        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Arial", Font.BOLD, 12));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(100, 100, 100)); // Màu nền nhạt hơn header chút

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
    // 2. INPUT PANEL (Style mới gọn gàng)
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

        // Title
        JLabel lblTitle = new JLabel("Thông tin môn học");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(primaryColor);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblTitle);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Mã môn & Loại môn
        JPanel row1 = new JPanel(new GridLayout(1, 2, 10, 0));
        row1.setBackground(Color.WHITE);
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        txtSubjectId = createTextField();
        cboType = new JComboBox<>(new String[]{"Văn hóa", "Tự nhiên", "Xã hội", "Kỹ năng"});

        row1.add(createFieldPanel("Mã môn:", txtSubjectId));
        row1.add(createFieldPanel("Loại:", cboType));
        contentPanel.add(row1);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Tên môn
        contentPanel.add(createFieldPanel("Tên môn học:", txtSubjectName = createTextField()));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Khối & Giáo viên
        contentPanel.add(createFieldPanel("Áp dụng khối:", cboGrade = new JComboBox<>(new String[]{"Tất cả (6-9)", "Khối 6-7", "Khối 8-9"})));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(createFieldPanel("Tổ trưởng bộ môn:", txtTeacher = createTextField()));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Số tiết & Hệ số (Gom 1 hàng)
        JPanel row2 = new JPanel(new GridLayout(1, 2, 10, 0));
        row2.setBackground(Color.WHITE);
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        spinnerWeeklyHours = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        spinnerCoefficient = new JSpinner(new SpinnerNumberModel(1, 1, 3, 1));

        row2.add(createFieldPanel("Số tiết/tuần:", spinnerWeeklyHours));
        row2.add(createFieldPanel("Hệ số:", spinnerCoefficient));
        contentPanel.add(row2);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Mô tả
        contentPanel.add(new JLabel("Mô tả:"));
        txtDescription = new JTextArea(3, 20);
        txtDescription.setFont(new Font("Arial", Font.PLAIN, 12));
        txtDescription.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        scrollDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(scrollDesc);

        // Scroll bao ngoài
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    // Helper tạo Label + Input
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
    // 3. TABLE PANEL (Đã fix Header & Renderer)
    // ============================================================
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(secondaryColor);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(secondaryColor);
        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(25);
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { searchSubject(); }
        });
        searchPanel.add(txtSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Mã MH", "Tên môn", "Loại", "Khối", "GVPT", "Tiết", "Hệ số", "Mô tả"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setGridColor(new Color(220, 220, 220));

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Custom Header Renderer (Màu xám đậm, chữ trắng)
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

        // Center Cell Renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 1 && i != 7) // Trừ cột Tên môn và Mô tả thì căn giữa hết
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) loadDataToForm(table.convertRowIndexToModel(row));
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ============================================================
    // 4. BUTTON PANEL (Style phẳng)
    // ============================================================
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton btnAdd = createButton("Thêm", new Color(40, 167, 69));
        JButton btnUpdate = createButton("Cập nhật", new Color(0, 123, 255));
        JButton btnDelete = createButton("Xóa", new Color(220, 53, 69));
        JButton btnClear = createButton("Làm mới", new Color(108, 117, 125));

        btnAdd.addActionListener(e -> addSubject());
        btnUpdate.addActionListener(e -> updateSubject());
        btnDelete.addActionListener(e -> deleteSubject());
        btnClear.addActionListener(e -> clearFields());

        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnClear);
        return panel;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 25, 10, 25));
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
    private void addSubject() {
        if (!validateInput()) return;
        // Check trùng mã
        String id = txtSubjectId.getText().trim().toUpperCase();
        for (int i=0; i<tableModel.getRowCount(); i++) {
            if(tableModel.getValueAt(i, 0).equals(id)) {
                JOptionPane.showMessageDialog(this, "Mã môn đã tồn tại!"); return;
            }
        }

        tableModel.addRow(new Object[]{
                id, txtSubjectName.getText(), cboType.getSelectedItem(), cboGrade.getSelectedItem(),
                txtTeacher.getText(), spinnerWeeklyHours.getValue(), spinnerCoefficient.getValue(), txtDescription.getText()
        });
        JOptionPane.showMessageDialog(this, "Thêm thành công!");
        clearFields();
    }

    private void updateSubject() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn môn để sửa!"); return; }
        if (!validateInput()) return;

        int modelRow = table.convertRowIndexToModel(row);
        tableModel.setValueAt(txtSubjectId.getText(), modelRow, 0);
        tableModel.setValueAt(txtSubjectName.getText(), modelRow, 1);
        tableModel.setValueAt(cboType.getSelectedItem(), modelRow, 2);
        tableModel.setValueAt(cboGrade.getSelectedItem(), modelRow, 3);
        tableModel.setValueAt(txtTeacher.getText(), modelRow, 4);
        tableModel.setValueAt(spinnerWeeklyHours.getValue(), modelRow, 5);
        tableModel.setValueAt(spinnerCoefficient.getValue(), modelRow, 6);
        tableModel.setValueAt(txtDescription.getText(), modelRow, 7);

        JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        clearFields();
    }

    private void deleteSubject() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn môn để xóa!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Bạn chắc chắn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            tableModel.removeRow(table.convertRowIndexToModel(row));
            clearFields();
        }
    }

    private void clearFields() {
        txtSubjectId.setText(""); txtSubjectName.setText(""); txtTeacher.setText(""); txtDescription.setText("");
        cboType.setSelectedIndex(0); cboGrade.setSelectedIndex(0);
        spinnerWeeklyHours.setValue(2); spinnerCoefficient.setValue(1);
        table.clearSelection();
    }

    private void loadDataToForm(int row) {
        txtSubjectId.setText(tableModel.getValueAt(row, 0).toString());
        txtSubjectName.setText(tableModel.getValueAt(row, 1).toString());
        cboType.setSelectedItem(tableModel.getValueAt(row, 2));
        cboGrade.setSelectedItem(tableModel.getValueAt(row, 3));
        txtTeacher.setText(tableModel.getValueAt(row, 4).toString());
        spinnerWeeklyHours.setValue(Integer.parseInt(tableModel.getValueAt(row, 5).toString()));
        spinnerCoefficient.setValue(Integer.parseInt(tableModel.getValueAt(row, 6).toString()));
        txtDescription.setText(tableModel.getValueAt(row, 7).toString());
    }

    private boolean validateInput() {
        if(txtSubjectId.getText().isEmpty() || txtSubjectName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập thiếu thông tin!"); return false;
        }
        return true;
    }

    private void searchSubject() {
        String text = txtSearch.getText();
        if (text.trim().length() == 0) rowSorter.setRowFilter(null);
        else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
    }

    private void backToDashboard() {
        this.dispose();
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }

    private void addSampleData() {
        tableModel.addRow(new Object[]{"TOAN", "Toán", "Văn hóa", "Tất cả", "Nguyễn A", 4, 2, "Môn chính"});
        tableModel.addRow(new Object[]{"VAN", "Văn", "Văn hóa", "Tất cả", "Trần B", 4, 2, "Môn chính"});
        tableModel.addRow(new Object[]{"ANH", "Tiếng Anh", "Văn hóa", "Tất cả", "Lê C", 3, 1, "Ngoại ngữ"});
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SubjectManagement().setVisible(true));
    }
}