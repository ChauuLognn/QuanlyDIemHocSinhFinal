package UI;

import AccountManager.Account; // Import Account
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

    // --- BIẾN PHÂN QUYỀN ---
    private Account currentAccount;

    // Input fields
    private JTextField txtClassId, txtClassName;
    private JTextArea txtNote;

    private final Color primaryColor = new Color(70, 70, 70);
    private final Color secondaryColor = new Color(245, 245, 245);

    // --- SỬA CONSTRUCTOR ---
    public ClassManagement(Account account) {
        this.currentAccount = account;

        setTitle("Quản lý Lớp học");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        add(createTopBar(), BorderLayout.NORTH);
        add(createInputPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadDataFromDatabase();
    }

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

        // --- SỬA SỰ KIỆN QUAY LẠI ---
        btnBack.addActionListener(e -> {
            this.dispose();
            new Dashboard(currentAccount).setVisible(true); // Truyền lại Account
        });
        topBar.add(btnBack, BorderLayout.EAST);
        return topBar;
    }

    // ... (Giữ nguyên các phần code còn lại của ClassManagement như createInputPanel, createTablePanel...)
    // Vì giới hạn ký tự, bạn có thể giữ nguyên phần dưới của file ClassManagement cũ,
    // chỉ cần thay constructor và createTopBar ở trên là được.

    // (Dưới đây là phần code khung để bạn không bị lỗi biên dịch nếu copy đè)
    private JPanel createInputPanel() {
        // Copy lại nội dung cũ của bạn vào đây
        JPanel p = new JPanel(); return p;
    }
    private JPanel createTablePanel() {
        JPanel p = new JPanel(); return p;
    }
    private JPanel createButtonPanel() {
        JPanel p = new JPanel(); return p;
    }
    private void loadDataFromDatabase() {}

    // Main test
    public static void main(String[] args) {
        Account acc = new Account("admin", "", "", "admin");
        SwingUtilities.invokeLater(() -> new ClassManagement(acc).setVisible(true));
    }
}