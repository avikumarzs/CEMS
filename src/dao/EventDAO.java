package dao;

import models.Event;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    // Method to fetch all events from the database
    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<>();
        String query = "SELECT * FROM Event";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Create an Event object for every row in the database
                Event event = new Event(
                    rs.getString("Event_ID"),
                    rs.getString("Title"),
                    rs.getDate("Event_Date"),
                    rs.getString("Status"),
                    rs.getInt("Current_Registrations"),
                    rs.getString("Venue_ID")
                );
                eventList.add(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("DEBUG: EventDAO fetched " + eventList.size() + " events from MySQL.");
        return eventList;
    }

    // Method to insert a new event into the database
    public boolean insertEvent(String eventId, String title, java.sql.Date eventDate, String venueId, String organizerId) 
    {
        // We auto-set Status to 'Upcoming', Registrations to 0, and hardcode the Admin_ID to 'U001' for now
        String query = "INSERT INTO Event (Event_ID, Title, Event_Date, Status, Current_Registrations, Venue_ID, Organizer_ID, Admin_ID) VALUES (?, ?, ?, 'Upcoming', 0, ?, ?, 'U001')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, eventId);
            stmt.setString(2, title);
            stmt.setDate(3, eventDate);
            stmt.setString(4, venueId);
            stmt.setString(5, organizerId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Returns true if it worked!
            
        } catch (Exception e) {
            System.out.println("Error inserting event:");
            e.printStackTrace();
            return false;
        }
    }
    // Fetch only events created by a specific organizer
    public List<Event> getEventsByOrganizer(String organizerId) {
        List<Event> eventList = new ArrayList<>();
        String query = "SELECT * FROM Event WHERE Organizer_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, organizerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Event event = new Event(
                        rs.getString("Event_ID"),
                        rs.getString("Title"),
                        rs.getDate("Event_Date"),
                        rs.getString("Status"),
                        rs.getInt("Current_Registrations"),
                        rs.getString("Venue_ID")
                    );
                    eventList.add(event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventList;
    }
    public boolean deleteEvent(String eventId) 
    {
        String deleteRegQuery = "DELETE FROM Registration WHERE Event_ID = ?";
        String deleteEventQuery = "DELETE FROM Event WHERE Event_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement regStmt = conn.prepareStatement(deleteRegQuery);
             PreparedStatement eventStmt = conn.prepareStatement(deleteEventQuery)) {

            regStmt.setString(1, eventId);
            regStmt.executeUpdate();

            eventStmt.setString(1, eventId);
            int rowsAffected = eventStmt.executeUpdate();

            return rowsAffected > 0; 
            
        } catch (Exception e) {
            System.out.println("Error deleting event:");
            e.printStackTrace();
            return false;
        }
    }
}