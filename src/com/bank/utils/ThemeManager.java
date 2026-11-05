package com.bank.utils;

import java.awt.Color;

public class ThemeManager {
    // Global state for the application theme
    public static boolean isDarkMode = false;

    /**
     * Primary background color for panels and frames
     */
    public static Color getBackground() {
        return isDarkMode ? new Color(30, 30, 30) : new Color(245, 247, 250);
    }

    /**
     * Primary text color for labels and headers
     * Automatically flips between pure white and a deep slate black
     */
    public static Color getForeground() {
        return isDarkMode ? Color.WHITE : new Color(44, 62, 80);
    }

    /**
     * Background color for cards/containers to create depth
     */
    public static Color getCardBackground() {
        return isDarkMode ? new Color(45, 45, 45) : Color.WHITE;
    }

    /**
     * Color for borders and separators
     */
    public static Color getBorderColor() {
        return isDarkMode ? new Color(60, 60, 60) : new Color(230, 230, 230);
    }

    /**
     * Muted color for subtext or descriptions
     */
    public static Color getMutedForeground() {
        return isDarkMode ? new Color(170, 170, 170) : new Color(127, 140, 141);
    }

    /**
     * NEW: Explicitly for the blinking cursor (caret) in input fields
     */
    public static Color getCaretColor() {
        return isDarkMode ? Color.WHITE : new Color(52, 152, 219); // White in dark, blue in light
    }

    /**
     * NEW: Color for text selection/highlighting
     */
    public static Color getSelectionColor() {
        return isDarkMode ? new Color(52, 152, 219, 100) : new Color(52, 152, 219, 50);
    }
}