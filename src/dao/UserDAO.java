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
}