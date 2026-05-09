package models;

import java.sql.Date;

public class Event {
    private String eventId;
    private String title;
    private Date eventDate;
    private String status;
    private int currentRegistrations;
    private String venueId;
    private String organizerId;
    private String adminId; // ADDED: To match Admin_ID in DB

    // Full Constructor for Database Retrieval
    public Event(String eventId, String title, Date eventDate, String status, int currentRegistrations, String venueId, String organizerId, String adminId) {
        this.eventId = eventId;
        this.title = title;
        this.eventDate = eventDate;
        this.status = status;
        this.currentRegistrations = currentRegistrations;
        this.venueId = venueId;
        this.organizerId = organizerId;
        this.adminId = adminId;
    }

    // Getters
    public String getEventId() { return eventId; }
    public String getTitle() { return title; }
    public Date getEventDate() { return eventDate; }
    public String getStatus() { return status; }
    public int getCurrentRegistrations() { return currentRegistrations; }
    public String getVenueId() { return venueId; }
    public String getOrganizerId() { return organizerId; }
    public String getAdminId() { return adminId; }

    // Setters
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
}