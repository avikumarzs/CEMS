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
    
    // Field to store the reference to the parent dashboard
    private OrganizerDashboard parentDashboard;
    
    public AddEventWindow(User currentUser, OrganizerDashboard parent) {
        this.parentDashboard = parent;
        setTitle("Propose New Event");
        setSize(480, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- HEADER ---
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

        // --- FORM ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JTextField idField = createStyledField("Event ID (e.g., E011)", formPanel);
        JTextField titleField = createStyledField("Event Title", formPanel);
        
        JLabel dateTitle = new JLabel("Event Date");
        dateTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        dateTitle.setForeground(Color.GRAY);
        dateTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(dateTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        String[] years = {"Year", "2026", "2027", "2028", "2029", "2030"};
        String[] months = {"Month", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String[] days = new String[32]; days[0] = "Day";
        for (int i = 1; i <= 31; i++) days[i] = String.format("%02d", i);

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

        JTextField venueField = createStyledField("Venue ID (e.g., V001)", formPanel);
        add(formPanel, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(0, 0, 30, 40));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(100, 35));
        JButton submitBtn = new JButton("Submit Proposal");
        submitBtn.setPreferredSize(new Dimension(160, 35));
        submitBtn.setBackground(new Color(204, 53, 69));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        footer.add(cancelBtn);
        footer.add(submitBtn);
        add(footer, BorderLayout.SOUTH);

        // --- LOGIC ---
        cancelBtn.addActionListener(e -> this.dispose());
        submitBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String venueId = venueField.getText().trim();

            if(id.isEmpty() || title.isEmpty() || venueId.isEmpty() || 
               yearBox.getSelectedIndex() == 0 || monthBox.getSelectedIndex() == 0 || dayBox.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields correctly!");
                return;
            }

            String dateString = yearBox.getSelectedItem() + "-" + monthBox.getSelectedItem() + "-" + dayBox.getSelectedItem();
            try {
                LocalDate localDate = LocalDate.parse(dateString);
                if (localDate.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(this, "Date must be in the future!");
                    return;
                }
                
                EventDAO dao = new EventDAO();
                if (dao.insertEvent(id, title, Date.valueOf(localDate), venueId, currentUser.getUserId(), "Pending")) {
                    JOptionPane.showMessageDialog(this, "Proposal Sent Successfully!");
                    
                    // --- THE FIX: Tells the parent to refresh! ---
                    if (parentDashboard != null) {
                        parentDashboard.loadMyEvents();
                    }
                    // ----------------------------------------------
                    
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Database Error: ID might exist.");
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date for this month!");
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
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
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