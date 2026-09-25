package gui;

import controller.database;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class ADD_TRANSACTIONS_WINDOW extends JFrame {
    private JComboBox<String> categoryCombo, companyCombo, typeCombo;
    private JTextField amountField, quantityField, pricePerUnitField, dateField;
    private JTextArea notesArea;
    private JLabel currencyLabel, quantityLabel, priceLabel;
    private final String userName, userPost;
    private double sellingPricePerUnit = 0.0;  // selling price fetched from companies table

    public ADD_TRANSACTIONS_WINDOW(String name, String post) {
        this.userName = name;
        this.userPost = post;

        setTitle("Add Transaction");
        setSize(550, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(230, 245, 255));
        setLayout(null);

        initComponents();
        loadCurrency();
        loadCategories();

        setVisible(true);
    }

    private void initComponents() {
        JLabel title = new JLabel("Add Transaction");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBounds(180, 15, 250, 30);
        add(title);

        // Transaction Type (Income / Expense)
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setBounds(50, 55, 100, 25);
        add(typeLabel);

        typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
        typeCombo.setBounds(150, 55, 200, 25);
        add(typeCombo);

        // Category label & combo
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setBounds(50, 95, 100, 25);
        add(categoryLabel);

        categoryCombo = new JComboBox<>();
        categoryCombo.setBounds(150, 95, 200, 25);
        categoryCombo.addActionListener(e -> {
            loadCompaniesForCategory();
            loadSellingPrice();
        });
        add(categoryCombo);

        // Company label & combo
        JLabel companyLabel = new JLabel("Company:");
        companyLabel.setBounds(50, 135, 100, 25);
        add(companyLabel);

        companyCombo = new JComboBox<>();
        companyCombo.setBounds(150, 135, 200, 25);
        companyCombo.addActionListener(e -> loadSellingPrice());
        add(companyCombo);

        // Quantity
        quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(50, 175, 100, 25);
        add(quantityLabel);

        quantityField = new JTextField();
        quantityField.setBounds(150, 175, 200, 25);
        quantityField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                updateAmount();
            }
        });
        add(quantityField);

        // Price/unit (disabled, fetched from companies)
        priceLabel = new JLabel("Price/unit:");
        priceLabel.setBounds(50, 215, 100, 25);
        add(priceLabel);

        pricePerUnitField = new JTextField();
        pricePerUnitField.setBounds(150, 215, 200, 25);
        pricePerUnitField.setEditable(false); // user cannot edit
        add(pricePerUnitField);

        // Amount (calculated)
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(50, 255, 100, 25);
        add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(150, 255, 200, 25);
        amountField.setEditable(false);
        add(amountField);

        currencyLabel = new JLabel("BDT");
        currencyLabel.setBounds(360, 255, 100, 25);
        add(currencyLabel);

        // Date
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setBounds(50, 295, 150, 25);
        add(dateLabel);

        dateField = new JTextField(LocalDate.now().toString());
        dateField.setBounds(200, 295, 150, 25);
        add(dateField);

        // Notes
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setBounds(50, 335, 100, 25);
        add(notesLabel);

        notesArea = new JTextArea();
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(notesArea);
        scrollPane.setBounds(150, 335, 300, 75);
        add(scrollPane);

        // Add button
        JButton addButton = new JButton("Add Transaction");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addButton.setBackground(new Color(52, 152, 219));
        addButton.setForeground(Color.WHITE);
        addButton.setBounds(180, 430, 200, 40);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addTransaction());
        add(addButton);
    }

    private void loadCurrency() {
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT value FROM settings WHERE setting_key = 'currency' LIMIT 1");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                currencyLabel.setText(rs.getString("value"));
            } else {
                currencyLabel.setText("BDT");
            }
        } catch (Exception e) {
            currencyLabel.setText("BDT");
        }
    }

    private void loadCategories() {
        categoryCombo.removeAllItems();
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT name FROM categories ORDER BY name ASC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categoryCombo.addItem(rs.getString("name"));
            }
        } catch (Exception e) {
            categoryCombo.addItem("-- Error Loading Categories --");
        }
    }

    private void loadCompaniesForCategory() {
        companyCombo.removeAllItems();
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        if (selectedCategory == null) return;

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT DISTINCT company_name FROM companies WHERE category_name = ? ORDER BY company_name ASC")) {
            ps.setString(1, selectedCategory);
            try (ResultSet rs = ps.executeQuery()) {
                boolean hasCompany = false;
                while (rs.next()) {
                    companyCombo.addItem(rs.getString("company_name"));
                    hasCompany = true;
                }
                if (!hasCompany) {
                    companyCombo.addItem("-- No Companies Found --");
                }
            }
        } catch (Exception e) {
            companyCombo.addItem("-- Error Loading Companies --");
        }
    }

    private void loadSellingPrice() {
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        String selectedCompany = (String) companyCombo.getSelectedItem();
        if (selectedCategory == null || selectedCompany == null
                || selectedCompany.startsWith("--")) {
            sellingPricePerUnit = 0.0;
            pricePerUnitField.setText("");
            updateAmount();
            return;
        }

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT selling_price FROM companies WHERE category_name = ? AND company_name = ? LIMIT 1")) {
            ps.setString(1, selectedCategory);
            ps.setString(2, selectedCompany);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sellingPricePerUnit = rs.getDouble("selling_price");
                    pricePerUnitField.setText(String.format("%.2f", sellingPricePerUnit));
                } else {
                    sellingPricePerUnit = 0.0;
                    pricePerUnitField.setText("");
                }
                updateAmount();
            }
        } catch (Exception e) {
            sellingPricePerUnit = 0.0;
            pricePerUnitField.setText("");
            updateAmount();
        }
    }

    private void updateAmount() {
        try {
            int qty = Integer.parseInt(quantityField.getText().trim());
            double price = sellingPricePerUnit;
            amountField.setText(String.format("%.2f", qty * price));
        } catch (Exception e) {
            amountField.setText("");
        }
    }

    private void addTransaction() {
        String type = (String) typeCombo.getSelectedItem();
        if (type == null) type = "Income";
        String category = (String) categoryCombo.getSelectedItem();
        String company = (String) companyCombo.getSelectedItem();
        String amountText = amountField.getText().trim();
        String dateText = dateField.getText().trim();
        String notes = notesArea.getText().trim();
        String currency = currencyLabel.getText().trim();

        if (category == null || company == null || amountText.isEmpty() || dateText.isEmpty() || currency.isEmpty()
                || company.startsWith("--")) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.");
            return;
        }

        double amount;
        Date sqlDate;
        int quantity = 0;

        try {
            amount = Double.parseDouble(amountText);
            sqlDate = Date.valueOf(dateText);
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0 || sellingPricePerUnit <= 0) throw new NumberFormatException();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity, amount or date.");
            return;
        }

        // Check stock and update companies & categories tables
        try (Connection conn = database.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement psCheck = conn.prepareStatement(
                    "SELECT quantity FROM companies WHERE category_name = ? AND company_name = ?");
            psCheck.setString(1, category);
            psCheck.setString(2, company);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                int stock = rs.getInt("quantity");
                if (stock < quantity) {
                    JOptionPane.showMessageDialog(this, "Not enough stock in inventory. Available: " + stock);
                    conn.rollback();
                    return;
                }
                PreparedStatement psUpdate = conn.prepareStatement(
                        "UPDATE companies SET quantity = quantity - ? WHERE category_name = ? AND company_name = ?");
                psUpdate.setInt(1, quantity);
                psUpdate.setString(2, category);
                psUpdate.setString(3, company);
                psUpdate.executeUpdate();

                // Keep categories.quantity updated
                PreparedStatement psCatUpdate = conn.prepareStatement(
                        "UPDATE categories SET quantity = (SELECT COALESCE(SUM(quantity), 0) FROM companies WHERE category_name = ?) WHERE name = ?");
                psCatUpdate.setString(1, category);
                psCatUpdate.setString(2, category);
                psCatUpdate.executeUpdate();
            } else {
                JOptionPane.showMessageDialog(this, "Item not found in inventory.");
                conn.rollback();
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO transactions (type, category, company_name, amount, currency, date, notes, name, status, quantity) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, type);
            ps.setString(2, category);
            ps.setString(3, company);
            ps.setDouble(4, amount);
            ps.setString(5, currency);
            ps.setDate(6, sqlDate);
            ps.setString(7, notes);
            ps.setString(8, userName);
            ps.setString(9, userPost);
            ps.setInt(10, quantity);

            if (ps.executeUpdate() > 0) {
                conn.commit();
                JOptionPane.showMessageDialog(this, "Transaction added successfully.");
                amountField.setText("");
                notesArea.setText("");
                quantityField.setText("");
                pricePerUnitField.setText(String.format("%.2f", sellingPricePerUnit));
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Failed to add transaction.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding transaction: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ADD_TRANSACTIONS_WINDOW("Admin", "Manager"));
    }
}
