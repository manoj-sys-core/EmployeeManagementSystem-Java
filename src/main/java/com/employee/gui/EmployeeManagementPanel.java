package com.employee.gui;

import com.employee.dao.DepartmentDAO;
import com.employee.dao.EmployeeDAO;
import com.employee.model.Department;
import com.employee.model.Employee;
import com.employee.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.Vector;

public class EmployeeManagementPanel extends JPanel {
    private EmployeeDAO employeeDAO;
    private DepartmentDAO departmentDAO;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> departmentFilter;
    private JComboBox<String> positionFilter;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton searchButton;
    private JLabel profilePictureLabel;
    private byte[] profilePictureData;

    public EmployeeManagementPanel() {
        employeeDAO = new EmployeeDAO();
        departmentDAO = new DepartmentDAO();
        initializeComponents();
        layoutComponents();
        setupListeners();
        loadEmployees();
    }

    private void initializeComponents() {
        // Table
        String[] columns = {"ID", "First Name", "Last Name", "Email", "Phone", "Department", "Position", "Salary", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getTableHeader().setReorderingAllowed(false);

        // Search and filter components
        searchField = new JTextField(15);
        departmentFilter = new JComboBox<>();
        departmentFilter.addItem("All Departments");
        positionFilter = new JComboBox<>();
        positionFilter.addItem("All Positions");

        // Buttons
        addButton = new JButton("Add Employee");
        editButton = new JButton("Edit Employee");
        deleteButton = new JButton("Delete Employee");
        refreshButton = new JButton("Refresh");
        searchButton = new JButton("Search");

        // Profile picture
        profilePictureLabel = new JLabel();
        profilePictureLabel.setPreferredSize(new Dimension(150, 150));
        profilePictureLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        profilePictureLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profilePictureLabel.setText("No Image");

        // Load departments and positions
        loadDepartments();
        loadPositions();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Employee Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Department:"));
        searchPanel.add(departmentFilter);
        searchPanel.add(new JLabel("Position:"));
        searchPanel.add(positionFilter);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        JScrollPane tableScrollPane = new JScrollPane(employeeTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 400));

        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBorder(BorderFactory.createTitledBorder("Employee Profile"));
        profilePanel.add(profilePictureLabel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        centerPanel.add(profilePanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupListeners() {
        addButton.addActionListener(e -> addEmployee());
        editButton.addActionListener(e -> editEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        refreshButton.addActionListener(e -> loadEmployees());
        searchButton.addActionListener(e -> searchEmployees());

        employeeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    displayEmployeeProfile();
                }
            }
        });

        profilePictureLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    changeProfilePicture();
                }
            }
        });
    }

    private void loadEmployees() {
        tableModel.setRowCount(0);
        List<Employee> employees = employeeDAO.getAllEmployees();

        for (Employee employee : employees) {
            Vector<Object> row = new Vector<>();
            row.add(employee.getEmployeeId());
            row.add(employee.getFirstName());
            row.add(employee.getLastName());
            row.add(employee.getEmail());
            row.add(employee.getPhone());

            Department dept = departmentDAO.getDepartmentById(employee.getDepartmentId());
            row.add(dept != null ? dept.getName() : "N/A");

            row.add(employee.getPosition());
            row.add(employee.getSalary());
            row.add(employee.getStatus());

            tableModel.addRow(row);
        }
    }

    private void loadDepartments() {
        List<Department> departments = departmentDAO.getAllDepartments();
        for (Department dept : departments) {
            departmentFilter.addItem(dept.getName());
        }
    }

    private void loadPositions() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        employees.stream()
                .map(Employee::getPosition)
                .distinct()
                .forEach(positionFilter::addItem);
    }

    private void addEmployee() {
        EmployeeDialog dialog = new EmployeeDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add Employee", null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadEmployees();
            loadPositions();
            displayEmployeeProfile();
        }
    }

    private void editEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
        Employee employee = employeeDAO.getEmployeeById(employeeId);

        EmployeeDialog dialog = new EmployeeDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit Employee", employee);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadEmployees();
            loadPositions();
            displayEmployeeProfile();
        }
    }

    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
        String employeeName = tableModel.getValueAt(selectedRow, 1) + " " + tableModel.getValueAt(selectedRow, 2);

        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete " + employeeName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            if (employeeDAO.deleteEmployee(employeeId)) {
                JOptionPane.showMessageDialog(this, "Employee deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEmployees();
                loadPositions();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete employee", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchEmployees() {
        String searchTerm = searchField.getText().trim();
        String department = departmentFilter.getSelectedItem().toString();
        String position = positionFilter.getSelectedItem().toString();

        if ("All Departments".equals(department)) {
            department = "";
        }
        if ("All Positions".equals(position)) {
            position = "";
        }

        List<Employee> employees = employeeDAO.searchEmployees(searchTerm, department, position);
        tableModel.setRowCount(0);

        for (Employee employee : employees) {
            Vector<Object> row = new Vector<>();
            row.add(employee.getEmployeeId());
            row.add(employee.getFirstName());
            row.add(employee.getLastName());
            row.add(employee.getEmail());
            row.add(employee.getPhone());
            Department dept = departmentDAO.getDepartmentById(employee.getDepartmentId());
            row.add(dept != null ? dept.getName() : "N/A");
            row.add(employee.getPosition());
            row.add(employee.getSalary());
            row.add(employee.getStatus());
            tableModel.addRow(row);
        }
    }

    // ✅ Updated to support URL or local file image display
    private void displayEmployeeProfile() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
        Employee employee = employeeDAO.getEmployeeById(employeeId);

        if (employee != null && employee.getProfilePicture() != null && !employee.getProfilePicture().isEmpty()) {
            ImageIcon icon = ImageUtils.resizeImage(employee.getProfilePicture(), 150, 150);
            if (icon != null) {
                profilePictureLabel.setIcon(icon);
                profilePictureLabel.setText("");
            } else {
                profilePictureLabel.setIcon(null);
                profilePictureLabel.setText("No Image");
            }
        } else {
            profilePictureLabel.setIcon(null);
            profilePictureLabel.setText("No Image");
        }
    }

    // ✅ Updated for consistency with ImageUtils
    private void changeProfilePicture() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee first", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", ImageUtils.IMAGE_EXTENSIONS));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String imagePath = selectedFile.getAbsolutePath();

            int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
            Employee employee = employeeDAO.getEmployeeById(employeeId);

            if (employee != null) {
                employee.setProfilePicture(imagePath);
                if (employeeDAO.updateEmployee(employee)) {
                    ImageIcon icon = ImageUtils.resizeImage(imagePath, 150, 150);
                    profilePictureLabel.setIcon(icon);
                    profilePictureLabel.setText("");
                    JOptionPane.showMessageDialog(this, "Profile picture updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update profile picture", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
