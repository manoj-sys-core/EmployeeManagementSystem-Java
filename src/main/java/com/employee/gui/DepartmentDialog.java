package com.employee.gui;

import com.employee.dao.DepartmentDAO;
import com.employee.dao.EmployeeDAO;
import com.employee.model.Department;
import com.employee.model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DepartmentDialog extends JDialog {
    private DepartmentDAO departmentDAO;
    private EmployeeDAO employeeDAO;
    private boolean saved = false;
    private Department department;

    // Form fields
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JComboBox<Employee> managerCombo;

    private JButton saveButton;
    private JButton cancelButton;

    public DepartmentDialog(JFrame parent, String title, Department department) {
        super(parent, title, true);
        this.department = department;
        departmentDAO = new DepartmentDAO();
        employeeDAO = new EmployeeDAO();

        initializeComponents();
        layoutComponents();
        setupListeners();

        if (department != null) {
            populateFields();
        }

        configureDialog();
    }

    private void initializeComponents() {
        // Form fields
        nameField = new JTextField(30);
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        managerCombo = new JComboBox<>();

        // Add "No Manager" option
        managerCombo.addItem(null);

        // Load employees
        List<Employee> employees = employeeDAO.getAllEmployees();
        for (Employee employee : employees) {
            managerCombo.addItem(employee);
        }

        // Buttons
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Department Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);

        // Manager
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JLabel("Manager:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(managerCombo, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDepartment();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void populateFields() {
        if (department == null) {
            return;
        }

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

    private void saveDepartment() {
        // Validate fields
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter department name", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Create or update department
            if (department == null) {
                department = new Department();
            }

            department.setName(nameField.getText().trim());
            department.setDescription(descriptionArea.getText().trim());

            Employee selectedManager = (Employee) managerCombo.getSelectedItem();
            if (selectedManager != null) {
                department.setManagerId(selectedManager.getEmployeeId());
            } else {
                department.setManagerId(0);
            }

            boolean success;
            if (department.getDepartmentId() > 0) {
                success = departmentDAO.updateDepartment(department);
            } else {
                success = departmentDAO.addDepartment(department);
            }

            if (success) {
                saved = true;
                JOptionPane.showMessageDialog(this, "Department saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save department", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configureDialog() {
        setSize(500, 300);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public boolean isSaved() {
        return saved;
    }
}