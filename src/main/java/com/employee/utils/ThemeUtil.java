package com.employee.utils;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class ThemeUtil {
    private static boolean isLightTheme = true;

    public static void setLightTheme() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            isLightTheme = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void setDarkTheme() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            isLightTheme = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isLightTheme() {
        return isLightTheme;
    }
}