package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import dao.UserDAO;
import models.User;

public class LoginWindow extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginWindow() {
        setTitle("Campus Event Management - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        JLabel titleLabel = new JLabel("Welcome to Campus Events", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel emailPanel = new JPanel(new FlowLayout());
        emailPanel.add(new JLabel("Email: "));
        emailField = new JTextField(20);
        emailPanel.add(emailField);

        JPanel passwordPanel = new JPanel(new FlowLayout());
        passwordPanel.add(new JLabel("Password: "));
        passwordField = new JPasswordField(20);
        passwordPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");
        buttonPanel.add(loginButton);

        add(titleLabel);
        add(emailPanel);
        add(passwordPanel);
        add(buttonPanel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLoginClick();
            }
        });
    }

    private void handleLoginClick(java.awt.event.ActionEvent evt) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        // Authenticate via the database
        dao.UserDAO userDAO = new dao.UserDAO();
        models.User loggedInUser = userDAO.authenticateUser(email, password);

        if (loggedInUser != null) {
            // Success! The database found a match.
            JOptionPane.showMessageDialog(this, "Login Successful!");
            this.dispose(); // Close the login window
            
            // The Traffic Cop Logic
            if (loggedInUser.getRole().equals("Organizer")) {
                new OrganizerDashboard(loggedInUser).setVisible(true); // Route to the new Organizer Portal
            } else {
                new DashboardWindow(loggedInUser).setVisible(true); // Admins and Students go here for now
            }
            
        } else {
            // Failed login
            JOptionPane.showMessageDialog(this, "Invalid email or password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    } // <-- This is the curly brace that was missing!
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginWindow().setVisible(true);
            }
        });
    }
}