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
    private JTable eventTable; // Made this a class variable so buttons can read it!
    private DefaultTableModel tableModel;

    public DashboardWindow(User user) {
        this.currentUser = user;

        setTitle("Campus Events - Dashboard (" + currentUser.getRole() + ")");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- TOP PANEL ---
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(welcomeLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton refreshBtn = new JButton("Refresh Events");
        JButton registerBtn = new JButton("Register for Selected Event");
        registerBtn.setBackground(new Color(0, 102, 204));
        registerBtn.setForeground(Color.WHITE);

        buttonPanel.add(refreshBtn);
        buttonPanel.add(registerBtn);

        // Only show "Add Event" to Organizers and Admins
        
        
        if (currentUser.getRole().equals("Admin") || currentUser.getRole().equals("Organizer")) {
            JButton addEventBtn = new JButton("Add New Event");
            
            JButton deleteEventBtn = new JButton("Delete Selected");
            deleteEventBtn.setBackground(new Color(220, 53, 69)); 
            deleteEventBtn.setForeground(Color.WHITE);

            buttonPanel.add(addEventBtn);
            buttonPanel.add(deleteEventBtn);

            addEventBtn.addActionListener(e -> new AddEventWindow(currentUser).setVisible(true));

            deleteEventBtn.addActionListener(e -> {
                int selectedRow = eventTable.getSelectedRow();
                
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Please select an event to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String eventId = (String) tableModel.getValueAt(selectedRow, 0);
                String eventTitle = (String) tableModel.getValueAt(selectedRow, 1);

                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to permanently delete '" + eventTitle + "' and all its registrations?", 
                    "Confirm Delete", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    EventDAO dao = new EventDAO();
                    boolean success = dao.deleteEvent(eventId);

                    if (success) {
                        JOptionPane.showMessageDialog(this, "Event Deleted Successfully!");
                        loadEventsIntoTable(); 
                    } else {
                        JOptionPane.showMessageDialog(this, "Error deleting event. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        topPanel.add(buttonPanel);
        add(topPanel, BorderLayout.NORTH);

        // --- CENTER PANEL (The Table) ---
        String[] columnNames = {"Event ID", "Title", "Date", "Status", "Registrations", "Venue ID"};
        tableModel = new DefaultTableModel(columnNames, 0);
        eventTable = new JTable(tableModel);
        eventTable.setRowHeight(25);
        
        // Prevent editing the cells directly
        eventTable.setDefaultEditor(Object.class, null); 
        
        JScrollPane scrollPane = new JScrollPane(eventTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        // --- BUTTON ACTIONS ---

        // 1. Refresh Events Logic
        refreshBtn.addActionListener(e -> loadEventsIntoTable());

        // 2. Register Logic
        registerBtn.addActionListener(e -> {
            int selectedRow = eventTable.getSelectedRow();
            
            // Check if they actually clicked a row
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an event from the table first!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Grab the Event ID from column 0 of the clicked row
            String eventId = (String) tableModel.getValueAt(selectedRow, 0);
            String eventTitle = (String) tableModel.getValueAt(selectedRow, 1);

            // Generate a random ID for the registration (e.g., R9823) and get today's date
            String regId = "R" + (int)(Math.random() * 10000);
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            // Ask for confirmation
            int confirm = JOptionPane.showConfirmDialog(this, "Register for " + eventTitle + "?", "Confirm Registration", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                RegistrationDAO regDao = new RegistrationDAO();
                boolean success = regDao.registerStudent(regId, today, currentUser.getUserId(), eventId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "Successfully Registered!");
                    loadEventsIntoTable(); // Reload the table so we can see the Trigger update the count!
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed. You might already be registered!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Load the events automatically when the window opens!
        loadEventsIntoTable();
    }

    // Helper method to grab data from DB and put it in the table
    private void loadEventsIntoTable() {
        EventDAO eventDAO = new EventDAO();
        List<Event> events = eventDAO.getAllEvents();
        
        tableModel.setRowCount(0); // Clear old data

        for (Event ev : events) {
            Object[] row = {
                ev.getEventId(),
                ev.getTitle(),
                ev.getEventDate(),
                ev.getStatus(),
                ev.getCurrentRegistrations(),
                ev.getVenueId()
            };
            tableModel.addRow(row);
        }
    }
}