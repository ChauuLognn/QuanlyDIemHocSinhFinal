package UI;

import AccountManager.Account;
import ClassManager.Classes;
import Database.ClassDatabase;
import Database.SubjectDatabase;
import ReportManager.ReportService;
import SubjectManager.Subject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class ReportManagement extends JFrame {
    private Account currentAccount;
    private ReportService reportService = new ReportService();

    private JComboBox<String> cboClass;
    private JComboBox<Subject> cboSubject;
    private JComboBox<String> cboSemester;

    public ReportManagement(Account account) {
        this.currentAccount = account;
        setTitle("Xuất Báo Cáo & Thống Kê");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Chỉ đóng cửa sổ này, không tắt app
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel lblTitle = new JLabel("XUẤT BÁO CÁO HỆ THỐNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.decode("#1E40AF"));
        lblTitle.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Main Content
        JPanel mainPanel = new JPanel(new GridLayout(4, 2, 20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        mainPanel.setBackground(Color.WHITE);

        // 1. Chọn Lớp
        mainPanel.add(createLabel("Chọn Lớp học:"));
        cboClass = new JComboBox<>();
        loadClasses();
        mainPanel.add(cboClass);

        // 2. Chọn Môn (Chỉ dùng khi xuất bảng điểm)
        mainPanel.add(createLabel("Chọn Môn học:"));
        cboSubject = new JComboBox<>();
        loadSubjects();
        mainPanel.add(cboSubject);

        // 3. Chọn Học kỳ
        mainPanel.add(createLabel("Chọn Học kỳ:"));
        cboSemester = new JComboBox<>(new String[]{"Học kỳ 1", "Học kỳ 2"});
        mainPanel.add(cboSemester);

        add(mainPanel, BorderLayout.CENTER);

        // Footer (Buttons)
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton btnExportList = createButton("Xuất Danh Sách Lớp", Color.decode("#059669"));
        btnExportList.addActionListener(e -> {
            String classID = cboClass.getSelectedItem().toString().split(" - ")[0]; // Lấy mã lớp
            reportService.exportClassList(classID);
        });

        JButton btnExportScore = createButton("Xuất Bảng Điểm Môn", Color.decode("#2563EB"));
        btnExportScore.addActionListener(e -> {
            String classID = cboClass.getSelectedItem().toString().split(" - ")[0];
            Subject sub = (Subject) cboSubject.getSelectedItem();
            int semester = cboSemester.getSelectedIndex() + 1;

            reportService.exportScoreBoard(classID, sub.getId(), semester);
        });

        JButton btnBack = createButton("Thoát", Color.decode("#6B7280"));
        btnBack.addActionListener(e -> this.dispose());

        footer.add(btnExportList);
        footer.add(btnExportScore);
        footer.add(btnBack);

        add(footer, BorderLayout.SOUTH);
    }

    // --- Helper Methods ---

    private void loadClasses() {
        ArrayList<Classes> list = ClassDatabase.getClassDB().getAllClasses();
        for (Classes c : list) {
            // Hiển thị dạng "9A1 - Lớp 9A1" để dễ nhìn
            cboClass.addItem(c.getClassID() + " - " + c.getClassName());
        }
    }

    private void loadSubjects() {
        for (Subject s : SubjectDatabase.getAllSubjects()) {
            cboSubject.addItem(s);
        }
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(180, 40));
        return btn;
    }
}