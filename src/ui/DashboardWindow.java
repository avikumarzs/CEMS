package ui;

import models.User;
import utils.HttpUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.http.HttpResponse;

public class DashboardWindow extends JFrame {

    private User currentUser;
    private JTable eventTable; 
    private DefaultTableModel tableModel;
    
    private boolean viewingMyEvents = false; 
    private JLabel pageTitle;
    private JButton mainActionBtn;

    public DashboardWindow(User user) {
        this.currentUser = user;
        setTitle("Student Dashboard - " + currentUser.getName());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==========================================
        // 1. THE SIDEBAR
        // ==========================================
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(new Color(33, 37, 41)); 
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        JPanel brandingPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        brandingPanel.setOpaque(false);
        JLabel brandLabel = new JLabel("CEMS Student", SwingConstants.CENTER);
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

        JPanel navPanel = new JPanel(new GridLayout(6, 1, 0, 15));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(40, 0, 0, 0));

        JButton browseBtn = createSidebarButton("Browse Events", new Color(108, 117, 125));
        JButton myEventsBtn = createSidebarButton("My Registrations", new Color(108, 117, 125));
        
        mainActionBtn = createSidebarButton("Register for Event", new Color(0, 102, 204)); 
        
        navPanel.add(browseBtn);
        navPanel.add(myEventsBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        navPanel.add(mainActionBtn);
        
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

        pageTitle = new JLabel("Available Campus Events");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 32));
        pageTitle.setForeground(new Color(33, 37, 41));
        pageTitle.setBorder(new EmptyBorder(0, 0, 25, 0));
        mainContent.add(pageTitle, BorderLayout.NORTH);

        String[] columnNames = {"Event ID", "Title", "Date", "Status", "Registrations", "Venue"};
        tableModel = new DefaultTableModel(columnNames, 0);
        eventTable = new JTable(tableModel);
        styleTable(eventTable);
        
        JScrollPane scrollPane = new JScrollPane(eventTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(233, 236, 239)));
        mainContent.add(scrollPane, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        // ==========================================
        // 3. LOGIC & STATE SWITCHING
        // ==========================================
        browseBtn.addActionListener(e -> {
            viewingMyEvents = false;
            pageTitle.setText("Available Campus Events");
            mainActionBtn.setText("Register for Event");
            mainActionBtn.setBackground(new Color(0, 102, 204));
            loadEvents(HttpUtils.fetchApprovedEvents());
        });

        myEventsBtn.addActionListener(e -> {
            viewingMyEvents = true;
            pageTitle.setText("My Registered Events");
            mainActionBtn.setText("Cancel Registration");
            mainActionBtn.setBackground(new Color(255, 152, 0)); 
            loadEvents(HttpUtils.fetchMyRegisteredEvents(currentUser.getUserId()));
        });

        mainActionBtn.addActionListener(e -> {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an event first!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String eventId = (String) tableModel.getValueAt(selectedRow, 0);
            String eventTitle = (String) tableModel.getValueAt(selectedRow, 1);

            if (viewingMyEvents) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to drop out of " + eventTitle + "?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    HttpResponse<String> res = HttpUtils.cancelRegistration(currentUser.getUserId(), eventId);
                    if (res != null && res.statusCode() == 200) {
                        JOptionPane.showMessageDialog(this, "Registration Cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadEvents(HttpUtils.fetchMyRegisteredEvents(currentUser.getUserId()));
                    } else {
                        JOptionPane.showMessageDialog(this, "System Error.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                int confirm = JOptionPane.showConfirmDialog(this, "Register for " + eventTitle + "?", "Confirm Registration", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    HttpResponse<String> res = HttpUtils.registerForEvent(currentUser.getUserId(), eventId);
                    
                    if (res == null) {
                        JOptionPane.showMessageDialog(this, "Network Error.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (res.statusCode() == 200) {
                        JOptionPane.showMessageDialog(this, "Registration Successful! Your seat is reserved.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadEvents(HttpUtils.fetchApprovedEvents()); 
                    } else if (res.statusCode() == 409) {
                        JOptionPane.showMessageDialog(this, "You are already registered for this event!", "Notice", JOptionPane.WARNING_MESSAGE);
                    } else if (res.statusCode() == 403) {
                        JOptionPane.showMessageDialog(this, "Registration Failed. The venue has reached maximum capacity.", "Venue Full", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "A database error occurred.", "System Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginWindow().setVisible(true);
        });

        loadEvents(HttpUtils.fetchApprovedEvents());
    }

    // ==========================================
    // 4. API FETCH & PARSE LOGIC
    // ==========================================
    private void loadEvents(HttpResponse<String> response) {
        tableModel.setRowCount(0);
        if (response != null && response.statusCode() == 200) {
            String json = response.body();
            if (!json.contains("{")) return; // Empty DB

            // Split JSON array into individual event objects
            String[] blocks = json.split("},\\{");
            for (String block : blocks) {
                String id = extractJsonValue(block, "event_id");
                String title = extractJsonValue(block, "title");
                String dateStr = extractJsonValue(block, "event_date");
                String status = extractJsonValue(block, "status");
                String regs = extractJsonValue(block, "current_registrations");
                String venue = extractJsonValue(block, "venue_name");

                if (id != null) {
                    tableModel.addRow(new Object[]{id, title, dateStr, status, regs, venue});
                }
            }
        }
    }

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
            if (endIndex == -1) endIndex = json.indexOf("]", startIndex);
        }
        
        String value = json.substring(startIndex, endIndex).trim();
        return value.equals("null") ? null : value;
    }

    // --- STYLING HELPERS ---
    private JButton createSidebarButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
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

        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(paddedRenderer);
    }
}