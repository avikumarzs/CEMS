package ui;

import models.User;
import utils.HttpUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.http.HttpResponse;

public class AdminDashboard extends JFrame {
    
    private JTable pendingTable;
    private DefaultTableModel tableModel;

    public AdminDashboard(User user) {
        setTitle("Admin Portal - " + user.getName());
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
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

        JPanel navPanel = new JPanel(new GridLayout(7, 1, 0, 15));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(40, 0, 0, 0));

        JButton approveBtn = createSidebarButton("Approve Event", new Color(40, 167, 69));
        JButton rejectBtn = createSidebarButton("Reject Event", new Color(255, 152, 0)); 
        JButton addVenueBtn = createSidebarButton("Manage Venues", new Color(108, 117, 125));
        JButton manageDeptsBtn = createSidebarButton("Manage Departments", new Color(108, 117, 125));
        JButton refreshBtn = createSidebarButton("Refresh Queue", new Color(0, 102, 204));
        
        navPanel.add(approveBtn);
        navPanel.add(rejectBtn); 
        navPanel.add(addVenueBtn);
        navPanel.add(manageDeptsBtn);
        navPanel.add(refreshBtn);
        sidebar.add(navPanel, BorderLayout.CENTER);

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

        String[] cols = {"Event ID", "Title", "Date", "Venue", "Proposed By", "Status"};
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

        addVenueBtn.addActionListener(e -> new ManageVenuesWindow().setVisible(true));

        // --- UPDATED: Route Approve Action Through API ---
        approveBtn.addActionListener(e -> {
            int row = pendingTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an event to approve!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = (String) tableModel.getValueAt(row, 0);
            
            HttpResponse<String> response = HttpUtils.updateEventStatus(id, "Scheduled");
            if (response != null && response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Event Approved Successfully!");
                loadPendingEvents();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to approve event.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- UPDATED: Route Reject Action Through API ---
        rejectBtn.addActionListener(e -> {
            int row = pendingTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an event to reject!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = (String) tableModel.getValueAt(row, 0);
            
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to reject this event?", "Confirm Rejection", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                HttpResponse<String> response = HttpUtils.updateEventStatus(id, "Rejected");
                if (response != null && response.statusCode() == 200) {
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

        manageDeptsBtn.addActionListener(e -> {
            new ManageDepartmentsWindow().setVisible(true);
        });

        loadPendingEvents();
    }

    // --- UPDATED: Fetch Pending Events from API ---
    private void loadPendingEvents() {
        tableModel.setRowCount(0);
        HttpResponse<String> response = HttpUtils.fetchPendingEvents();
        
        if (response != null && response.statusCode() == 200) {
            String json = response.body();
            for (String block : json.split("}")) {
                if (block.contains("event_id") || block.contains("Event_ID")) {
                    
                    // Fallback checks for lowercase vs uppercase based on TiDB responses
                    String id = extractJsonValue(block + "}", "event_id");
                    if (id == null) id = extractJsonValue(block + "}", "Event_ID");

                    String title = extractJsonValue(block + "}", "title");
                    if (title == null) title = extractJsonValue(block + "}", "Title");

                    String date = extractJsonValue(block + "}", "event_date");
                    if (date == null) date = extractJsonValue(block + "}", "Event_Date");

                    String venue = extractJsonValue(block + "}", "venue_name");
                    if (venue == null) venue = extractJsonValue(block + "}", "Venue_Name");

                    String org = extractJsonValue(block + "}", "organizer_id");
                    if (org == null) org = extractJsonValue(block + "}", "Organizer_ID");

                    String status = extractJsonValue(block + "}", "status");
                    if (status == null) status = extractJsonValue(block + "}", "Status");

                    if (id != null) {
                        tableModel.addRow(new Object[]{id, title, date, venue, org, status});
                    }
                }
            }
        }
    }

    // --- JSON PARSER UTILITY (ADDED) ---
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return null;
        startIndex += searchKey.length();
        int endIndex;
        if (json.charAt(startIndex) == '"') {
            startIndex++;
            endIndex = json.indexOf("\"", startIndex);
        } else {
            endIndex = json.indexOf(",", startIndex);
            if (endIndex == -1) endIndex = json.indexOf("}", startIndex);
        }
        String value = json.substring(startIndex, endIndex).trim();
        return value.equals("null") ? null : value;
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

        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(300); // Title
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Date
        table.getColumnModel().getColumn(3).setPreferredWidth(200); // Venue
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Proposed By
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
    }
}