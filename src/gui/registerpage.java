package gui;

import controller.database;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class registerpage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public registerpage() {
        setTitle("Create Account");
        setSize(440, 470);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        JLabel title = new JLabel("Create Account");
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(ModernTheme.TEXT_MAIN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Register a new employee profile");
        sub.setFont(ModernTheme.FONT_REGULAR);
        sub.setForeground(ModernTheme.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = ModernTheme.createTextField(20);
        passwordField = ModernTheme.createPasswordField();

        Dimension fieldSize = new Dimension(280, 36);
        usernameField.setMaximumSize(fieldSize);
        passwordField.setMaximumSize(fieldSize);

        JButton registerBtn = ModernTheme.createButton("Register", ModernTheme.PRIMARY);
        registerBtn.setMaximumSize(fieldSize);
        registerBtn.setPreferredSize(fieldSize);
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backBtn = ModernTheme.createButton("← Back to Home", new Color(148, 163, 184));
        backBtn.setMaximumSize(fieldSize);
        backBtn.setPreferredSize(fieldSize);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerBtn.addActionListener(e -> handleRegistration());
        backBtn.addActionListener(e -> {
            new homepage("Expense & Shop Management Portal");
            dispose();
        });

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(25));

        card.add(createFieldBlock("Desired Username:", usernameField));
        card.add(Box.createVerticalStrut(14));
        card.add(createFieldBlock("Password:", passwordField));
        card.add(Box.createVerticalStrut(24));

        card.add(registerBtn);
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

    private void handleRegistration() {
        String uname = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (uname.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all the fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection c = database.getConnection()) {
            if (c == null) {
                JOptionPane.showMessageDialog(this, "Database connection error.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "INSERT INTO employee (username, password) VALUES (?, ?)";
            try (PreparedStatement ps = c.prepareStatement(query)) {
                ps.setString(1, uname);
                ps.setString(2, pass);

                int row = ps.executeUpdate();
                if (row > 0) {
                    JOptionPane.showMessageDialog(this, "Successfully registered!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    new employeeloginpage();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            if (ex.getMessage() != null && (ex.getMessage().contains("duplicate key") || ex.getMessage().contains("Duplicate entry"))) {
                JOptionPane.showMessageDialog(this, "Username already exists! Please choose another.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

