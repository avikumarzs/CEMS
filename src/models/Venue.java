package models;

public class Venue {
    private String venueId;
    private String venueName;
    private int capacity;

    // --- CONSTRUCTOR ---
    public Venue(String venueId, String venueName, int capacity) {
        this.venueId = venueId;
        this.venueName = venueName;
        this.capacity = capacity;
    }

    // --- GETTERS & SETTERS ---
    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}