package ui;

import dao.EventDAO;
import models.Event;
import models.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    
    private JTable pendingTable;
    private DefaultTableModel tableModel;

    public AdminDashboard(User user) {
        setTitle("Admin Portal - " + user.getName());
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximized from start
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==========================================
        // 1. THE SIDEBAR (Dark Theme)
        // ==========================================
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(new Color(33, 37, 41)); 
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        // Branding
        JPanel brandingPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        brandingPanel.setOpaque(false);
        
        JLabel brandLabel = new JLabel("CEMS Admin", SwingConstants.CENTER);
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        brandLabel.setForeground(Color.WHITE);
        
        JLabel userLabel = new JLabel("Logged in as:", SwingConstants.CENTER);
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLabel.setForeground(new Color(173, 181, 189));
        
        JLabel nameLabel = new JLabel(user.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(new Color(74, 191, 164));

        brandingPanel.add(brandLabel);
        brandingPanel.add(userLabel);
        brandingPanel.add(nameLabel);
        sidebar.add(brandingPanel, BorderLayout.NORTH);

        // Navigation Buttons
        // Increased rows in GridLayout to accommodate the new button nicely
        JPanel navPanel = new JPanel(new GridLayout(6, 1, 0, 15));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(40, 0, 0, 0));

        JButton approveBtn = createSidebarButton("Approve Event", new Color(40, 167, 69));
        JButton rejectBtn = createSidebarButton("Reject Event", new Color(255, 152, 0)); 
        
        // --- NEW: Add Venue Button ---
        JButton addVenueBtn = createSidebarButton("Add New Venue", new Color(108, 117, 125));
        // -----------------------------

        JButton refreshBtn = createSidebarButton("Refresh Queue", new Color(0, 102, 204));
        
        navPanel.add(approveBtn);
        navPanel.add(rejectBtn); 
        navPanel.add(addVenueBtn); // Added to layout
        navPanel.add(refreshBtn);
        sidebar.add(navPanel, BorderLayout.CENTER);

        // Sidebar Bottom: Log Out
        JButton logoutBtn = createSidebarButton("Log Out", new Color(220, 53, 69));
        sidebar.add(logoutBtn, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);

        // ==========================================
        // 2. THE MAIN CONTENT AREA
        // ==========================================
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(248, 249, 250)); 
        mainContent.setBorder(new EmptyBorder(25, 40, 40, 40)); 

        JLabel title = new JLabel("Pending Event Approvals");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(33, 37, 41));
        title.setBorder(new EmptyBorder(0, 0, 25, 0));
        mainContent.add(title, BorderLayout.NORTH);

        String[] cols = {"Event ID", "Title", "Date", "Status", "Venue"};
        tableModel = new DefaultTableModel(cols, 0);
        pendingTable = new JTable(tableModel);
        
        styleTable(pendingTable);
        
        JScrollPane scrollPane = new JScrollPane(pendingTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(233, 236, 239)));
        mainContent.add(scrollPane, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        // ==========================================
        // 3. BUTTON ACTIONS
        // ==========================================
        
        refreshBtn.addActionListener(e -> loadPendingEvents());

        // --- NEW: Open Add Venue Window ---
        addVenueBtn.addActionListener(e -> {
            new AddVenueWindow().setVisible(true);
        });
        // ----------------------------------

        // APPROVE ACTION
        approveBtn.addActionListener(e -> {
            int row = pendingTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an event to approve!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = (String) tableModel.getValueAt(row, 0);
            if (new EventDAO().approveEvent(id)) {
                JOptionPane.showMessageDialog(this, "Event Approved Successfully!");
                loadPendingEvents();
            }
        });

        // REJECT ACTION
        rejectBtn.addActionListener(e -> {
            int row = pendingTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an event to reject!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = (String) tableModel.getValueAt(row, 0);
            
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to reject this event?", "Confirm Rejection", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (new EventDAO().rejectEvent(id)) {
                    JOptionPane.showMessageDialog(this, "Event Rejected.");
                    loadPendingEvents();
                } else {
                    JOptionPane.showMessageDialog(this, "Error rejecting event.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginWindow().setVisible(true);
        });

        loadPendingEvents();
    }

    private JButton createSidebarButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(45); 
        table.setFont(new Font("SansSerif", Font.PLAIN, 15));
        table.setShowVerticalLines(false); 
        table.setGridColor(new Color(233, 236, 239));
        table.setSelectionBackground(new Color(226, 240, 253)); 
        table.setSelectionForeground(new Color(33, 37, 41));
        table.setDefaultEditor(Object.class, null); 

        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(241, 243, 245));
        table.getTableHeader().setForeground(new Color(73, 80, 87));
        table.getTableHeader().setPreferredSize(new Dimension(0, 50)); 
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(206, 212, 218)));

        DefaultTableCellRenderer paddedRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(paddedRenderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(100); 
        table.getColumnModel().getColumn(1).setPreferredWidth(400); 
        table.getColumnModel().getColumn(2).setPreferredWidth(150); 
        table.getColumnModel().getColumn(3).setPreferredWidth(150); 
        table.getColumnModel().getColumn(4).setPreferredWidth(250); 
    }

    private void loadPendingEvents() {
        tableModel.setRowCount(0);
        List<Event> events = new EventDAO().getPendingEvents();
        for (Event ev : events) {
            tableModel.addRow(new Object[]{ev.getEventId(), ev.getTitle(), ev.getEventDate(), ev.getStatus(), ev.getVenueId()});
        }
    }
}