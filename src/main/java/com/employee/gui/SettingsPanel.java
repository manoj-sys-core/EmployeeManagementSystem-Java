package com.employee.gui;

import com.employee.utils.DatabaseUtil;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsPanel extends JPanel {

    private Properties properties;
    private JTextField dbUrlField, dbUsernameField, emailHostField, emailPortField, emailUsernameField, backupPathField, logsPathField;
    private JPasswordField dbPasswordField, emailPasswordField;
    private JButton saveButton, resetButton, testDbButton, testEmailButton, browseBackupButton, browseLogsButton, backupDbButton, restoreDbButton;

    public SettingsPanel() {
        FlatIntelliJLaf.setup();
        setBackground(new Color(245, 247, 250));
        setLayout(new BorderLayout());
        properties = DatabaseUtil.getProperties();

        JLabel titleLabel = new JLabel("Application Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.addTab("Database", createDatabaseSettingsPanel());
        tabbedPane.addTab("Email", createEmailSettingsPanel());
        tabbedPane.addTab("Application", createApplicationSettingsPanel());
        tabbedPane.addTab("Backup & Restore", createBackupRestorePanel());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));

        saveButton = createGradientButton("Save Settings", new Color(0x3498db), new Color(0x2ecc71));
        resetButton = createGradientButton("Reset to Default", new Color(0xe67e22), new Color(0xe74c3c));

        buttonPanel.add(resetButton);
        buttonPanel.add(saveButton);

        add(titleLabel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadSettings();
        setupListeners();
    }

    private JButton createGradientButton(String text, Color startColor, Color endColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 3);

                g2.dispose();
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        return button;
    }

    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 15),
                new Color(52, 73, 94)
        ));
        return panel;
    }

    private JPanel createDatabaseSettingsPanel() {
        JPanel panel = createCardPanel("Database Configuration");
        GridBagConstraints gbc = baseGbc();

        dbUrlField = new JTextField(30);
        dbUsernameField = new JTextField(15);
        dbPasswordField = new JPasswordField(15);
        testDbButton = createGradientButton("Test Connection", new Color(46, 204, 113), new Color(39, 174, 96));
        backupDbButton = createGradientButton("Backup DB", new Color(52, 152, 219), new Color(41, 128, 185));
        restoreDbButton = createGradientButton("Restore DB", new Color(231, 76, 60), new Color(192, 57, 43));

        addRow(panel, gbc, "Database URL:", dbUrlField, null);
        addRow(panel, gbc, "Username:", dbUsernameField, testDbButton);
        addRow(panel, gbc, "Password:", dbPasswordField, null);
        addRow(panel, gbc, "", backupDbButton, restoreDbButton);

        return wrapWithPadding(panel);
    }

    private JPanel createEmailSettingsPanel() {
        JPanel panel = createCardPanel("Email SMTP Configuration");
        GridBagConstraints gbc = baseGbc();

        emailHostField = new JTextField(20);
        emailPortField = new JTextField(5);
        emailUsernameField = new JTextField(20);
        emailPasswordField = new JPasswordField(15);
        testEmailButton = createGradientButton("Test Email", new Color(155, 89, 182), new Color(142, 68, 173));

        addRow(panel, gbc, "SMTP Host:", emailHostField, null);
        addRow(panel, gbc, "Port:", emailPortField, null);
        addRow(panel, gbc, "Username:", emailUsernameField, null);
        addRow(panel, gbc, "Password:", emailPasswordField, null);
        addRow(panel, gbc, "", testEmailButton, null);

        return wrapWithPadding(panel);
    }

    private JPanel createApplicationSettingsPanel() {
        JPanel panel = createCardPanel("Paths and Logs");
        GridBagConstraints gbc = baseGbc();

        backupPathField = new JTextField(25);
        logsPathField = new JTextField(25);
        browseBackupButton = createGradientButton("Browse", new Color(52, 152, 219), new Color(41, 128, 185));
        browseLogsButton = createGradientButton("Browse", new Color(52, 152, 219), new Color(41, 128, 185));

        addRow(panel, gbc, "Backup Path:", backupPathField, browseBackupButton);
        addRow(panel, gbc, "Logs Path:", logsPathField, browseLogsButton);

        return wrapWithPadding(panel);
    }

    private JPanel createBackupRestorePanel() {
        JPanel panel = createCardPanel("Backup and Restore Database");
        panel.setLayout(new GridLayout(2, 1, 15, 15));
        panel.setBackground(new Color(250, 250, 250));

        backupDbButton = createGradientButton("Create Backup Now", new Color(52, 152, 219), new Color(41, 128, 185));
        restoreDbButton = createGradientButton("Restore from Backup", new Color(231, 76, 60), new Color(192, 57, 43));

        panel.add(centerPanel(backupDbButton, "Create a full backup of the database"));
        panel.add(centerPanel(restoreDbButton, "Restore from an existing backup file"));

        return wrapWithPadding(panel);
    }

    private JPanel centerPanel(JButton button, String labelText) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setBorder(new EmptyBorder(10, 0, 5, 0));
        p.add(label, BorderLayout.NORTH);
        JPanel center = new JPanel();
        center.setBackground(Color.WHITE);
        center.add(button);
        p.add(center, BorderLayout.CENTER);
        p.setBorder(new CompoundBorder(new LineBorder(new Color(230, 230, 230)), new EmptyBorder(10, 10, 10, 10)));
        return p;
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String label, JComponent field, JComponent extra) {
        int row = panel.getComponentCount() / 2;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
        if (extra != null) {
            gbc.gridx = 2;
            gbc.weightx = 0;
            panel.add(extra, gbc);
        }
    }

    private JPanel wrapWithPadding(JPanel panel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(245, 247, 250));
        wrapper.add(panel, BorderLayout.CENTER);
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        return wrapper;
    }

    private void setupListeners() {
        saveButton.addActionListener(this::saveSettings);
        resetButton.addActionListener(e -> resetDefaults());
        testDbButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Database connection test placeholder"));
        testEmailButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Email test placeholder"));
        browseBackupButton.addActionListener(e -> browseDirectory(backupPathField));
        browseLogsButton.addActionListener(e -> browseDirectory(logsPathField));
    }

    private void browseDirectory(JTextField field) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            field.setText(fc.getSelectedFile().getAbsolutePath());
        }
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
            backupPathField.setText(properties.getProperty("app.backup.path", "./backups/"));
            logsPathField.setText(properties.getProperty("app.logs.path", "./logs/"));
        }
    }

    private void saveSettings(ActionEvent e) {
        try {
            properties.setProperty("db.url", dbUrlField.getText());
            properties.setProperty("db.username", dbUsernameField.getText());
            properties.setProperty("db.password", new String(dbPasswordField.getPassword()));
            properties.setProperty("email.smtp.host", emailHostField.getText());
            properties.setProperty("email.smtp.port", emailPortField.getText());
            properties.setProperty("email.username", emailUsernameField.getText());
            properties.setProperty("email.password", new String(emailPasswordField.getPassword()));
            properties.setProperty("app.backup.path", backupPathField.getText());
            properties.setProperty("app.logs.path", logsPathField.getText());

            String configPath = System.getProperty("user.dir") + "/src/main/resources/config.properties";
            try (FileOutputStream fos = new FileOutputStream(configPath)) {
                properties.store(fos, "Application Configuration");
            }
            JOptionPane.showMessageDialog(this, "Settings saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving settings", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetDefaults() {
        dbUrlField.setText("jdbc:mysql://localhost:3306/employee_management");
        dbUsernameField.setText("root");
        dbPasswordField.setText("");
        emailHostField.setText("smtp.gmail.com");
        emailPortField.setText("587");
        emailUsernameField.setText("");
        emailPasswordField.setText("");
        backupPathField.setText("./backups/");
        logsPathField.setText("./logs/");
        JOptionPane.showMessageDialog(this, "Settings reset to default values");
    }
}
