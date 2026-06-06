package ui;

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
    
    // State tracking for our toggle switch
    private String selectedRole = "Student"; 
    private JButton studentToggleBtn, organizerToggleBtn;

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

        // --- THE ROLE TOGGLE SWITCH ---
        JPanel togglePanel = new JPanel(new GridLayout(1, 2));
        togglePanel.setPreferredSize(new Dimension(0, 45));
        togglePanel.setBackground(Color.WHITE);
        
        studentToggleBtn = new JButton("Student Sign-Up");
        organizerToggleBtn = new JButton("Organizer Sign-Up");
        
        setToggleActive(studentToggleBtn, organizerToggleBtn);
        
        togglePanel.add(studentToggleBtn);
        togglePanel.add(organizerToggleBtn);
        
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(togglePanel, gbc);

        // --- FORM FIELDS ---
        nameField = createField("Full Name", mainPanel, gbc, 5);
        emailField = createField("Email Address", mainPanel, gbc, 7);
        
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

        // --- DEPARTMENT DROPDOWN (NEW 3-TIER API FETCH) ---
        deptLabel = new JLabel("Department");
        deptLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        deptLabel.setForeground(Color.GRAY);
        gbc.gridy = 11; gbc.insets = new Insets(5, 0, 5, 0);
        mainPanel.add(deptLabel, gbc);

        // Fetch and parse the live JSON from Spring Boot
        HttpResponse<String> deptRes = HttpUtils.fetchDepartments();
        List<String> deptList = new ArrayList<>();
        
        // --- NEW: Professional default placeholder ---
        deptList.add("--- Select a Department ---");
        
        if (deptRes != null && deptRes.statusCode() == 200) {
            String json = deptRes.body();
            String[] blocks = json.split("}"); 
            for (String block : blocks) {
                if (block.contains("dept_id")) {
                    String id = extractJsonValue(block + "}", "dept_id");
                    String deptName = extractJsonValue(block + "}", "name");
                    if (id != null && deptName != null) {
                        deptList.add(id + " - " + deptName);
                    }
                }
            }
        } else {
            // Cleaner error fallback
            deptList.add("Unavailable - System Error");
        }

        deptBox = new JComboBox<>(deptList.toArray(new String[0]));
        deptBox.setPreferredSize(new Dimension(0, 45));
        deptBox.setFont(new Font("SansSerif", Font.PLAIN, 15));
        gbc.gridy = 12; gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(deptBox, gbc);

        // --- BUTTONS ---
        JButton signupBtn = new JButton("Create Account");
        signupBtn.setPreferredSize(new Dimension(0, 45));
        signupBtn.setBackground(new Color(40, 167, 69)); 
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        signupBtn.setFocusPainted(false);
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 13; gbc.insets = new Insets(10, 0, 10, 0);
        mainPanel.add(signupBtn, gbc);

        JButton backBtn = new JButton("Already have an account? Sign in");
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setForeground(new Color(108, 117, 125));
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 14;
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

        // --- NAVIGATION ACTIONS ---
        backBtn.addActionListener(e -> {
            this.dispose();
            new LoginWindow().setVisible(true);
        });

        signupBtn.addActionListener(e -> handleSignup());
    }

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
    // 🌐 NEW 3-TIER API LOGIC
    // ==========================================
    private void handleSignup() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passField.getPassword());
        String userId = "U" + (int)(Math.random() * 10000);
        String deptId = null;

        if(name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields to continue.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailRegex)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.\nFormat: user@domain.com", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pass.length() < 6) {
            JOptionPane.showMessageDialog(this, "For your security, your password must be at least 6 characters long.", "Weak Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedRole.equals("Student")) {
            // Block if they leave it on the placeholder (Index 0) or if the DB failed
            if (deptBox.getSelectedIndex() == 0 || deptBox.getSelectedItem().toString().contains("Unavailable")) {
                JOptionPane.showMessageDialog(this, "Please select a valid department from the list.", "Missing Department", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String selectedDept = (String) deptBox.getSelectedItem();
            deptId = selectedDept.split(" - ")[0]; 
        }

        // Send HTTP Request instead of Direct DB logic
        HttpResponse<String> response = HttpUtils.sendSignupRequest(userId, name, email, pass, selectedRole, deptId);

        if (response == null) {
            JOptionPane.showMessageDialog(this, "Cannot connect to the server. Please ensure the backend API is running.", "Network Error", JOptionPane.ERROR_MESSAGE);
        } else if (response.statusCode() == 201 || response.statusCode() == 200) {
            JOptionPane.showMessageDialog(this, "Welcome aboard, " + name + "! Your account is ready.", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new LoginWindow().setVisible(true);
        } else if (response.statusCode() == 409) {
            JOptionPane.showMessageDialog(this, "An account with '" + email + "' already exists.\nPlease sign in instead.", "Account Exists", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Server encountered an error. Status Code: " + response.statusCode(), "System Error", JOptionPane.ERROR_MESSAGE);
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
}