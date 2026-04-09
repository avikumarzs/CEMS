package ui;

import dao.EventDAO;
import dao.RegistrationDAO;
import models.Event;
import models.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardWindow extends JFrame {

    private User currentUser;
    private JTable eventTable; 
    private DefaultTableModel tableModel;

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
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        JPanel brandingPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        brandingPanel.setOpaque(false);
        
        JLabel brandLabel = new JLabel("CEMS Student", SwingConstants.CENTER);
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
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

        JPanel navPanel = new JPanel(new GridLayout(5, 1, 0, 15));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(40, 0, 0, 0));

        JButton registerBtn = createSidebarButton("Register for Event", new Color(0, 102, 204));
        JButton refreshBtn = createSidebarButton("Refresh Events", new Color(108, 117, 125));
        
        navPanel.add(registerBtn);
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

        JLabel title = new JLabel("Available Campus Events");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(33, 37, 41));
        title.setBorder(new EmptyBorder(0, 0, 25, 0));
        mainContent.add(title, BorderLayout.NORTH);

        String[] columnNames = {"Event ID", "Title", "Date", "Status", "Registrations", "Venue"};
        tableModel = new DefaultTableModel(columnNames, 0);
        eventTable = new JTable(tableModel);
        
        // Apply the new premium styling to the table
        styleTable(eventTable);
        
        JScrollPane scrollPane = new JScrollPane(eventTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(233, 236, 239)));
        mainContent.add(scrollPane, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        // ==========================================
        // 3. BUTTON ACTIONS & LOGIC
        // ==========================================
        refreshBtn.addActionListener(e -> loadApprovedEvents());

        registerBtn.addActionListener(e -> {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an event to register for!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String eventId = (String) tableModel.getValueAt(selectedRow, 0);
            String eventTitle = (String) tableModel.getValueAt(selectedRow, 1);
            RegistrationDAO regDAO = new RegistrationDAO();

            if (regDAO.isAlreadyRegistered(currentUser.getUserId(), eventId)) {
                JOptionPane.showMessageDialog(this, "You are already registered for this event!", "Duplicate Registration", JOptionPane.ERROR_MESSAGE);
                return; 
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Register for " + eventTitle + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String regId = "R" + (int)(Math.random() * 10000);
                java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

                if (regDAO.registerStudent(regId, today, currentUser.getUserId(), eventId)) {
                    JOptionPane.showMessageDialog(this, "Successfully Registered!");
                    loadApprovedEvents(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed. Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginWindow().setVisible(true);
        });

        loadApprovedEvents();
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

    // --- THE NEW TABLE STYLING MAGIC ---
    private void styleTable(JTable table) {
        table.setRowHeight(45); // Massive breathing room
        table.setFont(new Font("SansSerif", Font.PLAIN, 15));
        table.setShowVerticalLines(false); // Clean web-look
        table.setGridColor(new Color(233, 236, 239));
        table.setSelectionBackground(new Color(226, 240, 253)); // Soft blue highlight
        table.setSelectionForeground(new Color(33, 37, 41));
        table.setDefaultEditor(Object.class, null); // Make read-only

        // Header Styling
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(241, 243, 245));
        table.getTableHeader().setForeground(new Color(73, 80, 87));
        table.getTableHeader().setPreferredSize(new Dimension(0, 50)); // Taller header
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(206, 212, 218)));

        // Padding Renderer (Pushes text away from cell walls)
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

        // Perfect Column Proportions
        table.getColumnModel().getColumn(0).setPreferredWidth(100); // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(350); // Title
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Date
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Status
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Registrations
        table.getColumnModel().getColumn(5).setPreferredWidth(200); // Venue
    }

    private void loadApprovedEvents() {
        tableModel.setRowCount(0); 
        List<Event> events = new EventDAO().getApprovedEvents();
        for (Event ev : events) {
            tableModel.addRow(new Object[]{ev.getEventId(), ev.getTitle(), ev.getEventDate(), ev.getStatus(), ev.getCurrentRegistrations(), ev.getVenueId()});
        }
    }
}