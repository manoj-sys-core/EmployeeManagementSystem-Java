package com.employee.gui;

import com.employee.dao.DepartmentDAO;
import com.employee.dao.EmployeeDAO;
import com.employee.model.Department;
import com.employee.model.Employee;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class DepartmentDialog extends JDialog {
    private final DepartmentDAO departmentDAO;
    private final EmployeeDAO employeeDAO;
    private boolean saved = false;
    private Department department;

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JComboBox<Employee> managerCombo;
    private JButton saveButton;
    private JButton cancelButton;

    public DepartmentDialog(JFrame parent, String title, Department department) {
        super(parent, title, true);
        this.department = department;

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {}

        departmentDAO = new DepartmentDAO();
        employeeDAO = new EmployeeDAO();

        initComponents();
        layoutComponents();
        setupListeners();

        if (department != null) {
            populateFields();
        }

        configureDialog();
    }

    private void initComponents() {
        Font font = new Font("Segoe UI", Font.PLAIN, 14);

        nameField = new JTextField(30);
        nameField.setFont(font);
        nameField.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setFont(font);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        managerCombo = new JComboBox<>();
        managerCombo.setFont(font);
        managerCombo.addItem(null);
        List<Employee> employees = employeeDAO.getAllEmployees();
        for (Employee e : employees) managerCombo.addItem(e);

        // Buttons
        saveButton = createPrimaryButton("💾 Save Department", new Color(33, 150, 243));
        cancelButton = createSecondaryButton("Cancel");
    }

    private JButton createPrimaryButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(color.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(color);
            }
        });
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(new Color(245, 245, 245));
        btn.setForeground(new Color(80, 80, 80));
        btn.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(235, 235, 235));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(245, 245, 245));
            }
        });
        return btn;
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 25, 25, 25));

        // Header section
        JLabel headerLabel = new JLabel(
                (department != null ? "✏️ Edit Department" : "🏢 Add New Department"),
                SwingConstants.LEFT
        );
        headerLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        headerLabel.setForeground(new Color(33, 37, 41));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        JSeparator accent = new JSeparator();
        accent.setForeground(new Color(33, 150, 243));
        headerPanel.add(accent, BorderLayout.SOUTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Department Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel managerLabel = new JLabel("Manager:");
        managerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(descLabel, gbc);
        gbc.gridx = 1;
        JScrollPane scroll = new JScrollPane(descriptionArea);
        scroll.setBorder(null);
        formPanel.add(scroll, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(managerLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(managerCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Rounded card wrapper
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.add(formPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(card, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void setupListeners() {
        saveButton.addActionListener((ActionEvent e) -> saveDepartment());
        cancelButton.addActionListener((ActionEvent e) -> dispose());
    }

    private void populateFields() {
        nameField.setText(department.getName());
        descriptionArea.setText(department.getDescription());

        if (department.getManagerId() > 0) {
            for (int i = 0; i < managerCombo.getItemCount(); i++) {
                Employee emp = managerCombo.getItemAt(i);
                if (emp != null && emp.getEmployeeId() == department.getManagerId()) {
                    managerCombo.setSelectedIndex(i);
                    break;
                }
            }
        } else managerCombo.setSelectedIndex(0);
    }

    private void saveDepartment() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a department name.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (department == null) department = new Department();

            department.setName(nameField.getText().trim());
            department.setDescription(descriptionArea.getText().trim());

            Employee manager = (Employee) managerCombo.getSelectedItem();
            department.setManagerId(manager != null ? manager.getEmployeeId() : 0);

            boolean success = (department.getDepartmentId() > 0)
                    ? departmentDAO.updateDepartment(department)
                    : departmentDAO.addDepartment(department);

            if (success) {
                saved = true;
                JOptionPane.showMessageDialog(this,
                        "Department saved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to save department.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "An error occurred: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configureDialog() {
        setSize(550, 400);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    public boolean isSaved() {
        return saved;
    }
}
