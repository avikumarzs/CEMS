package ui;

import dao.EventDAO;
import models.Event;
import models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrganizerDashboard extends JFrame {

    private User currentUser;
    private JTable eventTable;
    private DefaultTableModel tableModel;

    public OrganizerDashboard(User user) {
        this.currentUser = user;
        setTitle("Organizer Portal - " + currentUser.getName());
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JLabel title = new JLabel("My Proposed Events", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        topPanel.add(title);

        JPanel btnPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        
        JButton proposeBtn = new JButton("Propose New Event");
        proposeBtn.setBackground(new Color(0, 102, 204));
        proposeBtn.setForeground(Color.WHITE);
        
        JButton cancelBtn = new JButton("Cancel Event");
        cancelBtn.setBackground(new Color(220, 53, 69));
        cancelBtn.setForeground(Color.WHITE);

        // --- NEW LOG OUT BUTTON ---
        JButton logoutBtn = new JButton("Log Out");
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);

        btnPanel.add(refreshBtn);
        btnPanel.add(proposeBtn);
        btnPanel.add(cancelBtn);
        btnPanel.add(logoutBtn); // Added to panel
        topPanel.add(btnPanel);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Date", "Status", "Registrations", "Venue"};
        tableModel = new DefaultTableModel(cols, 0);
        eventTable = new JTable(tableModel);
        eventTable.setDefaultEditor(Object.class, null); 
        add(new JScrollPane(eventTable), BorderLayout.CENTER);

        // --- BUTTON ACTIONS ---
        refreshBtn.addActionListener(e -> loadMyEvents());
        proposeBtn.addActionListener(e -> new AddEventWindow(currentUser).setVisible(true));
        
        cancelBtn.addActionListener(e -> {
            int row = eventTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an event to cancel!");
                return;
            }
            String id = (String) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Cancel event?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (new EventDAO().deleteEvent(id)) {
                    JOptionPane.showMessageDialog(this, "Cancelled!");
                    loadMyEvents();
                }
            }
        });

        // Log out action
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginWindow().setVisible(true);
        });

        loadMyEvents();
    }

    private void loadMyEvents() {
        tableModel.setRowCount(0); 
        List<Event> events = new EventDAO().getEventsByOrganizer(currentUser.getUserId());
        for (Event ev : events) {
            tableModel.addRow(new Object[]{ev.getEventId(), ev.getTitle(), ev.getEventDate(), ev.getStatus(), ev.getCurrentRegistrations(), ev.getVenueId()});
        }
    }
}