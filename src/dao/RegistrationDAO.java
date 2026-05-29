package dao;

import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class RegistrationDAO {

    // ====================================================
    // REPLACES SQL FUNCTION: Get_Remaining_Capacity
    // ====================================================
    public int getRemainingCapacity(String eventId) {
        String query = "SELECT v.Capacity, e.Current_Registrations " +
                       "FROM Event e JOIN Venue v ON e.Venue_ID = v.Venue_ID " +
                       "WHERE e.Event_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, eventId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int capacity = rs.getInt("Capacity");
                int currentRegs = rs.getInt("Current_Registrations");
                return capacity - currentRegs;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Fail-safe
    }

    // ====================================================
    // SECURITY CHECK: Prevent Spam Clicking
    // ====================================================
    public boolean isAlreadyRegistered(String studentId, String eventId) {
        String query = "SELECT * FROM Registration WHERE Student_ID = ? AND Event_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            stmt.setString(2, eventId);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====================================================
    // REPLACES: Register_Student_Safe (Procedure) + INSERT Trigger
    // ====================================================
    public String registerStudentSafe(String studentId, String eventId) {
        // 1. Execute DB Constraints in Java
        if (isAlreadyRegistered(studentId, eventId)) {
            return "ALREADY_REGISTERED";
        }
        if (getRemainingCapacity(eventId) <= 0) {
            return "VENUE_FULL";
        }

        String regId = "R" + (int)(Math.random() * 10000);
        String insertRegQuery = "INSERT INTO Registration (Reg_ID, Reg_Date, Student_ID, Event_ID) VALUES (?, ?, ?, ?)";
        // Java handles the trigger logic here:
        String updateEventQuery = "UPDATE Event SET Current_Registrations = Current_Registrations + 1 WHERE Event_ID = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            // START TRANSACTION (Acts exactly like a SQL Stored Procedure)
            conn.setAutoCommit(false); 

            // Step 1: Insert Registration
            try (PreparedStatement insertStmt = conn.prepareStatement(insertRegQuery)) {
                insertStmt.setString(1, regId);
                insertStmt.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                insertStmt.setString(3, studentId);
                insertStmt.setString(4, eventId);
                insertStmt.executeUpdate();
            }

            // Step 2: Fire the "Trigger" (Update Event Seat Count)
            try (PreparedStatement updateStmt = conn.prepareStatement(updateEventQuery)) {
                updateStmt.setString(1, eventId);
                updateStmt.executeUpdate();
            }

            // COMMIT TRANSACTION: If both worked, save them to the cloud permanently
            conn.commit(); 
            return "SUCCESS";

        } catch (SQLException e) {
            // ROLLBACK: If it crashes halfway through, undo everything so data stays clean
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } 
            }
            e.printStackTrace();
            return "DB_ERROR";
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // ====================================================
    // REPLACES: Cancel_Registration (Procedure) + DELETE Trigger
    // ====================================================
    public boolean cancelRegistration(String studentId, String eventId) {
        String deleteRegQuery = "DELETE FROM Registration WHERE Student_ID = ? AND Event_ID = ?";
        // Java handles the trigger logic here:
        String updateEventQuery = "UPDATE Event SET Current_Registrations = Current_Registrations - 1 WHERE Event_ID = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // START TRANSACTION

            int rowsAffected = 0;
            // Step 1: Delete Registration
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteRegQuery)) {
                deleteStmt.setString(1, studentId);
                deleteStmt.setString(2, eventId);
                rowsAffected = deleteStmt.executeUpdate();
            }

            // Step 2: Only fire the "Trigger" if a row was actually deleted
            if (rowsAffected > 0) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateEventQuery)) {
                    updateStmt.setString(1, eventId);
                    updateStmt.executeUpdate();
                }
            }

            conn.commit(); // COMMIT
            return rowsAffected > 0;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}