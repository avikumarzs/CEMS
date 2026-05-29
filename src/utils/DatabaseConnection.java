package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane; // Added for UI popups

public class DatabaseConnection {
    
    private static final String URL = "jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/cems?sslMode=REQUIRED";
    private static final String USER = "UVdSiGfT4f26QUZ.root";
    private static final String PASSWORD = "XXRwQIyd2f9djaD2"; 

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            // SMART ERROR: Tell the user the database is down!
            JOptionPane.showMessageDialog(null, 
                "CRITICAL ERROR: Cannot connect to the cloud database.\nCheck your internet connection or credentials.", 
                "Server Offline", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return connection;
    }
}