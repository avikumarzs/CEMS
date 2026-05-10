package dao;

import models.Venue;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class VenueDAO {

    // 1. Fetch ALL Available Venues (Used by Organizers creating events)
    public List<Venue> getAvailableVenues() {
        List<Venue> venueList = new ArrayList<>();
        String query = "SELECT * FROM Venue WHERE Status = 'Available'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                venueList.add(new Venue(
                        rs.getString("Venue_ID"), rs.getString("Location"), 
                        rs.getInt("Capacity"), rs.getString("Status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return venueList;
    }

    // 2. NEW: Fetch literally ALL Venues (Used by Admin Manage Venues table)
    public List<Venue> getAllVenues() {
        List<Venue> venueList = new ArrayList<>();
        String query = "SELECT * FROM Venue ORDER BY Venue_ID";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                venueList.add(new Venue(
                        rs.getString("Venue_ID"), rs.getString("Location"), 
                        rs.getInt("Capacity"), rs.getString("Status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return venueList;
    }

    // 3. Fetch a single Venue's details by its ID
    public Venue getVenueById(String venueId) {
        Venue venue = null;
        String query = "SELECT * FROM Venue WHERE Venue_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, venueId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    venue = new Venue(
                            rs.getString("Venue_ID"), rs.getString("Location"), 
                            rs.getInt("Capacity"), rs.getString("Status")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return venue;
    }

    // 4. Add a new Venue
    public boolean insertVenue(String venueId, String location, int capacity) {
        String query = "INSERT INTO Venue (Venue_ID, Location, Capacity, Status) VALUES (?, ?, ?, 'Available')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, venueId);
            stmt.setString(2, location);
            stmt.setInt(3, capacity);
            return stmt.executeUpdate() > 0;
            
        } catch (java.sql.SQLException e) {
            // SMART BACKEND VALIDATION
            if (e.getErrorCode() == 1062) {
                System.out.println("SQL Warning [1062]: Admin attempted to create a duplicate Venue ID (" + venueId + ").");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    // 5. NEW: Safely Delete a Venue
    public boolean deleteVenue(String venueId) {
        // Step A: Check if the venue is tied to any events right now
        String checkQuery = "SELECT COUNT(*) FROM Event WHERE Venue_ID = ?";
        // Step B: If safe, delete it
        String deleteQuery = "DELETE FROM Venue WHERE Venue_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
            
            checkStmt.setString(1, venueId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // Refuse to delete because it is in use!
                }
            }

            deleteStmt.setString(1, venueId);
            return deleteStmt.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}