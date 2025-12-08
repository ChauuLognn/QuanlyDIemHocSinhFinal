package UI;

import AccountManager.Account;
import ClassManager.Classes;
import ClassManager.data.ClassDatabase;
import GradeManager.Grade;
import GradeManager.data.GradeDatabase;
import StudentManager.Student;
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

public class GradeManagement extends JFrame {
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
    private JComboBox<String> cboFilterClass;

    // Input fields (Chỉ để nhập điểm)
    private JTextField txtId, txtName; // Readonly
    private JTextField txtRegular, txtMid, txtFinal;
    private JButton btnSave, btnClear;

    public GradeManagement(Account account) {
        this.currentAccount = account;
        setTitle("Quản lý Bảng điểm");
        setSize(1350, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createTopBar(), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        mainPanel.add(createLeftPanel(), BorderLayout.WEST);
        mainPanel.add(createRightPanel(), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        loadDataFromDatabase();
    }

    private JPanel createTopBar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBackground(cardColor);
        navbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, lineColor));

        JLabel title = new JLabel("  QUẢN LÝ BẢNG ĐIỂM");
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
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Info
        addHeader(panel, "HỌC SINH ĐANG CHỌN");
        panel.add(createLabel("Mã học sinh"));
        txtId = createTextField(); txtId.setEditable(false);
        panel.add(txtId);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createLabel("Họ và tên"));
        txtName = createTextField(); txtName.setEditable(false);
        panel.add(txtName);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Scores
        addHeader(panel, "NHẬP ĐIỂM");
        panel.add(createLabel("Điểm Thường xuyên"));
        txtRegular = createTextField();
        panel.add(txtRegular);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createLabel("Điểm Giữa kỳ"));
        txtMid = createTextField();
        panel.add(txtMid);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createLabel("Điểm Cuối kỳ"));
        txtFinal = createTextField();
        panel.add(txtFinal);

        panel.add(Box.createVerticalGlue());

        // Button
        btnSave = createButton("Lưu điểm", Color.decode("#3B82F6"));
        btnSave.addActionListener(e -> saveGrade());
        panel.add(btnSave);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        btnClear = createButton("Hủy chọn", grayText);
        btnClear.addActionListener(e -> clearForm());
        panel.add(btnClear);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(bgColor);

        // Filter Bar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        top.setBackground(cardColor);
        top.setBorder(BorderFactory.createLineBorder(lineColor));

        JLabel lblSearch = new JLabel("Tìm kiếm:"); lblSearch.setFont(fontBold);
        txtSearch = new JTextField(15); txtSearch.setFont(fontPlain);
        txtSearch.setPreferredSize(new Dimension(200, 30));
        txtSearch.addKeyListener(new KeyAdapter() { public void keyReleased(KeyEvent e) { filter(); }});

        JLabel lblFilter = new JLabel("Lọc lớp:"); lblFilter.setFont(fontBold);
        cboFilterClass = new JComboBox<>();
        cboFilterClass.addItem("Tất cả");
        cboFilterClass.setFont(fontPlain);
        cboFilterClass.setBackground(Color.WHITE);
        // Load classes
        ArrayList<Classes> classes = ClassDatabase.getClassDB().getAllClasses();
        for (Classes c : classes) cboFilterClass.addItem(c.getClassName());

        cboFilterClass.addActionListener(e -> filter());

        top.add(lblSearch); top.add(txtSearch);
        top.add(lblFilter); top.add(cboFilterClass);
        panel.add(top, BorderLayout.NORTH);

        // Table
        String[] cols = {"Mã HS", "Họ Tên", "Lớp", "Đ.TX", "Đ.GK", "Đ.CK", "ĐTB", "Xếp loại"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(fontPlain);
        table.setGridColor(lineColor);
        table.setShowVerticalLines(false);

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Config columns
        table.getColumnModel().getColumn(1).setPreferredWidth(200);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 45));
        header.setFont(fontBold);
        header.setBackground(new Color(249, 250, 251));

        // Click
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) loadForm(table.convertRowIndexToModel(row));
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(lineColor));
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // Helper
    private void addHeader(JPanel p, String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 12)); l.setForeground(primaryColor);
        l.setAlignmentX(Component.LEFT_ALIGNMENT); p.add(l); p.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    private JLabel createLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.PLAIN, 12)); l.setForeground(grayText);
        l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }
    private JTextField createTextField() {
        JTextField t = new JTextField(); t.setFont(fontPlain); t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        t.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(lineColor), BorderFactory.createEmptyBorder(5,10,5,10)));
        return t;
    }
    private JButton createButton(String t, Color c) {
        JButton b = new JButton(t); b.setFont(fontBold); b.setForeground(Color.WHITE); b.setBackground(c);
        b.setFocusPainted(false); b.setBorderPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); b.setAlignmentX(Component.LEFT_ALIGNMENT);
        return b;
    }

    // Logic
    private void loadDataFromDatabase() {
        tableModel.setRowCount(0);
        HashMap<String, Grade> gradeMap = GradeDatabase.getGradeDB().getAllGradesAsMap();
        Map<String, String> classMap = new HashMap<>();
        for (Classes c : ClassDatabase.getClassDB().getAllClasses()) classMap.put(c.getClassID(), c.getClassName());

        ArrayList<Student> list = new StudentService().getAllStudents();
        StudentService service = new StudentService();

        for (Student s : list) {
            double r=0, m=0, f=0;
            Grade g = gradeMap.get(s.getStudentID());
            if (g != null) { r=g.getRegularScore(); m=g.getMidtermScore(); f=g.getFinalScore(); }

            double avg = service.calculateAvg(r, m, f);
            String className = classMap.getOrDefault(s.getStudentClass(), s.getStudentClass());

            tableModel.addRow(new Object[]{
                    s.getStudentID(), s.getStudentName(), className,
                    r, m, f, String.format("%.2f", avg), service.classify(avg)
            });
        }
    }

    private void filter() {
        String text = txtSearch.getText();
        String cls = cboFilterClass.getSelectedItem().toString();

        List<RowFilter<Object,Object>> filters = new ArrayList<>();
        if (text.trim().length() > 0) filters.add(RowFilter.regexFilter("(?i)" + text));
        if (!"Tất cả".equals(cls)) filters.add(RowFilter.regexFilter(cls, 2)); // Cột lớp là index 2

        rowSorter.setRowFilter(RowFilter.andFilter(filters));
    }

    private void loadForm(int row) {
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtName.setText(tableModel.getValueAt(row, 1).toString());
        txtRegular.setText(tableModel.getValueAt(row, 3).toString());
        txtMid.setText(tableModel.getValueAt(row, 4).toString());
        txtFinal.setText(tableModel.getValueAt(row, 5).toString());
    }

    private void saveGrade() {
        if (txtId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Chưa chọn học sinh!"); return; }
        try {
            double r = Double.parseDouble(txtRegular.getText());
            double m = Double.parseDouble(txtMid.getText());
            double f = Double.parseDouble(txtFinal.getText());

            new GradeManager.service.EditGrade().editScore(txtId.getText(), r, m, f);

            loadDataFromDatabase();
            JOptionPane.showMessageDialog(this, "Lưu điểm thành công!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage()); }
    }

    private void clearForm() {
        txtId.setText(""); txtName.setText(""); txtRegular.setText(""); txtMid.setText(""); txtFinal.setText("");
        table.clearSelection();
    }
}