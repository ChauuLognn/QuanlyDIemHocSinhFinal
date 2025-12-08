package UI;

import AccountManager.Account;
import ClassManager.Classes;
import Database.ClassDatabase;
import StudentManager.Student;
import StudentManager.service.AddStudent;
import StudentManager.service.DeleteStudent;
import StudentManager.service.EditStudent;
import StudentManager.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentManagement extends JFrame {
    private Account currentAccount;

    // --- COLORS ---
    private final Color primaryColor = Color.decode("#1E40AF");
    private final Color bgColor      = Color.decode("#F3F4F6");
    private final Color cardColor    = Color.WHITE;
    private final Color textColor    = Color.decode("#111827");
    private final Color grayText     = Color.decode("#6B7280");
    private final Color lineColor    = Color.decode("#E5E7EB");

    private final Font fontBold  = new Font("Segoe UI", Font.BOLD, 13);
    private final Font fontPlain = new Font("Segoe UI", Font.PLAIN, 13);

    // Components
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private StudentService studentService = new StudentService();

    // Inputs (BỎ PHẦN ĐIỂM SỐ)
    private JTextField txtId, txtName;
    private JComboBox<String> cboClassInput, cboGender;

    // Buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public StudentManagement(Account account) {
        this.currentAccount = account;
        setTitle("Quản lý Hồ sơ Học sinh");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createTopBar(), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Trái: Input Form (Chỉ còn thông tin cá nhân)
        mainPanel.add(createLeftPanel(), BorderLayout.WEST);
        // Phải: Bảng danh sách
        mainPanel.add(createRightPanel(), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        loadClassListToInput();
        loadDataFromDatabase();

        // Phân quyền: Nếu là GV thì chỉ được xem, không được sửa hồ sơ (Hoặc ẩn luôn trang này tùy bạn)
        if ("teacher".equals(currentAccount.getRole())) {
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Giáo viên vui lòng sang trang 'Quản lý Điểm' để nhập điểm.");
        }
    }

    private JPanel createTopBar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBackground(cardColor);
        navbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, lineColor));

        JLabel title = new JLabel("  HỒ SƠ HỌC SINH");
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
        btnBack.addActionListener(e -> {
            this.dispose();
            new Dashboard(currentAccount).setVisible(true);
        });
        navbar.add(btnBack, BorderLayout.EAST);
        return navbar;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cardColor);
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor),
                new EmptyBorder(25, 25, 25, 25)
        ));

        // --- GROUP 1: THÔNG TIN ---
        addHeader(panel, "THÔNG TIN CÁ NHÂN");

        panel.add(createLabel("Mã học sinh"));
        txtId = createTextField();
        panel.add(txtId);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createLabel("Họ và tên"));
        txtName = createTextField();
        panel.add(txtName);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createLabel("Lớp"));
        cboClassInput = createComboBox();
        panel.add(cboClassInput);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createLabel("Giới tính"));
        cboGender = new JComboBox<>(new String[]{"Nam", "Nữ"});
        styleComboBox(cboGender);
        panel.add(cboGender);

        panel.add(Box.createVerticalGlue()); // Đẩy nút xuống đáy

        // --- BUTTONS ---
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setBackground(cardColor);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        btnAdd = createButton("Thêm mới", Color.decode("#10B981"));
        btnUpdate = createButton("Cập nhật", Color.decode("#3B82F6"));
        btnDelete = createButton("Xóa HS", Color.decode("#EF4444"));
        btnClear = createButton("Làm mới", grayText);

        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearFields());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        panel.add(btnPanel);
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(bgColor);

        // Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setBackground(cardColor);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor),
                new EmptyBorder(8, 15, 8, 15)
        ));

        JLabel lblSearchText = new JLabel("Tìm kiếm:  ");
        lblSearchText.setFont(fontBold);
        lblSearchText.setForeground(textColor);

        txtSearch = new JTextField(25);
        txtSearch.setFont(fontPlain);
        txtSearch.setBorder(null);
        txtSearch.setPreferredSize(new Dimension(250, 30));
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { searchStudent(); }
        });

        searchPanel.add(lblSearchText);
        searchPanel.add(txtSearch);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(bgColor);
        topContainer.add(searchPanel, BorderLayout.WEST);
        panel.add(topContainer, BorderLayout.NORTH);

        // Table (Bỏ cột điểm)
        String[] columns = {"Mã HS", "Họ và Tên", "Lớp", "GT"};
        tableModel = new DefaultTableModel(columns, 0) {
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

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 45));
        header.setFont(fontBold);
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(grayText);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, lineColor));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 10, 0, 0));

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 1) table.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            else table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) loadDataToForm(table.convertRowIndexToModel(row));
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(lineColor));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // --- HELPER METHODS (Giống file cũ nhưng rút gọn) ---
    private void addHeader(JPanel p, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(primaryColor);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(grayText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(fontPlain);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return tf;
    }
    private JComboBox<String> createComboBox() {
        JComboBox<String> box = new JComboBox<>();
        styleComboBox(box);
        return box;
    }
    private void styleComboBox(JComboBox box) {
        box.setFont(fontPlain);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.setBackground(Color.WHITE);
    }
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(fontBold);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // --- LOGIC ---
    private void loadClassListToInput() {
        cboClassInput.removeAllItems();
        ArrayList<Classes> classes = ClassDatabase.getClassDB().getAllClasses();
        for (Classes c : classes) cboClassInput.addItem(c.getClassID() + " - " + c.getClassName());
    }

    private void loadDataFromDatabase() {
        tableModel.setRowCount(0);
        Map<String, String> classMap = new HashMap<>();
        for (Classes c : ClassDatabase.getClassDB().getAllClasses()) classMap.put(c.getClassID(), c.getClassName());

        ArrayList<Student> list = studentService.getAllStudents();
        for (Student s : list) {
            String className = classMap.getOrDefault(s.getStudentClass(), s.getStudentClass());
            tableModel.addRow(new Object[]{s.getStudentID(), s.getStudentName(), className, s.getGender()});
        }
    }

    private void addStudent() {
        if (!validateInput()) return;
        try {
            String cid = cboClassInput.getSelectedItem().toString().split(" - ")[0].trim();
            new AddStudent().add(txtId.getText().trim(), txtName.getText().trim(), cid, cboGender.getSelectedItem().toString());
            loadDataFromDatabase();
            clearFields();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage()); }
    }

    private void updateStudent() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        try {
            String oldID = tableModel.getValueAt(table.convertRowIndexToModel(row), 0).toString();
            String cid = cboClassInput.getSelectedItem().toString().split(" - ")[0].trim();
            new EditStudent().edit(oldID, txtId.getText().trim(), txtName.getText().trim(), cid, cboGender.getSelectedItem().toString());
            loadDataFromDatabase();
            clearFields();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage()); }
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        if (JOptionPane.showConfirmDialog(this, "Xóa học sinh này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                String id = tableModel.getValueAt(table.convertRowIndexToModel(row), 0).toString();
                new DeleteStudent().delete(id);
                loadDataFromDatabase();
                clearFields();
                JOptionPane.showMessageDialog(this, "Đã xóa!");
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage()); }
        }
    }

    private void clearFields() {
        txtId.setText(""); txtName.setText(""); table.clearSelection();
    }

    private void loadDataToForm(int row) {
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtName.setText(tableModel.getValueAt(row, 1).toString());
        String cid = tableModel.getValueAt(row, 2).toString();
        for (int i=0; i<cboClassInput.getItemCount(); i++) {
            if (cboClassInput.getItemAt(i).contains(cid)) { cboClassInput.setSelectedIndex(i); break; }
        }
        cboGender.setSelectedItem(tableModel.getValueAt(row, 3).toString());
    }

    private boolean validateInput() {
        if (txtId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Nhập Mã HS!"); return false; }
        return true;
    }

    private void searchStudent() {
        String text = txtSearch.getText();
        if (text.trim().length() == 0) rowSorter.setRowFilter(null);
        else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
    }
}