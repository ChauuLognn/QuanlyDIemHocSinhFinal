package UI;

import AccountManager.Account;
import ClassManager.Classes;
import Database.ClassDatabase;
import GradeManager.Grade;
import Database.GradeDatabase;
import StudentManager.Student;
import StudentManager.service.StudentService;
import SubjectManager.Subject; // NOTE: Import Subject
import Database.SubjectDatabase; // NOTE: Import SubjectDatabase

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
import java.util.List;

public class GradeManagement extends JFrame {
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
    private JComboBox<String> cboFilterClass;
    private JComboBox<String> cboSemester;
    private JComboBox<Subject> cboSubject; // NOTE: Đổi thành JComboBox<Subject>

    // Input fields
    private JTextField txtId, txtName;
    private JTextField txtRegular, txtMid, txtFinal;
    private JButton btnSave, btnClear;

    public GradeManagement(Account account) {
        this.currentAccount = account;
        setTitle("Quản lý Bảng điểm");
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

        loadDataFromDatabase();
    }

    // ================= TOP BAR =================
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

    // ================= LEFT PANEL (NHẬP ĐIỂM) =================
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cardColor);
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor),
                new EmptyBorder(25, 25, 25, 25)
        ));

        // Info
        addHeader(panel, "HỌC SINH ĐANG CHỌN");
        panel.add(createLabel("Mã học sinh"));
        txtId = createTextField();
        txtId.setEditable(false);
        txtId.setBackground(bgColor);
        panel.add(txtId);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createLabel("Họ và tên"));
        txtName = createTextField();
        txtName.setEditable(false);
        txtName.setBackground(bgColor);
        panel.add(txtName);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Scores
        addHeader(panel, "NHẬP ĐIỂM SỐ");
        panel.add(createLabel("Điểm Thường xuyên (Hệ số 1)"));
        txtRegular = createTextField();
        panel.add(txtRegular);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createLabel("Điểm Giữa kỳ (Hệ số 2)"));
        txtMid = createTextField();
        panel.add(txtMid);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createLabel("Điểm Cuối kỳ (Hệ số 3)"));
        txtFinal = createTextField();
        panel.add(txtFinal);

        panel.add(Box.createVerticalGlue());

        // Buttons
        btnSave = createButton("Lưu điểm", Color.decode("#10B981")); // Xanh lá
        btnSave.addActionListener(e -> saveGrade());
        panel.add(btnSave);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        btnClear = createButton("Hủy chọn", grayText);
        btnClear.addActionListener(e -> clearForm());
        panel.add(btnClear);

        return panel;
    }

    // ================= RIGHT PANEL (BẢNG + FILTER) =================
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(bgColor);

        // Filter Bar Container
        JPanel topContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topContainer.setBackground(cardColor);
        topContainer.setBorder(BorderFactory.createLineBorder(lineColor));

        // 1. Search
        JLabel lblSearch = new JLabel("Tìm kiếm:"); lblSearch.setFont(fontBold);
        txtSearch = new JTextField(15);
        txtSearch.setFont(fontPlain);
        txtSearch.setPreferredSize(new Dimension(150, 30));
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filter(); }
        });

        // 2. Class Filter
        JLabel lblFilter = new JLabel("Lớp:"); lblFilter.setFont(fontBold);
        cboFilterClass = new JComboBox<>();
        cboFilterClass.addItem("Tất cả");
        cboFilterClass.setFont(fontPlain);
        cboFilterClass.setBackground(Color.WHITE);
        cboFilterClass.setPreferredSize(new Dimension(100, 30));

        // Load classes list
        ArrayList<Classes> classes = ClassDatabase.getClassDB().getAllClasses();
        for (Classes c : classes) cboFilterClass.addItem(c.getClassName());
        cboFilterClass.addActionListener(e -> filter());

        // 3. Semester Filter
        JLabel lblSem = new JLabel("Học kỳ:"); lblSem.setFont(fontBold);
        cboSemester = new JComboBox<>(new String[]{"Học kỳ 1", "Học kỳ 2"});
        cboSemester.setFont(fontPlain);
        cboSemester.setBackground(Color.WHITE);
        cboSemester.setPreferredSize(new Dimension(100, 30));
        cboSemester.addActionListener(e -> loadDataFromDatabase());

        // 4. Subject Filter (NOTE: Thêm phần này)
        JLabel lblSub = new JLabel("Môn:"); lblSub.setFont(fontBold);
        cboSubject = new JComboBox<>();
        cboSubject.setFont(fontPlain);
        cboSubject.setBackground(Color.WHITE);
        cboSubject.setPreferredSize(new Dimension(150, 30));

        // NOTE: Load danh sách môn từ DB
        for (Subject s : SubjectDatabase.getAllSubjects()) {
            cboSubject.addItem(s);
        }
        cboSubject.addActionListener(e -> loadDataFromDatabase()); // NOTE: Load lại bảng khi chọn môn

        topContainer.add(lblSearch);
        topContainer.add(txtSearch);
        topContainer.add(Box.createHorizontalStrut(10));
        topContainer.add(lblFilter);
        topContainer.add(cboFilterClass);
        topContainer.add(Box.createHorizontalStrut(10));
        topContainer.add(lblSem);
        topContainer.add(cboSemester);
        topContainer.add(Box.createHorizontalStrut(10)); // Khoảng cách
        topContainer.add(lblSub); // NOTE: Thêm label Môn
        topContainer.add(cboSubject); // NOTE: Thêm combo Môn

        panel.add(topContainer, BorderLayout.NORTH);

        // Table
        String[] cols = {"Mã HS", "Họ Tên", "Lớp", "Đ.TX", "Đ.GK", "Đ.CK", "ĐTB", "Xếp loại"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
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

        // Column Width
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);

        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 45));
        header.setFont(fontBold);
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(grayText);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, lineColor));

        // Alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 10, 0, 0));

        for (int i = 0; i < table.getColumnCount(); i++) {
            if(i==1) table.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            else table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Click Event
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
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
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
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if(btn.isEnabled()) btn.setBackground(color.darker()); }
            public void mouseExited(MouseEvent e) { if(btn.isEnabled()) btn.setBackground(color); }
        });
        return btn;
    }

    // ================= LOGIC =================
    private void loadDataFromDatabase() {
        tableModel.setRowCount(0);

        int semester = cboSemester.getSelectedIndex() + 1;
        Subject selectedSubject = (Subject) cboSubject.getSelectedItem(); // NOTE: Lấy Subject đang chọn
        if (selectedSubject == null) return;
        String subjectID = selectedSubject.getId(); // NOTE: Lấy ID môn học

        // NOTE: Lấy điểm của môn học đó, trong học kỳ đó
        // (Bạn cần viết hàm getGradesBySubjectAndSemester trong GradeDatabase nếu chưa có, hoặc query từng em như dưới đây)

        ArrayList<Student> list = new StudentService().getAllStudents();
        StudentService service = new StudentService();

        for (Student s : list) {
            // NOTE: Lấy điểm cụ thể của HS, Môn, Kỳ
            Grade g = GradeDatabase.getGradeDB().getGrade(s.getStudentID(), subjectID, semester);

            double r=0, m=0, f=0;
            if (g != null) {
                r = g.getRegularScore();
                m = g.getMidtermScore();
                f = g.getFinalScore();
            }

            // NOTE: Tính ĐTB theo công thức cấp 2 (TX + 2*GK + 3*CK)/6
            double avg = (r + m*2 + f*3) / 6.0;

            tableModel.addRow(new Object[]{
                    s.getStudentID(),
                    s.getStudentName(),
                    s.getStudentClass(), // Hiển thị lớp (hoặc lấy từ Map nếu cần chuẩn)
                    r, m, f,
                    String.format("%.2f", avg),
                    classify(avg) // NOTE: Hàm xếp loại
            });
        }
        filter();
    }

    private String classify(double avg) {
        if (avg >= 8.0) return "Giỏi";
        if (avg >= 6.5) return "Khá";
        if (avg >= 5.0) return "TB";
        return "Yếu";
    }

    private void filter() {
        String text = txtSearch.getText();
        String cls = cboFilterClass.getSelectedItem().toString();

        List<RowFilter<Object,Object>> filters = new ArrayList<>();

        if (text.trim().length() > 0) filters.add(RowFilter.regexFilter("(?i)" + text));
        if (!"Tất cả".equals(cls)) filters.add(RowFilter.regexFilter(cls, 2));

        if (filters.isEmpty()) rowSorter.setRowFilter(null);
        else rowSorter.setRowFilter(RowFilter.andFilter(filters));
    }

    private void loadForm(int row) {
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtName.setText(tableModel.getValueAt(row, 1).toString());
        txtRegular.setText(tableModel.getValueAt(row, 3).toString());
        txtMid.setText(tableModel.getValueAt(row, 4).toString());
        txtFinal.setText(tableModel.getValueAt(row, 5).toString());
    }

    private void saveGrade() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa chọn học sinh!");
            return;
        }
        try {
            double r = Double.parseDouble(txtRegular.getText());
            double m = Double.parseDouble(txtMid.getText());
            double f = Double.parseDouble(txtFinal.getText());
            int semester = cboSemester.getSelectedIndex() + 1;
            Subject sub = (Subject) cboSubject.getSelectedItem(); // NOTE: Lấy môn học

            // NOTE: Gọi hàm lưu điểm có subjectID
            GradeDatabase.getGradeDB().saveGrade(txtId.getText(), sub.getId(), semester, r, m, f);

            loadDataFromDatabase();
            JOptionPane.showMessageDialog(this, "Lưu điểm thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtRegular.setText("");
        txtMid.setText("");
        txtFinal.setText("");
        table.clearSelection();
    }
}