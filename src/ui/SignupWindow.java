package ui;

import dao.UserDAO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class SignupWindow extends JFrame {

    private JTextField nameField, emailField;
    private JPasswordField passField;
    private JComboBox<String> roleBox;
    
    // Carousel Variables
    private JLabel carouselLabel;
    private Timer carouselTimer;
    private int currentImageIndex = 0;
    
    // Matching the Login images exactly
    private final String[] imagePaths = {
        "/assets/img1.jpeg", 
        "/assets/img2.jpeg", 
        "/assets/img3.jpeg",
        "/assets/img4.jpeg"
    };

    public SignupWindow() {
        setTitle("CEMS - Create Account");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==========================================
        // 1. LEFT PANEL (Seamless Carousel Match)
        // ==========================================
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BorderLayout());
        brandPanel.setBackground(new Color(33, 37, 41));
        brandPanel.setPreferredSize(new Dimension(650, 0)); // Exact match to Login

        carouselLabel = new JLabel();
        carouselLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel overlayPanel = new JPanel();
        overlayPanel.setLayout(new BoxLayout(overlayPanel, BoxLayout.Y_AXIS));
        overlayPanel.setOpaque(false); 
        overlayPanel.setBorder(new EmptyBorder(180, 60, 40, 60));

        JLabel titleLabel = new JLabel("Join CEMS");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 64)); 
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("<html>Create an account to start<br>organizing or attending events.</html>");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 22)); 
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
        // 2. RIGHT PANEL (Floating Card UI)
        // ==========================================
        
        JPanel rightBackground = new JPanel(new GridBagLayout());
        rightBackground.setBackground(new Color(241, 243, 245)); // Match Login gray

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(new CompoundBorder(
            new LineBorder(new Color(222, 226, 230), 1, true),
            new EmptyBorder(40, 60, 40, 60) 
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        JLabel header = new JLabel("Create Account");
        header.setFont(new Font("SansSerif", Font.BOLD, 36));
        header.setForeground(new Color(33, 37, 41));
        gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0, 0, 30, 0);
        formCard.add(header, gbc);

        gbc.gridwidth = 1; gbc.insets = new Insets(5, 0, 5, 0);

        // Fields
        nameField = createStyledField("Full Name", formCard, 1, gbc);
        emailField = createStyledField("Email Address", formCard, 3, gbc);
        passField = createStyledPasswordField("Password", formCard, 5, gbc);

        // Role Dropdown
        JLabel roleLabel = new JLabel("Register As");
        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        roleLabel.setForeground(new Color(108, 117, 125));
        gbc.gridy = 7; formCard.add(roleLabel, gbc);

        String[] roles = {"Student", "Organizer"};
        roleBox = new JComboBox<>(roles);
        roleBox.setPreferredSize(new Dimension(380, 50));
        roleBox.setBackground(new Color(248, 249, 250));
        roleBox.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridy = 8; gbc.insets = new Insets(5, 0, 15, 0);
        formCard.add(roleBox, gbc);

        // Buttons
        JButton signupBtn = new JButton("Sign Up Now");
        stylePremiumButton(signupBtn, new Color(40, 167, 69), new Color(33, 136, 56));
        gbc.gridy = 9; gbc.insets = new Insets(20, 0, 10, 0);
        formCard.add(signupBtn, gbc);

        JButton backBtn = new JButton("Already have an account? Login");
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setForeground(new Color(0, 102, 204));
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 10;
        formCard.add(backBtn, gbc);

        rightBackground.add(formCard);
        add(rightBackground, BorderLayout.CENTER);

        // ==========================================
        // 3. CAROUSEL TIMER & ACTIONS
        // ==========================================
        
        startCarousel();

        backBtn.addActionListener(e -> {
            if (carouselTimer != null) carouselTimer.stop(); // Prevent memory leak
            this.dispose();
            new LoginWindow().setVisible(true);
        });

        signupBtn.addActionListener(e -> handleSignup());
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
            ImageIcon scaledIcon = createCoverIcon(rawIcon.getImage(), 650, Toolkit.getDefaultToolkit().getScreenSize().height); 
            carouselLabel.setIcon(scaledIcon);
        } catch (Exception e) {
            carouselLabel.setBackground(new Color(33, 37, 41));
            carouselLabel.setOpaque(true);
        }
    }

    private ImageIcon createCoverIcon(Image img, int targetWidth, int targetHeight) {
        BufferedImage bimg = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bimg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double imgRatio = (double) img.getWidth(null) / img.getHeight(null);
        double targetRatio = (double) targetWidth / targetHeight;
        int drawWidth = targetWidth, drawHeight = targetHeight, x = 0, y = 0;

        if (imgRatio > targetRatio) {
            drawWidth = (int) (drawHeight * imgRatio);
            x = (targetWidth - drawWidth) / 2; 
        } else {
            drawHeight = (int) (drawWidth / imgRatio);
            y = (targetHeight - drawHeight) / 2; 
        }

        g2d.drawImage(img, x, y, drawWidth, drawHeight, null);
        g2d.dispose();
        return new ImageIcon(bimg);
    }

    private JTextField createStyledField(String labelText, JPanel container, int row, GridBagConstraints gbc) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(new Color(108, 117, 125));
        gbc.gridy = row; container.add(lbl, gbc);

        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(380, 50));
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)), 
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        gbc.gridy = row + 1; container.add(field, gbc);
        return field;
    }

    private JPasswordField createStyledPasswordField(String labelText, JPanel container, int row, GridBagConstraints gbc) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(new Color(108, 117, 125));
        gbc.gridy = row; container.add(lbl, gbc);

        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(380, 50));
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)), 
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        gbc.gridy = row + 1; container.add(field, gbc);
        return field;
    }

    private void stylePremiumButton(JButton btn, Color primaryColor, Color hoverColor) {
        btn.setPreferredSize(new Dimension(0, 55));
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
            if (carouselTimer != null) carouselTimer.stop(); // Stop timer before leaving
            this.dispose();
            new LoginWindow().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Email already in use.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}