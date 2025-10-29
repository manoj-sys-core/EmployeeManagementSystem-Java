package com.employee.gui;

import com.employee.dao.DepartmentDAO;
import com.employee.dao.EmployeeDAO;
import com.employee.model.Department;
import com.employee.model.Employee;
import com.employee.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EmployeeDialog extends JDialog {
    private EmployeeDAO employeeDAO;
    private DepartmentDAO departmentDAO;
    private boolean saved = false;
    private Employee employee;

    // Form fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<Department> departmentCombo;
    private JTextField positionField;
    private JTextField salaryField;
    private JTextField hireDateField;
    private JTextArea addressArea;
    private JTextField dobField;
    private JComboBox<String> genderCombo;
    private JComboBox<String> statusCombo;
    private JLabel profilePictureLabel;
    private String profilePicturePath;

    private JButton saveButton;
    private JButton cancelButton;
    private JButton browseButton;

    // Date formatter
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public EmployeeDialog(JFrame parent, String title, Employee employee) {
        super(parent, title, true);
        this.employee = employee;
        employeeDAO = new EmployeeDAO();
        departmentDAO = new DepartmentDAO();

        initializeComponents();
        layoutComponents();
        setupListeners();

        if (employee != null) {
            populateFields();
        }

        configureDialog();
    }

    private void initializeComponents() {
        // Form fields - all using plain JTextField
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        departmentCombo = new JComboBox<>();
        positionField = new JTextField(20);
        salaryField = new JTextField(20);
        hireDateField = new JTextField(10);
        dobField = new JTextField(10);
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        genderCombo = new JComboBox<>(new String[]{"MALE", "FEMALE", "OTHER"});
        statusCombo = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "TERMINATED"});

        // Profile picture
        profilePictureLabel = new JLabel();
        profilePictureLabel.setPreferredSize(new Dimension(150, 150));
        profilePictureLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        profilePictureLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profilePictureLabel.setText("No Image");

        // Buttons
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        browseButton = new JButton("Browse");

        // Load departments
        List<Department> departments = departmentDAO.getAllDepartments();
        for (Department dept : departments) {
            departmentCombo.addItem(dept);
        }
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

        // First row
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 3;
        formPanel.add(lastNameField, gbc);

        // Second row
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(emailField, gbc);

        // Third row
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 3;
        formPanel.add(departmentCombo, gbc);

        // Fourth row
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        formPanel.add(positionField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Salary:"), gbc);
        gbc.gridx = 3;
        formPanel.add(salaryField, gbc);

        // Fifth row
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Hire Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(hireDateField, gbc);
        formPanel.add(new JLabel("(YYYY-MM-DD)"), gbc);


        gbc.gridx = 2;
        formPanel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 3;
        formPanel.add(dobField, gbc);
        formPanel.add(new JLabel("(YYYY-MM-DD)"), gbc);



        // Sixth row
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        formPanel.add(genderCombo, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        formPanel.add(statusCombo, gbc);

        // Seventh row
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane addressScrollPane = new JScrollPane(addressArea);
        formPanel.add(addressScrollPane, gbc);

        // Profile picture panel
        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBorder(BorderFactory.createTitledBorder("Profile Picture"));
        profilePanel.add(profilePictureLabel, BorderLayout.CENTER);

        JPanel browsePanel = new JPanel(new FlowLayout());
        browsePanel.add(browseButton);
        profilePanel.add(browsePanel, BorderLayout.SOUTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Layout
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(formPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(profilePanel, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(leftPanel, BorderLayout.CENTER);
        contentPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveEmployee();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseProfilePicture();
            }
        });
    }

    private void populateFields() {
        if (employee == null) {
            return;
        }

        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        emailField.setText(employee.getEmail());
        phoneField.setText(employee.getPhone());

        // Select department
        for (int i = 0; i < departmentCombo.getItemCount(); i++) {
            Department dept = departmentCombo.getItemAt(i);
            if (dept.getDepartmentId() == employee.getDepartmentId()) {
                departmentCombo.setSelectedIndex(i);
                break;
            }
        }

        positionField.setText(employee.getPosition());
        salaryField.setText(String.valueOf(employee.getSalary()));

        // Set dates
        if (employee.getHireDate() != null) {
            hireDateField.setText(employee.getHireDate().format(DATE_FORMATTER));
        }

        if (employee.getDateOfBirth() != null) {
            dobField.setText(employee.getDateOfBirth().format(DATE_FORMATTER));
        }

        addressArea.setText(employee.getAddress());
        genderCombo.setSelectedItem(employee.getGender());
        statusCombo.setSelectedItem(employee.getStatus());

        if (employee.getProfilePicture() != null && !employee.getProfilePicture().isEmpty()) {
            profilePicturePath = employee.getProfilePicture();
            ImageIcon icon = new ImageIcon(profilePicturePath);
            Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            profilePictureLabel.setIcon(new ImageIcon(image));
            profilePictureLabel.setText("");
        }
    }

    private void saveEmployee() {
        // Validate fields
        if (firstNameField.getText().trim().isEmpty() ||
                lastNameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Create or update employee
            if (employee == null) {
                employee = new Employee();
            }

            employee.setFirstName(firstNameField.getText().trim());
            employee.setLastName(lastNameField.getText().trim());
            employee.setEmail(emailField.getText().trim());
            employee.setPhone(phoneField.getText().trim());

            Department selectedDept = (Department) departmentCombo.getSelectedItem();
            if (selectedDept != null) {
                employee.setDepartmentId(selectedDept.getDepartmentId());
            }

            employee.setPosition(positionField.getText().trim());

            try {
                employee.setSalary(Double.parseDouble(salaryField.getText().trim()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid salary format", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse dates from text fields
            String hireDateText = hireDateField.getText().trim();
            String dobText = dobField.getText().trim();

            if (!hireDateText.isEmpty()) {
                try {
                    employee.setHireDate(LocalDate.parse(hireDateText, DATE_FORMATTER));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Invalid hire date format. Use YYYY-MM-DD", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (!dobText.isEmpty()) {
                try {
                    employee.setDateOfBirth(LocalDate.parse(dobText, DATE_FORMATTER));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Invalid date of birth format. Use YYYY-MM-DD", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            employee.setAddress(addressArea.getText().trim());
            employee.setGender((String) genderCombo.getSelectedItem());
            employee.setStatus((String) statusCombo.getSelectedItem());
            employee.setProfilePicture(profilePicturePath);

            boolean success;
            if (employee.getEmployeeId() > 0) {
                success = employeeDAO.updateEmployee(employee);
            } else {
                success = employeeDAO.addEmployee(employee);
            }

            if (success) {
                saved = true;
                JOptionPane.showMessageDialog(this, "Employee saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save employee", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void browseProfilePicture() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", ImageUtils.IMAGE_EXTENSIONS));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            profilePicturePath = selectedFile.getAbsolutePath();

            ImageIcon icon = new ImageIcon(profilePicturePath);
            Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            profilePictureLabel.setIcon(new ImageIcon(image));
            profilePictureLabel.setText("");
        }
    }

    private void configureDialog() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public boolean isSaved() {
        return saved;
    }
}