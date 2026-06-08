package ui;

import models.User;
import utils.HttpUtils;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

public class LoginWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainCardPanel;

    // Form fields
    private JTextField standardEmailField = new JTextField();
    private JPasswordField standardPasswordField = new JPasswordField();
    private JPasswordField profilePasswordField = new JPasswordField();
    private JLabel profileWelcomeLabel = new JLabel("", SwingConstants.CENTER);
    private JPanel profileGrid;
    private JButton backToProfilesBtn;
    private String selectedProfileEmail = "";
    private Preferences prefs = Preferences.userNodeForPackage(LoginWindow.class);

    // --- OBJECTIVE 1 & 2: Class-level button and status label references ---
    // Needed so executeLogin() can disable/enable them from outside their creating methods.
    private JButton standardLoginBtn = new JButton("Sign In to Account");
    private JButton profileLoginBtn  = new JButton("Sign In");
    private JLabel  standardStatusLabel = makeStatusLabel();
    private JLabel  profileStatusLabel  = makeStatusLabel();

    public LoginWindow() {
        setTitle("CEMS - System Login");
        setSize(650, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        cardLayout   = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);

        mainCardPanel.add(createProfileScreen(),       "PROFILES");
        mainCardPanel.add(createStandardLoginScreen(), "STANDARD_LOGIN");
        mainCardPanel.add(createPasswordScreen(),      "PASSWORD_ENTRY");

        add(mainCardPanel, BorderLayout.CENTER);

        String savedUsers = prefs.get("saved_users", "");
        if (savedUsers.isEmpty()) cardLayout.show(mainCardPanel, "STANDARD_LOGIN");
        else                      cardLayout.show(mainCardPanel, "PROFILES");
    }

    // ==========================================
    // SCREEN 1: PROFILE SELECTOR (MRU List)
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

        profileGrid = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        profileGrid.setBackground(Color.WHITE);
        refreshProfiles();

        gbc.gridy = 3; gbc.weighty = 0.5;
        panel.add(profileGrid, gbc);

        JButton clear = new JButton("Clear all saved profiles");
        styleFooterBtn(clear);
        clear.addActionListener(e -> {
            prefs.remove("saved_users");
            refreshProfiles();
        });
        gbc.gridy = 4; gbc.weighty = 0; gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(clear, gbc);

        return panel;
    }

    private void refreshProfiles() {
        profileGrid.removeAll();
        String savedData = prefs.get("saved_users", "");

        if (!savedData.isEmpty()) {
            for (String entry : savedData.split(";")) {
                String[] p = entry.split(":");
                if (p.length == 2) profileGrid.add(createAvatar(p[0], p[1], false));
            }
        }

        profileGrid.add(createAvatar("New Login", "", true));
        profileGrid.revalidate();
        profileGrid.repaint();

        if (savedData.isEmpty()) {
            if (backToProfilesBtn != null) backToProfilesBtn.setVisible(false);
            cardLayout.show(mainCardPanel, "STANDARD_LOGIN");
        } else {
            if (backToProfilesBtn != null) backToProfilesBtn.setVisible(true);
        }
    }

    private void deleteSavedUser(String emailToRemove) {
        String saved = prefs.get("saved_users", "");
        List<String> list = new ArrayList<>(Arrays.asList(saved.split(";")));
        list.removeIf(entry -> entry.endsWith(":" + emailToRemove));

        if (list.isEmpty()) prefs.remove("saved_users");
        else                prefs.put("saved_users", String.join(";", list));

        refreshProfiles();
    }

    private JPanel createAvatar(String name, String email, boolean isNew) {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(Color.WHITE);

        JPanel avatarContainer = new JPanel(null);
        avatarContainer.setOpaque(false);
        avatarContainer.setPreferredSize(new Dimension(100, 100));
        avatarContainer.setMaximumSize(new Dimension(100, 100));
        avatarContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn = new JButton(isNew ? "+" : String.valueOf(name.charAt(0)).toUpperCase());
        btn.setBounds(0, 10, 90, 90);
        btn.setFont(new Font("SansSerif", isNew ? Font.PLAIN : Font.BOLD, isNew ? 42 : 36));
        btn.setBackground(isNew ? new Color(241, 243, 245) : new Color(0, 102, 204));
        btn.setForeground(isNew ? Color.GRAY : Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.putClientProperty("JButton.arc", 999);

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

        avatarContainer.add(btn);

        if (!isNew) {
            JButton delBtn = new JButton("×");
            delBtn.setBounds(70, 0, 26, 26);
            delBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
            delBtn.setBackground(new Color(220, 53, 69));
            delBtn.setForeground(Color.WHITE);
            delBtn.setMargin(new Insets(0, 0, 0, 0));
            delBtn.setFocusPainted(false);
            delBtn.setBorderPainted(false);
            delBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            delBtn.putClientProperty("JButton.buttonType", "roundRect");
            delBtn.putClientProperty("JButton.arc", 999);
            delBtn.addActionListener(e -> deleteSavedUser(email));
            avatarContainer.add(delBtn);
            avatarContainer.setComponentZOrder(delBtn, 0);
        }

        JLabel lbl = new JLabel(name.split(" ")[0]);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(new Color(52, 58, 64));

        c.add(avatarContainer);
        c.add(Box.createVerticalStrut(8));
        c.add(lbl);
        return c;
    }

    // ==========================================
    // SCREEN 2: STANDARD LOGIN FORM
    // ==========================================
    private JPanel createStandardLoginScreen() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(Color.WHITE);

        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.setPreferredSize(new Dimension(400, 600));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        addBranding(formContainer, gbc);

        JLabel title = new JLabel("Sign In", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 40, 0);
        formContainer.add(title, gbc);

        JLabel eLbl = new JLabel("Email Address");
        eLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridy = 3; gbc.insets = new Insets(5, 0, 2, 0);
        formContainer.add(eLbl, gbc);
        styleField(standardEmailField);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 15, 0);
        formContainer.add(standardEmailField, gbc);

        JLabel pLbl = new JLabel("Password");
        pLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridy = 5; gbc.insets = new Insets(5, 0, 2, 0);
        formContainer.add(pLbl, gbc);
        styleField(standardPasswordField);
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 30, 0);
        formContainer.add(standardPasswordField, gbc);

        // --- OBJECTIVE 1 & 2: Use class-level button + add status label ---
        stylePrimaryButton(standardLoginBtn, new Color(0, 102, 204));
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 8, 0);
        formContainer.add(standardLoginBtn, gbc);

        // Loading label — hidden by default, shown during network call
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 8, 0);
        formContainer.add(standardStatusLabel, gbc);

        JButton signupLink = new JButton("Don't have an account? Create one");
        styleFooterBtn(signupLink);
        gbc.gridy = 9; gbc.insets = new Insets(0, 0, 0, 0);
        formContainer.add(signupLink, gbc);

        backToProfilesBtn = new JButton("← Back to saved profiles");
        styleFooterBtn(backToProfilesBtn);
        backToProfilesBtn.setVisible(!prefs.get("saved_users", "").isEmpty());
        gbc.gridy = 10;
        formContainer.add(backToProfilesBtn, gbc);

        // Wire actions
        standardLoginBtn.addActionListener(e ->
            executeLogin(standardEmailField.getText(), new String(standardPasswordField.getPassword()),
                         standardLoginBtn, standardStatusLabel));
        standardPasswordField.addActionListener(e -> standardLoginBtn.doClick());
        signupLink.addActionListener(e -> { this.dispose(); new SignupWindow().setVisible(true); });
        backToProfilesBtn.addActionListener(e -> cardLayout.show(mainCardPanel, "PROFILES"));

        outerPanel.add(formContainer);
        return outerPanel;
    }

    // ==========================================
    // SCREEN 3: PROFILE PASSWORD ENTRY
    // ==========================================
    private JPanel createPasswordScreen() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(Color.WHITE);

        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.setPreferredSize(new Dimension(400, 600));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        addBranding(formContainer, gbc);

        profileWelcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 40, 0);
        formContainer.add(profileWelcomeLabel, gbc);

        styleField(profilePasswordField);
        gbc.gridy = 4; gbc.insets = new Insets(10, 0, 20, 0);
        formContainer.add(profilePasswordField, gbc);

        // --- OBJECTIVE 1 & 2: Use class-level button + add status label ---
        stylePrimaryButton(profileLoginBtn, new Color(0, 102, 204));
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 8, 0);
        formContainer.add(profileLoginBtn, gbc);

        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 8, 0);
        formContainer.add(profileStatusLabel, gbc);

        JButton back = new JButton("Not you? Switch account");
        styleFooterBtn(back);
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 0, 0);
        formContainer.add(back, gbc);

        // Wire actions
        profileLoginBtn.addActionListener(e ->
            executeLogin(selectedProfileEmail, new String(profilePasswordField.getPassword()),
                         profileLoginBtn, profileStatusLabel));
        profilePasswordField.addActionListener(e -> profileLoginBtn.doClick());
        back.addActionListener(e -> cardLayout.show(mainCardPanel, "PROFILES"));

        outerPanel.add(formContainer);
        return outerPanel;
    }

    // ==========================================
    // OBJECTIVE 1 & 2: SWINGWORKER LOGIN
    // ==========================================
    private void executeLogin(String email, String password, JButton callerBtn, JLabel statusLabel) {
        // Validate on the EDT before touching the network
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both your email and password to sign in.",
                "Missing Credentials", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Invalid Format", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Disable UI & show cold-start warning
        callerBtn.setEnabled(false);
        callerBtn.setText("Connecting...");
        statusLabel.setText("Waking up server... this may take up to 60 seconds.");
        statusLabel.setVisible(true);

        new SwingWorker<HttpResponse<String>, Void>() {
            @Override
            protected HttpResponse<String> doInBackground() {
                return HttpUtils.sendLoginRequest(email, password);
            }

            @Override
            protected void done() {
                // Restore UI state on EDT
                callerBtn.setEnabled(true);
                callerBtn.setText(callerBtn == standardLoginBtn ? "Sign In to Account" : "Sign In");
                statusLabel.setVisible(false);

                try {
                    handleLoginResponse(get(), email);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginWindow.this,
                        "An unexpected error occurred. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // Extracted so both SwingWorker paths share the same routing logic
    private void handleLoginResponse(HttpResponse<String> response, String email) {
        if (response == null) {
            JOptionPane.showMessageDialog(this,
                "Cannot connect to the server. Please check your internet connection.",
                "Network Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (response.statusCode() == 200) {
            String json = response.body();
            String id     = extractJsonValue(json, "user_id");
            String name   = extractJsonValue(json, "name");
            String role   = extractJsonValue(json, "role");
            String deptId = extractJsonValue(json, "dept_id");

            User user = new User(id, name, email, role, deptId);
            saveUser(user.getName(), email);
            this.dispose();

            if      ("Admin".equalsIgnoreCase(user.getRole()))     new AdminDashboard(user).setVisible(true);
            else if ("Organizer".equalsIgnoreCase(user.getRole())) new OrganizerDashboard(user).setVisible(true);
            else                                                    new DashboardWindow(user).setVisible(true);

        } else if (response.statusCode() == 401) {
            JOptionPane.showMessageDialog(this,
                "We couldn't find an account matching those credentials.\nPlease check your email and password.",
                "Authentication Failed", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Server encountered an error. Status Code: " + response.statusCode(),
                "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // LIGHTWEIGHT JSON PARSER UTILITY
    // ==========================================
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return null;

        startIndex += searchKey.length();
        int endIndex;

        if (json.charAt(startIndex) == '"') {
            startIndex++;
            endIndex = json.indexOf("\"", startIndex);
        } else {
            endIndex = json.indexOf(",", startIndex);
            if (endIndex == -1) endIndex = json.indexOf("}", startIndex);
        }

        String value = json.substring(startIndex, endIndex).trim();
        return value.equals("null") ? null : value;
    }

    // ==========================================
    // STYLING HELPERS
    // ==========================================
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
        t.setFont(new Font("SansSerif", Font.BOLD, 36));
        t.setForeground(new Color(0, 102, 204));
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 2, 0);
        p.add(t, gbc);

        JLabel s = new JLabel("CAMPUS EVENT MANAGEMENT SYSTEM", SwingConstants.CENTER);
        s.setFont(new Font("SansSerif", Font.BOLD, 10));
        s.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 35, 0);
        p.add(s, gbc);
    }

    private void styleField(JTextField f) {
        f.setPreferredSize(new Dimension(0, 48));
        f.setFont(new Font("SansSerif", Font.PLAIN, 15));
    }

    private void stylePrimaryButton(JButton b, Color bg) {
        b.setPreferredSize(new Dimension(0, 48));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
    }

    private void styleFooterBtn(JButton b) {
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setForeground(Color.GRAY);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /** Creates the shared "Waking up server..." label, hidden by default. */
    private static JLabel makeStatusLabel() {
        JLabel lbl = new JLabel("", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lbl.setForeground(new Color(230, 120, 0)); // amber — informational, not an error
        lbl.setVisible(false);
        return lbl;
    }
}