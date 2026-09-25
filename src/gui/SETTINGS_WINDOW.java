package gui;

import controller.database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Calendar;

public class SETTINGS_WINDOW extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton saveButton;

    private JTextField totalBudgetField;
    private JButton saveBudgetButton;

    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;

    private double oldTotalBudget = 0;

    public SETTINGS_WINDOW() {
        setTitle("Settings - Selling Prices & Monthly Budget");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Month selector
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        monthCombo = new JComboBox<>(months);

        // Year selector: from currentYear - 5 to currentYear + 5
        yearCombo = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = currentYear - 5; y <= currentYear + 5; y++) {
            yearCombo.addItem(y);
        }

        // Set current month and year selected
        monthCombo.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        yearCombo.setSelectedItem(currentYear);

        topPanel.add(new JLabel("Month: "));
        topPanel.add(monthCombo);

        topPanel.add(new JLabel("Year: "));
        topPanel.add(yearCombo);

        topPanel.add(new JLabel("Total Monthly Budget (BDT): "));
        totalBudgetField = new JTextField(10);
        topPanel.add(totalBudgetField);

        saveBudgetButton = new JButton("Save Monthly Budget");
        topPanel.add(saveBudgetButton);

        topPanel.add(new JLabel("Currency: "));
        JTextField currencyField = new JTextField(5);
        loadCurrencySetting(currencyField);
        topPanel.add(currencyField);

        JButton saveCurrencyBtn = new JButton("Save Currency");
        saveCurrencyBtn.addActionListener(e -> saveCurrencySetting(currencyField.getText().trim()));
        topPanel.add(saveCurrencyBtn);

        add(topPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Category", "Company", "Quantity", "Buying Price (BDT)", "Selling Price (BDT)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0, 3 -> Integer.class;
                    case 4, 5 -> Double.class;
                    default -> String.class;
                };
            }
        };

        table = new JTable(tableModel);
        ModernTheme.styleTable(table);
        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(0)); // Hide ID

        add(new JScrollPane(table), BorderLayout.CENTER);

        saveButton = new JButton("Save Selling Prices");
        add(saveButton, BorderLayout.SOUTH);

        // Load budget for selected month/year and load company settings
        loadBudget();
        loadSettings();

        // Listeners
        saveButton.addActionListener(e -> saveSellingPrices());
        saveBudgetButton.addActionListener(e -> saveMonthlyBudget());

        monthCombo.addActionListener(e -> loadBudget());
        yearCombo.addActionListener(e -> loadBudget());

        setVisible(true);
    }

    private void loadSettings() {
        tableModel.setRowCount(0);
        String query = "SELECT id, category_name, company_name, quantity, price, selling_price FROM companies ORDER BY category_name, company_name";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String category = rs.getString("category_name");
                String company = rs.getString("company_name");
                int quantity = rs.getInt("quantity");
                double buyingPrice = rs.getDouble("price");
                double sellingPrice = rs.getDouble("selling_price");

                tableModel.addRow(new Object[]{id, category, company, quantity, buyingPrice, sellingPrice});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading settings: " + e.getMessage());
        }
    }

    private void loadBudget() {
        int year = (int) yearCombo.getSelectedItem();
        int month = monthCombo.getSelectedIndex() + 1; // January = 1

        String key = String.format("budget_%04d_%02d", year, month);

        try (Connection conn = database.getConnection()) {
            String totalQuery = "SELECT value FROM settings WHERE setting_key = ?";
            try (PreparedStatement stmt = conn.prepareStatement(totalQuery)) {
                stmt.setString(1, key);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        double total = Double.parseDouble(rs.getString("value"));
                        oldTotalBudget = total;
                        totalBudgetField.setText(String.valueOf(total));
                    } else {
                        totalBudgetField.setText("0");
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading budget: " + e.getMessage());
        }
    }

    private void saveSellingPrices() {
        try (Connection conn = database.getConnection()) {
            conn.setAutoCommit(false);

            String updateSQL = "UPDATE companies SET selling_price = ? WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSQL);

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int id = (int) tableModel.getValueAt(i, 0);
                Object priceObj = tableModel.getValueAt(i, 5);

                if (priceObj == null || priceObj.toString().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Selling price cannot be empty at row " + (i + 1));
                    conn.rollback();
                    return;
                }

                double price;
                try {
                    price = Double.parseDouble(priceObj.toString());
                    if (price < 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid selling price at row " + (i + 1));
                    conn.rollback();
                    return;
                }

                updateStmt.setDouble(1, price);
                updateStmt.setInt(2, id);
                updateStmt.addBatch();
            }

            updateStmt.executeBatch();
            conn.commit();

            JOptionPane.showMessageDialog(this, "✅ Selling prices saved successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving selling prices: " + e.getMessage());
        }
    }

    private void saveMonthlyBudget() {
        int year = (int) yearCombo.getSelectedItem();
        int month = monthCombo.getSelectedIndex() + 1;
        String key = String.format("budget_%04d_%02d", year, month);

        String val = totalBudgetField.getText().trim();
        double newBudget;

        try {
            newBudget = Double.parseDouble(val);
            if (newBudget < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Invalid monthly budget.");
            return;
        }

        try (Connection conn = database.getConnection()) {
            String updateTotal = "INSERT INTO settings (setting_key, value) VALUES (?, ?) " +
                    "ON CONFLICT (setting_key) DO UPDATE SET value = EXCLUDED.value";
            try (PreparedStatement stmt = conn.prepareStatement(updateTotal)) {
                stmt.setString(1, key);
                stmt.setString(2, String.valueOf(newBudget));
                stmt.executeUpdate();
            }

            oldTotalBudget = newBudget;
            JOptionPane.showMessageDialog(this, "✅ Monthly budget updated for " + monthCombo.getSelectedItem() + " " + year + ".");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving budget: " + e.getMessage());
        }
    }

    private void loadCurrencySetting(JTextField currencyField) {
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT value FROM settings WHERE setting_key = 'currency'")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                currencyField.setText(rs.getString("value"));
            } else {
                currencyField.setText("BDT");
            }
        } catch (Exception e) {
            currencyField.setText("BDT");
        }
    }

    private void saveCurrencySetting(String curr) {
        if (curr == null || curr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Currency cannot be empty.");
            return;
        }
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO settings (setting_key, value) VALUES ('currency', ?) " +
                             "ON CONFLICT (setting_key) DO UPDATE SET value = EXCLUDED.value")) {
            ps.setString(1, curr);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Currency updated to: " + curr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving currency: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SETTINGS_WINDOW::new);
    }
}
