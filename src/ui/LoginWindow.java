package ui;

import dao.UserDAO;
import models.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

public class LoginWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainCardPanel;

    // Class fields initialized once
    private JTextField standardEmailField = new JTextField();
    private JPasswordField standardPasswordField = new JPasswordField();
    private JPasswordField profilePasswordField = new JPasswordField();
    private JLabel profileWelcomeLabel = new JLabel("", SwingConstants.CENTER);
    
    private String selectedProfileEmail = "";
    private Preferences prefs = Preferences.userNodeForPackage(LoginWindow.class);

    public LoginWindow() {
        setTitle("CEMS - System Login");
        setSize(650, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);

        // Pre-initialize panels
        mainCardPanel.add(createProfileScreen(), "PROFILES");
        mainCardPanel.add(createStandardLoginScreen(), "STANDARD_LOGIN");
        mainCardPanel.add(createPasswordScreen(), "PASSWORD_ENTRY");

        add(mainCardPanel, BorderLayout.CENTER);

        // Auto-routing logic
        String savedUsers = prefs.get("saved_users", "");
        if (savedUsers.isEmpty()) {
            cardLayout.show(mainCardPanel, "STANDARD_LOGIN");
        } else {
            cardLayout.show(mainCardPanel, "PROFILES");
        }
    }

    // ==========================================
    // SCREEN 1: PROFILE SELECTOR
    // ==========================================
    private JPanel createProfileScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;

        addBranding(panel, gbc);

        JLabel title = new JLabel("Who is logging in?", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 40, 0);
        panel.add(title, gbc);

        // The Profile Row
        JPanel grid = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        grid.setBackground(Color.WHITE);

        String savedData = prefs.get("saved_users", "");
        if (!savedData.isEmpty()) {
            for (String entry : savedData.split(";")) {
                String[] p = entry.split(":");
                if (p.length == 2) grid.add(createAvatar(p[0], p[1], false));
            }
        }
        grid.add(createAvatar("New Login", "", true));

        gbc.gridy = 3; gbc.weighty = 0.5;
        panel.add(grid, gbc);

        JButton clear = new JButton("Clear all saved profiles");
        styleFooterBtn(clear);
        clear.addActionListener(e -> {
            prefs.remove("saved_users");
            cardLayout.show(mainCardPanel, "STANDARD_LOGIN");
        });
        gbc.gridy = 4; gbc.weighty = 0; gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(clear, gbc);

        return panel;
    }

    private JPanel createAvatar(String name, String email, boolean isNew) {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(Color.WHITE);

        JButton btn = new JButton(isNew ? "+" : String.valueOf(name.charAt(0)).toUpperCase());
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(90, 90));
        btn.setMaximumSize(new Dimension(90, 90));
        btn.setFont(new Font("SansSerif", isNew ? Font.PLAIN : Font.BOLD, isNew ? 42 : 36));
        btn.setBackground(isNew ? new Color(241, 243, 245) : new Color(0, 102, 204));
        btn.setForeground(isNew ? Color.GRAY : Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.putClientProperty("JButton.arc", 999);

        JLabel lbl = new JLabel(name.split(" ")[0]);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(new Color(52, 58, 64));

        btn.addActionListener(e -> {
            if (isNew) {
                cardLayout.show(mainCardPanel, "STANDARD_LOGIN");
            } else {
                selectedProfileEmail = email;
                profileWelcomeLabel.setText("Welcome, " + name.split(" ")[0]);
                cardLayout.show(mainCardPanel, "PASSWORD_ENTRY");
                profilePasswordField.requestFocusInWindow();
            }
        });

        c.add(btn);
        c.add(Box.createVerticalStrut(12));
        c.add(lbl);
        return c;
    }

    // ==========================================
    // SCREEN 2: STANDARD LOGIN (FIXED)
    // ==========================================
    private JPanel createStandardLoginScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 80, 30, 80));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        addBranding(panel, gbc);

        JLabel title = new JLabel("Sign In", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(title, gbc);

        // Email Field
        JLabel eLbl = new JLabel("Email Address"); eLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridy = 3; gbc.insets = new Insets(5, 0, 2, 0); panel.add(eLbl, gbc);
        styleField(standardEmailField);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 15, 0); panel.add(standardEmailField, gbc);

        // Password Field
        JLabel pLbl = new JLabel("Password"); pLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridy = 5; gbc.insets = new Insets(5, 0, 2, 0); panel.add(pLbl, gbc);
        styleField(standardPasswordField);
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 30, 0); panel.add(standardPasswordField, gbc);

        JButton loginBtn = new JButton("Sign In");
        stylePrimaryButton(loginBtn, new Color(0, 102, 204));
        gbc.gridy = 7; panel.add(loginBtn, gbc);

        JButton signup = new JButton("Create new account");
        styleFooterBtn(signup);
        gbc.gridy = 8; panel.add(signup, gbc);

        // Logic
        loginBtn.addActionListener(e -> {
            System.out.println("Standard Login Attempt: " + standardEmailField.getText());
            executeLogin(standardEmailField.getText(), new String(standardPasswordField.getPassword()));
        });
        signup.addActionListener(e -> { this.dispose(); new SignupWindow().setVisible(true); });

        return panel;
    }

    // ==========================================
    // SCREEN 3: PROFILE PASSWORD (FIXED)
    // ==========================================
    private JPanel createPasswordScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 80, 30, 80));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        addBranding(panel, gbc);

        profileWelcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(profileWelcomeLabel, gbc);

        styleField(profilePasswordField);
        gbc.gridy = 4; gbc.insets = new Insets(10, 0, 20, 0);
        panel.add(profilePasswordField, gbc);

        JButton loginBtn = new JButton("Sign In");
        stylePrimaryButton(loginBtn, new Color(0, 102, 204));
        gbc.gridy = 5; panel.add(loginBtn, gbc);

        JButton back = new JButton("Not you? Switch account");
        styleFooterBtn(back);
        gbc.gridy = 6; panel.add(back, gbc);

        loginBtn.addActionListener(e -> {
            System.out.println("Profile Login Attempt for: " + selectedProfileEmail);
            executeLogin(selectedProfileEmail, new String(profilePasswordField.getPassword()));
        });
        profilePasswordField.addActionListener(e -> loginBtn.doClick()); // Enter key support
        back.addActionListener(e -> cardLayout.show(mainCardPanel, "PROFILES"));

        return panel;
    }

    // ==========================================
    // LOGIC & STYLING
    // ==========================================
    private void executeLogin(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty!");
            return;
        }
        
        User user = new UserDAO().authenticateUser(email, password);
        if (user != null) {
            System.out.println("Login Success: " + user.getName());
            saveUser(user.getName(), email);
            this.dispose();
            if (user.getRole().equals("Admin")) new AdminDashboard(user).setVisible(true);
            else if (user.getRole().equals("Organizer")) new OrganizerDashboard(user).setVisible(true);
            else new DashboardWindow(user).setVisible(true);
        } else {
            System.out.println("Login Failed for: " + email);
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveUser(String name, String email) {
        String saved = prefs.get("saved_users", "");
        String entry = name + ":" + email;
        List<String> list = new ArrayList<>();
        if (!saved.isEmpty()) list.addAll(Arrays.asList(saved.split(";")));
        list.remove(entry); 
        list.add(0, entry); 
        if (list.size() > 4) list = list.subList(0, 4);
        prefs.put("saved_users", String.join(";", list));
    }

    private void addBranding(JPanel p, GridBagConstraints gbc) {
        JLabel t = new JLabel("C E M S", SwingConstants.CENTER);
        t.setFont(new Font("SansSerif", Font.BOLD, 36)); t.setForeground(new Color(0, 102, 204));
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 2, 0); p.add(t, gbc);
        JLabel s = new JLabel("CAMPUS EVENT MANAGEMENT SYSTEM", SwingConstants.CENTER);
        s.setFont(new Font("SansSerif", Font.BOLD, 10)); s.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 35, 0); p.add(s, gbc);
    }

    private void styleField(JTextField f) {
        f.setPreferredSize(new Dimension(0, 48));
        f.setFont(new Font("SansSerif", Font.PLAIN, 15));
    }

    private void stylePrimaryButton(JButton b, Color bg) {
        b.setPreferredSize(new Dimension(0, 48));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleFooterBtn(JButton b) {
        b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setForeground(Color.GRAY); b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}