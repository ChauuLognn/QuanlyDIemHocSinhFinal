package UI;

import AccountManager.Account;
import ClassManager.Classes;
import ClassManager.data.ClassDatabase;
import ClassManager.service.AddClass;
import ClassManager.service.DeleteClass;
import ClassManager.service.EditClass;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ClassManagement extends JFrame {
    private Account currentAccount;

    // --- COLORS & FONTS ---
    private final Color primaryColor = Color.decode("#1E40AF"); // Xanh đậm
    private final Color bgColor      = Color.decode("#F3F4F6"); // Nền xám nhạt
    private final Color cardColor    = Color.WHITE;             // Nền trắng
    private final Color textColor    = Color.decode("#111827"); // Chữ đen
    private final Color grayText     = Color.decode("#6B7280"); // Chữ xám
    private final Color lineColor    = Color.decode("#E5E7EB"); // Viền xám

    private final Font fontBold = new Font("Segoe UI", Font.BOLD, 13);
    private final Font fontPlain = new Font("Segoe UI", Font.PLAIN, 13);

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
        mainPanel.add(createLeftPanel(), BorderLayout.WEST);

        // Giữa: Bảng danh sách
        mainPanel.add(createRightPanel(), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Load dữ liệu
        loadDataFromDatabase();
    }

    // ================= 1. TOP BAR =================
    private JPanel createTopBar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBackground(cardColor);
        navbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, lineColor));

        JLabel title = new JLabel("  QUẢN LÝ LỚP HỌC");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(primaryColor);
        title.setBorder(new EmptyBorder(0, 15, 0, 0));
        navbar.add(title, BorderLayout.WEST);

        JButton btnBack = new JButton("← Dashboard");
        btnBack.setFont(fontBold);
        btnBack.setForeground(grayText);
        btnBack.setBackground(cardColor);
        btnBack.setBorder(new EmptyBorder(0, 15, 0, 15));
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnBack.setForeground(primaryColor); }
            public void mouseExited(MouseEvent e) { btnBack.setForeground(grayText); }
        });

        btnBack.addActionListener(e -> {
            this.dispose();
            new Dashboard(currentAccount).setVisible(true);
        });

        navbar.add(btnBack, BorderLayout.EAST);
        return navbar;
    }

    // ================= 2. LEFT PANEL (INPUT & BUTTONS) =================
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cardColor);
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor),
                new EmptyBorder(25, 25, 25, 25)
        ));

        // --- Header ---
        JLabel lblTitle = new JLabel("THÔNG TIN LỚP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(primaryColor);
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

        // Đẩy nút xuống dưới cùng
        panel.add(Box.createVerticalGlue());

        // --- Buttons Grid ---
        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 10, 10)); // 2 hàng 2 cột
        btnGrid.setBackground(cardColor);
        btnGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        btnAdd = createButton("Thêm", Color.decode("#10B981"));    // Xanh lá
        btnUpdate = createButton("Sửa", Color.decode("#3B82F6"));  // Xanh dương
        btnDelete = createButton("Xóa", Color.decode("#EF4444"));  // Đỏ
        btnClear = createButton("Mới", grayText);                  // Xám

        btnAdd.addActionListener(e -> addClass());
        btnUpdate.addActionListener(e -> updateClass());
        btnDelete.addActionListener(e -> deleteClass());
        btnClear.addActionListener(e -> clearForm());

        btnGrid.add(btnAdd);
        btnGrid.add(btnUpdate);
        btnGrid.add(btnDelete);
        btnGrid.add(btnClear);

        panel.add(btnGrid);

        return panel;
    }

    // ================= 3. RIGHT PANEL (TABLE) =================
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(bgColor);

        // --- Search Bar ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setBackground(cardColor);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor),
                new EmptyBorder(8, 15, 8, 15)
        ));

        JLabel lblSearch = new JLabel("Tìm kiếm:  ");
        lblSearch.setFont(fontBold);
        lblSearch.setForeground(textColor);

        txtSearch = new JTextField(20);
        txtSearch.setFont(fontPlain);
        txtSearch.setBorder(null);
        txtSearch.setPreferredSize(new Dimension(200, 30));
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) rowSorter.setRowFilter(null);
                else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);

        // Container để không bị giãn
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(bgColor);
        topContainer.add(searchPanel, BorderLayout.WEST);

        panel.add(topContainer, BorderLayout.NORTH);

        // --- Table ---
        String[] cols = {"Mã lớp", "Tên lớp", "Sĩ số hiện tại"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(fontPlain);
        table.setGridColor(lineColor);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(textColor);

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 45));
        header.setFont(fontBold);
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(grayText);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, lineColor));

        // Center Align
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(lineColor));
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
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(grayText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(fontPlain);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return tf;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(fontBold);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if(btn.isEnabled()) btn.setBackground(color.darker()); }
            public void mouseExited(MouseEvent e) { if(btn.isEnabled()) btn.setBackground(color); }
        });
        return btn;
    }

    // ================= LOGIC =================
    private void loadDataFromDatabase() {
        tableModel.setRowCount(0);
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
            if(id.isEmpty() || name.isEmpty()){ JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!"); return; }

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

        if (JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa lớp này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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