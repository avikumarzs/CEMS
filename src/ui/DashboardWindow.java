package ui;

import javax.swing.*;
import java.awt.*;
import models.User;

public class DashboardWindow extends JFrame {

    private User currentUser;

    // The constructor requires the logged-in User object
    public DashboardWindow(User user) {
        this.currentUser = user;

        // Configure the window
        setTitle("Campus Events - Dashboard (" + currentUser.getRole() + ")");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout());

        // Create a Welcome Header
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(welcomeLabel, BorderLayout.NORTH);

        // Center Panel for buttons/actions
        JPanel centerPanel = new JPanel(new FlowLayout());
        
        // Let's add a placeholder button for viewing events
        JButton viewEventsBtn = new JButton("View All Events");
        viewEventsBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        centerPanel.add(viewEventsBtn);

        // If the user is an Admin, give them a special button!
        if (currentUser.getRole().equals("Admin")) {
            JButton adminBtn = new JButton("Admin Control Panel");
            adminBtn.setFont(new Font("Arial", Font.PLAIN, 16));
            adminBtn.setBackground(Color.RED);
            adminBtn.setForeground(Color.WHITE);
            centerPanel.add(adminBtn);
        }

        add(centerPanel, BorderLayout.CENTER);
    }
}