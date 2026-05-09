package ui;

import dao.VenueDAO;
import models.Venue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ManageVenuesWindow extends JFrame {

    private JTextField idField, locationField, capacityField;
    private JTable venueTable;
    private DefaultTableModel tableModel;
    private VenueDAO venueDAO = new VenueDAO();

    public ManageVenuesWindow() {
        setTitle("CEMS - Manage Venues");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(33, 37, 41));
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBorder(new EmptyBorder(60, 30, 40, 30));

        JLabel iconLabel = new JLabel("🏢");
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
        
        JLabel sideTitle = new JLabel("<html>Manage<br>Venues</html>");
        sideTitle.setFont(new Font("SansSerif", Font.BOLD, 32));
        sideTitle.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel("<html><br>Register new campus<br>locations or remove<br>obsolete venues.</html>");
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

        // Top Form: Add Venue
        JPanel addPanel = new JPanel(new GridLayout(2, 3, 15, 10));
        addPanel.setBackground(Color.WHITE);
        
        addPanel.add(createLabel("Venue ID (e.g., V004)"));
        addPanel.add(createLabel("Location Name"));
        addPanel.add(createLabel("Max Capacity"));
        
        idField = createField();
        locationField = createField();
        capacityField = createField();
        
        addPanel.add(idField);
        addPanel.add(locationField);
        addPanel.add(capacityField);

        JButton addBtn = new JButton("Add Venue");
        stylePrimaryButton(addBtn, new Color(40, 167, 69)); // Green
        
        JPanel topContainer = new JPanel(new BorderLayout(0, 15));
        topContainer.setBackground(Color.WHITE);
        topContainer.add(addPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(addBtn);
        topContainer.add(btnPanel, BorderLayout.SOUTH);
        
        mainContent.add(topContainer, BorderLayout.NORTH);

        // Bottom Table: View/Delete Venues
        String[] cols = {"Venue ID", "Location Name", "Capacity", "Status"};
        tableModel = new DefaultTableModel(cols, 0);
        venueTable = new JTable(tableModel);
        styleTable(venueTable);

        JScrollPane scrollPane = new JScrollPane(venueTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        JButton deleteBtn = new JButton("Delete Selected Venue");
        stylePrimaryButton(deleteBtn, new Color(220, 53, 69)); // Red
        
        JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomContainer.setBackground(Color.WHITE);
        bottomContainer.add(deleteBtn);
        mainContent.add(bottomContainer, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);

        // --- ACTIONS ---
        loadVenues();

        addBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String loc = locationField.getText().trim();
            String capStr = capacityField.getText().trim();

            if (id.isEmpty() || loc.isEmpty() || capStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all details.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!id.matches("^V\\d{3}$")) {
                JOptionPane.showMessageDialog(this, "Venue ID must start with 'V' followed by 3 digits (e.g., V001).", "Format Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int cap = Integer.parseInt(capStr);
                if (cap <= 0) throw new NumberFormatException();
                
                if (venueDAO.insertVenue(id, loc, cap)) {
                    JOptionPane.showMessageDialog(this, "Venue added successfully!");
                    idField.setText(""); locationField.setText(""); capacityField.setText("");
                    loadVenues(); // Refresh Table
                } else {
                    JOptionPane.showMessageDialog(this, "That Venue ID might already exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Capacity must be a valid positive number.", "Format Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = venueTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a venue to delete from the table.");
                return;
            }

            String id = (String) tableModel.getValueAt(row, 0);
            String loc = (String) tableModel.getValueAt(row, 1);

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to completely delete " + loc + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                // If this returns false, our DAO successfully protected the database!
                if (venueDAO.deleteVenue(id)) {
                    JOptionPane.showMessageDialog(this, "Venue deleted successfully.");
                    loadVenues(); 
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Deletion Blocked!\n\nThere are events currently scheduled at this venue.\nPlease cancel or relocate those events before deleting the venue.", 
                        "Venue In Use", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void loadVenues() {
        tableModel.setRowCount(0);
        List<Venue> venues = venueDAO.getAllVenues();
        for (Venue v : venues) {
            tableModel.addRow(new Object[]{v.getVenueId(), v.getLocation(), v.getCapacity(), v.getStatus()});
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