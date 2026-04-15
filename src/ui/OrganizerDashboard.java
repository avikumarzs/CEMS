package ui;

import dao.EventDAO;
import dao.VenueDAO;
import models.Event;
import models.Venue;
import models.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrganizerDashboard extends JFrame {

    private User currentUser;
    private JTable mainTable;
    private DefaultTableModel tableModel;
    private JLabel pageTitle;
    private JButton proposeBtn;
    private boolean viewingVenues = false; 

    public OrganizerDashboard(User user) {
        this.currentUser = user;
        setTitle("Organizer Portal - " + currentUser.getName());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(new Color(33, 37, 41)); 
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        JPanel brandingPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        brandingPanel.setOpaque(false);
        JLabel brandLabel = new JLabel("CEMS Organizer", SwingConstants.CENTER);
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        brandLabel.setForeground(Color.WHITE);
        JLabel nameLabel = new JLabel(user.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(new Color(74, 191, 164));
        brandingPanel.add(brandLabel);
        brandingPanel.add(nameLabel);
        sidebar.add(brandingPanel, BorderLayout.NORTH);

        JPanel navPanel = new JPanel(new GridLayout(6, 1, 0, 15));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(40, 0, 0, 0));
        
        JButton myEventsBtn = createSidebarButton("My Events", new Color(108, 117, 125));
        JButton viewVenuesBtn = createSidebarButton("View Venues", new Color(108, 117, 125));
        
        proposeBtn = createSidebarButton("Propose Event", new Color(0, 102, 204));
        JButton cancelBtn = createSidebarButton("Cancel Event", new Color(220, 53, 69));
        
        navPanel.add(myEventsBtn);
        navPanel.add(viewVenuesBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(proposeBtn);
        navPanel.add(cancelBtn);
        sidebar.add(navPanel, BorderLayout.CENTER);

        JButton logoutBtn = createSidebarButton("Log Out", new Color(220, 53, 69));
        sidebar.add(logoutBtn, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.WEST);

        // --- MAIN CONTENT ---
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(248, 249, 250)); 
        mainContent.setBorder(new EmptyBorder(25, 40, 40, 40)); 
        
        pageTitle = new JLabel("My Proposed Events");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        pageTitle.setForeground(new Color(33, 37, 41));
        pageTitle.setBorder(new EmptyBorder(0, 0, 25, 0));
        mainContent.add(pageTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        mainTable = new JTable(tableModel);
        styleTable(mainTable);
        
        JScrollPane scrollPane = new JScrollPane(mainTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(233, 236, 239)));
        mainContent.add(scrollPane, BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER);

        // --- NAVIGATION LOGIC ---
        
        myEventsBtn.addActionListener(e -> {
            viewingVenues = false;
            pageTitle.setText("My Proposed Events");
            proposeBtn.setEnabled(true);
            loadMyEvents();
        });

        viewVenuesBtn.addActionListener(e -> {
            viewingVenues = true;
            pageTitle.setText("Available Campus Venues");
            proposeBtn.setEnabled(false); 
            loadVenues();
        });

        proposeBtn.addActionListener(e -> new AddEventWindow(currentUser, this).setVisible(true));
        
        cancelBtn.addActionListener(e -> {
            if (viewingVenues) {
                JOptionPane.showMessageDialog(this, "You cannot delete venues. Contact Admin.");
                return;
            }
            handleDelete();
        });

        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginWindow().setVisible(true);
        });

        loadMyEvents(); 
    }

    // CRITICAL FIX: This MUST be public so AddEventWindow can trigger it!
    public void loadMyEvents() {
        viewingVenues = false;
        String[] cols = {"Event ID", "Title", "Date", "Status", "Registrations", "Venue"};
        tableModel.setColumnIdentifiers(cols);
        tableModel.setRowCount(0);
        List<Event> events = new EventDAO().getEventsByOrganizer(currentUser.getUserId());
        for (Event ev : events) {
            tableModel.addRow(new Object[]{ev.getEventId(), ev.getTitle(), ev.getEventDate(), ev.getStatus(), ev.getCurrentRegistrations(), ev.getVenueId()});
        }
    }

    private void loadVenues() {
        String[] cols = {"Venue ID", "Venue Name", "Max Capacity"};
        tableModel.setColumnIdentifiers(cols);
        tableModel.setRowCount(0);
        List<Venue> venues = new VenueDAO().getAllVenues();
        for (Venue v : venues) {
            tableModel.addRow(new Object[]{v.getVenueId(), v.getVenueName(), v.getCapacity()});
        }
    }

    private void handleDelete() {
        int row = mainTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an event to cancel!");
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Cancel event?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (new EventDAO().deleteEvent(id)) {
                loadMyEvents();
            }
        }
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
    }
}