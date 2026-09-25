package gui;

import controller.database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Calendar;

public class MANAGE_EXPENSE_WINDOW extends JFrame {

    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    private JLabel totalBudgetLabel;
    private JTextField expenseNameField;
    private JTextField expenseAmountField;
    private JTable expensesTable;
    private DefaultTableModel tableModel;

    public MANAGE_EXPENSE_WINDOW() {
        ModernTheme.setupWindow(this, "Manage Expenses", ModernTheme.WIN_MAIN_W, ModernTheme.WIN_MAIN_H, JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));

        add(ModernTheme.createHeader("Manage Expenses", "Record operating costs and track monthly budget", ModernTheme.PINK, new Color(190, 24, 93)), BorderLayout.NORTH);

        JPanel body = new JPanel(null);
        body.setOpaque(false);
        body.setPreferredSize(new Dimension(ModernTheme.WIN_MAIN_W - 40, 440));
        add(new JScrollPane(body), BorderLayout.CENTER);

        initComponents(body);

        Calendar cal = Calendar.getInstance();
        monthCombo.setSelectedIndex(cal.get(Calendar.MONTH));
        yearCombo.setSelectedItem(cal.get(Calendar.YEAR));

        loadTotalBudget();
        loadExpenses();

        setVisible(true);
    }

    private void initComponents(JPanel body) {
        JLabel titleLabel = new JLabel("Monthly Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBounds(250, 10, 300, 30);
        body.add(titleLabel);

        monthCombo = new JComboBox<>(new String[]{
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        });
        monthCombo.setBounds(250, 50, 120, 25);
        body.add(monthCombo);

        yearCombo = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = currentYear - 2; y <= currentYear + 5; y++) {
            yearCombo.addItem(y);
        }
        yearCombo.setBounds(380, 50, 80, 25);
        body.add(yearCombo);

        totalBudgetLabel = new JLabel("Total Budget: 0.00 BDT");
        totalBudgetLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalBudgetLabel.setBounds(480, 50, 300, 25);
        body.add(totalBudgetLabel);

        JLabel expenseNameLabel = new JLabel("Expense Name:");
        expenseNameLabel.setBounds(30, 90, 120, 25);
        body.add(expenseNameLabel);

        expenseNameField = new JTextField();
        expenseNameField.setBounds(150, 90, 200, 25);
        body.add(expenseNameField);

        JLabel expenseAmountLabel = new JLabel("Amount (BDT):");
        expenseAmountLabel.setBounds(370, 90, 120, 25);
        body.add(expenseAmountLabel);

        expenseAmountField = new JTextField();
        expenseAmountField.setBounds(480, 90, 150, 25);
        body.add(expenseAmountField);

        JButton addExpenseButton = ModernTheme.createButton("Add Expense", ModernTheme.SUCCESS);
        addExpenseButton.setBounds(270, 130, 160, 34);
        body.add(addExpenseButton);

        tableModel = new DefaultTableModel(new String[]{"Expense Name", "Amount (BDT)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // disable editing
            }
        };
        expensesTable = new JTable(tableModel);
        ModernTheme.styleTable(expensesTable);
        JScrollPane scrollPane = new JScrollPane(expensesTable);
        scrollPane.setBounds(30, 180, 630, 250);
        body.add(scrollPane);

        // Listeners
        monthCombo.addActionListener(e -> {
            loadTotalBudget();
            loadExpenses();
        });

        yearCombo.addActionListener(e -> {
            loadTotalBudget();
            loadExpenses();
        });

        addExpenseButton.addActionListener(e -> addExpense());
    }

    private void loadTotalBudget() {
        int month = monthCombo.getSelectedIndex() + 1;
        int year = (Integer) yearCombo.getSelectedItem();
        String key = String.format("budget_%04d_%02d", year, month);

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT value FROM settings WHERE setting_key = ?")) {
            ps.setString(1, key);

            try (ResultSet rs = ps.executeQuery()) {
                double totalBudget = 0;
                if (rs.next()) {
                    totalBudget = Double.parseDouble(rs.getString("value"));
                }
                totalBudgetLabel.setText("Total Budget: " + String.format("%.2f BDT", totalBudget));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading total budget: " + e.getMessage());
        }
    }

    private void loadExpenses() {
        tableModel.setRowCount(0);
        int month = monthCombo.getSelectedIndex() + 1;
        int year = (Integer) yearCombo.getSelectedItem();

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT expense_name, amount FROM expenses WHERE month = ? AND year = ?")) {
            ps.setInt(1, month);
            ps.setInt(2, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getString("expense_name"),
                            rs.getDouble("amount")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage());
        }
    }

    private void addExpense() {
        String expenseName = expenseNameField.getText().trim();
        String amountText = expenseAmountField.getText().trim();

        if (expenseName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an expense name.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
            return;
        }

        int month = monthCombo.getSelectedIndex() + 1;
        int year = (Integer) yearCombo.getSelectedItem();

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO expenses (expense_name, amount, month, year) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, expenseName);
            ps.setDouble(2, amount);
            ps.setInt(3, month);
            ps.setInt(4, year);
            ps.executeUpdate();

            loadExpenses();

            expenseNameField.setText("");
            expenseAmountField.setText("");

            JOptionPane.showMessageDialog(this, "Expense added successfully!");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding expense: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MANAGE_EXPENSE_WINDOW::new);
    }
}
