package com.employee.utils;

import javax.swing.*;
import java.awt.*;

public class IconProvider {
    public static ImageIcon loadIcon(String fileName, int size) {
        try {
            ImageIcon icon = new ImageIcon(IconProvider.class.getResource("/icons/" + fileName));
            Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            System.err.println("Icon not found: " + fileName);
            return new ImageIcon();
        }
    }
}
