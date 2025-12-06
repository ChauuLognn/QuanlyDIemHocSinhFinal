package UI;

import AccountManager.Account;
import ClassManager.Classes;
import ClassManager.data.ClassDatabase;
import ClassManager.service.AddClass;
import ClassManager.service.DeleteClass;
import ClassManager.service.EditClass;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ClassManagement extends JFrame {
    private Account currentAccount;

    // --- COLORS & FONTS (Modern Style) ---
    private final Color primaryColor = Color.decode("#1E40AF");
    private final Color accentColor  = Color.decode("#3B82F6");
    private final Color bgColor      = Color.decode("#F3F4F6");
    private final Color cardColor    = Color.WHITE;
    private final Color textColor    = Color.decode("#111827");
    private final Color grayText     = Color.decode("#6B7280");

    private final Font fontBold = new Font("Segoe UI", Font.BOLD, 14);
    private final Font fontPlain = new Font("Segoe UI", Font.PLAIN, 14);

    // Components
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // Input fields
    private JTextField txtClassId, txtClassName;

    // Buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public ClassManagement(Account account) {
        this.currentAccount = account;

        setTitle("Quản lý Lớp học");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Top Bar
        add(createTopBar(), BorderLayout.NORTH);

        // 2. Main Content (Split Layout)
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Trái: Form nhập liệu
        mainPanel.add(createInputPanel(), BorderLayout.WEST);

        // Giữa: Bảng danh sách
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Load dữ liệu
        loadDataFromDatabase();
    }

    // ================= TOP BAR =================
    private JPanel createTopBar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setPreferredSize(new Dimension(0, 70));
        navbar.setBackground(cardColor);
        navbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));

        JLabel title = new JLabel("  QUẢN LÝ LỚP HỌC");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(primaryColor);
        title.setBorder(new EmptyBorder(0, 20, 0, 0));
        navbar.add(title, BorderLayout.WEST);

        JButton btnBack = new JButton("← Quay lại Dashboard");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(primaryColor);
        btnBack.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnBack.addActionListener(e -> {
            this.dispose();
            new Dashboard(currentAccount).setVisible(true);
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setBackground(cardColor);
        right.add(btnBack);
        navbar.add(right, BorderLayout.EAST);

        return navbar;
    }

    // ================= INPUT PANEL (LEFT) =================
    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cardColor);
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(229, 231, 235)),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel lblTitle = new JLabel("THÔNG TIN LỚP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(textColor);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Fields ---
        panel.add(createLabel("Mã lớp"));
        txtClassId = createTextField();
        panel.add(txtClassId);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createLabel("Tên lớp"));
        txtClassName = createTextField();
        panel.add(txtClassName);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- Buttons ---
        btnAdd = createButton("Thêm lớp", Color.decode("#10B981")); // Xanh lá
        btnAdd.addActionListener(e -> addClass());
        panel.add(btnAdd);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        btnUpdate = createButton("Cập nhật", Color.decode("#3B82F6")); // Xanh dương
        btnUpdate.addActionListener(e -> updateClass());
        panel.add(btnUpdate);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        btnDelete = createButton("Xóa lớp", Color.decode("#EF4444")); // Đỏ
        btnDelete.addActionListener(e -> deleteClass());
        panel.add(btnDelete);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        btnClear = createButton("Làm mới", grayText);
        btnClear.addActionListener(e -> clearForm());
        panel.add(btnClear);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(bgColor);

        // Search Bar
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(cardColor);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblSearch = new JLabel("Tìm kiếm:  ");
        lblSearch.setFont(fontBold);
        txtSearch = new JTextField();
        txtSearch.setFont(fontPlain);
        txtSearch.setBorder(null);
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) rowSorter.setRowFilter(null);
                else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"Mã lớp", "Tên lớp", "Sĩ số hiện tại"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(fontPlain);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(239, 246, 255)); // Xanh nhạt khi chọn
        table.setSelectionForeground(textColor);

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 45));
        header.setFont(fontBold);
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(grayText);

        // Center Align
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);

        // Click Event
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if(row >= 0) loadDataToForm(table.convertRowIndexToModel(row));
            }
        });

        return panel;
    }

    // ================= HELPER METHODS =================
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(grayText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(fontPlain);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Padding bên trong
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return tf;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(fontBold);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect đơn giản
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(color.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(color); }
        });
        return btn;
    }

    // ================= LOGIC =================
    private void loadDataFromDatabase() {
        tableModel.setRowCount(0);
        // Đồng bộ số lượng học sinh trước khi hiển thị
        ClassDatabase.getClassDB().syncAllClassesStudentCount();
        ArrayList<Classes> list = ClassDatabase.getClassDB().getAllClasses();

        for (Classes c : list) {
            tableModel.addRow(new Object[]{
                    c.getClassID(),
                    c.getClassName(),
                    c.getStudentNumber()
            });
        }
    }

    private void addClass() {
        try {
            String id = txtClassId.getText().trim();
            String name = txtClassName.getText().trim();

            AddClass service = new AddClass();
            service.add(id, name);

            loadDataFromDatabase();
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm lớp thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateClass() {
        int row = table.getSelectedRow();
        if(row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp để sửa!"); return; }

        try {
            int modelRow = table.convertRowIndexToModel(row);
            String oldID = tableModel.getValueAt(modelRow, 0).toString();

            String newId = txtClassId.getText().trim();
            String newName = txtClassName.getText().trim();

            EditClass service = new EditClass();
            service.edit(oldID, newId, newName);

            loadDataFromDatabase();
            clearForm();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteClass() {
        int row = table.getSelectedRow();
        if(row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp để xóa!"); return; }

        if (JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa lớp này?\nHành động này không thể hoàn tác.", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                int modelRow = table.convertRowIndexToModel(row);
                String id = tableModel.getValueAt(modelRow, 0).toString();

                DeleteClass service = new DeleteClass();
                service.delete(id);

                loadDataFromDatabase();
                clearForm();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadDataToForm(int row) {
        txtClassId.setText(tableModel.getValueAt(row, 0).toString());
        txtClassName.setText(tableModel.getValueAt(row, 1).toString());
    }

    private void clearForm() {
        txtClassId.setText(""); txtClassName.setText("");
        table.clearSelection();
    }

    // Main Test
    public static void main(String[] args) {
        Account mock = new Account("admin", "", "", "admin");
        SwingUtilities.invokeLater(() -> new ClassManagement(mock).setVisible(true));
    }
}