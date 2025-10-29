package com.employee.gui;

import javax.swing.*;
import java.awt.*;

public class SalaryManagementPanel extends JPanel {
    public SalaryManagementPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Salary Management Module", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
}