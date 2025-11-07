package com.employee.model;

import java.time.LocalDate;

public class Department {
    private int departmentId;
    private String name;
    private String description;
    private int managerId;
    private LocalDate createdAt;

    public Department() {}

    public Department(int departmentId, String name, String description, int managerId, LocalDate createdAt) {
        this.departmentId = departmentId;
        this.name = name;
        this.description = description;
        this.managerId = managerId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getManagerId() { return managerId; }
    public void setManagerId(int managerId) { this.managerId = managerId; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    // ✅ Add this to make it display nicely in JComboBox or JTable
    @Override
    public String toString() {
        // Example: "HR - Human Resources"
        if (description != null && !description.isEmpty()) {
            return departmentId + " - " + name + " (" + description + ")";
        } else {
            return departmentId + " - " + name;
        }
    }
}
