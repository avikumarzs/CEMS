package dao;

import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegistrationDAO {

    // Method to register a student for an event
    public boolean registerStudent(String regId, java.sql.Date regDate, String studentId, String eventId) {
        String query = "INSERT INTO Registration (Reg_ID, Reg_Date, Student_ID, Event_ID) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, regId);
            stmt.setDate(2, regDate);
            stmt.setString(3, studentId);
            stmt.setString(4, eventId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            System.out.println("Error registering for event:");
            e.printStackTrace();
            return false;
        }
    }
}