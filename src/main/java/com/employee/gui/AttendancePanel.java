package com.employee.gui;

import com.employee.dao.AttendanceDAO;
import com.employee.dao.EmployeeDAO;
import com.employee.model.Attendance;
import com.employee.model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;

public class AttendancePanel extends JPanel {
    private AttendanceDAO attendanceDAO;
    private EmployeeDAO employeeDAO;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JComboBox<Employee> employeeCombo;
    private JTextField dateField;
    private JTextField checkInField;
    private JTextField checkOutField;
    private JComboBox<String> statusCombo;
    private JTextField notesField;
    private JButton markButton;
    private JButton checkInButton;
    private JButton checkOutButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton exportButton;
    private JLabel statusLabel;
    private JTextField searchField;
    private JComboBox<String> filterStatusCombo;

    public AttendancePanel() {
        attendanceDAO = new AttendanceDAO();
        employeeDAO = new EmployeeDAO();
        initializeComponents();
        layoutComponents();
        setupListeners();
        loadAttendance();
    }

    private void initializeComponents() {
        // Table
        String[] columns = {"ID", "Employee", "Date", "Check In", "Check Out", "Status", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        attendanceTable = new JTable(tableModel);
        attendanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        attendanceTable.getTableHeader().setReorderingAllowed(false);

        // Form components
        employeeCombo = new JComboBox<>();
        loadEmployees();

        dateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), 10);
        checkInField = new JTextField(8);
        checkOutField = new JTextField(8);
        statusCombo = new JComboBox<>(new String[]{"PRESENT", "ABSENT", "LATE", "HALF_DAY"});
        notesField = new JTextField(20);

        // Search and filter
        searchField = new JTextField(15);
        filterStatusCombo = new JComboBox<>(new String[]{"All", "PRESENT", "ABSENT", "LATE", "HALF_DAY"});

        // Buttons
        markButton = new JButton("Mark Attendance");
        checkInButton = new JButton("Check In");
        checkOutButton = new JButton("Check Out");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");
        exportButton = new JButton("Export to CSV");

