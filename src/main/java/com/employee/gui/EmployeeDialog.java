package com.employee.gui;

import com.employee.dao.DepartmentDAO;
import com.employee.dao.EmployeeDAO;
import com.employee.model.Department;
import com.employee.model.Employee;
import com.employee.utils.ImageUtils;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.time.format.DateTimeFormatter;

public class EmployeeDialog extends JDialog {
    private final EmployeeDAO employeeDAO;
    private final DepartmentDAO departmentDAO;
    private boolean saved = false;
    private Employee employee;

    private JTextField firstNameField, lastNameField, emailField, phoneField,
            positionField, salaryField, hireDateField, dobField, addressField;
    private JComboBox<Department> departmentCombo;
    private JComboBox<String> genderCombo, statusCombo;
    private JLabel profilePictureLabel;
    private JButton saveButton, cancelButton, browseButton;
    private String profilePicturePath;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public EmployeeDialog(JFrame parent, String title, Employee employee) {
        super(parent, title, true);
        this.employee = employee;
        try { UIManager.setLookAndFeel(new FlatLightLaf()); } catch (Exception ignored) {}

        employeeDAO = new EmployeeDAO();
        departmentDAO = new DepartmentDAO();

        initializeComponents();
        layoutComponents();
        setupListeners();

        if (employee != null) populateFields();
        configureDialog();
    }

