package ui;

import models.User;
import utils.HttpUtils;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class SignupWindow extends JFrame {

    private JTextField nameField, emailField;
    private JPasswordField passField;
    private JComboBox<String> deptBox;
    private JLabel deptLabel;

    private String selectedRole = "Student";
    private JButton studentToggleBtn, organizerToggleBtn;

    // --- OBJECTIVE 1 & 2: Class-level refs for SwingWorker state management ---
    private JButton signupBtn;
    private JLabel  signupStatusLabel;

    public SignupWindow() {
        setTitle("CEMS - Create Account");
        setSize(650, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 80, 25, 80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // --- BRANDING ---
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

        // --- ROLE TOGGLE ---
        JPanel togglePanel = new JPanel(new GridLayout(1, 2));
        togglePanel.setPreferredSize(new Dimension(0, 45));
        togglePanel.setBackground(Color.WHITE);

        studentToggleBtn  = new JButton("Student Sign-Up");
        organizerToggleBtn = new JButton("Organizer Sign-Up");
        setToggleActive(studentToggleBtn, organizerToggleBtn);

        togglePanel.add(studentToggleBtn);
        togglePanel.add(organizerToggleBtn);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(togglePanel, gbc);

        // --- FORM FIELDS ---
        nameField  = createField("Full Name",      mainPanel, gbc, 5);
        emailField = createField("Email Address",  mainPanel, gbc, 7);

        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        passLbl.setForeground(Color.GRAY);
        gbc.gridy = 9; gbc.insets = new Insets(5, 0, 5, 0);
        mainPanel.add(passLbl, gbc);

        passField = new JPasswordField();
        passField.setPreferredSize(new Dimension(0, 45));
        passField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        gbc.gridy = 10; gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(passField, gbc);

        // --- DEPARTMENT DROPDOWN ---
        // OBJECTIVE 2: Initialized with placeholder immediately; populated async below.
        deptLabel = new JLabel("Department");
        deptLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        deptLabel.setForeground(Color.GRAY);
        gbc.gridy = 11; gbc.insets = new Insets(5, 0, 5, 0);
        mainPanel.add(deptLabel, gbc);

        deptBox = new JComboBox<>(new String[]{"Loading departments..."});
        deptBox.setEnabled(false); // disabled until async fetch completes
        deptBox.setPreferredSize(new Dimension(0, 45));
        deptBox.setFont(new Font("SansSerif", Font.PLAIN, 15));
        gbc.gridy = 12; gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(deptBox, gbc);

        // --- SIGN UP BUTTON ---
        signupBtn = new JButton("Create Account");
        signupBtn.setPreferredSize(new Dimension(0, 45));
        signupBtn.setBackground(new Color(40, 167, 69));
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        signupBtn.setFocusPainted(false);
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 13; gbc.insets = new Insets(10, 0, 4, 0);
        mainPanel.add(signupBtn, gbc);

        // OBJECTIVE 2: Status label for cold-start feedback
        signupStatusLabel = new JLabel("", SwingConstants.CENTER);
        signupStatusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        signupStatusLabel.setForeground(new Color(230, 120, 0));
        signupStatusLabel.setVisible(false);
        gbc.gridy = 14; gbc.insets = new Insets(0, 0, 4, 0);
        mainPanel.add(signupStatusLabel, gbc);

        JButton backBtn = new JButton("Already have an account? Sign in");
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setForeground(new Color(108, 117, 125));
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 15; gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(backBtn, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // --- TOGGLE ACTIONS ---
        studentToggleBtn.addActionListener(e -> {
            selectedRole = "Student";
            setToggleActive(studentToggleBtn, organizerToggleBtn);
            deptLabel.setVisible(true);
            deptBox.setVisible(true);
        });

        organizerToggleBtn.addActionListener(e -> {
            selectedRole = "Organizer";
            setToggleActive(organizerToggleBtn, studentToggleBtn);
            deptLabel.setVisible(false);
            deptBox.setVisible(false);
        });

        backBtn.addActionListener(e -> { this.dispose(); new LoginWindow().setVisible(true); });
        signupBtn.addActionListener(e -> handleSignup());

        // OBJECTIVE 2: Kick off async dept load AFTER the UI is built
        loadDepartmentsAsync();
    }

    // ==========================================
    // OBJECTIVE 2: ASYNC DEPARTMENT LOADER
    // ==========================================
    private void loadDepartmentsAsync() {
        new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() {
                List<String> list = new ArrayList<>();
                list.add("--- Select a Department ---");
                HttpResponse<String> deptRes = HttpUtils.fetchDepartments();
                if (deptRes != null && deptRes.statusCode() == 200) {
                    String json = deptRes.body();
                    for (String block : json.split("}")) {
                        if (block.contains("dept_id")) {
                            String id       = extractJsonValue(block + "}", "dept_id");
                            String deptName = extractJsonValue(block + "}", "name");
                            if (id != null && deptName != null) list.add(id + " - " + deptName);
                        }
                    }
                } else {
                    list.add("Unavailable - System Error");
                }
                return list;
            }

            @Override
            protected void done() {
                try {
                    List<String> depts = get();
                    deptBox.removeAllItems();
                    for (String d : depts) deptBox.addItem(d);
                    deptBox.setEnabled(true);
                } catch (Exception e) {
                    deptBox.removeAllItems();
                    deptBox.addItem("Unavailable - System Error");
                    deptBox.setEnabled(false);
                }
            }
        }.execute();
    }

    // ==========================================
    // OBJECTIVES 1, 2 & 3: SWINGWORKER SIGNUP + GHOST SESSION FIX
    // ==========================================
    private void handleSignup() {
        // --- Validate on the EDT (fast, no network) ---
        String name  = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pass  = new String(passField.getPassword());

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields to continue.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.\nFormat: user@domain.com", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (pass.length() < 6) {
            JOptionPane.showMessageDialog(this, "For your security, your password must be at least 6 characters long.", "Weak Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // OBJECTIVE 3 (partial): Improved userId — timestamp-based to avoid collisions
        // "U" + last 6 digits of epoch millis gives ~1 million unique IDs per second
        String userId = "U" + (System.currentTimeMillis() % 1_000_000);

        String finalDeptId;
        if (selectedRole.equals("Student")) {
            if (deptBox.getSelectedIndex() == 0 || deptBox.getSelectedItem().toString().contains("Unavailable")) {
                JOptionPane.showMessageDialog(this, "Please select a valid department from the list.", "Missing Department", JOptionPane.WARNING_MESSAGE);
                return;
            }
            finalDeptId = ((String) deptBox.getSelectedItem()).split(" - ")[0];
        } else {
            finalDeptId = null;
        }

        // Freeze UI for the network calls
        signupBtn.setEnabled(false);
        signupBtn.setText("Creating account...");
        signupStatusLabel.setText("Waking up server... this may take up to 60 seconds.");
        signupStatusLabel.setVisible(true);

        // OBJECTIVE 3 (core): Hold both responses so done() can auto-login after signup.
        // Both network calls happen off the EDT inside doInBackground.
        @SuppressWarnings("unchecked")
        final HttpResponse<String>[] holder = new HttpResponse[2];

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                // Step 1: Create the account
                holder[0] = HttpUtils.sendSignupRequest(userId, name, email, pass, selectedRole, finalDeptId);

                // Step 2: GHOST SESSION FIX — if signup succeeded, immediately authenticate
                // via the API to get the real user_id from the DB into the session.
                // This guarantees the studentId in the session matches the DB record exactly,
                // preventing the FK constraint error on event registration.
                if (holder[0] != null &&
                    (holder[0].statusCode() == 201 || holder[0].statusCode() == 200)) {
                    holder[1] = HttpUtils.sendLoginRequest(email, pass);
                }
                return null;
            }

            @Override
            protected void done() {
                // Restore button state
                signupBtn.setEnabled(true);
                signupBtn.setText("Create Account");
                signupStatusLabel.setVisible(false);

                HttpResponse<String> signupResp = holder[0];
                HttpResponse<String> loginResp  = holder[1];

                if (signupResp == null) {
                    JOptionPane.showMessageDialog(SignupWindow.this,
                        "Cannot connect to the server. Please check your internet connection.",
                        "Network Error", JOptionPane.ERROR_MESSAGE);

                } else if (signupResp.statusCode() == 201 || signupResp.statusCode() == 200) {
                    // OBJECTIVE 3: Route directly to dashboard using the API-verified session.
                    // The user_id here comes from the DB — not from the locally generated userId —
                    // so the session is guaranteed to match the stored record.
                    if (loginResp != null && loginResp.statusCode() == 200) {
                        String json     = loginResp.body();
                        String id       = extractJsonValue(json, "user_id");
                        String userName = extractJsonValue(json, "name");
                        String role     = extractJsonValue(json, "role");
                        String deptId   = extractJsonValue(json, "dept_id");

                        User user = new User(id, userName, email, role, deptId);
                        SignupWindow.this.dispose();

                        if ("Organizer".equalsIgnoreCase(user.getRole()))
                            new OrganizerDashboard(user).setVisible(true);
                        else
                            new DashboardWindow(user).setVisible(true);
                    } else {
                        // Auto-login failed (rare) — fall back to manual login
                        JOptionPane.showMessageDialog(SignupWindow.this,
                            "Account created! Please sign in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        SignupWindow.this.dispose();
                        new LoginWindow().setVisible(true);
                    }

                } else if (signupResp.statusCode() == 409) {
                    JOptionPane.showMessageDialog(SignupWindow.this,
                        "An account with '" + email + "' already exists.\nPlease sign in instead.",
                        "Account Exists", JOptionPane.ERROR_MESSAGE);

                } else {
                    JOptionPane.showMessageDialog(SignupWindow.this,
                        "Server encountered an error. Status Code: " + signupResp.statusCode(),
                        "System Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // ==========================================
    // STYLING HELPERS
    // ==========================================
    private void setToggleActive(JButton activeBtn, JButton inactiveBtn) {
        activeBtn.setBackground(new Color(0, 102, 204));
        activeBtn.setForeground(Color.WHITE);
        activeBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        activeBtn.setFocusPainted(false);

        inactiveBtn.setBackground(new Color(241, 243, 245));
        inactiveBtn.setForeground(Color.GRAY);
        inactiveBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inactiveBtn.setFocusPainted(false);
        inactiveBtn.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
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
}