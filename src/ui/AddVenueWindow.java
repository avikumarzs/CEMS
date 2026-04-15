package ui;

import dao.VenueDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AddVenueWindow extends JFrame {
    
    public AddVenueWindow() {
        setTitle("CEMS - Add New Venue");
        setSize(750, 500); 
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

        JLabel iconLabel = new JLabel("🏢");
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
        
        JLabel sideTitle = new JLabel("<html>New<br>Venue</html>");
        sideTitle.setFont(new Font("SansSerif", Font.BOLD, 36));
        sideTitle.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel("<html><br>Register a new campus<br>location for organizers<br>to use.</html>");
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

        // --- SECTION 1: VENUE DETAILS ---
        // Header takes up Rows 0 and 1
        addSectionHeader("VENUE DETAILS", mainContent, gbc, 0);

        // ID Field starts at Row 2
        JTextField idField = createStyledField("Venue ID (e.g., V001)", mainContent, gbc, 2);
        
        // Name Field starts at Row 4
        JTextField nameField = createStyledField("Venue Name (e.g., Main Auditorium)", mainContent, gbc, 4);
        
        // Capacity Field starts at Row 6
        JTextField capacityField = createStyledField("Max Capacity (e.g., 500)", mainContent, gbc, 6);

        // ==========================================
        // 3. ACTION FOOTER
        // ==========================================
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(0, 50, 40, 50));

        JButton cancelBtn = new JButton("Dismiss");
        styleSecondaryButton(cancelBtn);

        JButton submitBtn = new JButton("Add Venue");
        stylePrimaryButton(submitBtn, new Color(40, 167, 69)); // Green for success

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
            String name = nameField.getText().trim();
            String capacityStr = capacityField.getText().trim();

            if(id.isEmpty() || name.isEmpty() || capacityStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all details.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!id.matches("^V\\d{3}$")) {
                JOptionPane.showMessageDialog(this, "Venue ID must start with 'V' followed by 3 digits (e.g., V001).", "Format Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int capacity = Integer.parseInt(capacityStr);
                if (capacity <= 0) {
                    JOptionPane.showMessageDialog(this, "Capacity must be greater than zero.", "Logic Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (new VenueDAO().insertVenue(id, name, capacity)) {
                    JOptionPane.showMessageDialog(this, "Venue Added Successfully!");
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Database Error: That Venue ID might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Capacity must be a valid number.", "Format Error", JOptionPane.ERROR_MESSAGE);
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
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        gbc.gridy = row + 1; 
        gbc.insets = new Insets(5, 0, 15, 0);
        container.add(field, gbc);
        return field;
    }

    private void stylePrimaryButton(JButton btn, Color bg) {
        btn.setPreferredSize(new Dimension(140, 45));
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
}