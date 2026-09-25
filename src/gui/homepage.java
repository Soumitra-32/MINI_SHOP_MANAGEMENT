
package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class homepage extends JFrame {

    public homepage(String title) {
        ModernTheme.setupWindow(this, title, ModernTheme.WIN_HOME_W, ModernTheme.WIN_HOME_H, JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        // Gradient hero banner
        add(ModernTheme.createHeader("Mini Shop & Expense Portal", "Point of Sale - Inventory - Finance Analytics", ModernTheme.PRIMARY, ModernTheme.VIOLET), BorderLayout.NORTH);

        // Center card panel
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel card = ModernTheme.createCard(35);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 400));

        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(ModernTheme.FONT_TITLE);
        titleLabel.setForeground(ModernTheme.TEXT_MAIN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Please select an option to continue");
        subtitleLabel.setFont(ModernTheme.FONT_REGULAR);
        subtitleLabel.setForeground(ModernTheme.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton adminBtn = ModernTheme.createButton("Admin Login", ModernTheme.PRIMARY);
        JButton empBtn = ModernTheme.createButton("Employee Login", ModernTheme.SUCCESS);
        JButton regBtn = ModernTheme.createButton("Register New Account", ModernTheme.ACCENT_DARK);
        JButton aboutBtn = ModernTheme.createOutlineButton("About Application");

        Dimension btnSize = new Dimension(320, 44);
        for (JButton b : new JButton[]{adminBtn, empBtn, regBtn, aboutBtn}) {
            b.setMaximumSize(btnSize);
            b.setPreferredSize(btnSize);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        adminBtn.addActionListener(e -> {
            new adminloginpage();
            dispose();
        });

        empBtn.addActionListener(e -> {
            new employeeloginpage();
            dispose();
        });

        regBtn.addActionListener(e -> {
            new registerpage();
            dispose();
        });

        aboutBtn.addActionListener(e -> {
            new ABOUT_WINDOW();
            dispose();
        });

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(30));
        card.add(adminBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(empBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(regBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(aboutBtn);

        centerWrapper.add(card);
        add(centerWrapper, BorderLayout.CENTER);
    }
}

