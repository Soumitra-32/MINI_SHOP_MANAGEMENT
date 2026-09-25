package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ABOUT_WINDOW extends JFrame {

    public ABOUT_WINDOW() {
        ModernTheme.setupWindow(this, "About - Mini Shop & Expense Management System",
                ModernTheme.WIN_DIALOG_W, ModernTheme.WIN_DIALOG_H, JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(ModernTheme.createHeader("Mini Shop & Expense Management", "Version 2.0  |  Powered by PostgreSQL & Java Swing", ModernTheme.PRIMARY, ModernTheme.VIOLET), BorderLayout.NORTH);

        JTextArea description = new JTextArea();
        description.setText("""
                Welcome to Mini Shop & Expense Management System!

                This application helps retail stores track stock, sales,
                product categories, operating expenses, and monthly profits.

                KEY FEATURES:
                - Sales / Income and Expense transaction recording
                - Automatic stock deduction with low-stock alerts
                - Category and supplier (company) catalog management
                - Monthly budget control with restock verification
                - Dashboard charts + monthly financial reports

                IDEAL FOR:
                - Retail shops, mini-marts and small POS businesses
                - Managers monitoring staff sales and store revenue

                Support: +880-1234-567890   |   support@minishop.com
                """);
        description.setFont(ModernTheme.FONT_REGULAR);
        description.setForeground(ModernTheme.TEXT_MAIN);
        description.setBackground(ModernTheme.CARD_BG);
        description.setEditable(false);
        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        description.setBorder(new EmptyBorder(12, 16, 12, 16));

        JScrollPane scrollPane = new JScrollPane(description);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 16, 0, 16),
                BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1, true)
        ));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(ModernTheme.CARD_BG);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.BORDER_COLOR));

        JButton backButton = ModernTheme.createButton("Back to Homepage", ModernTheme.PRIMARY);
        backButton.addActionListener(e -> {
            new homepage("Expense & Shop Management Portal");
            dispose();
        });

        JButton closeButton = ModernTheme.createButton("Close", new Color(148, 163, 184));
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(backButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
}

