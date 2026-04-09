package ui;

import dao.EventDAO;
import models.User;
import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class AddEventWindow extends JFrame {
    
    public AddEventWindow(User currentUser) {
        setTitle("Propose New Event");
        setSize(400, 300);
        setLocationRelativeTo(null); 
        setLayout(new GridLayout(6, 2, 10, 10)); 

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

        add(new JLabel("")); 

        JButton saveBtn = new JButton("Submit Proposal");
        saveBtn.setBackground(new Color(34, 139, 34)); 
        saveBtn.setForeground(Color.WHITE);
        add(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                String id = idField.getText();
                String title = titleField.getText();
                Date date = Date.valueOf(dateField.getText());
                String venueId = venueField.getText();
                String status = "Pending"; // Hardcoded for approval workflow
                
                EventDAO dao = new EventDAO();
                boolean success = dao.insertEvent(id, title, date, venueId, currentUser.getUserId(), status);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "Event Proposed! Waiting for Admin approval.");
                    this.dispose(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date Format! Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}