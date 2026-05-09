package dao;

import utils.DatabaseConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrationDAO {

    // 1. THE SECURITY CHECK: Does this student already have a ticket?
    public boolean isAlreadyRegistered(String studentId, String eventId) {
        String query = "SELECT COUNT(*) FROM Registration WHERE Student_ID = ? AND Event_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, studentId);
            stmt.setString(2, eventId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Returns true if they are already in the DB
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; 
    }

    // 2. THE INSERT: Actually register the student using our Stored Procedure!
    // NOTE: I removed the "Date regDate" parameter because our SQL procedure uses CURDATE() automatically!
    public boolean registerStudent(String regId, String studentId, String eventId) {
        // Notice the {CALL ...} syntax! This tells JDBC to run a procedure instead of a raw query.
        String query = "{CALL Register_Student_Safe(?, ?, ?)}";
        
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            
            stmt.setString(1, regId);
            stmt.setString(2, studentId);
            stmt.setString(3, eventId);
            
            return stmt.executeUpdate() > 0; 
            
        } catch (SQLException e) {
            // THE MAGIC: If the Venue is full, MySQL throws error 45000. We catch it here!
            if ("45000".equals(e.getSQLState())) {
                System.out.println("Registration Blocked by Database: " + e.getMessage());
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    // 3. THE DELETE: Cancel a registration using our Stored Procedure!
    public boolean unregisterStudent(String studentId, String eventId) {
        String query = "{CALL Cancel_Registration(?, ?)}";
        
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            
            stmt.setString(1, studentId);
            stmt.setString(2, eventId);
            
            return stmt.executeUpdate() > 0; 
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}