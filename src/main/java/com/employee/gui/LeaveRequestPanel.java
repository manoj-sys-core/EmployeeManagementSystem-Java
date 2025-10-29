package com.employee.gui;

import com.employee.model.User;
import javax.swing.*;
import java.awt.*;

public class LeaveRequestPanel extends JPanel {
    public LeaveRequestPanel(User user) {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Leave Requests", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
}