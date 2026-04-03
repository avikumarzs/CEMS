package models;

public class User {
    // 1. Private fields (Encapsulation)
    private String userId;
    private String name;
    private String email;
    private String role; // Student, Organizer, Admin
    private String deptId;

    // 2. Constructor
    public User(String userId, String name, String email, String role, String deptId) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.deptId = deptId;
    }

    // 3. Getters (We only need getters for now since the DB handles updates)
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getDeptId() { return deptId; }
}