package com.employee.dao;

import com.employee.model.Employee;
import com.employee.utils.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM employees";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    public Employee getEmployeeById(int employeeId) {
        String query = "SELECT * FROM employees WHERE employee_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, employeeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractEmployeeFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Employee getEmployeeByName(String fullName) {
        String query = "SELECT * FROM employees WHERE CONCAT(first_name, ' ', last_name) = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, fullName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractEmployeeFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addEmployee(Employee employee) {
        String query = "INSERT INTO employees (first_name, last_name, email, phone, department_id, " +
                "position, salary, hire_date, profile_picture, address, date_of_birth, gender, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getEmail());
            pstmt.setString(4, employee.getPhone());
            pstmt.setInt(5, employee.getDepartmentId());
            pstmt.setString(6, employee.getPosition());
            pstmt.setDouble(7, employee.getSalary());
            pstmt.setDate(8, Date.valueOf(employee.getHireDate()));
            pstmt.setString(9, employee.getProfilePicture());
            pstmt.setString(10, employee.getAddress());
            pstmt.setDate(11, Date.valueOf(employee.getDateOfBirth()));
            pstmt.setString(12, employee.getGender());
            pstmt.setString(13, employee.getStatus());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        employee.setEmployeeId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateEmployee(Employee employee) {
        String query = "UPDATE employees SET first_name = ?, last_name = ?, email = ?, phone = ?, " +
                "department_id = ?, position = ?, salary = ?, hire_date = ?, profile_picture = ?, " +
                "address = ?, date_of_birth = ?, gender = ?, status = ? WHERE employee_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getEmail());
            pstmt.setString(4, employee.getPhone());
            pstmt.setInt(5, employee.getDepartmentId());
            pstmt.setString(6, employee.getPosition());
            pstmt.setDouble(7, employee.getSalary());
            pstmt.setDate(8, Date.valueOf(employee.getHireDate()));
            pstmt.setString(9, employee.getProfilePicture());
            pstmt.setString(10, employee.getAddress());
            pstmt.setDate(11, Date.valueOf(employee.getDateOfBirth()));
            pstmt.setString(12, employee.getGender());
            pstmt.setString(13, employee.getStatus());
            pstmt.setInt(14, employee.getEmployeeId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteEmployee(int employeeId) {
        String query = "DELETE FROM employees WHERE employee_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, employeeId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Employee> searchEmployees(String searchTerm, String department, String position) {
        List<Employee> employees = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM employees WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            query.append(" AND (first_name LIKE ? OR last_name LIKE ? OR email LIKE ?)");
            String pattern = "%" + searchTerm + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        if (department != null && !department.trim().isEmpty()) {
            query.append(" AND department_id = (SELECT department_id FROM departments WHERE name = ?)");
            params.add(department);
        }

        if (position != null && !position.trim().isEmpty()) {
            query.append(" AND position LIKE ?");
            params.add("%" + position + "%");
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(extractEmployeeFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("employee_id"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setLastName(rs.getString("last_name"));
        employee.setEmail(rs.getString("email"));
        employee.setPhone(rs.getString("phone"));
        employee.setDepartmentId(rs.getInt("department_id"));
        employee.setPosition(rs.getString("position"));
        employee.setSalary(rs.getDouble("salary"));
        employee.setHireDate(rs.getDate("hire_date").toLocalDate());
        employee.setProfilePicture(rs.getString("profile_picture"));
        employee.setAddress(rs.getString("address"));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            employee.setDateOfBirth(dob.toLocalDate());
        }

        employee.setGender(rs.getString("gender"));
        employee.setStatus(rs.getString("status"));

        return employee;
    }
}