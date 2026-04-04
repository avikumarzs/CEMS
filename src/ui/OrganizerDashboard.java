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

        // --- TOP PANEL ---
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JLabel welcomeLabel = new JLabel("Organizer Portal: Propose & Manage Events", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        topPanel.add(welcomeLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton refreshBtn = new JButton("Refresh");
        JButton proposeEventBtn = new JButton("Propose New Event");
        proposeEventBtn.setBackground(new Color(0, 102, 204));
        proposeEventBtn.setForeground(Color.WHITE);
        
        JButton deleteEventBtn = new JButton("Cancel Event");
        deleteEventBtn.setBackground(new Color(220, 53, 69));
        deleteEventBtn.setForeground(Color.WHITE);

        buttonPanel.add(refreshBtn);
        buttonPanel.add(proposeEventBtn);
        buttonPanel.add(deleteEventBtn);
        topPanel.add(buttonPanel);
        add(topPanel, BorderLayout.NORTH);

        // --- CENTER PANEL (The Table) ---
        String[] columnNames = {"Event ID", "Title", "Date", "Status", "Registrations", "Venue"};
        tableModel = new DefaultTableModel(columnNames, 0);
        eventTable = new JTable(tableModel);
        eventTable.setRowHeight(25);
        eventTable.setDefaultEditor(Object.class, null); 
        
        JScrollPane scrollPane = new JScrollPane(eventTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        // --- BUTTON ACTIONS ---
        refreshBtn.addActionListener(e -> loadMyEvents());

        proposeEventBtn.addActionListener(e -> {
            new AddEventWindow(currentUser).setVisible(true);
        });

        deleteEventBtn.addActionListener(e -> {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an event to cancel!");
                return;
            }
            String eventId = (String) tableModel.getValueAt(selectedRow, 0);
            String eventTitle = (String) tableModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this, "Cancel '" + eventTitle + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (new EventDAO().deleteEvent(eventId)) {
                    JOptionPane.showMessageDialog(this, "Event Cancelled!");
                    loadMyEvents();
                }
            }
        });

        loadMyEvents(); // Load data on startup
    }

    private void loadMyEvents() {
        EventDAO eventDAO = new EventDAO();
        // ONLY GET EVENTS FOR THIS ORGANIZER!
        List<Event> events = eventDAO.getEventsByOrganizer(currentUser.getUserId());
        
        tableModel.setRowCount(0); 

        for (Event ev : events) {
            Object[] row = {
                ev.getEventId(), ev.getTitle(), ev.getEventDate(),
                ev.getStatus(), ev.getCurrentRegistrations(), ev.getVenueId()
            };
            tableModel.addRow(row);
        }
    }
}