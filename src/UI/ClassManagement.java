package UI;

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
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // Input fields (Chỉ còn 2 trường)
    private JTextField txtClassId, txtClassName;
    private JTextArea txtNote;

    // Colors
    private final Color primaryColor = new Color(70, 70, 70);
    private final Color secondaryColor = new Color(245, 245, 245);

    public ClassManagement() {
        setTitle("Quản lý Lớp học");
        setSize(1000, 600); // Thu nhỏ cửa sổ lại cho gọn
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

        // --- TẢI DỮ LIỆU ---
        loadDataFromDatabase();
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

        JButton btnBack = new JButton("← Quay lại Dashboard");
        btnBack.setFont(new Font("Arial", Font.BOLD, 12));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(100, 100, 100));
        btnBack.setOpaque(true);
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            this.dispose();
            new Dashboard().setVisible(true);
        });
        topBar.add(btnBack, BorderLayout.EAST);
        return topBar;
    }

    // ================= INPUT PANEL (ĐÃ RÚT GỌN) =================
    private JPanel createInputPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(300, 0)); // Thu hẹp panel lại
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

        // Chỉ còn Mã lớp & Tên lớp
        content.add(createField("Mã lớp:", txtClassId = new JTextField()));
        content.add(Box.createRigidArea(new Dimension(0, 15)));

        content.add(createField("Tên lớp:", txtClassName = new JTextField()));
        content.add(Box.createRigidArea(new Dimension(0, 15)));

        // Ghi chú
        content.add(new JLabel("Ghi chú:"));
        txtNote = new JTextArea(5, 20); // Tăng chiều cao lên chút
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

    // ================= TABLE PANEL (ĐÃ RÚT GỌN CỘT) =================
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

        // Table (Bỏ cột GVCN, Niên khóa, Sĩ số tối đa)
        String[] cols = {"Mã lớp", "Tên lớp", "Sĩ số hiện tại"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(35); // Tăng chiều cao dòng cho thoáng
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

        // Center Align Cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        // Click event
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if(row >= 0) loadDataToForm(table.convertRowIndexToModel(row));
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

    private void loadDataFromDatabase() {
        tableModel.setRowCount(0);
        ClassDatabase.getClassDB().syncAllClassesStudentCount();
        ArrayList<Classes> list = ClassDatabase.getClassDB().getAllClasses();

        for (Classes c : list) {
            tableModel.addRow(new Object[]{
                    c.getClassID(),
                    c.getClassName(),
                    c.getStudentNumber() // Đã được sync chính xác
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
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void updateClass() {
        int row = table.getSelectedRow();
        if(row < 0) { JOptionPane.showMessageDialog(this, "Chọn lớp để sửa!"); return; }

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
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void deleteClass() {
        int row = table.getSelectedRow();
        if(row < 0) { JOptionPane.showMessageDialog(this, "Chọn lớp để xóa!"); return; }

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
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }

    private void loadDataToForm(int row) {
        txtClassId.setText(tableModel.getValueAt(row, 0).toString());
        txtClassName.setText(tableModel.getValueAt(row, 1).toString());
    }

    private void clearForm() {
        txtClassId.setText(""); txtClassName.setText(""); txtNote.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClassManagement().setVisible(true));
    }
}