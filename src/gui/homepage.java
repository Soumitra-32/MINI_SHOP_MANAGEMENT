
package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class homepage extends JFrame {

    public homepage(String title) {
        setTitle(title);
        setSize(650, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ModernTheme.BG_LIGHT);
        setLayout(new BorderLayout());

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        // Center card panel
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(ModernTheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1, true),
                new EmptyBorder(35, 45, 35, 45)
        ));

        JLabel titleLabel = new JLabel("Mini Shop & Expense Portal");
        titleLabel.setFont(ModernTheme.FONT_TITLE);
        titleLabel.setForeground(ModernTheme.TEXT_MAIN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Please select an option to continue");
        subtitleLabel.setFont(ModernTheme.FONT_REGULAR);
        subtitleLabel.setForeground(ModernTheme.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton adminBtn = ModernTheme.createButton("Admin Login", ModernTheme.PRIMARY);
        JButton empBtn = ModernTheme.createButton("Employee Login", ModernTheme.SUCCESS);
        JButton regBtn = ModernTheme.createButton("Register New Account", new Color(100, 116, 139));
        JButton aboutBtn = ModernTheme.createButton("About Application", new Color(71, 85, 105));

        Dimension btnSize = new Dimension(260, 42);
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

