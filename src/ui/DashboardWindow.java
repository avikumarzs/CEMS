package ui;

import dao.EventDAO;
import dao.RegistrationDAO;
import models.Event;
import models.User;
import javax.swing.*;
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
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JLabel welcomeLabel = new JLabel("Available Campus Events", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(welcomeLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshBtn = new JButton("Refresh Events");
        
        JButton registerBtn = new JButton("Register for Event");
        registerBtn.setBackground(new Color(0, 102, 204));
        registerBtn.setForeground(Color.WHITE);

        // --- NEW LOG OUT BUTTON ---
        JButton logoutBtn = new JButton("Log Out");
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);

        buttonPanel.add(refreshBtn);
        buttonPanel.add(registerBtn);
        buttonPanel.add(logoutBtn); // Added to panel
        topPanel.add(buttonPanel);
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Title", "Date", "Status", "Registrations", "Venue"};
        tableModel = new DefaultTableModel(columnNames, 0);
        eventTable = new JTable(tableModel);
        eventTable.setRowHeight(25);
        eventTable.setDefaultEditor(Object.class, null); 
        
        JScrollPane scrollPane = new JScrollPane(eventTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        // --- BUTTON ACTIONS ---
        refreshBtn.addActionListener(e -> loadApprovedEvents());

        registerBtn.addActionListener(e -> {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an event!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String eventId = (String) tableModel.getValueAt(selectedRow, 0);
            String eventTitle = (String) tableModel.getValueAt(selectedRow, 1);
            String regId = "R" + (int)(Math.random() * 10000);
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            int confirm = JOptionPane.showConfirmDialog(this, "Register for " + eventTitle + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (new RegistrationDAO().registerStudent(regId, today, currentUser.getUserId(), eventId)) {
                    JOptionPane.showMessageDialog(this, "Successfully Registered!");
                    loadApprovedEvents(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Log out action
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginWindow().setVisible(true);
        });

        loadApprovedEvents();
    }

    private void loadApprovedEvents() {
        tableModel.setRowCount(0); 
        List<Event> events = new EventDAO().getApprovedEvents();
        
        for (Event ev : events) {
            tableModel.addRow(new Object[]{ev.getEventId(), ev.getTitle(), ev.getEventDate(), ev.getStatus(), ev.getCurrentRegistrations(), ev.getVenueId()});
        }
    }
}