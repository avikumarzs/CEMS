package ui;

import dao.EventDAO;
import models.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;

public class AddEventWindow extends JFrame {
    
    public AddEventWindow(User currentUser) {
        setTitle("Propose New Event");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. HEADER PANEL (Dark Branding)
        JPanel header = new JPanel();
        header.setBackground(new Color(33, 37, 41));
        header.setPreferredSize(new Dimension(0, 80));
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(0, 25, 0, 0));

        JLabel titleLabel = new JLabel("Event Proposal");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // 2. FORM PANEL (Clean White)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Styling Helper: Add Field Label and Input
        JTextField idField = createStyledField("Event ID (e.g., E011)", formPanel);
        JTextField titleField = createStyledField("Event Title", formPanel);
        JTextField dateField = createStyledField("Date (YYYY-MM-DD)", formPanel);
        JTextField venueField = createStyledField("Venue ID (e.g., V001)", formPanel);

        add(formPanel, BorderLayout.CENTER);

        // 3. FOOTER (Actions)
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(0, 0, 30, 40));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(100, 35));
        
        JButton submitBtn = new JButton("Submit Proposal");
        submitBtn.setPreferredSize(new Dimension(160, 35));
        submitBtn.setBackground(new Color(0, 102, 204));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        submitBtn.setFocusPainted(false);
        submitBtn.setBorderPainted(false);

        footer.add(cancelBtn);
        footer.add(submitBtn);
        add(footer, BorderLayout.SOUTH);

        // --- LOGIC ---

        cancelBtn.addActionListener(e -> this.dispose());

        submitBtn.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String title = titleField.getText().trim();
                Date date = Date.valueOf(dateField.getText().trim());
                String venueId = venueField.getText().trim();
                String status = "Pending";
                
                if(id.isEmpty() || title.isEmpty() || venueId.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required!");
                    return;
                }

                EventDAO dao = new EventDAO();
                if (dao.insertEvent(id, title, date, venueId, currentUser.getUserId(), status)) {
                    JOptionPane.showMessageDialog(this, "Proposal Sent Successfully!");
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Check if ID already exists.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date! Use YYYY-MM-DD format.");
            }
        });
    }

    // Helper method to keep UI code clean
    private JTextField createStyledField(String labelText, JPanel container) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(Color.GRAY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)), 
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        container.add(lbl);
        container.add(Box.createRigidArea(new Dimension(0, 5)));
        container.add(field);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        
        return field;
    }
}