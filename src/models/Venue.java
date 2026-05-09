package models;

public class Venue {
    private String venueId;
    private String location; // CHANGED: Matches 'Location' column in DB
    private int capacity;
    private String status; // ADDED: Matches 'Status' column in DB

    // Full Constructor
    public Venue(String venueId, String location, int capacity, String status) {
        this.venueId = venueId;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
    }

    // Getters
    public String getVenueId() { return venueId; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public String getStatus() { return status; }

    // Setters
    public void setLocation(String location) { this.location = location; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setStatus(String status) { this.status = status; }
}
