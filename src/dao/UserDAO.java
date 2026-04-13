package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.User;
import utils.DatabaseConnection;

public class UserDAO {

    // This method checks the database for a matching email and password
    public User authenticateUser(String email, String password) {
        User loggedInUser = null;
        
        // The SQL query with ? placeholders to prevent SQL Injection attacks
        String sql = "SELECT * FROM User WHERE Email = ? AND Password = ?";

        // Try-with-resources: automatically closes the connection when done to save memory
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Fill in the ? placeholders with the user's typed input
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            // Execute the query and store the result
            ResultSet rs = pstmt.executeQuery();

            // If rs.next() is true, it means we found a match in the database!
            if (rs.next()) {
                // Extract the data from the database row
                String id = rs.getString("User_ID");
                String name = rs.getString("Name");
                String role = rs.getString("Role");
                String deptId = rs.getString("Dept_ID");

                // Create a new User Java Object with this data
                loggedInUser = new User(id, name, email, role, deptId);
            }

        } catch (SQLException e) {
            System.out.println("Database error during login authentication.");
            e.printStackTrace();
        }

        // Returns the User object if successful, or null if login failed
        return loggedInUser;
    }
    // --- NEW: Register a New User ---
    public boolean registerUser(String userId, String name, String email, String password, String role) {
        // We check if the email already exists first to prevent crashes
        String checkQuery = "SELECT COUNT(*) FROM User WHERE Email = ?";
        String insertQuery = "INSERT INTO User (User_ID, Name, Email, Password, Role, Dept_ID) VALUES (?, ?, ?, ?, ?, NULL)";

        try (Connection conn = utils.DatabaseConnection.getConnection()) {
            // 1. Check for duplicate email
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, email);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // Email already exists!
                }
            }

            // 2. Insert new user
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, userId);
                insertStmt.setString(2, name);
                insertStmt.setString(3, email);
                insertStmt.setString(4, password);
                insertStmt.setString(5, role);
                return insertStmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}