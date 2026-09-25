package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class admin extends JFrame {

    public admin(String name, String post) {
        ModernTheme.setupWindow(this, "Admin Dashboard - " + name, ModernTheme.WIN_MAIN_W, ModernTheme.WIN_MAIN_H, JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Gradient header banner
        add(ModernTheme.createHeader("Welcome, " + name + " (Admin)", "Mini Shop & Expense Management Control Center", ModernTheme.PRIMARY, ModernTheme.VIOLET), BorderLayout.NORTH);

        // Center Action Grid
        JPanel gridPanel = new JPanel(new GridLayout(4, 2, 16, 16));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        gridPanel.add(createActionTile("Dashboard Analytics", "View charts, incomes, and employee overview", ModernTheme.PRIMARY, () -> new DASHBOARD_WINDOW()));
        gridPanel.add(createActionTile("Add Transaction", "Record a new sale, product order or transaction", ModernTheme.SUCCESS, () -> new ADD_TRANSACTIONS_WINDOW(name, post)));
        gridPanel.add(createActionTile("View Transactions", "Inspect, filter, search, and delete transactions", ModernTheme.SKY, () -> new VIEW_TRANSACTIONS_WINDOW()));
        gridPanel.add(createActionTile("Manage Categories", "Add or delete product categories & companies", ModernTheme.VIOLET, () -> new MANAGE_CATEGORIES_WINDOW()));
        gridPanel.add(createActionTile("Inventory Stock", "Check stock levels and purchase more stock", ModernTheme.WARNING, () -> new INVENTORY_WINDOW()));
        gridPanel.add(createActionTile("Monthly Expenses", "Record business expenses against budget", ModernTheme.PINK, () -> new MANAGE_EXPENSE_WINDOW()));
        gridPanel.add(createActionTile("Financial Reports", "Generate monthly profit, loss & top selling list", ModernTheme.VIOLET, () -> new REPORTS_WINDOW()));
        gridPanel.add(createActionTile("System Settings", "Configure monthly budgets and selling prices", ModernTheme.SLATE_DARK, () -> new SETTINGS_WINDOW()));

        add(gridPanel, BorderLayout.CENTER);

        // Footer Toolbar
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        footer.setBackground(ModernTheme.CARD_BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.BORDER_COLOR));

        JButton logoutBtn = ModernTheme.createButton("Log Out", ModernTheme.SLATE);
        logoutBtn.addActionListener(e -> {
            new homepage("Expense & Shop Management Portal");
            dispose();
        });

        JButton exitBtn = ModernTheme.createButton("Exit App", ModernTheme.DANGER);
        exitBtn.addActionListener(e -> System.exit(0));

        footer.add(logoutBtn);
        footer.add(exitBtn);
        add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createActionTile(String title, String desc, Color accent, Runnable action) {
        JPanel tile = new JPanel(new BorderLayout(8, 8));
        tile.setBackground(ModernTheme.CARD_BG);
        tile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));
        tile.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(ModernTheme.FONT_SUBTITLE);
        titleLbl.setForeground(accent);

        JLabel descLbl = new JLabel("<html>" + desc + "</html>");
        descLbl.setFont(ModernTheme.FONT_REGULAR);
        descLbl.setForeground(ModernTheme.TEXT_MUTED);

        tile.add(titleLbl, BorderLayout.NORTH);
        tile.add(descLbl, BorderLayout.CENTER);

        tile.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                tile.setBackground(new Color(241, 245, 249));
                tile.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(accent, 1, true),
                        new EmptyBorder(12, 16, 12, 16)
                ));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                tile.setBackground(ModernTheme.CARD_BG);
                tile.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1, true),
                        new EmptyBorder(12, 16, 12, 16)
                ));
            }
        });
        return tile;
    }
}

