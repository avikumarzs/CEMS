package ui;

import dao.EventDAO;
import models.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddEventWindow extends JFrame {
    
    public AddEventWindow(User currentUser) {
        setTitle("Propose New Event");
        setSize(480, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. HEADER PANEL
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

        // 2. FORM PANEL
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JTextField idField = createStyledField("Event ID (e.g., E011)", formPanel);
        JTextField titleField = createStyledField("Event Title", formPanel);
        
        // --- THE UPGRADED PREMIUM DATE SELECTOR ---
        JLabel dateTitle = new JLabel("Event Date");
        dateTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        dateTitle.setForeground(Color.GRAY);
        dateTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(dateTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Define Items with "Default" placeholders
        String[] years = {"Year", "2026", "2027", "2028", "2029", "2030"};
        String[] months = {"Month", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String[] days = new String[32];
        days[0] = "Day";
        for (int i = 1; i <= 31; i++) {
            days[i] = String.format("%02d", i);
        }

        JComboBox<String> yearBox = new JComboBox<>(years);
        JComboBox<String> monthBox = new JComboBox<>(months);
        JComboBox<String> dayBox = new JComboBox<>(days);

        JPanel dateGrid = new JPanel(new GridLayout(1, 3, 15, 0)); 
        dateGrid.setBackground(Color.WHITE);
        dateGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55)); 
        
        dateGrid.add(styleDateColumn("YEAR", yearBox));
        dateGrid.add(styleDateColumn("MONTH", monthBox));
        dateGrid.add(styleDateColumn("DAY", dayBox));

        formPanel.add(dateGrid);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        // ------------------------------------------

        JTextField venueField = createStyledField("Venue ID (e.g., V001)", formPanel);

        add(formPanel, BorderLayout.CENTER);

        // 3. FOOTER
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
                JOptionPane.showMessageDialog(this, "Please fill in all text fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Check if placeholders are still selected
            if (yearBox.getSelectedIndex() == 0 || monthBox.getSelectedIndex() == 0 || dayBox.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Please select a valid Year, Month, and Day!", "Date Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. Format Validation
            if (!id.matches("^E\\d{3,4}$")) {
                JOptionPane.showMessageDialog(this, "Event ID must start with 'E' followed by 3-4 digits.", "Format Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!venueId.matches("^V\\d{3}$")) {
                JOptionPane.showMessageDialog(this, "Venue ID must start with 'V' followed by 3 digits.", "Format Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4. DATE EXTRACTION & CALENDAR VALIDATION
            String y = (String) yearBox.getSelectedItem();
            String m = (String) monthBox.getSelectedItem();
            String d = (String) dayBox.getSelectedItem();
            String dateString = y + "-" + m + "-" + d;
            
            LocalDate localDate;
            try {
                localDate = LocalDate.parse(dateString);
                // Future check:
                if (localDate.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(this, "Event date must be today or in the future!", "Logic Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date for this Month! (e.g., Check if the month has " + d + " days).", "Date Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Success: Send to DAO
            EventDAO dao = new EventDAO();
            if (dao.insertEvent(id, title, Date.valueOf(localDate), venueId, currentUser.getUserId(), status)) {
                JOptionPane.showMessageDialog(this, "Proposal Sent Successfully!");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Database Error: ID might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

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

    private JPanel styleDateColumn(String microLabel, JComboBox<String> box) {
        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(microLabel);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(new Color(173, 181, 189));
        box.setFont(new Font("SansSerif", Font.BOLD, 14));
        box.setBackground(new Color(248, 249, 250));
        box.setPreferredSize(new Dimension(box.getPreferredSize().width, 35));
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(box, BorderLayout.CENTER);
        return panel;
    }
}