package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    // CHANGE THESE to match your local MySQL setup
    private static final String URL = "jdbc:mysql://localhost:3306/campus_events_db";
    private static final String USER = "root"; 
    private static final String PASSWORD = "avi1"; // <-- UPDATE THIS!

    // This method returns the active connection to our database
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load the MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish the connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            // We will remove this print statement later, but it's good for testing now
            System.out.println("Success: Connected to MySQL Database!");
            
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL JDBC Driver not found. Did you add the .jar file?");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error: Database connection failed. Check your URL, username, and password.");
            e.printStackTrace();
        }
        return connection;
    }

    // --- TEMPORARY MAIN METHOD FOR TESTING ---
    public static void main(String[] args) {
        System.out.println("Attempting to connect to the database...");
        Connection conn = getConnection();
        
        if (conn != null) {
            System.out.println("BOOM! The database is officially hooked up.");
        } else {
            System.out.println("Uh oh. Something went wrong.");
        }
    }
}