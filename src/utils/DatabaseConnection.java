package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane; // Added for UI popups

public class DatabaseConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/campus_events_db";
    private static final String USER = "root"; 
    private static final String PASSWORD = "avi1"; 

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
                "CRITICAL ERROR: Cannot connect to the database.\nEnsure your MySQL server is running.", 
                "Server Offline", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return connection;
    }
}