package ui;

import dao.EventDAO;
import models.Event;
import models.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
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
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(new Color(33, 37, 41)); 
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        JPanel brandingPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        brandingPanel.setOpaque(false);
        JLabel brandLabel = new JLabel("CEMS Organizer", SwingConstants.CENTER);
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
        JButton proposeBtn = createSidebarButton("Propose Event", new Color(0, 102, 204));
        JButton cancelBtn = createSidebarButton("Cancel Event", new Color(220, 53, 69));
        JButton refreshBtn = createSidebarButton("Refresh List", new Color(108, 117, 125));
        navPanel.add(proposeBtn);
        navPanel.add(cancelBtn);
        navPanel.add(refreshBtn);
        sidebar.add(navPanel, BorderLayout.CENTER);

        JButton logoutBtn = createSidebarButton("Log Out", new Color(220, 53, 69));
        sidebar.add(logoutBtn, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.WEST);

        // --- MAIN CONTENT ---
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(248, 249, 250)); 
        mainContent.setBorder(new EmptyBorder(25, 40, 40, 40)); 
        JLabel title = new JLabel("My Proposed Events");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(33, 37, 41));
        title.setBorder(new EmptyBorder(0, 0, 25, 0));
        mainContent.add(title, BorderLayout.NORTH);

        String[] cols = {"Event ID", "Title", "Date", "Status", "Registrations", "Venue"};
        tableModel = new DefaultTableModel(cols, 0);
        eventTable = new JTable(tableModel);
        styleTable(eventTable);
        JScrollPane scrollPane = new JScrollPane(eventTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainContent.add(scrollPane, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // --- ACTIONS ---
        refreshBtn.addActionListener(e -> loadMyEvents());
        
        // FIXED LINE: Passes 'this' to the child window
        proposeBtn.addActionListener(e -> new AddEventWindow(currentUser, this).setVisible(true));
        
        cancelBtn.addActionListener(e -> {
            int row = eventTable.getSelectedRow();
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
        });

        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginWindow().setVisible(true);
        });

        loadMyEvents();
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

    // Public so the child window can trigger it
    public void loadMyEvents() {
        tableModel.setRowCount(0); 
        List<Event> events = new EventDAO().getEventsByOrganizer(currentUser.getUserId());
        for (Event ev : events) {
            tableModel.addRow(new Object[]{ev.getEventId(), ev.getTitle(), ev.getEventDate(), ev.getStatus(), ev.getCurrentRegistrations(), ev.getVenueId()});
        }
    }
}