package ui;

import dao.EventDAO;
import dao.RegistrationDAO;
import models.Event;
import models.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        setSize(900, 600);
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
        // 2. THE MAIN CONTENT AREA (Light Theme)
        // ==========================================
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(248, 249, 250)); 
        mainContent.setBorder(new EmptyBorder(25, 30, 30, 30)); 

        JLabel title = new JLabel("Available Campus Events");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(33, 37, 41));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainContent.add(title, BorderLayout.NORTH);

        String[] columnNames = {"Event ID", "Title", "Date", "Status", "Registrations", "Venue"};
        tableModel = new DefaultTableModel(columnNames, 0);
        eventTable = new JTable(tableModel);
        eventTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        eventTable.setRowHeight(30);
        eventTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        eventTable.getTableHeader().setBackground(new Color(233, 236, 239));
        eventTable.setDefaultEditor(Object.class, null); 
        
        JScrollPane scrollPane = new JScrollPane(eventTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        // ==========================================
        // 3. BUTTON ACTIONS & SECURITY LOGIC
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

            // --- THE NEW SECURITY CHECK ---
            if (regDAO.isAlreadyRegistered(currentUser.getUserId(), eventId)) {
                JOptionPane.showMessageDialog(this, "You are already registered for this event!", "Duplicate Registration", JOptionPane.ERROR_MESSAGE);
                return; // Stop them right here!
            }
            // ------------------------------

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

    private void loadApprovedEvents() {
        tableModel.setRowCount(0); 
        List<Event> events = new EventDAO().getApprovedEvents();
        
        for (Event ev : events) {
            tableModel.addRow(new Object[]{ev.getEventId(), ev.getTitle(), ev.getEventDate(), ev.getStatus(), ev.getCurrentRegistrations(), ev.getVenueId()});
        }
    }
}