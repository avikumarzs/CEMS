package ui;

import dao.EventDAO;
import models.Event;
import models.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    
    private JTable pendingTable;
    private DefaultTableModel tableModel;

    public AdminDashboard(User user) {
        setTitle("Admin Portal - " + user.getName());
        setSize(900, 600); // Made it a bit bigger for the new layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==========================================
        // 1. THE SIDEBAR (Dark Theme)
        // ==========================================
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(new Color(33, 37, 41)); // Dark Slate
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        // Sidebar Top: Branding & User Info
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
        nameLabel.setForeground(new Color(74, 191, 164)); // Mint Green accent

        brandingPanel.add(brandLabel);
        brandingPanel.add(userLabel);
        brandingPanel.add(nameLabel);
        sidebar.add(brandingPanel, BorderLayout.NORTH);

        // Sidebar Middle: Navigation Buttons
        JPanel navPanel = new JPanel(new GridLayout(5, 1, 0, 15));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(40, 0, 0, 0));

        JButton approveBtn = createSidebarButton("Approve Event", new Color(40, 167, 69));
        JButton refreshBtn = createSidebarButton("Refresh Queue", new Color(0, 102, 204));
        navPanel.add(approveBtn);
        navPanel.add(refreshBtn);
        sidebar.add(navPanel, BorderLayout.CENTER);

        // Sidebar Bottom: Log Out (Red)
        JButton logoutBtn = createSidebarButton("Log Out", new Color(220, 53, 69));
        sidebar.add(logoutBtn, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);

        // ==========================================
        // 2. THE MAIN CONTENT AREA (Light Theme)
        // ==========================================
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(248, 249, 250)); // Off-white
        mainContent.setBorder(new EmptyBorder(25, 30, 30, 30)); // Nice padding

        JLabel title = new JLabel("Pending Event Approvals");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(33, 37, 41));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainContent.add(title, BorderLayout.NORTH);

        // The Table
        String[] cols = {"Event ID", "Title", "Date", "Status", "Venue"};
        tableModel = new DefaultTableModel(cols, 0);
        pendingTable = new JTable(tableModel);
        pendingTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pendingTable.setRowHeight(30);
        pendingTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        pendingTable.getTableHeader().setBackground(new Color(233, 236, 239));
        pendingTable.setDefaultEditor(Object.class, null); 
        
        JScrollPane scrollPane = new JScrollPane(pendingTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        // ==========================================
        // 3. BUTTON ACTIONS
        // ==========================================
        refreshBtn.addActionListener(e -> loadPendingEvents());

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

        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginWindow().setVisible(true);
        });

        loadPendingEvents();
    }

    // Helper method to make sleek sidebar buttons
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

    private void loadPendingEvents() {
        tableModel.setRowCount(0);
        List<Event> events = new EventDAO().getPendingEvents();
        for (Event ev : events) {
            tableModel.addRow(new Object[]{ev.getEventId(), ev.getTitle(), ev.getEventDate(), ev.getStatus(), ev.getVenueId()});
        }
    }
}