import javax.swing.*;
import ui.LoginWindow;

public class Main {
    public static void main(String[] args) {
        // 1. Set Global UI Properties for a modern look
        try {
            // Makes the window borders and buttons match the Operating System (Windows 10/11)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Modernizing ToolTips and Dialogs
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Launch the Application
        SwingUtilities.invokeLater(() -> {
            LoginWindow login = new LoginWindow();
            login.setVisible(true);
        });
    }
}
