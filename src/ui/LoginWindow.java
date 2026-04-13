package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import dao.UserDAO;
import models.User;

public class LoginWindow extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    
    // Carousel Variables
    private JLabel carouselLabel;
    private Timer carouselTimer;
    private int currentImageIndex = 0;
    
    // Updated with correct .jpeg extensions and all 4 images
    private final String[] imagePaths = {
        "/assets/img1.jpeg", 
        "/assets/img2.jpeg", 
        "/assets/img3.jpeg",
        "/assets/img4.jpeg"
    };

    public LoginWindow() {
        setTitle("CEMS - System Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==========================================
        // 1. LEFT PANEL (Expanded Carousel)
        // ==========================================
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BorderLayout());
        brandPanel.setBackground(new Color(33, 37, 41));
        
        // INCREASED WIDTH to 650 for a stunning visual presence
        brandPanel.setPreferredSize(new Dimension(650, 0)); 

        carouselLabel = new JLabel();
        carouselLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel overlayPanel = new JPanel();
        overlayPanel.setLayout(new BoxLayout(overlayPanel, BoxLayout.Y_AXIS));
        overlayPanel.setOpaque(false); 
        overlayPanel.setBorder(new EmptyBorder(180, 60, 40, 60));

        JLabel titleLabel = new JLabel("C E M S");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 64)); // Bigger Title
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Campus Event Management");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 22)); // Bigger Subtitle
        subtitleLabel.setForeground(new Color(248, 249, 250));

        overlayPanel.add(titleLabel);
        overlayPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        overlayPanel.add(subtitleLabel);

        JLayeredPane layers = new JLayeredPane();
        layers.setLayout(new OverlayLayout(layers));
        layers.add(overlayPanel, JLayeredPane.PALETTE_LAYER);
        layers.add(carouselLabel, JLayeredPane.DEFAULT_LAYER);

        brandPanel.add(layers, BorderLayout.CENTER);
        add(brandPanel, BorderLayout.WEST);

        // ==========================================
        // 2. RIGHT PANEL (Upgraded Form)
        // ==========================================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 50, 10, 50);

        JLabel signInLabel = new JLabel("Sign In");
        signInLabel.setFont(new Font("SansSerif", Font.BOLD, 36)); // Larger Header
        gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(10, 50, 40, 50);
        formPanel.add(signInLabel, gbc);

        gbc.gridwidth = 1; gbc.insets = new Insets(5, 50, 5, 50);
        
        // Premium Email Field
        JLabel emailLbl = new JLabel("Email Address");
        emailLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        emailLbl.setForeground(Color.GRAY);
        gbc.gridy = 1; formPanel.add(emailLbl, gbc);
        
        emailField = new JTextField();
        styleField(emailField);
        gbc.gridy = 2; formPanel.add(emailField, gbc);

        // Premium Password Field
        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        passLbl.setForeground(Color.GRAY);
        gbc.gridy = 3; gbc.insets = new Insets(20, 50, 5, 50); // Extra top space
        formPanel.add(passLbl, gbc);
        
        passwordField = new JPasswordField();
        styleField(passwordField);
        gbc.gridy = 4; formPanel.add(passwordField, gbc);

        // --- UPGRADED BUTTON ---
        JButton loginBtn = new JButton("Secure Login");
        stylePremiumButton(loginBtn, new Color(0, 53, 69), new Color(0, 85, 110)); // Navy with lighter hover
        
        gbc.gridy = 5; gbc.insets = new Insets(40, 50, 10, 50);
        formPanel.add(loginBtn, gbc);

        // Signup Link
        JButton signupLink = new JButton("Don't have an account? Create one");
        signupLink.setContentAreaFilled(false);
        signupLink.setBorderPainted(false);
        signupLink.setForeground(new Color(0, 102, 204));
        signupLink.setFont(new Font("SansSerif", Font.BOLD, 14));
        signupLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6; formPanel.add(signupLink, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ==========================================
        // 3. CAROUSEL TIMER & ACTIONS
        // ==========================================
        
        startCarousel();

        // QoL: Pressing 'Enter' triggers login
        passwordField.addActionListener(e -> handleLogin());
        loginBtn.addActionListener(e -> handleLogin());
        
        signupLink.addActionListener(e -> {
            this.dispose();
            new SignupWindow().setVisible(true);
        });
    }

    private void startCarousel() {
        updateCarouselImage();
        carouselTimer = new Timer(4000, e -> {
            currentImageIndex = (currentImageIndex + 1) % imagePaths.length;
            updateCarouselImage();
        });
        carouselTimer.start();
    }

    private void updateCarouselImage() {
        try {
            ImageIcon rawIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(imagePaths[currentImageIndex])));
            Image img = rawIcon.getImage();
            
            // INCREASED SCALING WIDTH to match the new 650px panel
            Image scaledImg = img.getScaledInstance(650, 1080, Image.SCALE_SMOOTH); 
            
            carouselLabel.setIcon(new ImageIcon(scaledImg));
        } catch (Exception e) {
            System.out.println("Could not load image: " + imagePaths[currentImageIndex]);
            carouselLabel.setBackground(new Color(33, 37, 41));
            carouselLabel.setOpaque(true);
        }
    }

    // --- NEW: Premium Field Styling ---
    private void styleField(JTextField field) {
        field.setPreferredSize(new Dimension(380, 50)); // Much wider and taller
        field.setFont(new Font("SansSerif", Font.PLAIN, 16)); // Larger text
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)), 
            BorderFactory.createEmptyBorder(5, 15, 5, 15) // Deep internal padding
        ));
    }

    // --- NEW: Premium Button Styling with Hover ---
    private void stylePremiumButton(JButton btn, Color primaryColor, Color hoverColor) {
        btn.setPreferredSize(new Dimension(0, 55)); // Massive click target
        btn.setBackground(primaryColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hoverColor); }
            public void mouseExited(MouseEvent e) { btn.setBackground(primaryColor); }
        });
    }

    private void handleLogin() {
        User loggedInUser = new UserDAO().authenticateUser(emailField.getText(), new String(passwordField.getPassword()));
        if (loggedInUser != null) {
            this.dispose();
            if (loggedInUser.getRole().equals("Admin")) new AdminDashboard(loggedInUser).setVisible(true);
            else if (loggedInUser.getRole().equals("Organizer")) new OrganizerDashboard(loggedInUser).setVisible(true);
            else new DashboardWindow(loggedInUser).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}