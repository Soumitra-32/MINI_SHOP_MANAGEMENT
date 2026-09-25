package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class employee extends JFrame {

    public employee(String name, String post) {
        ModernTheme.setupWindow(this, "Employee Portal - " + name, ModernTheme.WIN_MAIN_W, ModernTheme.WIN_MAIN_H, JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        add(ModernTheme.createHeader("Welcome, " + name + " (Employee)", "Sales & Daily Transaction Management", ModernTheme.SUCCESS, new Color(4, 120, 87)), BorderLayout.NORTH);

        // Action Grid
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 16, 16));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        gridPanel.add(createActionTile("Add Transaction", "Enter sale item, select customer and quantity", ModernTheme.SUCCESS, () -> new ADD_TRANSACTIONS_WINDOW(name, post)));
        gridPanel.add(createActionTile("View Transactions", "Search and view transaction records", ModernTheme.PRIMARY, () -> new VIEW_TRANSACTIONS_WINDOW()));
        gridPanel.add(createActionTile("Reports & Summary", "View monthly sales and revenue summary", ModernTheme.VIOLET, () -> new REPORTS_WINDOW()));
        gridPanel.add(createActionTile("Inventory Stock", "Check available stock levels in shop", ModernTheme.WARNING, () -> new INVENTORY_WINDOW()));

        add(gridPanel, BorderLayout.CENTER);

        // Footer
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
                new EmptyBorder(14, 18, 14, 18)
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
                        new EmptyBorder(14, 18, 14, 18)
                ));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                tile.setBackground(ModernTheme.CARD_BG);
                tile.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1, true),
                        new EmptyBorder(14, 18, 14, 18)
                ));
            }
        });
        return tile;
    }
}

