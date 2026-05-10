package dao;

import models.Department;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    // 1. Fetch all departments
    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        String query = "SELECT * FROM Department ORDER BY Dept_ID";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(new Department(rs.getString("Dept_ID"), rs.getString("Name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Add a new department
    public boolean insertDepartment(String deptId, String name) {
        String query = "INSERT INTO Department (Dept_ID, Name) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, deptId);
            stmt.setString(2, name);
            return stmt.executeUpdate() > 0;
            
        } catch (java.sql.SQLException e) {
            // SMART BACKEND VALIDATION
            if (e.getErrorCode() == 1062) {
                System.out.println("SQL Warning [1062]: Admin attempted to create a duplicate Department ID (" + deptId + ").");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    // 3. Safely delete a department (Handles Foreign Keys automatically!)
    public boolean deleteDepartment(String deptId) {
        // Step A: Detach any users currently in this department so the database doesn't crash
        String updateUsersQuery = "UPDATE User SET Dept_ID = NULL WHERE Dept_ID = ?";
        // Step B: Actually delete the department
        String deleteDeptQuery = "DELETE FROM Department WHERE Dept_ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateUsersQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteDeptQuery)) {
            
            // 1. Set User departments to NULL
            updateStmt.setString(1, deptId);
            updateStmt.executeUpdate();
            
            // 2. Delete the Department
            deleteStmt.setString(1, deptId);
            return deleteStmt.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}