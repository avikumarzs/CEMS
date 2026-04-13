package dao;

import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

    // 2. THE INSERT: Actually register the student
    public boolean registerStudent(String regId, Date regDate, String studentId, String eventId) {
        String query = "INSERT INTO Registration (Reg_ID, Reg_Date, Student_ID, Event_ID) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, regId);
            stmt.setDate(2, regDate);
            stmt.setString(3, studentId);
            stmt.setString(4, eventId);
            
            return stmt.executeUpdate() > 0; // Triggers will automatically update Event count!
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // --- NEW: Cancel a registration ---
    public boolean unregisterStudent(String studentId, String eventId) {
        String query = "DELETE FROM Registration WHERE Student_ID = ? AND Event_ID = ?";
        
        try (java.sql.Connection conn = utils.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, studentId);
            stmt.setString(2, eventId);
            
            return stmt.executeUpdate() > 0; // Triggers should auto-decrement the event count!
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}