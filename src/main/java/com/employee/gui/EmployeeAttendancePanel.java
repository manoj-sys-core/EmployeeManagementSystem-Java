package com.employee.gui;

import com.employee.model.User;
import javax.swing.*;
import java.awt.*;

public class EmployeeAttendancePanel extends JPanel {
    public EmployeeAttendancePanel(User user) {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("My Attendance", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
}