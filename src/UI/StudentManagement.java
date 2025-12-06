package UI;

import AccountManager.Account;
import ClassManager.Classes;
import ClassManager.data.ClassDatabase;
import GradeManager.Grade;
import GradeManager.data.GradeDatabase;
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

    // --- COLORS & FONTS ---
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

    // Inputs
    private JTextField txtId, txtName;
    private JComboBox<String> cboClassInput, cboGender;
    private JTextField txtRegularScore, txtMidtermScore, txtFinalScore;

    // Buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public StudentManagement(Account account) {
        this.currentAccount = account;

        setTitle("Quản lý Học sinh");
        setSize(1350, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Top Bar
        add(createTopBar(), BorderLayout.NORTH);

        // 2. Main Content
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        mainPanel.add(createLeftPanel(), BorderLayout.WEST);
        mainPanel.add(createRightPanel(), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Load Data
        loadClassListToInput();
        loadDataFromDatabase();

        // Phân quyền
        applyPermissions();
    }

    // ================= 1. TOP BAR =================
    private JPanel createTopBar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBackground(cardColor);
        navbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, lineColor));

        JLabel title = new JLabel("  QUẢN LÝ HỌC SINH");
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

    // ================= 2. LEFT PANEL (FORM NHẬP LIỆU ĐÃ CHỈNH SỬA) =================
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cardColor);
        panel.setPreferredSize(new Dimension(340, 0));
        // Viền panel
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor),
                new EmptyBorder(25, 25, 25, 25) // Padding rộng hơn cho thoáng
        ));

        // --- GROUP 1: THÔNG TIN CƠ BẢN ---
        addHeader(panel, "THÔNG TIN HỌC SINH");

        panel.add(createLabel("Mã học sinh"));
        txtId = createTextField();
        panel.add(txtId);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Khoảng cách field

        panel.add(createLabel("Họ và tên"));
        txtName = createTextField();
        panel.add(txtName);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Hàng Lớp + Giới tính (Đặt chung 1 dòng)
        JPanel row = new JPanel(new GridLayout(1, 2, 15, 0));
        row.setBackground(cardColor);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JPanel pClass = new JPanel(new BorderLayout(0,5));
        pClass.setBackground(cardColor);
        pClass.add(createLabel("Lớp"), BorderLayout.NORTH);
        cboClassInput = createComboBox();
        pClass.add(cboClassInput, BorderLayout.CENTER);

        JPanel pGender = new JPanel(new BorderLayout(0,5));
        pGender.setBackground(cardColor);
        pGender.add(createLabel("Giới tính"), BorderLayout.NORTH);
        cboGender = new JComboBox<>(new String[]{"Nam", "Nữ"});
        styleComboBox(cboGender);
        pGender.add(cboGender, BorderLayout.CENTER);

        row.add(pClass);
        row.add(pGender);
        panel.add(row);

        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Cách xa nhóm điểm ra

        // --- GROUP 2: ĐIỂM SỐ ---
        addHeader(panel, "NHẬP ĐIỂM SỐ");

        // Các ô điểm
        panel.add(createLabel("Thường xuyên (Hệ số 1)"));
        txtRegularScore = createTextField();
        panel.add(txtRegularScore);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createLabel("Giữa kỳ (Hệ số 2)"));
        txtMidtermScore = createTextField();
        panel.add(txtMidtermScore);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createLabel("Cuối kỳ (Hệ số 3)"));
        txtFinalScore = createTextField();
        panel.add(txtFinalScore);

        panel.add(Box.createVerticalGlue()); // Đẩy nút xuống dưới cùng

        // --- BUTTONS ---
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10)); // Grid 2x2 nút to đẹp
        btnPanel.setBackground(cardColor);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        btnAdd = createButton("Thêm mới", Color.decode("#10B981"));   // Xanh lá
        btnUpdate = createButton("Cập nhật", Color.decode("#3B82F6")); // Xanh dương
        btnDelete = createButton("Xóa HS", Color.decode("#EF4444"));   // Đỏ
        btnClear = createButton("Làm mới", grayText);                  // Xám

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

    // ================= 3. RIGHT PANEL (BẢNG DỮ LIỆU) =================
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(bgColor);

        // --- SEARCH BAR (Sửa lại: Dùng chữ "Tìm kiếm" thay cho Icon) ---
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
        txtSearch.setBorder(null); // Bỏ viền input cho liền mạch với panel
        txtSearch.setPreferredSize(new Dimension(200, 30));

        // Thêm sự kiện tìm kiếm
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { searchStudent(); }
        });

        searchPanel.add(lblSearchText);
        searchPanel.add(txtSearch);

        // Bọc searchPanel vào 1 container để nó không bị giãn hết chiều ngang
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(bgColor);
        topContainer.add(searchPanel, BorderLayout.WEST);

        panel.add(topContainer, BorderLayout.NORTH);

        // --- TABLE ---
        String[] columns = {"Mã HS", "Họ và Tên", "Lớp", "GT", "Đ.TX", "Đ.GK", "Đ.CK", "ĐTB", "Xếp loại"};

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

        // Sắp xếp & Tìm kiếm
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Căn chỉnh cột
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(250); // Tên rộng
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);

        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 45));
        header.setFont(fontBold);
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(grayText);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, lineColor));

        // Renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 10, 0, 0));

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 1) table.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            else table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Logic màu điểm số
        DefaultTableCellRenderer scoreRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(JLabel.CENTER);
                if (!isSelected) {
                    try {
                        double s = Double.parseDouble(value.toString());
                        if (s < 5.0) c.setForeground(Color.decode("#EF4444")); // Đỏ
                        else if (s >= 8.0) c.setForeground(Color.decode("#10B981")); // Xanh
                        else c.setForeground(textColor);
                    } catch (Exception e) { c.setForeground(textColor); }
                }
                return c;
            }
        };
        for(int i=4; i<=7; i++) table.getColumnModel().getColumn(i).setCellRenderer(scoreRenderer);

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

    // ================= HELPER UI METHODS =================
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
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Chiều cao to hơn chút
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor),
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding trong
        ));
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
        ((JComponent) box.getRenderer()).setBorder(new EmptyBorder(5,5,5,5));
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

    // ================= LOGIC & PERMISSIONS =================
    private void applyPermissions() {
        if (currentAccount == null) return;
        if ("teacher".equals(currentAccount.getRole())) {
            btnAdd.setEnabled(false);
            btnDelete.setEnabled(false);
            btnAdd.setBackground(lineColor);
            btnDelete.setBackground(lineColor);

            txtId.setEditable(false);
            txtName.setEditable(false);
            cboClassInput.setEnabled(false);
            cboGender.setEnabled(false);
            btnUpdate.setText("Lưu điểm");
        }
    }

    private void loadClassListToInput() {
        cboClassInput.removeAllItems();
        ArrayList<Classes> classes = ClassDatabase.getClassDB().getAllClasses();
        for (Classes c : classes) {
            cboClassInput.addItem(c.getClassID() + " - " + c.getClassName());
        }
    }

    private void loadDataFromDatabase() {
        tableModel.setRowCount(0);
        Map<String, String> classMap = new HashMap<>();
        for (Classes c : ClassDatabase.getClassDB().getAllClasses()) classMap.put(c.getClassID(), c.getClassName());

        HashMap<String, Grade> gradeMap = GradeDatabase.getGradeDB().getAllGradesAsMap();
        ArrayList<Student> list = new StudentService().getAllStudents();

        for (Student s : list) {
            double r=0, m=0, f=0;
            Grade g = gradeMap.get(s.getStudentID());
            if (g != null) { r=g.getRegularScore(); m=g.getMidtermScore(); f=g.getFinalScore(); }

            double avg = studentService.calculateAvg(r, m, f);
            String className = classMap.getOrDefault(s.getStudentClass(), s.getStudentClass());

            tableModel.addRow(new Object[]{
                    s.getStudentID(), s.getStudentName(), className, s.getGender(),
                    r, m, f, String.format("%.2f", avg), studentService.classify(avg)
            });
        }
    }

    // --- CRUD ---
    private void addStudent() {
        if (!validateInput()) return;
        try {
            String cid = cboClassInput.getSelectedItem().toString().split(" - ")[0].trim();
            new AddStudent().add(txtId.getText().trim(), txtName.getText().trim(), cid, cboGender.getSelectedItem().toString());
            new GradeManager.service.AddGrade().addScore(txtId.getText().trim(), parseScore(txtRegularScore), parseScore(txtMidtermScore), parseScore(txtFinalScore));

            loadDataFromDatabase();
            clearFields();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage()); }
    }

    private void updateStudent() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn dòng để sửa!"); return; }
        try {
            String oldID = tableModel.getValueAt(table.convertRowIndexToModel(row), 0).toString();
            String cid = cboClassInput.getSelectedItem().toString().split(" - ")[0].trim();

            if (!"teacher".equals(currentAccount.getRole())) {
                new EditStudent().edit(oldID, txtId.getText().trim(), txtName.getText().trim(), cid, cboGender.getSelectedItem().toString());
            }
            new GradeManager.service.EditGrade().editScore(txtId.getText().trim(), parseScore(txtRegularScore), parseScore(txtMidtermScore), parseScore(txtFinalScore));

            loadDataFromDatabase();
            clearFields();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage()); }
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn dòng để xóa!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Xóa học sinh này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                String id = tableModel.getValueAt(table.convertRowIndexToModel(row), 0).toString();
                new DeleteStudent().delete(id);
                try { GradeDatabase.getGradeDB().deleteGrade(id); } catch(Exception ignored){}
                loadDataFromDatabase();
                clearFields();
                JOptionPane.showMessageDialog(this, "Đã xóa!");
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage()); }
        }
    }

    private void clearFields() {
        txtId.setText(""); txtName.setText("");
        txtRegularScore.setText(""); txtMidtermScore.setText(""); txtFinalScore.setText("");
        table.clearSelection();
    }

    private void loadDataToForm(int row) {
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtName.setText(tableModel.getValueAt(row, 1).toString());
        String cid = tableModel.getValueAt(row, 2).toString();
        for (int i=0; i<cboClassInput.getItemCount(); i++) {
            if (cboClassInput.getItemAt(i).contains(cid)) {
                cboClassInput.setSelectedIndex(i);
                break;
            }
        }
        cboGender.setSelectedItem(tableModel.getValueAt(row, 3).toString());
        txtRegularScore.setText(tableModel.getValueAt(row, 4).toString());
        txtMidtermScore.setText(tableModel.getValueAt(row, 5).toString());
        txtFinalScore.setText(tableModel.getValueAt(row, 6).toString());
    }

    private double parseScore(JTextField tf) {
        try { return Double.parseDouble(tf.getText().trim()); } catch (Exception e) { return 0; }
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

    public static void main(String[] args) {
        Account mock = new Account("admin", "", "", "admin");
        SwingUtilities.invokeLater(() -> new StudentManagement(mock).setVisible(true));
    }
}