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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ReportManagement extends JFrame {
    private Account currentAccount;
    private ReportService reportService = new ReportService();

    private JComboBox<String> cboClass;
    private JComboBox<Subject> cboSubject;
    private JComboBox<String> cboSemester;

    private final Color primaryColor = Color.decode("#1E40AF");
    private final Color cardColor = Color.WHITE;
    private final Color grayText = Color.decode("#6B7280");
    private final Color lineColor = Color.decode("#E5E7EB");
    private final Font fontBold = new Font("Segoe UI", Font.BOLD, 14);

    public ReportManagement(Account account) {
        this.currentAccount = account;
        setTitle("Xuất Báo Cáo & Thống Kê");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createTopBar(), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createLabel("Chọn Lớp học:"));
        cboClass = new JComboBox<>();
        cboClass.setBackground(Color.WHITE);
        loadClasses();
        mainPanel.add(cboClass);

        mainPanel.add(createLabel("Chọn Môn học:"));
        cboSubject = new JComboBox<>();
        cboSubject.setBackground(Color.WHITE);
        loadSubjects();
        mainPanel.add(cboSubject);

        mainPanel.add(createLabel("Chọn Học kỳ:"));
        cboSemester = new JComboBox<>(new String[]{"Học kỳ 1", "Học kỳ 2"});
        cboSemester.setBackground(Color.WHITE);
        mainPanel.add(cboSemester);

        add(mainPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton btnExportList = createButton("Xuất Danh Sách Lớp", Color.decode("#059669"));
        btnExportList.addActionListener(e -> {
            if (cboClass.getSelectedItem() == null) return;
            String classID = cboClass.getSelectedItem().toString().split(" - ")[0];
            reportService.exportClassList(classID);
        });

        JButton btnExportScore = createButton("Xuất Bảng Điểm", Color.decode("#2563EB"));
        btnExportScore.addActionListener(e -> {
            if (cboClass.getSelectedItem() == null || cboSubject.getSelectedItem() == null) return;
            String classID = cboClass.getSelectedItem().toString().split(" - ")[0];
            Subject sub = (Subject) cboSubject.getSelectedItem();
            int semester = cboSemester.getSelectedIndex() + 1;
            reportService.exportScoreBoard(classID, sub.getId(), semester);
        });

        footer.add(btnExportList);
        footer.add(btnExportScore);

        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createTopBar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBackground(cardColor);
        navbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, lineColor));

        JLabel title = new JLabel("  XUẤT BÁO CÁO HỆ THỐNG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(primaryColor);
        title.setBorder(new EmptyBorder(0, 15, 0, 0));
        navbar.add(title, BorderLayout.WEST);

        JButton btnBack = new JButton("← Dashboard");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setForeground(grayText);
        btnBack.setBackground(cardColor);
        btnBack.setBorder(new EmptyBorder(0, 15, 0, 20));
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnBack.setForeground(primaryColor);
            }

            public void mouseExited(MouseEvent e) {
                btnBack.setForeground(grayText);
            }
        });

        btnBack.addActionListener(e -> {
            this.dispose();
            new Dashboard(currentAccount).setVisible(true);
        });

        navbar.add(btnBack, BorderLayout.EAST);
        return navbar;
    }

    private void loadClasses() {
        ArrayList<Classes> list = ClassDatabase.getInstance().getAllClasses();
        for (Classes c : list) cboClass.addItem(c.getClassID() + " - " + c.getClassName());
    }

    private void loadSubjects() {
        for (Subject s : SubjectDatabase.getAllSubjects()) cboSubject.addItem(s);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(fontBold);
        lbl.setForeground(Color.decode("#374151"));
        return lbl;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(fontBold);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }
}