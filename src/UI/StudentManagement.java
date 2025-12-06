package UI;

import AccountManager.Account;
import ClassManager.Classes;
import ClassManager.data.ClassDatabase;
import GradeManager.Grade;
import GradeManager.data.GradeDatabase;
import StudentManager.Student;
import StudentManager.data.StudentDatabase;
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
import java.util.List;
import java.util.Map;

public class StudentManagement extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private StudentService studentService = new StudentService();

    // --- BIẾN PHÂN QUYỀN ---
    private Account currentAccount;

    // Input fields
    private JTextField txtId, txtName;
    private JComboBox<String> cboClassInput;
    private JComboBox<String> cboGender;
    private JTextField txtRegularScore;
    private JTextField txtMidtermScore;
    private JTextField txtFinalScore;

    // Component phân quyền (để khóa nút)
    private JButton btnAdd, btnUpdate, btnDelete;

    private JComboBox<String> cboFilter;
    private final Color primaryColor = new Color(70, 70, 70);
    private final Color secondaryColor = new Color(245, 245, 245);

    // --- SỬA CONSTRUCTOR: NHẬN THAM SỐ ACCOUNT ---
    public StudentManagement(Account account) {
        this.currentAccount = account; // Lưu tài khoản

        setTitle("Quản lý điểm học sinh - " + account.getUsername());
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        add(createTopBar(), BorderLayout.NORTH);
        add(createInputPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadDataFromDatabase();
        loadClassListToInput();

        // GỌI HÀM PHÂN QUYỀN SAU KHI GIAO DIỆN ĐÃ TẠO XONG
        applyPermissions();
    }

    // --- LOGIC PHÂN QUYỀN ---
    private void applyPermissions() {
        if (currentAccount == null) return;
        String role = currentAccount.getRole();

        // Nếu là GIÁO VIÊN: Chỉ được sửa điểm, không được thêm/xóa HS
        if ("teacher".equals(role)) {
            btnAdd.setEnabled(false);    // Khóa nút Thêm
            btnDelete.setEnabled(false); // Khóa nút Xóa

            // Khóa các ô thông tin cá nhân (chỉ cho sửa điểm)
            txtId.setEditable(false);
            txtName.setEditable(false);
            cboClassInput.setEnabled(false);
            cboGender.setEnabled(false);

            // Đổi màu nút bị khóa để dễ nhận biết
            btnAdd.setBackground(Color.GRAY);
            btnDelete.setBackground(Color.GRAY);

            // Đổi text nút cập nhật cho rõ nghĩa
            btnUpdate.setText("Cập nhật điểm số");
        }
    }

    private void loadClassListToInput() {
        cboClassInput.removeAllItems();
        ArrayList<Classes> classes = ClassDatabase.getClassDB().getAllClasses();
        for (Classes c : classes) {
            cboClassInput.addItem(c.getClassID() + " - " + c.getClassName());
        }
    }

    // ================= TOP BAR =================
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(primaryColor);
        topBar.setPreferredSize(new Dimension(0, 60));
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("QUẢN LÝ ĐIỂM HỌC SINH");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        topBar.add(title, BorderLayout.WEST);

        JButton btnBack = new JButton("← Quay lại Dashboard");
        btnBack.setFont(new Font("Arial", Font.BOLD, 12));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(100, 100, 100));
        btnBack.setOpaque(true);
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnBack.setBackground(new Color(120, 120, 120)); }
            public void mouseExited(MouseEvent e) { btnBack.setBackground(new Color(100, 100, 100)); }
        });

        // SỬA NÚT BACK: Truyền lại Account về Dashboard
        btnBack.addActionListener(e -> {
            this.dispose();
            new Dashboard(currentAccount).setVisible(true);
        });

        topBar.add(btnBack, BorderLayout.EAST);
        return topBar;
    }

    // ================= INPUT PANEL =================
    private JPanel createInputPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(340, 0));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblInfo = new JLabel("1. Thông tin học sinh");
        lblInfo.setFont(new Font("Arial", Font.BOLD, 14));
        lblInfo.setForeground(primaryColor);
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblInfo);
        content.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel row1 = new JPanel(new GridLayout(1, 2, 10, 0));
        row1.setBackground(Color.WHITE);
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        txtId = createTextField();
        cboGender = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboGender.setFont(new Font("Arial", Font.PLAIN, 13));

        row1.add(createFieldPanel("Mã HS:", txtId));
        row1.add(createFieldPanel("Giới tính:", cboGender));
        content.add(row1);
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        content.add(createFieldPanel("Họ và tên:", txtName = createTextField()));
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        cboClassInput = new JComboBox<>();
        cboClassInput.setFont(new Font("Arial", Font.PLAIN, 13));
        content.add(createFieldPanel("Lớp:", cboClassInput));

        content.add(Box.createRigidArea(new Dimension(0, 25)));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        content.add(sep);
        content.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel lblScore = new JLabel("2. Nhập điểm chi tiết");
        lblScore.setFont(new Font("Arial", Font.BOLD, 14));
        lblScore.setForeground(primaryColor);
        lblScore.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblScore);
        content.add(Box.createRigidArea(new Dimension(0, 15)));

        txtRegularScore = createTextField();
        txtMidtermScore = createTextField();
        txtFinalScore = createTextField();

        content.add(createFieldPanel("Điểm thường xuyên (Hệ số 1):", txtRegularScore));
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(createFieldPanel("Điểm giữa kì (Hệ số 2):", txtMidtermScore));
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(createFieldPanel("Điểm cuối kì (Hệ số 3):", txtFinalScore));
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

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

    // ================= TABLE PANEL =================
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(secondaryColor);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(secondaryColor);
        searchPanel.add(new JLabel("Tìm kiếm:"));

        txtSearch = new JTextField(20);
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { searchStudent(); }
        });
        searchPanel.add(txtSearch);

        searchPanel.add(new JLabel("  Lọc lớp:"));

        cboFilter = new JComboBox<>();
        cboFilter.addItem("Tất cả");

        cboFilter.addActionListener(e -> {
            if (cboFilter.getSelectedItem() == null) return;
            String selected = cboFilter.getSelectedItem().toString();
            if (selected.equals("Tất cả")) rowSorter.setRowFilter(null);
            else rowSorter.setRowFilter(RowFilter.regexFilter(selected, 2));
        });
        searchPanel.add(cboFilter);

        panel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = {
                "Mã HS", "Họ tên", "Lớp", "GT",
                "Đ.Thường xuyên", "Đ.Giữa kì", "Đ.Cuối kì",
                "ĐTB", "Xếp loại"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setGridColor(new Color(220, 220, 220));
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 35));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(primaryColor);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Arial", Font.BOLD, 12));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                if (!isSelected) {
                    try {
                        String text = value.toString();
                        if (column >= 4 && column <= 7) {
                            double score = Double.parseDouble(text);
                            if (score < 5.0) {
                                c.setForeground(Color.RED);
                                c.setFont(new Font("Arial", Font.BOLD, 12));
                            } else if (score >= 8.0) {
                                c.setForeground(new Color(0, 100, 0));
                                c.setFont(new Font("Arial", Font.BOLD, 12));
                            } else {
                                c.setForeground(Color.BLACK);
                                c.setFont(new Font("Arial", Font.PLAIN, 12));
                            }
                        } else if (column == 8) {
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

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    loadDataToForm(table.convertRowIndexToModel(row));
                }
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
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        // Gán các nút vào biến toàn cục để hàm applyPermissions() sử dụng
        btnAdd = createButton("Thêm", new Color(40, 167, 69));
        btnUpdate = createButton("Cập nhật", new Color(0, 123, 255));
        btnDelete = createButton("Xóa", new Color(220, 53, 69));
        JButton btnClear = createButton("Làm mới", new Color(108, 117, 125));

        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearFields());

        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnClear);
        return panel;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 25, 10, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if(btn.isEnabled()) btn.setBackground(bg.darker());
            }
            public void mouseExited(MouseEvent e) {
                if(btn.isEnabled()) btn.setBackground(bg);
            }
        });
        return btn;
    }

    // ================= BUSINESS LOGIC =================
    private void updateClassFilter() {
        String currentSelection = (cboFilter.getSelectedItem() != null) ? cboFilter.getSelectedItem().toString() : "Tất cả";
        cboFilter.removeAllItems();
        cboFilter.addItem("Tất cả");

        List<String> classes = studentService.getUniqueClassList();

        for (String className : classes) {
            cboFilter.addItem(className);
        }

        if (currentSelection.equals("Tất cả") || classes.contains(currentSelection)) {
            cboFilter.setSelectedItem(currentSelection);
        }
    }

    private void loadDataFromDatabase() {
        tableModel.setRowCount(0);

        // 1. TẢI TÊN LỚP
        Map<String, String> classMap = new HashMap<>();
        for (Classes c : ClassDatabase.getClassDB().getAllClasses()) {
            classMap.put(c.getClassID(), c.getClassName());
        }

        // 2. TẢI ĐIỂM
        HashMap<String, Grade> gradeMap = GradeDatabase.getGradeDB().getAllGradesAsMap();

        ArrayList<Student> list = studentService.getAllStudents();

        for (Student s : list) {
            double reg = 0, mid = 0, fin = 0;
            Grade g = gradeMap.get(s.getStudentID());
            if (g != null) {
                reg = g.getRegularScore();
                mid = g.getMidtermScore();
                fin = g.getFinalScore();
            }

            double avg = studentService.calculateAvg(reg, mid, fin);
            String rank = studentService.classify(avg);

            String classID = s.getStudentClass();
            String classNameDisplay = classMap.getOrDefault(classID, classID);

            tableModel.addRow(new Object[]{
                    s.getStudentID(),
                    s.getStudentName(),
                    classNameDisplay,
                    s.getGender(),
                    reg, mid, fin, String.format("%.2f", avg), rank
            });
        }
        updateClassFilter();
    }

    private void addStudent() {
        if (!validateInput()) return;
        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String gender = cboGender.getSelectedItem().toString();

            String selectedClass = cboClassInput.getSelectedItem().toString();
            String classID = selectedClass.split(" - ")[0].trim();

            AddStudent service = new AddStudent();
            service.add(id, name, classID, gender);

            double reg = parseScore(txtRegularScore);
            double mid = parseScore(txtMidtermScore);
            double fin = parseScore(txtFinalScore);

            GradeManager.service.AddGrade gradeService = new GradeManager.service.AddGrade();
            gradeService.addScore(id, reg, mid, fin);

            loadDataFromDatabase();
            clearFields();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void updateStudent() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn dòng để sửa!"); return; }
        if (!validateInput()) return;

        try {
            int modelRow = table.convertRowIndexToModel(row);
            String oldID = tableModel.getValueAt(modelRow, 0).toString();

            String newId = txtId.getText().trim();
            String name = txtName.getText().trim();
            String gender = cboGender.getSelectedItem().toString();

            String selectedClass = cboClassInput.getSelectedItem().toString();
            String classID = selectedClass.split(" - ")[0].trim();

            EditStudent service = new EditStudent();
            service.edit(oldID, newId, name, classID, gender);

            double reg = parseScore(txtRegularScore);
            double mid = parseScore(txtMidtermScore);
            double fin = parseScore(txtFinalScore);

            GradeManager.service.EditGrade gradeEditService = new GradeManager.service.EditGrade();
            gradeEditService.editScore(newId, reg, mid, fin);

            loadDataFromDatabase();
            clearFields();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn dòng để xóa!"); return; }

        if (JOptionPane.showConfirmDialog(this, "Xóa học sinh này và toàn bộ điểm?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                int modelRow = table.convertRowIndexToModel(row);
                String id = tableModel.getValueAt(modelRow, 0).toString();

                DeleteStudent service = new DeleteStudent();
                service.delete(id);
                try { GradeDatabase.getGradeDB().deleteGrade(id); } catch (Exception ignored) {}

                loadDataFromDatabase();
                clearFields();
                JOptionPane.showMessageDialog(this, "Đã xóa thành công!");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        txtId.setText(""); txtName.setText("");
        if (cboClassInput.getItemCount() > 0) cboClassInput.setSelectedIndex(0);
        txtRegularScore.setText(""); txtMidtermScore.setText(""); txtFinalScore.setText("");
        table.clearSelection();
    }

    private void loadDataToForm(int row) {
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtName.setText(tableModel.getValueAt(row, 1).toString());

        String classIDInTable = tableModel.getValueAt(row, 2).toString();
        for (int i = 0; i < cboClassInput.getItemCount(); i++) {
            String item = cboClassInput.getItemAt(i);
            if (item.startsWith(classIDInTable + " -") || item.equals(classIDInTable)) {
                cboClassInput.setSelectedIndex(i);
                break;
            }
        }

        String gender = tableModel.getValueAt(row, 3).toString();
        if (gender.equalsIgnoreCase("Nam") || gender.equalsIgnoreCase("Nữ")) {
            cboGender.setSelectedItem(gender);
        } else {
            cboGender.setSelectedIndex(0);
        }

        txtRegularScore.setText(tableModel.getValueAt(row, 4).toString());
        txtMidtermScore.setText(tableModel.getValueAt(row, 5).toString());
        txtFinalScore.setText(tableModel.getValueAt(row, 6).toString());
    }

    private double parseScore(JTextField tf) throws NumberFormatException {
        String text = tf.getText().trim();
        if (text.isEmpty()) return 0;
        double s = Double.parseDouble(text);
        if (s < 0 || s > 10) throw new NumberFormatException("Điểm phải từ 0-10");
        return s;
    }


    private boolean validateInput() {
        if (txtId.getText().isEmpty() || txtName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã HS và Tên!");
            return false;
        }
        return true;
    }

    private void searchStudent() {
        String text = txtSearch.getText();
        if (text.trim().length() == 0) rowSorter.setRowFilter(null);
        else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
    }

    // Main này chỉ để test UI, thực tế sẽ gọi từ Dashboard
    public static void main(String[] args) {
        Account testAcc = new Account("admin", "123", "", "admin");
        SwingUtilities.invokeLater(() -> new StudentManagement(testAcc).setVisible(true));
    }
}