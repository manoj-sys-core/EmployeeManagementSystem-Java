package com.employee.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ImageUtils {
    public static final String[] IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "bmp"};

    // ✅ Handles both local and online image resizing
    public static ImageIcon resizeImage(String imagePath, int width, int height) {
        try {
            BufferedImage originalImage;

            if (imagePath.startsWith("http")) {
                // Load from URL
                originalImage = ImageIO.read(new URL(imagePath));
            } else {
                // Load from local file
                originalImage = ImageIO.read(new File(imagePath));
            }

            if (originalImage == null) return null;

            Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static boolean isValidImageFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        if (filePath.startsWith("http")) {
            // Assume valid if it's a URL
            return true;
        }

        String extension = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();
        for (String validExtension : IMAGE_EXTENSIONS) {
            if (validExtension.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
