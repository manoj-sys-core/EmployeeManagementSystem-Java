package com.employee.dao;

import com.employee.model.Attendance;
import com.employee.utils.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public List<Attendance> getAllAttendance() {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM attendance ORDER BY date DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                attendanceList.add(extractAttendanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceList;
    }

    public List<Attendance> getAttendanceByEmployee(int employeeId) {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM attendance WHERE employee_id = ? ORDER BY date DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, employeeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    attendanceList.add(extractAttendanceFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceList;
    }

    public List<Attendance> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM attendance WHERE date BETWEEN ? AND ? ORDER BY date DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    attendanceList.add(extractAttendanceFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceList;
    }

    public boolean markAttendance(Attendance attendance) {
        String query = "INSERT INTO attendance (employee_id, date, check_in, check_out, status, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE check_in = VALUES(check_in), check_out = VALUES(check_out), " +
                "status = VALUES(status), notes = VALUES(notes)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, attendance.getEmployeeId());
            pstmt.setDate(2, Date.valueOf(attendance.getDate()));
            pstmt.setTime(3, attendance.getCheckIn() != null ? Time.valueOf(attendance.getCheckIn()) : null);
            pstmt.setTime(4, attendance.getCheckOut() != null ? Time.valueOf(attendance.getCheckOut()) : null);
            pstmt.setString(5, attendance.getStatus());
            pstmt.setString(6, attendance.getNotes());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateAttendance(Attendance attendance) {
        String query = "UPDATE attendance SET check_in = ?, check_out = ?, status = ?, notes = ? " +
                "WHERE attendance_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setTime(1, attendance.getCheckIn() != null ? Time.valueOf(attendance.getCheckIn()) : null);
            pstmt.setTime(2, attendance.getCheckOut() != null ? Time.valueOf(attendance.getCheckOut()) : null);
            pstmt.setString(3, attendance.getStatus());
            pstmt.setString(4, attendance.getNotes());
            pstmt.setInt(5, attendance.getAttendanceId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteAttendance(int attendanceId) {
        String query = "DELETE FROM attendance WHERE attendance_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, attendanceId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Attendance getAttendanceByEmployeeAndDate(int employeeId, LocalDate date) {
        String query = "SELECT * FROM attendance WHERE employee_id = ? AND date = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, employeeId);
            pstmt.setDate(2, Date.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAttendanceFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getAttendanceCountByStatus(String status, LocalDate startDate, LocalDate endDate) {
        String query = "SELECT COUNT(*) FROM attendance WHERE status = ? AND date BETWEEN ? AND ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double getAttendancePercentage(int employeeId, LocalDate startDate, LocalDate endDate) {
        String query = "SELECT " +
                "SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) as present, " +
                "COUNT(*) as total " +
                "FROM attendance " +
                "WHERE employee_id = ? AND date BETWEEN ? AND ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, employeeId);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int present = rs.getInt("present");
                    int total = rs.getInt("total");
                    return total > 0 ? (double) present / total * 100 : 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private Attendance extractAttendanceFromResultSet(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setAttendanceId(rs.getInt("attendance_id"));
        attendance.setEmployeeId(rs.getInt("employee_id"));
        attendance.setDate(rs.getDate("date").toLocalDate());

        Time checkInTime = rs.getTime("check_in");
        if (checkInTime != null) {
            attendance.setCheckIn(checkInTime.toLocalTime());
        }

        Time checkOutTime = rs.getTime("check_out");
        if (checkOutTime != null) {
            attendance.setCheckOut(checkOutTime.toLocalTime());
        }

        attendance.setStatus(rs.getString("status"));
        attendance.setNotes(rs.getString("notes"));

        return attendance;
    }
}