package gui;

import controller.database;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

        ModernTheme.setupWindow(this, "Add Transaction", ModernTheme.WIN_MAIN_W, ModernTheme.WIN_MAIN_H, DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        loadCurrency();
        loadCategories();

        setVisible(true);
    }

    private void initComponents() {
        add(ModernTheme.createHeader("New Transaction", "Record a sale — stock deducts automatically", ModernTheme.SUCCESS, new Color(4, 120, 87)), BorderLayout.NORTH);

        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        centerWrap.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel form = ModernTheme.createCard(26);
        form.setLayout(new GridBagLayout());
        form.setPreferredSize(new Dimension(560, 430));

        typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
        categoryCombo = new JComboBox<>();
        categoryCombo.addActionListener(e -> {
            loadCompaniesForCategory();
            loadSellingPrice();
        });
        companyCombo = new JComboBox<>();
        companyCombo.addActionListener(e -> loadSellingPrice());
        quantityLabel = new JLabel("Quantity:");
        priceLabel = new JLabel("Price / unit:");
        quantityField = ModernTheme.createTextField(10);
        quantityField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                updateAmount();
            }
        });
        pricePerUnitField = ModernTheme.createTextField(10);
        pricePerUnitField.setEditable(false);
        amountField = ModernTheme.createTextField(10);
        amountField.setEditable(false);
        currencyLabel = new JLabel("BDT");
        currencyLabel.setFont(ModernTheme.FONT_BOLD);
        dateField = ModernTheme.createTextField(10);
        dateField.setText(LocalDate.now().toString());
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setFont(ModernTheme.FONT_REGULAR);
        JScrollPane notesScroll = new JScrollPane(notesArea);

        GridBagConstraints gc = new GridBagConstraints();
        // Kept tight: eight rows plus the button bar must fit in the 640px window,
        // otherwise the last row (Notes) is the one that gets squeezed.
        gc.insets = new Insets(5, 8, 5, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        addRow(form, gc, 0, "Type:", typeCombo);
        addRow(form, gc, 1, "Category:", categoryCombo);
        addRow(form, gc, 2, "Company:", companyCombo);
        addRow(form, gc, 3, "Quantity:", quantityField);
        addRow(form, gc, 4, "Price / unit:", pricePerUnitField);
        JPanel amountRow = new JPanel(new BorderLayout(8, 0));
        amountRow.setOpaque(false);
        amountRow.add(amountField, BorderLayout.CENTER);
        amountRow.add(currencyLabel, BorderLayout.EAST);
        addRow(form, gc, 5, "Amount:", amountRow);
        addRow(form, gc, 6, "Date (YYYY-MM-DD):", dateField);
        addRow(form, gc, 7, "Notes:", notesScroll);

        JButton addButton = ModernTheme.createButton("Save Transaction", ModernTheme.SUCCESS);
        addButton.addActionListener(e -> addTransaction());

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnWrap.setOpaque(false);
        btnWrap.add(addButton);

        JPanel page = new JPanel(new BorderLayout(0, 12));
        page.setOpaque(false);
        page.add(form, BorderLayout.CENTER);
        page.add(btnWrap, BorderLayout.SOUTH);

        centerWrap.add(page);
        add(centerWrap, BorderLayout.CENTER);
    }

    private void addRow(JPanel form, GridBagConstraints gc, int row, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setFont(ModernTheme.FONT_BOLD);
        l.setForeground(ModernTheme.TEXT_MAIN);
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0.35;
        form.add(l, gc);
        gc.gridx = 1; gc.weightx = 0.65;
        form.add(field, gc);
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
