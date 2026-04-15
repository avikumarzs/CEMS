package ui;

import dao.EventDAO;
import dao.VenueDAO;
import models.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AddEventWindow extends JFrame {
    
    private OrganizerDashboard parentDashboard;
    private User currentUser;
    
    public AddEventWindow(User currentUser, OrganizerDashboard parent) {
        this.parentDashboard = parent;
        this.currentUser = currentUser;
        
        setTitle("CEMS - Create Proposal");
        setSize(800, 650); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // ==========================================
        // 1. LEFT BRANDING PANEL (The Sidebar)
        // ==========================================
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(33, 37, 41)); 
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBorder(new EmptyBorder(60, 30, 40, 30));

        JLabel iconLabel = new JLabel("✨");
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
        
        JLabel sideTitle = new JLabel("<html>New<br>Proposal</html>");
        sideTitle.setFont(new Font("SansSerif", Font.BOLD, 36));
        sideTitle.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel("<html><br>Fill in the details to submit<br>your event for admin<br>approval.</html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel.setForeground(new Color(173, 181, 189));

        sidebar.add(iconLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(sideTitle);
        sidebar.add(descLabel);
        add(sidebar, BorderLayout.WEST);

        // ==========================================
        // 2. MAIN FORM PANEL (The Card)
        // ==========================================
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(new EmptyBorder(40, 50, 40, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // --- SECTION 1: EVENT IDENTITY ---
        // Header takes up Rows 0 and 1
        addSectionHeader("BASIC INFORMATION", mainContent, gbc, 0);

        // ID Field starts at Row 2 (takes 2 and 3)
        JTextField idField = createStyledField("Event ID (e.g., E011)", mainContent, gbc, 2); 
        
        // Title Field starts at Row 4 (takes 4 and 5)
        JTextField titleField = createStyledField("Event Title", mainContent, gbc, 4);

        // --- SECTION 2: LOGISTICS ---
        gbc.insets = new Insets(30, 0, 10, 0); 
        // Header takes up Rows 6 and 7
        addSectionHeader("DATE & LOCATION", mainContent, gbc, 6); 
        gbc.insets = new Insets(5, 0, 15, 0); 

        // Custom Date Grid - Row 8
        JLabel dateLbl = new JLabel("Scheduled Date");
        dateLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        dateLbl.setForeground(Color.GRAY);
        gbc.gridy = 8; mainContent.add(dateLbl, gbc);

        // Date Dropdowns - Row 9
        JPanel dateGrid = new JPanel(new GridLayout(1, 3, 15, 0));
        dateGrid.setBackground(Color.WHITE);
        String[] years = {"Year", "2026", "2027", "2028", "2029"};
        String[] months = {"Month", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String[] days = new String[32]; days[0] = "Day";
        for (int i = 1; i <= 31; i++) days[i] = String.format("%02d", i);

        JComboBox<String> yearBox = createStyledCombo(years);
        JComboBox<String> monthBox = createStyledCombo(months);
        JComboBox<String> dayBox = createStyledCombo(days);
        dateGrid.add(yearBox); dateGrid.add(monthBox); dateGrid.add(dayBox);

        gbc.gridy = 9; mainContent.add(dateGrid, gbc);

        // Venue Label - Row 10
        JLabel venueLbl = new JLabel("Select Venue");
        venueLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        venueLbl.setForeground(Color.GRAY);
        gbc.gridy = 10; mainContent.add(venueLbl, gbc);

        // Venue Dropdown - Row 11
        List<String> venueList = new VenueDAO().getAllVenueDisplayNames();
        JComboBox<String> venueBox = createStyledCombo(venueList.toArray(new String[0]));
        gbc.gridy = 11; gbc.insets = new Insets(5, 0, 15, 0);
        mainContent.add(venueBox, gbc);

        // ==========================================
        // 3. ACTION FOOTER
        // ==========================================
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(0, 50, 40, 50));

        JButton cancelBtn = new JButton("Dismiss");
        styleSecondaryButton(cancelBtn);

        JButton submitBtn = new JButton("Submit Proposal");
        stylePrimaryButton(submitBtn, new Color(0, 53, 69)); 

        footer.add(cancelBtn);
        footer.add(submitBtn);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(mainContent, BorderLayout.CENTER);
        rightPanel.add(footer, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.CENTER);

        // --- LOGIC ---
        cancelBtn.addActionListener(e -> this.dispose());

        submitBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String selectedVenue = (String) venueBox.getSelectedItem();
            String venueId = "";

            if (selectedVenue != null && selectedVenue.contains(" - ")) {
                venueId = selectedVenue.split(" - ")[0]; 
            }

            if(id.isEmpty() || title.isEmpty() || venueId.isEmpty() || 
               yearBox.getSelectedIndex() == 0 || monthBox.getSelectedIndex() == 0 || dayBox.getSelectedIndex() == 0) {
                showToast("Please fill in all details and select a valid venue.");
                return;
            }

            try {
                String dateStr = yearBox.getSelectedItem() + "-" + monthBox.getSelectedItem() + "-" + dayBox.getSelectedItem();
                LocalDate localDate = LocalDate.parse(dateStr);
                
                if (localDate.isBefore(LocalDate.now())) {
                    showToast("Date must be in the future.");
                    return;
                }

                if (new EventDAO().insertEvent(id, title, Date.valueOf(localDate), venueId, currentUser.getUserId(), "Pending")) {
                    if (parentDashboard != null) parentDashboard.loadMyEvents();
                    JOptionPane.showMessageDialog(this, "Event successfully proposed!");
                    this.dispose();
                } else {
                    showToast("Error: Event ID might already exist.");
                }
            } catch (DateTimeParseException ex) {
                showToast("Invalid date selected.");
            }
        });
    }

    // --- STYLING HELPERS ---
    private void addSectionHeader(String text, JPanel container, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(new Color(108, 117, 125));
        gbc.gridy = row;
        container.add(label, gbc);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(233, 236, 239));
        gbc.gridy = row + 1;
        gbc.insets = new Insets(5, 0, 15, 0);
        container.add(sep, gbc);
    }

    private JTextField createStyledField(String labelText, JPanel container, GridBagConstraints gbc, int row) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(Color.GRAY);
        gbc.gridy = row; container.add(lbl, gbc);
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 42));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)), BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        gbc.gridy = row + 1; 
        gbc.insets = new Insets(5, 0, 15, 0);
        container.add(field, gbc);
        return field;
    }

    private JComboBox<String> createStyledCombo(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setPreferredSize(new Dimension(0, 42));
        box.setBackground(new Color(248, 249, 250));
        box.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return box;
    }

    private void stylePrimaryButton(JButton btn, Color bg) {
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setPreferredSize(new Dimension(100, 45));
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.GRAY);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void showToast(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}