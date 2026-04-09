package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import dao.UserDAO;
import models.User;

public class LoginWindow extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginWindow() {
        setTitle("CEMS - System Login");
        setSize(700, 450); // Wider for the split-screen look
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==========================================
        // 1. LEFT PANEL (Dark Brand Panel)
        // ==========================================
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(new Color(33, 37, 41)); // Dark Slate
        brandPanel.setPreferredSize(new Dimension(300, 450));
        brandPanel.setBorder(new EmptyBorder(120, 20, 20, 20));

        JLabel titleLabel = new JLabel("C E M S");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Campus Event Management");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(173, 181, 189)); // Light gray
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel versionLabel = new JLabel("Version 1.0");
        versionLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        versionLabel.setForeground(new Color(74, 191, 164)); // Mint Green accent
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandPanel.add(titleLabel);
        brandPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
        brandPanel.add(subtitleLabel);
        brandPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spacing
        brandPanel.add(versionLabel);

        add(brandPanel, BorderLayout.WEST);

        // ==========================================
        // 2. RIGHT PANEL (Clean Form Panel)
        // ==========================================
        JPanel formPanel = new JPanel(new GridBagLayout()); // GridBag allows perfect centering
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 30, 10, 30); // Padding around elements

        // Sign In Header
        JLabel signInLabel = new JLabel("Sign In");
        signInLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        signInLabel.setForeground(new Color(33, 37, 41));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 30, 30, 30); // Extra bottom padding
        formPanel.add(signInLabel, gbc);

        // Reset insets for the form fields
        gbc.insets = new Insets(5, 30, 5, 30); 
        gbc.gridwidth = 1;

        // Email Field
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        emailLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        formPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        styleTextField(emailField);
        gbc.gridy = 2;
        formPanel.add(emailField, gbc);

        // Password Field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        passwordLabel.setForeground(Color.GRAY);
        gbc.gridy = 3;
        gbc.insets = new Insets(15, 30, 5, 30); // Extra top padding
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 30, 25, 30); // Extra bottom padding before button
        formPanel.add(passwordField, gbc);

        // Login Button
        loginButton = new JButton("Secure Login");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 102, 204)); // Brand Blue
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(loginButton.getPreferredSize().width, 40)); // Make it taller
        
        gbc.gridy = 5;
        formPanel.add(loginButton, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ==========================================
        // 3. ACTIONS & LOGIC
        // ==========================================
        loginButton.addActionListener(e -> handleLoginClick());

        // QoL: Pressing 'Enter' in the password field clicks the login button
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLoginClick();
                }
            }
        });
    }

    // Helper method to style text inputs like a modern web app
    private void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(250, 35));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        // Adds a subtle gray border with nice internal padding
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)), 
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void handleLoginClick() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User loggedInUser = userDAO.authenticateUser(email, password);

        if (loggedInUser != null) {
            this.dispose(); 
            
            // ROUTING LOGIC
            if (loggedInUser.getRole().equals("Admin")) {
                new AdminDashboard(loggedInUser).setVisible(true);
            } else if (loggedInUser.getRole().equals("Organizer")) {
                new OrganizerDashboard(loggedInUser).setVisible(true); 
            } else {
                new DashboardWindow(loggedInUser).setVisible(true); // Student View
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    } 

    public static void main(String[] args) {
        // Set system look and feel for smoother window rendering
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true));
    }
}