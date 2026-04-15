package ui;

import dao.UserDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SignupWindow extends JFrame {

    private JTextField nameField, emailField;
    private JPasswordField passField;
    private JComboBox<String> roleBox;

    public SignupWindow() {
        setTitle("CEMS - Create Account");
        // MATCHED width to 650 for a seamless transition from the Profile Login screen
        setSize(650, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 80, 25, 80)); // Slightly more side padding for the wider window

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // --- BRANDING HEADER ---
        JLabel brandTitle = new JLabel("C E M S", SwingConstants.CENTER);
        brandTitle.setFont(new Font("SansSerif", Font.BOLD, 36));
        brandTitle.setForeground(new Color(0, 102, 204));
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 2, 0);
        mainPanel.add(brandTitle, gbc);

        JLabel brandSub = new JLabel("CAMPUS EVENT MANAGEMENT SYSTEM", SwingConstants.CENTER);
        brandSub.setFont(new Font("SansSerif", Font.BOLD, 10));
        brandSub.setForeground(new Color(173, 181, 189));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(brandSub, gbc);

        // --- PAGE CONTEXT ---
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Join CEMS to manage events", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(subtitleLabel, gbc);

        // --- FORM FIELDS ---
        nameField = createField("Full Name", mainPanel, gbc, 4);
        emailField = createField("Email Address", mainPanel, gbc, 6);
        
        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        passLbl.setForeground(Color.GRAY);
        gbc.gridy = 8; gbc.insets = new Insets(5, 0, 5, 0);
        mainPanel.add(passLbl, gbc);
        
        passField = new JPasswordField();
        passField.setPreferredSize(new Dimension(0, 45));
        passField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        gbc.gridy = 9; gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(passField, gbc);

        JLabel roleLabel = new JLabel("Register As");
        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        roleLabel.setForeground(Color.GRAY);
        gbc.gridy = 10; gbc.insets = new Insets(5, 0, 5, 0);
        mainPanel.add(roleLabel, gbc);

        String[] roles = {"Student", "Organizer"};
        roleBox = new JComboBox<>(roles);
        roleBox.setPreferredSize(new Dimension(0, 45));
        roleBox.setFont(new Font("SansSerif", Font.PLAIN, 15));
        gbc.gridy = 11; gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(roleBox, gbc);

        // --- BUTTONS ---
        JButton signupBtn = new JButton("Create Account");
        signupBtn.setPreferredSize(new Dimension(0, 45));
        signupBtn.setBackground(new Color(40, 167, 69)); 
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        signupBtn.setFocusPainted(false);
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 12; gbc.insets = new Insets(10, 0, 10, 0);
        mainPanel.add(signupBtn, gbc);

        JButton backBtn = new JButton("Already have an account? Sign in");
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setForeground(new Color(108, 117, 125));
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 13;
        mainPanel.add(backBtn, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // --- ACTIONS ---
        backBtn.addActionListener(e -> {
            this.dispose();
            new LoginWindow().setVisible(true);
        });

        signupBtn.addActionListener(e -> handleSignup());
    }

    private JTextField createField(String labelText, JPanel container, GridBagConstraints gbc, int row) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(Color.GRAY);
        gbc.gridy = row; gbc.insets = new Insets(5, 0, 5, 0);
        container.add(lbl, gbc);

        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 45));
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        gbc.gridy = row + 1; gbc.insets = new Insets(0, 0, 15, 0);
        container.add(field, gbc);
        return field;
    }

    private void handleSignup() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passField.getPassword());
        String role = (String) roleBox.getSelectedItem();
        String userId = "U" + (int)(Math.random() * 10000);

        if(name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return;
        }

        if(new UserDAO().registerUser(userId, name, email, pass, role)) {
            JOptionPane.showMessageDialog(this, "Welcome aboard, " + name + "! Please login.");
            this.dispose();
            new LoginWindow().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Email already in use.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}