package gui;

import controller.database;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class adminloginpage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField adminKeyField;

    public adminloginpage() {
        setTitle("Admin Portal Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 520);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ModernTheme.BG_LIGHT);
        setLayout(new BorderLayout());

        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(ModernTheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1, true),
                new EmptyBorder(30, 40, 30, 40)
        ));

        JLabel title = new JLabel("Admin Sign In");
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(ModernTheme.TEXT_MAIN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Enter your credentials to access system");
        sub.setFont(ModernTheme.FONT_REGULAR);
        sub.setForeground(ModernTheme.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = ModernTheme.createTextField(20);
        passwordField = ModernTheme.createPasswordField();
        adminKeyField = ModernTheme.createTextField(20);

        Dimension fieldSize = new Dimension(280, 36);
        usernameField.setMaximumSize(fieldSize);
        passwordField.setMaximumSize(fieldSize);
        adminKeyField.setMaximumSize(fieldSize);

        JButton loginBtn = ModernTheme.createButton("Log In", ModernTheme.PRIMARY);
        loginBtn.setMaximumSize(fieldSize);
        loginBtn.setPreferredSize(fieldSize);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backBtn = ModernTheme.createButton("← Back to Home", new Color(148, 163, 184));
        backBtn.setMaximumSize(fieldSize);
        backBtn.setPreferredSize(fieldSize);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> handleLogin());
        backBtn.addActionListener(e -> {
            new homepage("Expense & Shop Management Portal");
            dispose();
        });

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(25));

        card.add(createFieldBlock("Username:", usernameField));
        card.add(Box.createVerticalStrut(12));
        card.add(createFieldBlock("Password:", passwordField));
        card.add(Box.createVerticalStrut(12));
        card.add(createFieldBlock("Admin Key:", adminKeyField));
        card.add(Box.createVerticalStrut(22));

        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(backBtn);

        centerWrapper.add(card);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private JPanel createFieldBlock(String labelText, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(ModernTheme.FONT_BOLD);
        lbl.setForeground(ModernTheme.TEXT_MAIN);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        return p;
    }

    private void handleLogin() {
        String name = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        String akey = adminKeyField.getText().trim();

        if (name.isEmpty() || pass.isEmpty() || akey.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection c = database.getConnection()) {
            if (c != null) {
                String query = "SELECT * FROM admin WHERE name = ? AND password = ? AND admin_key = ?";
                try (PreparedStatement ps = c.prepareStatement(query)) {
                    ps.setString(1, name);
                    ps.setString(2, pass);
                    ps.setString(3, akey);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(this, "Welcome, " + name + "!");
                            new admin(name, "admin");
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid credentials or admin key!", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

