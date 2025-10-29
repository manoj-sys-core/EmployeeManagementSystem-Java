package com.employee.gui;

import com.employee.dao.DepartmentDAO;
import com.employee.dao.EmployeeDAO;
import com.employee.model.Department;
import com.employee.model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

public class DepartmentManagementPanel extends JPanel {
    private DepartmentDAO departmentDAO;
    private EmployeeDAO employeeDAO;
    private JTable departmentTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JComboBox<Employee> managerCombo;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton refreshButton;
    private JButton viewEmployeesButton;
    private JLabel statsLabel;
    private JTextField searchField;

    public DepartmentManagementPanel() {
        departmentDAO = new DepartmentDAO();
        employeeDAO = new EmployeeDAO();
        initializeComponents();
        layoutComponents();
        setupListeners();
        loadDepartments();
    }

    private void initializeComponents() {
        // Table
        String[] columns = {"ID", "Name", "Description", "Manager", "Employee Count", "Created Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        departmentTable = new JTable(tableModel);
        departmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        departmentTable.getTableHeader().setReorderingAllowed(false);

        // Form components
        nameField = new JTextField(30);
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        managerCombo = new JComboBox<>();
        loadManagers();

        // Search
        searchField = new JTextField(20);

        // Buttons
        addButton = new JButton("Add Department");
        updateButton = new JButton("Update Department");
        deleteButton = new JButton("Delete Department");
        clearButton = new JButton("Clear Form");
        refreshButton = new JButton("Refresh");
        viewEmployeesButton = new JButton("View Employees");

        // Stats label
        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsLabel.setForeground(Color.BLUE);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Department Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(refreshButton);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Department Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Department Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JLabel("Manager:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(managerCombo, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(viewEmployeesButton);

        // Stats panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.add(statsLabel);

        // Table panel
        JScrollPane tableScrollPane = new JScrollPane(departmentTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 300));

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        centerPanel.add(statsPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(tableScrollPane, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDepartment();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDepartment();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteDepartment();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDepartments();
            }
        });

        viewEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewDepartmentEmployees();
            }
        });

        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchDepartments();
            }
        });

        departmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    loadSelectedDepartment();
                }
            }
        });
    }

    private void loadManagers() {
        managerCombo.removeAllItems();
        managerCombo.addItem(null); // No manager option

        List<Employee> employees = employeeDAO.getAllEmployees();
        for (Employee employee : employees) {
            managerCombo.addItem(employee);
        }
    }

    private void loadDepartments() {
        tableModel.setRowCount(0);
        List<Department> departments = departmentDAO.getAllDepartments();
        int totalEmployees = 0;

        for (Department department : departments) {
            addDepartmentToTable(department);
            totalEmployees += departmentDAO.getEmployeeCountByDepartment(department.getDepartmentId());
        }

        statsLabel.setText("Total Departments: " + departments.size() + " | Total Employees: " + totalEmployees);
    }

    private void addDepartmentToTable(Department department) {
        Vector<Object> row = new Vector<>();
        row.add(department.getDepartmentId());
        row.add(department.getName());
        row.add(department.getDescription());

        // Get manager name
        String managerName = "Not Assigned";
        if (department.getManagerId() > 0) {
            Employee manager = employeeDAO.getEmployeeById(department.getManagerId());
            if (manager != null) {
                managerName = manager.getFullName();
            }
        }
        row.add(managerName);

        // Get employee count
        int employeeCount = departmentDAO.getEmployeeCountByDepartment(department.getDepartmentId());
        row.add(employeeCount);

        row.add(department.getCreatedAt());

        tableModel.addRow(row);
    }

    private void loadSelectedDepartment() {
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow == -1) return;

        int departmentId = (int) tableModel.getValueAt(selectedRow, 0);
        Department department = departmentDAO.getDepartmentById(departmentId);

        if (department != null) {
            nameField.setText(department.getName());
            descriptionArea.setText(department.getDescription());

            // Select manager
            if (department.getManagerId() > 0) {
                for (int i = 0; i < managerCombo.getItemCount(); i++) {
                    Employee employee = managerCombo.getItemAt(i);
                    if (employee != null && employee.getEmployeeId() == department.getManagerId()) {
                        managerCombo.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                managerCombo.setSelectedIndex(0); // "No Manager"
            }
        }
    }

    private void addDepartment() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter department name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Department department = new Department();
        department.setName(nameField.getText().trim());
        department.setDescription(descriptionArea.getText().trim());

        Employee selectedManager = (Employee) managerCombo.getSelectedItem();
        if (selectedManager != null) {
            department.setManagerId(selectedManager.getEmployeeId());
        } else {
            department.setManagerId(0);
        }

        if (departmentDAO.addDepartment(department)) {
            JOptionPane.showMessageDialog(this, "Department added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadDepartments();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add department", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDepartment() {
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a department to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter department name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int departmentId = (int) tableModel.getValueAt(selectedRow, 0);
        Department department = departmentDAO.getDepartmentById(departmentId);

        if (department != null) {
            department.setName(nameField.getText().trim());
            department.setDescription(descriptionArea.getText().trim());

            Employee selectedManager = (Employee) managerCombo.getSelectedItem();
            if (selectedManager != null) {
                department.setManagerId(selectedManager.getEmployeeId());
            } else {
                department.setManagerId(0);
            }

            if (departmentDAO.updateDepartment(department)) {
                JOptionPane.showMessageDialog(this, "Department updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDepartments();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update department", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteDepartment() {
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a department to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int departmentId = (int) tableModel.getValueAt(selectedRow, 0);
        String departmentName = (String) tableModel.getValueAt(selectedRow, 1);
        int employeeCount = (int) tableModel.getValueAt(selectedRow, 4);

        if (employeeCount > 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cannot delete department '" + departmentName + "' because it has " + employeeCount + " employees.\n" +
                            "Please reassign or remove employees first.",
                    "Cannot Delete",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the department '" + departmentName + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            if (departmentDAO.deleteDepartment(departmentId)) {
                JOptionPane.showMessageDialog(this, "Department deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDepartments();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete department", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        descriptionArea.setText("");
        managerCombo.setSelectedIndex(0);
        departmentTable.clearSelection();
    }

    private void searchDepartments() {
        String searchText = searchField.getText().trim().toLowerCase();

        tableModel.setRowCount(0);
        List<Department> departments = departmentDAO.getAllDepartments();

        for (Department department : departments) {
            if (searchText.isEmpty() ||
                    department.getName().toLowerCase().contains(searchText) ||
                    department.getDescription().toLowerCase().contains(searchText)) {
                addDepartmentToTable(department);
            }
        }
    }

    private void viewDepartmentEmployees() {
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a department", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int departmentId = (int) tableModel.getValueAt(selectedRow, 0);
        String departmentName = (String) tableModel.getValueAt(selectedRow, 1);

        // Create a dialog to show employees
        JDialog employeeDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                "Employees in " + departmentName, true);
        employeeDialog.setSize(700, 500);
        employeeDialog.setLocationRelativeTo(this);

        // Create employee table
        String[] columns = {"ID", "Name", "Email", "Phone", "Position", "Salary", "Status"};
        DefaultTableModel employeeTableModel = new DefaultTableModel(columns, 0);
        JTable employeeTable = new JTable(employeeTableModel);

        // Load employees for this department
        List<Employee> employees = employeeDAO.getAllEmployees();
        for (Employee employee : employees) {
            if (employee.getDepartmentId() == departmentId) {
                Vector<Object> row = new Vector<>();
                row.add(employee.getEmployeeId());
                row.add(employee.getFullName());
                row.add(employee.getEmail());
                row.add(employee.getPhone());
                row.add(employee.getPosition());
                row.add(employee.getSalary());
                row.add(employee.getStatus());
                employeeTableModel.addRow(row);
            }
        }

        employeeDialog.add(new JScrollPane(employeeTable));
        employeeDialog.setVisible(true);
    }
}