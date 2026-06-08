package utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

public class HttpUtils {

    // Target your local Spring Boot middleware pipeline
    // Switch from localhost to your live production API
    private static final String BASE_URL = "https://cems-api.onrender.com/api";
    
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(65))
            .build();

    /**
     * Sends authentication credentials to the Spring Boot middleware layer.
     * @public
     * @param email The target user login identifier
     * @param password The target user validation password
     * @return HttpResponse containing the JSON user block or an error payload status string
     */
    public static HttpResponse<String> sendLoginRequest(String email, String password) {
        // Construct a manual stateless JSON body segment
        String jsonPayload = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonPayload))
                    .build();

            // Fire request over the local wire and return the web server response package
            return client.send(request, BodyHandlers.ofString());
            

        } catch (Exception e) {
            e.printStackTrace();
            return null; // Signals a completely drop in network availability
        }
    }
    /**
     * Fetches the live list of departments from the cloud via the API.
     */
    public static HttpResponse<String> fetchDepartments() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/departments"))
                    .GET()
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Sends a secure account creation payload to the API.
     */
    public static HttpResponse<String> sendSignupRequest(String userId, String name, String email, String password, String role, String deptId) {
        // Format deptId securely (handle nulls for Organizers)
        String deptJson = (deptId == null || deptId.isEmpty()) ? "null" : "\"" + deptId + "\"";
        
        String jsonPayload = String.format("{\"userId\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"role\":\"%s\",\"deptId\":%s}", 
                userId, name, email, password, role, deptJson);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/signup"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonPayload))
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            return null;
        }
    }
    public static HttpResponse<String> fetchApprovedEvents() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/events/approved"))
                    .GET()
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }

    public static HttpResponse<String> fetchMyRegisteredEvents(String studentId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/events/student/" + studentId))
                    .GET()
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }

    public static HttpResponse<String> registerForEvent(String studentId, String eventId) {
        String jsonPayload = String.format("{\"studentId\":\"%s\",\"eventId\":\"%s\"}", studentId, eventId);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/events/register"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonPayload))
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }

    public static HttpResponse<String> cancelRegistration(String studentId, String eventId) {
        String jsonPayload = String.format("{\"studentId\":\"%s\",\"eventId\":\"%s\"}", studentId, eventId);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/events/cancel"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonPayload))
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }
    // Fetch all venues (for Admin)
    public static HttpResponse<String> fetchAllVenues() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/venues"))
                    .GET()
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }

    // Fetch only available venues (for Organizers)
    public static HttpResponse<String> fetchAvailableVenues() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/venues/available"))
                    .GET()
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }

    // Create a new venue
    public static HttpResponse<String> createVenue(String venueId, String location, int capacity) {
        String jsonPayload = String.format("{\"venueId\":\"%s\",\"location\":\"%s\",\"capacity\":%d}", 
                venueId, location, capacity);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/venues"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonPayload))
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }

    // Delete a venue
    public static HttpResponse<String> deleteVenue(String venueId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/venues/" + venueId))
                    .DELETE()
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }
    // Fetch Organizer's Events
    public static HttpResponse<String> fetchOrganizerEvents(String organizerId) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/events/organizer/" + organizerId)).GET().build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }

    // Fetch Pending Events (Admins)
    public static HttpResponse<String> fetchPendingEvents() {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/events/pending")).GET().build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }

    // Create a new event
    public static HttpResponse<String> createEvent(String eventId, String title, String eventDate, String venueId, String organizerId, String status) {
        String jsonPayload = String.format("{\"eventId\":\"%s\",\"title\":\"%s\",\"eventDate\":\"%s\",\"venueId\":\"%s\",\"organizerId\":\"%s\",\"status\":\"%s\"}", 
                eventId, title, eventDate, venueId, organizerId, status);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/events"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonPayload))
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }

    // Update Event Status (Approve/Reject)
    public static HttpResponse<String> updateEventStatus(String eventId, String newStatus) {
        String jsonPayload = String.format("{\"status\":\"%s\"}", newStatus);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/events/" + eventId + "/status"))
                    .header("Content-Type", "application/json")
                    .PUT(BodyPublishers.ofString(jsonPayload))
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }

    // Delete Event
    public static HttpResponse<String> deleteEvent(String eventId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/events/" + eventId))
                    .DELETE()
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }
    // --- ADMIN: Fetch all departments ---
    public static HttpResponse<String> fetchAllDepartments() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/departments"))
                    .GET()
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }

    // --- ADMIN: Create a new department ---
    public static HttpResponse<String> createDepartment(String deptId, String name) {
        String jsonPayload = String.format("{\"deptId\":\"%s\",\"name\":\"%s\"}", deptId, name);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/departments"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonPayload))
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }

    // --- ADMIN: Delete a department ---
    public static HttpResponse<String> deleteDepartment(String deptId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/departments/" + deptId))
                    .DELETE()
                    .build();
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) { return null; }
    }
}