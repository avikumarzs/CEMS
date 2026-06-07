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
    private static final String BASE_URL = "https://cems-api.onrender.com";
    
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
}