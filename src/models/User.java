package models;

public class User {
    private String userId;
    private String name;
    private String email;
    private String role; // Student, Organizer, Admin
    private String password; 
    private String deptId;

    // Legacy Constructor for direct Database Retrieval (6 parameters)
    public User(String userId, String name, String email, String role, String password, String deptId) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
        this.deptId = deptId;
    }

    // 🌐 NEW: Secure Web API Constructor (5 parameters - No Password needed!)
    public User(String userId, String name, String email, String role, String deptId) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = null; // We purposefully don't hold the password in the UI memory anymore
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