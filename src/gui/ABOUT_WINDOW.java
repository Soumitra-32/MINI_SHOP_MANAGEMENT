package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ABOUT_WINDOW extends JFrame {

    public ABOUT_WINDOW() {
        setTitle("About - Mini Shop & Expense Management System");
        setSize(540, 460);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(ModernTheme.BG_LIGHT);
        setLayout(new BorderLayout(10, 10));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernTheme.CARD_BG);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Mini Shop & Expense Management", SwingConstants.CENTER);
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(ModernTheme.TEXT_MAIN);

        JLabel sub = new JLabel("Version 2.0 • Powered by PostgreSQL & Java Swing", SwingConstants.CENTER);
        sub.setFont(ModernTheme.FONT_REGULAR);
        sub.setForeground(ModernTheme.TEXT_MUTED);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JTextArea description = new JTextArea();
        description.setText("""
                Welcome to Mini Shop & Expense Management System!

                This application helps businesses efficiently track stock, sales transactions,
                categories, operating expenses, and monthly financial performance.

                ✦ Key Features:
                • Dual transaction management (Sales/Income & Expenditures)
                • Live stock decrementing and low-stock inventory dashboard alerts
                • Category and product/company supplier inventory management
                • Monthly budget tracking and financial reporting analytics
                • PostgreSQL persistence for local and cloud deployments

                👤 Ideal For:
                • Retail shops, mini-marts, and small business points of sale
                • Managers handling employee transactions and store revenue

                📞 Support: +880-1234-567890
                📧 Contact: support@minishop.com
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