        // Status label
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.BLUE);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Attendance Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Quick actions panel
        JPanel quickActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quickActionsPanel.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        quickActionsPanel.add(new JLabel("Employee:"));
        quickActionsPanel.add(employeeCombo);
        quickActionsPanel.add(checkInButton);
        quickActionsPanel.add(checkOutButton);
        quickActionsPanel.add(statusLabel);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(filterStatusCombo);
        searchPanel.add(refreshButton);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Attendance Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Employee:"), gbc);
        gbc.gridx = 1;
        formPanel.add(employeeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Check In:"), gbc);
        gbc.gridx = 1;
        formPanel.add(checkInField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Check Out:"), gbc);
        gbc.gridx = 1;
        formPanel.add(checkOutField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        formPanel.add(statusCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        formPanel.add(notesField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(markButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);

        // Table panel
        JScrollPane tableScrollPane = new JScrollPane(attendanceTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 300));

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(quickActionsPanel, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        centerPanel.add(tableScrollPane, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupListeners() {
        markButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markAttendance();
            }
        });

        checkInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quickCheckIn();
            }
        });

        checkOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quickCheckOut();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAttendance();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAttendance();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAttendance();
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToCSV();
            }
        });

        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterAttendance();
            }
        });

        filterStatusCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterAttendance();
            }
        });

        attendanceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedAttendance();
            }
        });
    }

    private void loadEmployees() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        employeeCombo.removeAllItems();
        for (Employee employee : employees) {
            employeeCombo.addItem(employee);
        }
    }

    private void loadAttendance() {
        tableModel.setRowCount(0);
        List<Attendance> attendanceList = attendanceDAO.getAllAttendance();

        for (Attendance attendance : attendanceList) {
            addAttendanceToTable(attendance);
        }
    }

    private void addAttendanceToTable(Attendance attendance) {
        Vector<Object> row = new Vector<>();
        row.add(attendance.getAttendanceId());

        Employee employee = employeeDAO.getEmployeeById(attendance.getEmployeeId());
        row.add(employee != null ? employee.getFullName() : "Unknown");

        row.add(attendance.getDate());
        row.add(attendance.getCheckIn());
        row.add(attendance.getCheckOut());
        row.add(attendance.getStatus());
        row.add(attendance.getNotes());

        tableModel.addRow(row);
    }

    private void loadSelectedAttendance() {
        int selectedRow = attendanceTable.getSelectedRow();
        if (selectedRow == -1) return;

        int attendanceId = (int) tableModel.getValueAt(selectedRow, 0);
        String employeeName = (String) tableModel.getValueAt(selectedRow, 1);
        LocalDate date = LocalDate.parse((String) tableModel.getValueAt(selectedRow, 2));

        Employee employee = employeeDAO.getEmployeeByName(employeeName);
        if (employee != null) {
            Attendance attendance = attendanceDAO.getAttendanceByEmployeeAndDate(employee.getEmployeeId(), date);

            if (attendance != null) {
                dateField.setText(attendance.getDate().toString());
                checkInField.setText(attendance.getCheckIn() != null ? attendance.getCheckIn().toString() : "");
                checkOutField.setText(attendance.getCheckOut() != null ? attendance.getCheckOut().toString() : "");
                statusCombo.setSelectedItem(attendance.getStatus());
                notesField.setText(attendance.getNotes());
            }
        }
    }

    private void markAttendance() {
        Employee selectedEmployee = (Employee) employeeCombo.getSelectedItem();
        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate date = LocalDate.parse(dateField.getText());
            LocalTime checkIn = checkInField.getText().isEmpty() ? null : LocalTime.parse(checkInField.getText());
            LocalTime checkOut = checkOutField.getText().isEmpty() ? null : LocalTime.parse(checkOutField.getText());
            String status = (String) statusCombo.getSelectedItem();
            String notes = notesField.getText().trim();

            Attendance attendance = new Attendance();
            attendance.setEmployeeId(selectedEmployee.getEmployeeId());
            attendance.setDate(date);
            attendance.setCheckIn(checkIn);
            attendance.setCheckOut(checkOut);
            attendance.setStatus(status);
            attendance.setNotes(notes);

            if (attendanceDAO.markAttendance(attendance)) {
                JOptionPane.showMessageDialog(this, "Attendance marked successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAttendance();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to mark attendance", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date or time format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quickCheckIn() {
        Employee selectedEmployee = (Employee) employeeCombo.getSelectedItem();
        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        Attendance attendance = attendanceDAO.getAttendanceByEmployeeAndDate(selectedEmployee.getEmployeeId(), today);

        if (attendance == null) {
            attendance = new Attendance();
            attendance.setEmployeeId(selectedEmployee.getEmployeeId());
            attendance.setDate(today);
            attendance.setCheckIn(now);
            attendance.setStatus("PRESENT");
        } else {
            attendance.setCheckIn(now);
            if (attendance.getStatus().equals("ABSENT")) {
                attendance.setStatus("PRESENT");
            }
        }

        if (attendanceDAO.markAttendance(attendance)) {
            statusLabel.setText("Checked in at " + now);
            statusLabel.setForeground(Color.GREEN);
            loadAttendance();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to check in", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quickCheckOut() {
        Employee selectedEmployee = (Employee) employeeCombo.getSelectedItem();
        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        Attendance attendance = attendanceDAO.getAttendanceByEmployeeAndDate(selectedEmployee.getEmployeeId(), today);

        if (attendance == null) {
            JOptionPane.showMessageDialog(this, "Please check in first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        attendance.setCheckOut(now);

        if (attendanceDAO.updateAttendance(attendance)) {
            statusLabel.setText("Checked out at " + now);
            statusLabel.setForeground(Color.ORANGE);
            loadAttendance();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to check out", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAttendance() {
        int selectedRow = attendanceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select attendance record to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int attendanceId = (int) tableModel.getValueAt(selectedRow, 0);
            LocalDate date = LocalDate.parse(dateField.getText());
            LocalTime checkIn = checkInField.getText().isEmpty() ? null : LocalTime.parse(checkInField.getText());
            LocalTime checkOut = checkOutField.getText().isEmpty() ? null : LocalTime.parse(checkOutField.getText());
            String status = (String) statusCombo.getSelectedItem();
            String notes = notesField.getText().trim();

            Attendance attendance = new Attendance();
            attendance.setAttendanceId(attendanceId);
            attendance.setDate(date);
            attendance.setCheckIn(checkIn);
            attendance.setCheckOut(checkOut);
            attendance.setStatus(status);
            attendance.setNotes(notes);

            if (attendanceDAO.updateAttendance(attendance)) {
                JOptionPane.showMessageDialog(this, "Attendance updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAttendance();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update attendance", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date or time format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAttendance() {
        int selectedRow = attendanceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select attendance record to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this attendance record?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            int attendanceId = (int) tableModel.getValueAt(selectedRow, 0);

            if (attendanceDAO.deleteAttendance(attendanceId)) {
                JOptionPane.showMessageDialog(this, "Attendance deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAttendance();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete attendance", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filterAttendance() {
        String searchText = searchField.getText().trim().toLowerCase();
        String statusFilter = (String) filterStatusCombo.getSelectedItem();

        tableModel.setRowCount(0);
        List<Attendance> attendanceList = attendanceDAO.getAllAttendance();

        for (Attendance attendance : attendanceList) {
            Employee employee = employeeDAO.getEmployeeById(attendance.getEmployeeId());
            String employeeName = employee != null ? employee.getFullName().toLowerCase() : "";

            boolean matchesSearch = searchText.isEmpty() || employeeName.contains(searchText);
            boolean matchesStatus = statusFilter.equals("All") || attendance.getStatus().equals(statusFilter);

            if (matchesSearch && matchesStatus) {
                addAttendanceToTable(attendance);
            }
        }
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".csv")) {
                filePath += ".csv";
            }

            try (java.io.PrintWriter writer = new java.io.PrintWriter(filePath)) {
                // Write headers
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.print(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();

                // Write data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        Object value = tableModel.getValueAt(i, j);
                        writer.print(value != null ? value.toString() : "");
                        if (j < tableModel.getColumnCount() - 1) {
                            writer.print(",");
                        }
                    }
                    writer.println();
                }

                JOptionPane.showMessageDialog(this, "Attendance exported successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to export attendance", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        checkInField.setText("");
        checkOutField.setText("");
        statusCombo.setSelectedIndex(0);
        notesField.setText("");
    }
}