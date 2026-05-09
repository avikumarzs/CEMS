package models;

public class Department {
    private String deptId;
    private String name;

    // Full Constructor
    public Department(String deptId, String name) {
        this.deptId = deptId;
        this.name = name;
    }

    // Getters
    public String getDeptId() { return deptId; }
    public String getName() { return name; }

    // Setters
    public void setDeptId(String deptId) { this.deptId = deptId; }
    public void setName(String name) { this.name = name; }
}