    private void initializeComponents() {
        Font font = new Font("Segoe UI", Font.PLAIN, 15);

        firstNameField = createTextField(font);
        lastNameField = createTextField(font);
        emailField = createTextField(font);
        phoneField = createTextField(font);
        positionField = createTextField(font);
        salaryField = createTextField(font);
        hireDateField = createTextField(font);
        dobField = createTextField(font);

        // ✅ Address Field (replaced TextArea with wide TextField)
        addressField = new JTextField(60);
        addressField.setFont(font);
        addressField.setPreferredSize(new Dimension(800, 40));
        addressField.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        genderCombo = new JComboBox<>(new String[]{"MALE", "FEMALE", "OTHER"});
        genderCombo.setFont(font);

        statusCombo = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "TERMINATED"});
        statusCombo.setFont(font);

        departmentCombo = new JComboBox<>();
        departmentCombo.setFont(font);
        for (Department d : departmentDAO.getAllDepartments()) departmentCombo.addItem(d);

        profilePictureLabel = new JLabel("No Image", SwingConstants.CENTER);
        profilePictureLabel.setPreferredSize(new Dimension(240, 240));
        profilePictureLabel.setOpaque(true);
        profilePictureLabel.setBackground(new Color(255, 255, 255, 230));
        profilePictureLabel.setFont(font);
        profilePictureLabel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true));

        browseButton = createGlassButton("📁 Browse");
        saveButton = createGradientButton("💾 Save Employee", new Color(56, 189, 248), new Color(37, 99, 235));
        cancelButton = createGlassButton("Cancel");
    }

    private JTextField createTextField(Font font) {
        JTextField field = new JTextField(20);
        field.setFont(font);
        field.setPreferredSize(new Dimension(270, 40));
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        return field;
    }

    private JButton createGradientButton(String text, Color c1, Color c2) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
        };
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(220, 50));
        button.setBorder(new EmptyBorder(8, 18, 8, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createGlassButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setForeground(new Color(37, 99, 235));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 42));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new LineBorder(new Color(200, 210, 255), 1, true));
        return button;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel(employee == null ? "🧍 Add New Employee" : "✏️ Edit Employee");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 26));
        title.setForeground(new Color(37, 99, 235));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(20, 30, 20, 30)
        ));
        header.add(title, BorderLayout.WEST);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;

        addFormRow(formPanel, gbc, row++, "👤 First Name:", firstNameField, "👤 Last Name:", lastNameField);
        addFormRow(formPanel, gbc, row++, "📧 Email:", emailField, "📱 Phone:", phoneField);
        addFormRow(formPanel, gbc, row++, "🏢 Department:", departmentCombo, "💼 Position:", positionField);
        addFormRow(formPanel, gbc, row++, "💰 Salary:", salaryField, "📅 Hire Date:", hireDateField);
        addFormRow(formPanel, gbc, row++, "🎂 Date of Birth:", dobField, "⚧ Gender:", genderCombo);
        addFormRow(formPanel, gbc, row++, "📍 Status:", statusCombo, null, null);

        // ✅ Address now uses wide text field
        gbc.gridx = 0;
        gbc.gridy = row++;
        formPanel.add(new JLabel("🏠 Address:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        formPanel.add(addressField, gbc);

        JPanel profilePanel = new JPanel(new BorderLayout(10, 10));
        profilePanel.setOpaque(false);
        profilePanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel profileTitle = new JLabel("Profile Picture", SwingConstants.CENTER);
        profileTitle.setFont(new Font("Segoe UI Semibold", Font.BOLD, 17));
        profileTitle.setForeground(new Color(70, 70, 70));

        JPanel imageHolder = new JPanel(new GridBagLayout());
        imageHolder.setOpaque(false);
        imageHolder.add(profilePictureLabel);

        JPanel browsePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        browsePanel.setOpaque(false);
        browsePanel.add(browseButton);

        profilePanel.add(profileTitle, BorderLayout.NORTH);
        profilePanel.add(imageHolder, BorderLayout.CENTER);
        profilePanel.add(browsePanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        JPanel mainPanel = new JPanel(new BorderLayout(25, 25));
        mainPanel.setBackground(new Color(247, 249, 252));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(profilePanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row,
                            String label1, Component comp1, String label2, Component comp2) {
        gbc.gridy = row;
        gbc.gridx = 0;
        panel.add(new JLabel(label1), gbc);
        gbc.gridx = 1;
        panel.add(comp1, gbc);

        if (label2 != null && comp2 != null) {
            gbc.gridx = 2;
            panel.add(new JLabel(label2), gbc);
            gbc.gridx = 3;
            panel.add(comp2, gbc);
        }
    }

    private void setupListeners() {
        saveButton.addActionListener(e -> saveEmployee());
        cancelButton.addActionListener(e -> dispose());
        browseButton.addActionListener(e -> browseProfilePicture());
    }

    private void populateFields() {
        if (employee == null) return;
        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        emailField.setText(employee.getEmail());
        phoneField.setText(employee.getPhone());
        positionField.setText(employee.getPosition());
        salaryField.setText(String.valueOf(employee.getSalary()));
        if (employee.getHireDate() != null)
            hireDateField.setText(employee.getHireDate().format(DATE_FORMATTER));
        if (employee.getDateOfBirth() != null)
            dobField.setText(employee.getDateOfBirth().format(DATE_FORMATTER));
        addressField.setText(employee.getAddress());
        genderCombo.setSelectedItem(employee.getGender());
        statusCombo.setSelectedItem(employee.getStatus());

        for (int i = 0; i < departmentCombo.getItemCount(); i++) {
            Department d = departmentCombo.getItemAt(i);
            if (d.getDepartmentId() == employee.getDepartmentId()) {
                departmentCombo.setSelectedIndex(i);
                break;
            }
        }

        if (employee.getProfilePicture() != null && !employee.getProfilePicture().isEmpty()) {
            profilePicturePath = employee.getProfilePicture();
            ImageIcon icon = ImageUtils.resizeImage(profilePicturePath, 240, 240);
            if (icon != null) {
                profilePictureLabel.setIcon(icon);
                profilePictureLabel.setText("");
            }
        }
    }

    private void browseProfilePicture() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            profilePicturePath = file.getAbsolutePath();
            ImageIcon icon = ImageUtils.resizeImage(profilePicturePath, 240, 240);
            if (icon != null) {
                profilePictureLabel.setIcon(icon);
                profilePictureLabel.setText("");
            }
        }
    }

    private void configureDialog() {
        setSize(1250, 850);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void saveEmployee() {
        // implement save logic
    }

    public boolean isSaved() { return saved; }
}
