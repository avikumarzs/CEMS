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
                
                // UPDATED: Added 'password' to the constructor to match the new 3NF User Model
                loggedInUser = new User(id, name, email, role, password, deptId);
            }

        } catch (SQLException e) {
            System.out.println("Database error during login authentication.");
            e.printStackTrace();
        }

        // Returns the User object if successful, or null if login failed
        return loggedInUser;
    }

    // --- Register a New User ---
    // --- UPDATED: Register a New User (Now handles Departments!) ---
    // --- UPDATED: Register a New User with Exact Error Parsing ---
    public boolean registerUser(String userId, String name, String email, String password, String role, String deptId) {
        String insertQuery = "INSERT INTO User (User_ID, Name, Email, Password, Role, Dept_ID) VALUES (?, ?, ?, ?, ?, ?)";

        // Notice we are catching SQLException now, not a generic Exception
        try (java.sql.Connection conn = utils.DatabaseConnection.getConnection();
             java.sql.PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
            
            insertStmt.setString(1, userId);
            insertStmt.setString(2, name);
            insertStmt.setString(3, email);
            insertStmt.setString(4, password);
            insertStmt.setString(5, role);
            
            if (deptId == null || deptId.isEmpty()) {
                insertStmt.setNull(6, java.sql.Types.VARCHAR);
            } else {
                insertStmt.setString(6, deptId);
            }
            
            return insertStmt.executeUpdate() > 0;
            
        } catch (java.sql.SQLException e) {
            // THE MAGIC: Translating MySQL Error Codes into context!
            if (e.getErrorCode() == 1062) {
                System.out.println("SQL Warning [1062]: User attempted to register with an existing email: " + email);
                // Return false safely so the UI can show the "Email already exists" popup
                return false; 
            } else if (e.getErrorCode() == 1048) {
                System.out.println("SQL Warning [1048]: A required database field was left null.");
                return false;
            } else {
                // If it's a critical crash (like the database server being turned off), print the full stack trace
                System.out.println("CRITICAL SQL ERROR: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
    }
}