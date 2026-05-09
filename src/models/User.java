package models;

public class User {
    private String userId;
    private String name;
    private String email;
    private String role; // Student, Organizer, Admin
    private String password; // ADDED: To match Password in DB
    private String deptId;

    // Full Constructor for Database Retrieval
    public User(String userId, String name, String email, String role, String password, String deptId) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
        this.deptId = deptId;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getPassword() { return password; }
    public String getDeptId() { return deptId; }
}