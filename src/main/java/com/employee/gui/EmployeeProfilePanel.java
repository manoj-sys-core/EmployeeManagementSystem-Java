// EmployeeProfilePanel.java
package com.employee.gui;

import com.employee.model.User;
import javax.swing.*;
import java.awt.*;

public class EmployeeProfilePanel extends JPanel {
    public EmployeeProfilePanel(User user) {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Employee Profile: " + user.getUsername(), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
}

