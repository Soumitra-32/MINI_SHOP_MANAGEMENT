package gui;

import controller.database;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class employeeloginpage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public employeeloginpage() {
        ModernTheme.setupWindow(this, "Employee Portal Login", ModernTheme.WIN_AUTH_W, ModernTheme.WIN_AUTH_H, JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
        setVisible(true);
    }

    private void initUI() {
        add(ModernTheme.createHeader("Employee Sign In", "Access daily sales workspace", ModernTheme.SUCCESS, new Color(4, 120, 87)), BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(ModernTheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1, true),
                new EmptyBorder(30, 40, 30, 40)
        ));

        usernameField = ModernTheme.createTextField(20);
        passwordField = ModernTheme.createPasswordField();

        Dimension fieldSize = new Dimension(280, 36);
        usernameField.setMaximumSize(fieldSize);
        passwordField.setMaximumSize(fieldSize);

        JButton loginBtn = ModernTheme.createButton("Log In", ModernTheme.SUCCESS);
        loginBtn.setMaximumSize(fieldSize);
        loginBtn.setPreferredSize(fieldSize);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backBtn = ModernTheme.createButton("Back to Home", new Color(148, 163, 184));
        backBtn.setMaximumSize(fieldSize);
        backBtn.setPreferredSize(fieldSize);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> handleLogin());
        backBtn.addActionListener(e -> {
            new homepage("Expense & Shop Management Portal");
            dispose();
        });

        card.add(createFieldBlock("Username:", usernameField));
        card.add(Box.createVerticalStrut(14));
        card.add(createFieldBlock("Password:", passwordField));
        card.add(Box.createVerticalStrut(24));

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

        if (name.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Validation Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection c = database.getConnection()) {
            if (c != null) {
                String query = "SELECT * FROM employee WHERE username = ? AND password = ?";
                try (PreparedStatement ps = c.prepareStatement(query)) {
                    ps.setString(1, name);
                    ps.setString(2, pass);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(this, "Welcome, " + name + "!");
                            new employee(name, "employee");
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid employee username or password!", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
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

