package com.employee.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageUtils {
    public static final String[] IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "bmp"};

    public static ImageIcon resizeImage(String imagePath, int width, int height) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static boolean isValidImageFile(String filePath) {
        if (filePath == null) {
            return false;
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