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

    // 1. For Students: Fetch ONLY Approved Events (Includes Real Venue Name)
    public List<Event> getApprovedEvents() {
        List<Event> eventList = new ArrayList<>();
        String query = "SELECT e.Event_ID, e.Title, e.Event_Date, e.Status, e.Current_Registrations, v.Location AS Venue_Name " +
                       "FROM Event e JOIN Venue v ON e.Venue_ID = v.Venue_ID " +
                       "WHERE e.Status = 'Approved'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                eventList.add(new Event(
                        rs.getString("Event_ID"), 
                        rs.getString("Title"), 
                        rs.getDate("Event_Date"),
                        rs.getString("Status"), 
                        rs.getInt("Current_Registrations"), 
                        rs.getString("Venue_Name") // Passing the Location instead of ID
                ));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return eventList;
    }

    // 2. For Organizers: Fetch ONLY their own events (Includes Real Venue Name)
    public List<Event> getEventsByOrganizer(String organizerId) {
        List<Event> eventList = new ArrayList<>();
        String query = "SELECT e.Event_ID, e.Title, e.Event_Date, e.Status, e.Current_Registrations, v.Location AS Venue_Name " +
                       "FROM Event e JOIN Venue v ON e.Venue_ID = v.Venue_ID " +
                       "WHERE e.Organizer_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, organizerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    eventList.add(new Event(
                            rs.getString("Event_ID"), 
                            rs.getString("Title"), 
                            rs.getDate("Event_Date"),
                            rs.getString("Status"), 
                            rs.getInt("Current_Registrations"), 
                            rs.getString("Venue_Name") // Passing the Location instead of ID
                    ));
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return eventList;
    }

    // 3. For Admins: Fetch ONLY Pending events (Includes Real Venue Name)
    public List<Event> getPendingEvents() {
        List<Event> eventList = new ArrayList<>();
        String query = "SELECT e.Event_ID, e.Title, e.Event_Date, e.Status, e.Current_Registrations, v.Location AS Venue_Name " +
                       "FROM Event e JOIN Venue v ON e.Venue_ID = v.Venue_ID " +
                       "WHERE e.Status = 'Pending'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                eventList.add(new Event(
                        rs.getString("Event_ID"), 
                        rs.getString("Title"), 
                        rs.getDate("Event_Date"),
                        rs.getString("Status"), 
                        rs.getInt("Current_Registrations"), 
                        rs.getString("Venue_Name") // Passing the Location instead of ID
                ));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return eventList;
    }

    // 4. Create a new Event (Defaults to Pending via the UI)
    public boolean insertEvent(String eventId, String title, Date eventDate, String venueId, String organizerId, String status) {
        // Admin_ID is hardcoded to 'U001' as the default system approver
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
            
        } catch (Exception e) { 
            System.out.println("Error inserting new event:");
            e.printStackTrace(); 
            return false; 
        }
    }

    // 5. Approve an Event (Admin only)
    public boolean approveEvent(String eventId) {
        String query = "UPDATE Event SET Status = 'Approved' WHERE Event_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, eventId);
            return stmt.executeUpdate() > 0;
            
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    // 6. Delete an Event (Handles Foreign Keys automatically)
    public boolean deleteEvent(String eventId) {
        String deleteRegQuery = "DELETE FROM Registration WHERE Event_ID = ?";
        String deleteEventQuery = "DELETE FROM Event WHERE Event_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement regStmt = conn.prepareStatement(deleteRegQuery);
             PreparedStatement eventStmt = conn.prepareStatement(deleteEventQuery)) {
            
            // 1. Delete all student registrations tied to this event first
            regStmt.setString(1, eventId);
            regStmt.executeUpdate(); 
            
            // 2. Then delete the actual event
            eventStmt.setString(1, eventId);
            return eventStmt.executeUpdate() > 0; 
            
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }
    // 7. Reject an Event (Admin only - sets status to 'Rejected')
    public boolean rejectEvent(String eventId) {
        String query = "UPDATE Event SET Status = 'Rejected' WHERE Event_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, eventId);
            return stmt.executeUpdate() > 0;
            
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }
}