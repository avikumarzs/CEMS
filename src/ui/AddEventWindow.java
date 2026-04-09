package ui;

import dao.EventDAO;
import models.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.util.Calendar;

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

        // Styling Helper: Add Field Labels and Inputs
        JTextField idField = createStyledField("Event ID (e.g., E011)", formPanel);
        JTextField titleField = createStyledField("Event Title", formPanel);
        
        // --- THE NEW NATIVE DATE SPINNER ---
        JSpinner dateSpinner = createStyledDateSpinner("Date", formPanel);
        
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
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String venueId = venueField.getText().trim();
            String status = "Pending";
            
            // 1. Basic Empty Check
            if(id.isEmpty() || title.isEmpty() || venueId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All text fields are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Strict ID Format Check
            if (!id.matches("^E\\d{3,4}$")) {
                JOptionPane.showMessageDialog(this, "Event ID must start with 'E' followed by 3-4 digits (e.g., E011).", "Format Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // 2.5 Strict Venue ID Check
            if (!venueId.matches("^V\\d{3}$")) {
                JOptionPane.showMessageDialog(this, "Venue ID must start with 'V' followed by 3 digits (e.g., V001).", "Format Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Extract Date from the new Spinner
            java.util.Date utilDate = (java.util.Date) dateSpinner.getValue();
            Date sqlDate = new Date(utilDate.getTime()); // Convert to SQL format
            
            // 4. Time Travel Check (Date must be in the future)
            java.util.Date today = new java.util.Date(); 
            if (sqlDate.before(today)) {
                JOptionPane.showMessageDialog(this, "Event date must be in the future!", "Logic Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // If it passes all tests, send it to the database!
            EventDAO dao = new EventDAO();
            if (dao.insertEvent(id, title, sqlDate, venueId, currentUser.getUserId(), status)) {
                JOptionPane.showMessageDialog(this, "Proposal Sent Successfully!");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Database Error: That Event ID might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Helper method for standard text fields
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

    // Helper method specifically for the new Date Spinner
    private JSpinner createStyledDateSpinner(String labelText, JPanel container) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(Color.GRAY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Sets up a spinner that defaults to today's date
        SpinnerDateModel dateModel = new SpinnerDateModel(new java.util.Date(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(dateModel);
        
        // Forces the visual format to YYYY-MM-DD
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        
        // Styling to match the text fields
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        editor.getTextField().setFont(new Font("SansSerif", Font.PLAIN, 14));
        spinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        container.add(lbl);
        container.add(Box.createRigidArea(new Dimension(0, 5)));
        container.add(spinner);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        
        return spinner;
    }
}