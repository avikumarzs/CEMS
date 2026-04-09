package dao;

import models.Event;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    // 1. For Students: Fetch ONLY Approved Events
    public List<Event> getApprovedEvents() {
        List<Event> eventList = new ArrayList<>();
        String query = "SELECT * FROM Event WHERE Status = 'Approved'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                eventList.add(new Event(rs.getString("Event_ID"), rs.getString("Title"), rs.getDate("Event_Date"),
                        rs.getString("Status"), rs.getInt("Current_Registrations"), rs.getString("Venue_ID")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return eventList;
    }

    // 2. For Organizers: Fetch ONLY their own events (Pending or Approved)
    public List<Event> getEventsByOrganizer(String organizerId) {
        List<Event> eventList = new ArrayList<>();
        String query = "SELECT * FROM Event WHERE Organizer_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, organizerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    eventList.add(new Event(rs.getString("Event_ID"), rs.getString("Title"), rs.getDate("Event_Date"),
                            rs.getString("Status"), rs.getInt("Current_Registrations"), rs.getString("Venue_ID")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return eventList;
    }

    // 3. For Admins: Fetch ONLY Pending events
    public List<Event> getPendingEvents() {
        List<Event> eventList = new ArrayList<>();
        String query = "SELECT * FROM Event WHERE Status = 'Pending'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                eventList.add(new Event(rs.getString("Event_ID"), rs.getString("Title"), rs.getDate("Event_Date"),
                        rs.getString("Status"), rs.getInt("Current_Registrations"), rs.getString("Venue_ID")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return eventList;
    }

    // 4. Create a new Event (Defaults to Pending via the UI)
    public boolean insertEvent(String eventId, String title, Date eventDate, String venueId, String organizerId, String status) {
        String query = "INSERT INTO Event (Event_ID, Title, Event_Date, Status, Venue_ID, Organizer_ID, Admin_ID) VALUES (?, ?, ?, ?, ?, ?, 'U001')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, eventId);
            stmt.setString(2, title);
            stmt.setDate(3, eventDate);
            stmt.setString(4, status); 
            stmt.setString(5, venueId);
            stmt.setString(6, organizerId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 5. Approve an Event (Admin only)
    public boolean approveEvent(String eventId) {
        String query = "UPDATE Event SET Status = 'Approved' WHERE Event_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, eventId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 6. Delete an Event (Handles Foreign Keys)
    public boolean deleteEvent(String eventId) {
        String deleteRegQuery = "DELETE FROM Registration WHERE Event_ID = ?";
        String deleteEventQuery = "DELETE FROM Event WHERE Event_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement regStmt = conn.prepareStatement(deleteRegQuery);
             PreparedStatement eventStmt = conn.prepareStatement(deleteEventQuery)) {
            
            regStmt.setString(1, eventId);
            regStmt.executeUpdate(); // Delete registrations first
            
            eventStmt.setString(1, eventId);
            return eventStmt.executeUpdate() > 0; // Then delete event
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}