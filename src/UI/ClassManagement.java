package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;

public class ClassManagement extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // Input fields
    private JTextField txtClassId, txtClassName, txtTeacher, txtAcademicYear;
    private JSpinner spinnerCapacity;
    private JTextArea txtNote;

    // Colors
    private Color primaryColor = new Color(70, 70, 70);
    private Color secondaryColor = new Color(245, 245, 245);

    public ClassManagement() {
        setTitle("Quản lý Lớp học");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        // 1. Top bar
        add(createTopBar(), BorderLayout.NORTH);

        // 2. Left - Input form
        add(createInputPanel(), BorderLayout.WEST);

        // 3. Center - Table
        add(createTablePanel(), BorderLayout.CENTER);

        // 4. Bottom - Buttons
        add(createButtonPanel(), BorderLayout.SOUTH);

        // Add dummy data
        addSampleData();
    }

    // ================= TOP BAR =================
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(primaryColor);
        topBar.setPreferredSize(new Dimension(0, 60));
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("QUẢN LÝ LỚP HỌC");
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

        // Hover effect
        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnBack.setBackground(new Color(120, 120, 120)); }
            public void mouseExited(MouseEvent e) { btnBack.setBackground(new Color(100, 100, 100)); }
        });

        topBar.add(btnBack, BorderLayout.EAST);
        return topBar;
    }

    // ================= INPUT PANEL =================
    private JPanel createInputPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(320, 0));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblInfo = new JLabel("Thông tin lớp học");
        lblInfo.setFont(new Font("Arial", Font.BOLD, 14));
        lblInfo.setForeground(primaryColor);
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblInfo);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // Các trường nhập liệu
        content.add(createField("Mã lớp:", txtClassId = new JTextField()));
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        content.add(createField("Tên lớp:", txtClassName = new JTextField()));
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        content.add(createField("GVCN:", txtTeacher = new JTextField()));
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        content.add(createField("Niên khóa:", txtAcademicYear = new JTextField("2023-2024")));
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Sĩ số tối đa
        JPanel pnlCap = new JPanel(new BorderLayout(0, 5));
        pnlCap.setBackground(Color.WHITE);
        pnlCap.add(new JLabel("Sĩ số tối đa:"), BorderLayout.NORTH);
        spinnerCapacity = new JSpinner(new SpinnerNumberModel(45, 30, 60, 1));
        pnlCap.add(spinnerCapacity, BorderLayout.CENTER);
        pnlCap.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        content.add(pnlCap);
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Ghi chú
        content.add(new JLabel("Ghi chú:"));
        txtNote = new JTextArea(3, 20);
        txtNote.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane scrollNote = new JScrollPane(txtNote);
        scrollNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(scrollNote);

        mainPanel.add(content, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createField(String label, JTextField tf) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(Color.WHITE);
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(tf, BorderLayout.CENTER);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        return p;
    }

    // ================= TABLE PANEL =================
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(secondaryColor);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(secondaryColor);
        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(20);
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) rowSorter.setRowFilter(null);
                else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
        searchPanel.add(txtSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"Mã lớp", "Tên lớp", "GVCN", "Sĩ số", "Niên khóa", "Ghi chú"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Style Header
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 35));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(primaryColor);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Arial", Font.BOLD, 12));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i=0; i<table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);

        // Click event
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if(row >= 0) loadData(table.convertRowIndexToModel(row));
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ================= BUTTON PANEL =================
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(Color.WHITE);

        panel.add(createButton("Thêm lớp", new Color(40, 167, 69), e -> addClass()));
        panel.add(createButton("Cập nhật", new Color(0, 123, 255), e -> updateClass()));
        panel.add(createButton("Xóa lớp", new Color(220, 53, 69), e -> deleteClass()));
        panel.add(createButton("Làm mới", new Color(108, 117, 125), e -> clearForm()));

        return panel;
    }

    private JButton createButton(String text, Color color, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        return btn;
    }

    // ================= LOGIC =================
    private void addClass() {
        if(txtClassId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Nhập mã lớp!"); return; }
        tableModel.addRow(new Object[]{
                txtClassId.getText(), txtClassName.getText(), txtTeacher.getText(),
                spinnerCapacity.getValue(), txtAcademicYear.getText(), txtNote.getText()
        });
        JOptionPane.showMessageDialog(this, "Thêm thành công!");
        clearForm();
    }

    private void updateClass() {
        int row = table.getSelectedRow();
        if(row < 0) return;
        int modelRow = table.convertRowIndexToModel(row);
        tableModel.setValueAt(txtClassId.getText(), modelRow, 0);
        tableModel.setValueAt(txtClassName.getText(), modelRow, 1);
        tableModel.setValueAt(txtTeacher.getText(), modelRow, 2);
        tableModel.setValueAt(spinnerCapacity.getValue(), modelRow, 3);
        tableModel.setValueAt(txtAcademicYear.getText(), modelRow, 4);
        tableModel.setValueAt(txtNote.getText(), modelRow, 5);
        JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
    }

    private void deleteClass() {
        int row = table.getSelectedRow();
        if(row >= 0 && JOptionPane.showConfirmDialog(this, "Xóa lớp này?", "Confirm", JOptionPane.YES_NO_OPTION) == 0) {
            tableModel.removeRow(table.convertRowIndexToModel(row));
            clearForm();
        }
    }

    private void loadData(int row) {
        txtClassId.setText(tableModel.getValueAt(row, 0).toString());
        txtClassName.setText(tableModel.getValueAt(row, 1).toString());
        txtTeacher.setText(tableModel.getValueAt(row, 2).toString());
        spinnerCapacity.setValue(Integer.parseInt(tableModel.getValueAt(row, 3).toString()));
        txtAcademicYear.setText(tableModel.getValueAt(row, 4).toString());
        txtNote.setText(tableModel.getValueAt(row, 5).toString());
    }

    private void clearForm() {
        txtClassId.setText(""); txtClassName.setText(""); txtTeacher.setText(""); txtNote.setText("");
        table.clearSelection();
    }

    private void backToDashboard() {
        this.dispose();
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }

    private void addSampleData() {
        tableModel.addRow(new Object[]{"6A1", "Lớp 6A1", "Nguyễn Văn A", 45, "2023-2024", "Lớp chọn"});
        tableModel.addRow(new Object[]{"7A2", "Lớp 7A2", "Trần Thị B", 42, "2023-2024", ""});
        tableModel.addRow(new Object[]{"8A1", "Lớp 8A1", "Lê Văn C", 40, "2023-2024", "Chuyên Toán"});
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClassManagement().setVisible(true));
    }
}