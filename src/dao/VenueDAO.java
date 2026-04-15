package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import models.Venue;

public class VenueDAO {

    // --- 1. Insert Venue (Used by AdminDashboard) ---
    public boolean insertVenue(String venueId, String name, int capacity) {
        
        // ⚠️ EDIT HERE: If your DB column is 'Name', change 'Venue_Name' to 'Name'
        String query = "INSERT INTO Venue (Venue_ID, Location, Capacity) VALUES (?, ?, ?)";
        
        try (Connection conn = utils.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, venueId);
            stmt.setString(2, name);
            stmt.setInt(3, capacity);
            
            return stmt.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- 2. Fetch Display Names (Used by AddEventWindow Dropdown) ---
    public List<String> getAllVenueDisplayNames() {
        List<String> venues = new ArrayList<>();
        String query = "SELECT * FROM Venue"; 
        
        try (Connection conn = utils.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            venues.add("-- Select a Venue --");
            
            while (rs.next()) {
                // ⚠️ EDIT HERE: Change "Venue_Name" to "Name" if needed!
                venues.add(rs.getString("Venue_ID") + " - " + rs.getString("Location"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (venues.size() == 1) { // Only the placeholder exists
            venues.add("-- No Venues Available --");
        }
        
        return venues;
    }

    // --- 3. Fetch Full Venues (Used by OrganizerDashboard Table) ---
    public List<Venue> getAllVenues() {
        List<Venue> list = new ArrayList<>();
        String query = "SELECT * FROM Venue";
        
        try (Connection conn = utils.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(new Venue(
                    rs.getString("Venue_ID"),
                    // ⚠️ EDIT HERE: Change "Venue_Name" to "Name" if needed!
                    rs.getString("Location"), 
                    rs.getInt("Capacity")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}