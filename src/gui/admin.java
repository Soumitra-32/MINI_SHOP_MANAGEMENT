package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class admin {
    private final JFrame frame = new JFrame("Admin Dashboard");

    public admin(String name, String post) {
        frame.setSize(880, 620);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(ModernTheme.BG_LIGHT);
        frame.setLayout(new BorderLayout());

        // Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(20, 28, 20, 28));

        JLabel welcomeLabel = new JLabel("Welcome, " + name + " (Admin)");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Mini Shop & Expense Management Control Center");
        subLabel.setFont(ModernTheme.FONT_REGULAR);
        subLabel.setForeground(new Color(224, 238, 255));

        JPanel textWrap = new JPanel(new GridLayout(2, 1, 0, 4));
        textWrap.setOpaque(false);
        textWrap.add(welcomeLabel);
        textWrap.add(subLabel);
        headerPanel.add(textWrap, BorderLayout.WEST);

        frame.add(headerPanel, BorderLayout.NORTH);

        // Center Action Grid
        JPanel gridPanel = new JPanel(new GridLayout(4, 2, 16, 16));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        gridPanel.add(createActionTile("Dashboard Analytics", "View charts, incomes, and employee overview", ModernTheme.PRIMARY, () -> new DASHBOARD_WINDOW()));
        gridPanel.add(createActionTile("Add Transaction", "Record a new sale, product order or transaction", ModernTheme.SUCCESS, () -> new ADD_TRANSACTIONS_WINDOW(name, post)));
        gridPanel.add(createActionTile("View Transactions", "Inspect, filter, search, and delete transactions", new Color(14, 165, 233), () -> new VIEW_TRANSACTIONS_WINDOW()));
        gridPanel.add(createActionTile("Manage Categories", "Add or delete product categories & companies", new Color(99, 102, 241), () -> new MANAGE_CATEGORIES_WINDOW()));
        gridPanel.add(createActionTile("Inventory Stock", "Check stock levels and purchase more stock", new Color(245, 158, 11), () -> new INVENTORY_WINDOW()));
        gridPanel.add(createActionTile("Monthly Expenses", "Record business expenses against budget", new Color(236, 72, 153), () -> new MANAGE_EXPENSE_WINDOW()));
        gridPanel.add(createActionTile("Financial Reports", "Generate monthly profit, loss & top selling list", new Color(139, 92, 246), () -> new REPORTS_WINDOW()));
        gridPanel.add(createActionTile("System Settings", "Configure monthly budgets and selling prices", new Color(71, 85, 105), () -> new SETTINGS_WINDOW()));

        frame.add(gridPanel, BorderLayout.CENTER);

        // Footer Toolbar
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        footer.setBackground(ModernTheme.CARD_BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.BORDER_COLOR));

        JButton logoutBtn = ModernTheme.createButton("Log Out", new Color(100, 116, 139));
        logoutBtn.addActionListener(e -> {
            new homepage("Expense & Shop Management Portal");
            frame.dispose();
        });

        JButton exitBtn = ModernTheme.createButton("Exit App", ModernTheme.DANGER);
        exitBtn.addActionListener(e -> System.exit(0));

        footer.add(logoutBtn);
        footer.add(exitBtn);
        frame.add(footer, BorderLayout.SOUTH);

        frame.setVisible(true);
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

