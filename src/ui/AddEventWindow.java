package ui;

import dao.EventDAO;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class AddEventWindow extends JFrame {
    
    public AddEventWindow(User currentUser) {
        setTitle("Create New Event");
        setSize(400, 300);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new GridLayout(6, 2, 10, 10)); // Grid layout for neat rows

        // Labels and Text Fields
        add(new JLabel("  Event ID (e.g., E011):"));
        JTextField idField = new JTextField();
        add(idField);

        add(new JLabel("  Title:"));
        JTextField titleField = new JTextField();
        add(titleField);

        add(new JLabel("  Date (YYYY-MM-DD):"));
        JTextField dateField = new JTextField();
        add(dateField);

        add(new JLabel("  Venue ID (e.g., V001):"));
        JTextField venueField = new JTextField();
        add(venueField);

        // Empty space for layout formatting
        add(new JLabel("")); 

        // Save Button
        JButton saveBtn = new JButton("Save Event");
        saveBtn.setBackground(new Color(34, 139, 34)); // Nice green color
        saveBtn.setForeground(Color.WHITE);
        add(saveBtn);

        // What happens when we click Save?
        saveBtn.addActionListener(e -> {
            try {
                // Grab the text from the form
                String id = idField.getText();
                String title = titleField.getText();
                Date date = Date.valueOf(dateField.getText()); // Converts string to SQL Date format
                String venueId = venueField.getText();
                
                // Send it to our DAO!
                EventDAO dao = new EventDAO();
                boolean success = dao.insertEvent(id, title, date, venueId, currentUser.getUserId());
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "Event Added Successfully!");
                    this.dispose(); // Close this form window
                } else {
                    JOptionPane.showMessageDialog(this, "Database Error! Check VS Code terminal.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                // Catches typing "April 12" instead of "2026-04-12"
                JOptionPane.showMessageDialog(this, "Invalid Date Format! Please use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}