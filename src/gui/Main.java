package gui;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        ModernTheme.applySystemLookAndFeel();
        SwingUtilities.invokeLater(() -> new homepage("Expense & Shop Management Portal"));
    }
}

