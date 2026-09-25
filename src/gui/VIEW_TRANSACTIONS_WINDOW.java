package gui;

import controller.database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class VIEW_TRANSACTIONS_WINDOW extends JFrame {

    private JTable transactionTable;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> typeFilterCombo;

    public VIEW_TRANSACTIONS_WINDOW() {
        setTitle("All Transactions");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        initToolbar();
        initTable();
        loadTransactions();

        setVisible(true);
    }

    private void initToolbar() {
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolPanel.setBackground(new Color(225, 238, 250));

        toolPanel.add(new JLabel("Filter by Type:"));
        typeFilterCombo = new JComboBox<>(new String[]{"All", "Income", "Expense"});
        typeFilterCombo.addActionListener(e -> loadTransactions());
        toolPanel.add(typeFilterCombo);

        toolPanel.add(new JLabel("Search:"));
        searchField = new JTextField(12);
        toolPanel.add(searchField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(52, 152, 219));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(e -> loadTransactions());
        toolPanel.add(searchBtn);

        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            typeFilterCombo.setSelectedIndex(0);
            loadTransactions();
        });
        toolPanel.add(resetBtn);

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> deleteSelectedTransaction());
        toolPanel.add(deleteBtn);

        add(toolPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] columnNames = {"ID", "#", "Type", "Category", "Company", "Quantity", "Amount", "Currency", "Date", "Notes", "User", "Role"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(model);
        transactionTable.setRowHeight(25);
        transactionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        transactionTable.getColumnModel().getColumn(0).setMinWidth(0);
        transactionTable.getColumnModel().getColumn(0).setMaxWidth(0);
        transactionTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadTransactions() {
        model.setRowCount(0);
        String selectedType = (String) typeFilterCombo.getSelectedItem();
        String search = searchField.getText().trim();

        StringBuilder sql = new StringBuilder("SELECT id, type, category, company_name, quantity, amount, currency, date, notes, name, status FROM transactions WHERE 1=1");
        if (selectedType != null && !selectedType.equals("All")) {
            sql.append(" AND UPPER(type) = UPPER(?)");
        }
        if (!search.isEmpty()) {
            sql.append(" AND (LOWER(category) LIKE LOWER(?) OR LOWER(name) LIKE LOWER(?) OR LOWER(notes) LIKE LOWER(?) OR LOWER(company_name) LIKE LOWER(?))");
        }
        sql.append(" ORDER BY date DESC, id DESC");

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (selectedType != null && !selectedType.equals("All")) {
                ps.setString(paramIndex++, selectedType);
            }
            if (!search.isEmpty()) {
                String pattern = "%" + search + "%";
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                int count = 1;
                while (rs.next()) {
                    Object[] row = new Object[12];
                    row[0] = rs.getInt("id");
                    row[1] = count++;
                    row[2] = rs.getString("type");
                    row[3] = rs.getString("category");
                    row[4] = rs.getString("company_name");
                    row[5] = rs.getInt("quantity");
                    row[6] = rs.getDouble("amount");
                    row[7] = rs.getString("currency");
                    row[8] = rs.getDate("date");
                    row[9] = rs.getString("notes");
                    row[10] = rs.getString("name");
                    row[11] = rs.getString("status");
                    model.addRow(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage());
        }
    }

    private void deleteSelectedTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this transaction?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int transactionId = (int) model.getValueAt(selectedRow, 0);

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM transactions WHERE id = ?")) {
            ps.setInt(1, transactionId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Transaction deleted successfully.");
                loadTransactions();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete transaction.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting transaction: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VIEW_TRANSACTIONS_WINDOW::new);
    }

}

