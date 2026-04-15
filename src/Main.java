import com.formdev.flatlaf.themes.FlatMacLightLaf;
import javax.swing.UIManager;
import java.awt.Insets;
import ui.LoginWindow;

public class Main {
    public static void main(String[] args) {
        
        try {
            // 1. Switch to the ultra-premium Mac theme instead of the basic flat theme
            UIManager.setLookAndFeel(new FlatMacLightLaf()); 
            
            // 2. Global Web-App Styling Tweaks
            UIManager.put("Button.arc", 12);         // Apple-style rounded buttons
            UIManager.put("Component.arc", 12);      // Rounded dropdowns
            UIManager.put("TextComponent.arc", 12);  // Rounded text fields
            
            UIManager.put("Component.focusWidth", 2); // Thicker, web-like blue focus ring
            UIManager.put("Component.innerFocusWidth", 1);
            
            UIManager.put("ScrollBar.showButtons", false); // Hide the clunky up/down arrows
            UIManager.put("ScrollBar.thumbArc", 999);      // Pill-shaped scrollbars
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
            
            System.out.println("SUCCESS: FlatLaf Mac UI Loaded!");
            
        } catch (Exception ex) {
            System.err.println("FAILED to load FlatLaf.");
            ex.printStackTrace();
        }
        
        java.awt.EventQueue.invokeLater(() -> {
            new LoginWindow().setVisible(true);
        });
    }
}