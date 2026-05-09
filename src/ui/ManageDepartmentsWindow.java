package ui;

import dao.DepartmentDAO;
import models.Department;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ManageDepartmentsWindow extends JFrame {

    private JTextField idField, nameField;
    private JTable deptTable;
    private DefaultTableModel tableModel;
    private DepartmentDAO deptDAO = new DepartmentDAO();

    public ManageDepartmentsWindow() {
        setTitle("CEMS - Manage Departments");
        // FIX 1: Slightly wider window to give the table and fields more breathing room
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(33, 37, 41));
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBorder(new EmptyBorder(60, 30, 40, 30));

        JLabel iconLabel = new JLabel("📚");
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
        
        JLabel sideTitle = new JLabel("<html>Manage<br>Departments</html>");
        // FIX 2: Scaled down font size from 32 to 28 so "Departments" stops overflowing
        sideTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        sideTitle.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel("<html><br>Add new academic<br>departments or remove<br>obsolete ones.</html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel.setForeground(new Color(173, 181, 189));

        sidebar.add(iconLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(sideTitle);
        sidebar.add(descLabel);
        add(sidebar, BorderLayout.WEST);

        // --- MAIN CONTENT ---
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Top Form: Add Department
        JPanel addPanel = new JPanel(new GridLayout(2, 2, 20, 10)); // Increased horizontal gap to 20
        addPanel.setBackground(Color.WHITE);
        
        addPanel.add(createLabel("Department ID (e.g., CS01)"));
        addPanel.add(createLabel("Department Name"));
        
        idField = createField();
        nameField = createField();
        addPanel.add(idField);
        addPanel.add(nameField);

        JButton addBtn = new JButton("Add Department");
        stylePrimaryButton(addBtn, new Color(40, 167, 69)); // Green
        
        // FIX 3: Rebuilt the top container to stack the button nicely underneath the fields
        JPanel topContainer = new JPanel(new BorderLayout(0, 15));
        topContainer.setBackground(Color.WHITE);
        topContainer.add(addPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(addBtn);
        topContainer.add(btnPanel, BorderLayout.SOUTH);
        
        mainContent.add(topContainer, BorderLayout.NORTH);

        // Bottom Table: View/Delete Departments
        String[] cols = {"Dept ID", "Department Name"};
        tableModel = new DefaultTableModel(cols, 0);
        deptTable = new JTable(tableModel);
        styleTable(deptTable);

        JScrollPane scrollPane = new JScrollPane(deptTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        JButton deleteBtn = new JButton("Delete Department");
        stylePrimaryButton(deleteBtn, new Color(220, 53, 69)); // Red
        
        JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomContainer.setBackground(Color.WHITE);
        bottomContainer.add(deleteBtn);
        mainContent.add(bottomContainer, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);

        // --- ACTIONS ---
        loadDepartments();

        addBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (deptDAO.insertDepartment(id, name)) {
                JOptionPane.showMessageDialog(this, "Department added successfully!");
                idField.setText("");
                nameField.setText("");
                loadDepartments(); // Refresh Table
            } else {
                JOptionPane.showMessageDialog(this, "Error adding department. ID might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = deptTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a department to delete from the table.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = (String) tableModel.getValueAt(row, 0);
            String name = (String) tableModel.getValueAt(row, 1);

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete " + name + "?\nUsers in this department will be safely unassigned.", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                if (deptDAO.deleteDepartment(id)) {
                    JOptionPane.showMessageDialog(this, "Department deleted successfully.");
                    loadDepartments(); // Refresh Table
                } else {
                    JOptionPane.showMessageDialog(this, "Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void loadDepartments() {
        tableModel.setRowCount(0);
        List<Department> depts = deptDAO.getAllDepartments();
        for (Department d : depts) {
            tableModel.addRow(new Object[]{d.getDeptId(), d.getName()});
        }
    }

    // --- STYLING HELPERS ---
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(Color.GRAY);
        return lbl;
    }

    private JTextField createField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 42));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        return field;
    }

    private void stylePrimaryButton(JButton btn, Color bg) {
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40); 
        table.setFont(new Font("SansSerif", Font.PLAIN, 15));
        table.setShowVerticalLines(false); 
        table.setGridColor(new Color(233, 236, 239));
        table.setSelectionBackground(new Color(226, 240, 253)); 
        table.setSelectionForeground(new Color(33, 37, 41));
        table.setDefaultEditor(Object.class, null); 

        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(241, 243, 245));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40)); 
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(206, 212, 218)));

        DefaultTableCellRenderer padded = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return c;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(padded);
    }
}