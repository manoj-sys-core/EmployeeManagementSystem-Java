package com.employee.gui;

import com.employee.utils.DatabaseUtil;
import com.employee.utils.ThemeUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsPanel extends JPanel {
    private Properties properties;
    private JTextField dbUrlField;
    private JTextField dbUsernameField;
    private JPasswordField dbPasswordField;
    private JTextField emailHostField;
    private JTextField emailPortField;
    private JTextField emailUsernameField;
    private JPasswordField emailPasswordField;
    private JComboBox<String> themeCombo;
    private JTextField backupPathField;
    private JTextField logsPathField;
    private JButton saveButton;
    private JButton resetButton;
    private JButton testDbButton;
    private JButton testEmailButton;
    private JButton browseBackupButton;
    private JButton browseLogsButton;
    private JButton backupDbButton;
    private JButton restoreDbButton;

    public SettingsPanel() {
        properties = DatabaseUtil.getProperties();
        initializeComponents();
        layoutComponents();
        setupListeners();
        loadSettings();
    }

    private void initializeComponents() {
        // Database settings
        dbUrlField = new JTextField(30);
        dbUsernameField = new JTextField(15);
        dbPasswordField = new JPasswordField(15);
        testDbButton = new JButton("Test Connection");
        backupDbButton = new JButton("Backup Database");
        restoreDbButton = new JButton("Restore Database");

        // Email settings
        emailHostField = new JTextField(20);
        emailPortField = new JTextField(5);
        emailUsernameField = new JTextField(20);
        emailPasswordField = new JPasswordField(15);
        testEmailButton = new JButton("Test Email");

        // Application settings
        themeCombo = new JComboBox<>(new String[]{"Light", "Dark"});
        backupPathField = new JTextField(25);
        logsPathField = new JTextField(25);
        browseBackupButton = new JButton("Browse");
        browseLogsButton = new JButton("Browse");

        // Buttons
        saveButton = new JButton("Save Settings");
        resetButton = new JButton("Reset to Default");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Application Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create main panel with tabs
        JTabbedPane settingsTabs = new JTabbedPane();

        // Database settings panel
        JPanel dbPanel = createDatabaseSettingsPanel();
        settingsTabs.addTab("Database", dbPanel);

        // Email settings panel
        JPanel emailPanel = createEmailSettingsPanel();
        settingsTabs.addTab("Email", emailPanel);

        // Application settings panel
        JPanel appPanel = createApplicationSettingsPanel();
        settingsTabs.addTab("Application", appPanel);

        // Backup & Restore panel
        JPanel backupPanel = createBackupRestorePanel();
        settingsTabs.addTab("Backup & Restore", backupPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(resetButton);
        buttonPanel.add(saveButton);

        // Layout
        add(titleLabel, BorderLayout.NORTH);
        add(settingsTabs, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createDatabaseSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Database URL
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Database URL:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(dbUrlField, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(dbUsernameField, gbc);

        // Test connection button
        gbc.gridx = 2;
        panel.add(testDbButton, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(dbPasswordField, gbc);

        // Backup and Restore buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        panel.add(backupDbButton, gbc);
        gbc.gridx = 1;
        panel.add(restoreDbButton, gbc);

        return panel;
    }

    private JPanel createEmailSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // SMTP Host
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("SMTP Host:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(emailHostField, gbc);

        // Port
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;
        panel.add(emailPortField, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(emailUsernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(emailPasswordField, gbc);

        // Test email button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(testEmailButton, gbc);

        return panel;
    }

    private JPanel createApplicationSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Theme
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Theme:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(themeCombo, gbc);

        // Backup path
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Backup Path:"), gbc);
        gbc.gridx = 1;
        panel.add(backupPathField, gbc);
        gbc.gridx = 2;
        panel.add(browseBackupButton, gbc);

        // Logs path
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Logs Path:"), gbc);
        gbc.gridx = 1;
        panel.add(logsPathField, gbc);
        gbc.gridx = 2;
        panel.add(browseLogsButton, gbc);

        return panel;
    }

    private JPanel createBackupRestorePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Backup section
        JPanel backupPanel = new JPanel(new BorderLayout());
        backupPanel.setBorder(BorderFactory.createTitledBorder("Database Backup"));
        backupPanel.add(new JLabel("Create a backup of the entire database"), BorderLayout.NORTH);

        JButton backupButton = new JButton("Create Backup Now");
        backupButton.addActionListener(e -> backupDatabase());
        backupPanel.add(backupButton, BorderLayout.CENTER);

        // Restore section
        JPanel restorePanel = new JPanel(new BorderLayout());
        restorePanel.setBorder(BorderFactory.createTitledBorder("Database Restore"));
        restorePanel.add(new JLabel("Restore database from a backup file"), BorderLayout.NORTH);

        JButton restoreButton = new JButton("Restore from Backup");
        restoreButton.addActionListener(e -> restoreDatabase());
        restorePanel.add(restoreButton, BorderLayout.CENTER);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(backupPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(restorePanel, gbc);

        return panel;
    }

    private void setupListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetToDefault();
            }
        });

        testDbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testDatabaseConnection();
            }
        });

        testEmailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testEmailSettings();
            }
        });

        browseBackupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseDirectory(backupPathField);
            }
        });

        browseLogsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseDirectory(logsPathField);
            }
        });

        themeCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Preview theme change
                String selectedTheme = (String) themeCombo.getSelectedItem();
                if ("Dark".equals(selectedTheme)) {
                    ThemeUtil.setDarkTheme();
                } else {
                    ThemeUtil.setLightTheme();
                }
                SwingUtilities.updateComponentTreeUI(SettingsPanel.this);
            }
        });
    }

    private void loadSettings() {
        if (properties != null) {
            dbUrlField.setText(properties.getProperty("db.url", ""));
            dbUsernameField.setText(properties.getProperty("db.username", ""));
            dbPasswordField.setText(properties.getProperty("db.password", ""));

            emailHostField.setText(properties.getProperty("email.smtp.host", ""));
            emailPortField.setText(properties.getProperty("email.smtp.port", ""));
            emailUsernameField.setText(properties.getProperty("email.username", ""));
            emailPasswordField.setText(properties.getProperty("email.password", ""));

            String theme = properties.getProperty("app.theme", "light");
            themeCombo.setSelectedItem(theme.substring(0, 1).toUpperCase() + theme.substring(1));

            backupPathField.setText(properties.getProperty("app.backup.path", "./backups/"));
            logsPathField.setText(properties.getProperty("app.logs.path", "./logs/"));
        }
    }

    private void saveSettings() {
        try {
            // Update properties
            properties.setProperty("db.url", dbUrlField.getText());
            properties.setProperty("db.username", dbUsernameField.getText());
            properties.setProperty("db.password", new String(dbPasswordField.getPassword()));

            properties.setProperty("email.smtp.host", emailHostField.getText());
            properties.setProperty("email.smtp.port", emailPortField.getText());
            properties.setProperty("email.username", emailUsernameField.getText());
            properties.setProperty("email.password", new String(emailPasswordField.getPassword()));

            String selectedTheme = (String) themeCombo.getSelectedItem();
            properties.setProperty("app.theme", selectedTheme.toLowerCase());

            properties.setProperty("app.backup.path", backupPathField.getText());
            properties.setProperty("app.logs.path", logsPathField.getText());

            // Save to file
            String configPath = System.getProperty("user.dir") + "/src/main/resources/config.properties";
            try (FileOutputStream fos = new FileOutputStream(configPath)) {
                properties.store(fos, "Employee Management System Configuration");
            }

            JOptionPane.showMessageDialog(this, "Settings saved successfully!\nPlease restart the application for changes to take effect.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save settings: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetToDefault() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset all settings to default?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            // Set default values
            dbUrlField.setText("jdbc:mysql://localhost:3306/employee_management");
            dbUsernameField.setText("root");
            dbPasswordField.setText("");

            emailHostField.setText("smtp.gmail.com");
            emailPortField.setText("587");
            emailUsernameField.setText("");
            emailPasswordField.setText("");

            themeCombo.setSelectedItem("Light");
            backupPathField.setText("./backups/");
            logsPathField.setText("./logs/");

            JOptionPane.showMessageDialog(this, "Settings reset to default values", "Reset Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void testDatabaseConnection() {
        try {
            // Temporarily update connection properties
            String originalUrl = properties.getProperty("db.url");
            String originalUsername = properties.getProperty("db.username");
            String originalPassword = properties.getProperty("db.password");

            properties.setProperty("db.url", dbUrlField.getText());
            properties.setProperty("db.username", dbUsernameField.getText());
            properties.setProperty("db.password", new String(dbPasswordField.getPassword()));

            // Test connection
            java.sql.Connection conn = DatabaseUtil.getConnection();
            if (conn != null && !conn.isClosed()) {
                JOptionPane.showMessageDialog(this, "Database connection successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                conn.close();
            } else {
                JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Restore original properties
            properties.setProperty("db.url", originalUrl);
            properties.setProperty("db.username", originalUsername);
            properties.setProperty("db.password", originalPassword);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void testEmailSettings() {
        // This is a placeholder for email testing
        JOptionPane.showMessageDialog(this,
                "Email testing functionality would be implemented here.\n" +
                        "It would send a test email to verify the SMTP settings.",
                "Email Test",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void browseDirectory(JTextField field) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            field.setText(selectedFile.getAbsolutePath());
        }
    }

    private void backupDatabase() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SQL files", "sql"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".sql")) {
                filePath += ".sql";
            }

            // This is a placeholder for database backup
            JOptionPane.showMessageDialog(this,
                    "Database backup functionality would be implemented here.\n" +
                            "Backup file: " + filePath,
                    "Backup",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void restoreDatabase() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SQL files", "sql"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to restore the database?\nThis will overwrite all current data!",
                    "Confirm Restore",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                // This is a placeholder for database restore
                JOptionPane.showMessageDialog(this,
                        "Database restore functionality would be implemented here.\n" +
                                "Restore file: " + filePath,
                        "Restore",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}