package gui;

import controller.database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class REPORTS_WINDOW extends JFrame {

    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    private JLabel totalIncomeLabel, totalExpenseLabel, netProfitLabel, transactionsCountLabel;
    private JTable topProductsTable;
    private DefaultTableModel topProductsModel;

    public REPORTS_WINDOW() {
        setTitle("Reports & Financial Analytics");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(235, 245, 255));

        initToolbar();
        initSummaryCards();
        initDetailTables();

        generateReport();
        setVisible(true);
    }

    private void initToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        toolbar.setBackground(new Color(210, 230, 250));

        toolbar.add(new JLabel("Report Month:"));
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        toolbar.add(monthCombo);

        toolbar.add(new JLabel("Year:"));
        yearCombo = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 3; y <= currentYear + 3; y++) {
            yearCombo.addItem(y);
        }
        yearCombo.setSelectedItem(currentYear);
        toolbar.add(yearCombo);

        JButton generateBtn = new JButton("Generate Report");
        generateBtn.setBackground(new Color(52, 152, 219));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        generateBtn.addActionListener(e -> generateReport());
        toolbar.add(generateBtn);

        monthCombo.addActionListener(e -> generateReport());
        yearCombo.addActionListener(e -> generateReport());

        add(toolbar, BorderLayout.NORTH);
    }

    private void initSummaryCards() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        summaryPanel.setOpaque(false);

        totalIncomeLabel = createCard("Total Income", "0.00 BDT", new Color(46, 204, 113), summaryPanel);
        totalExpenseLabel = createCard("Total Expense", "0.00 BDT", new Color(231, 76, 60), summaryPanel);
        netProfitLabel = createCard("Net Profit", "0.00 BDT", new Color(52, 152, 219), summaryPanel);
        transactionsCountLabel = createCard("Transactions", "0", new Color(155, 89, 182), summaryPanel);

        add(summaryPanel, BorderLayout.WEST);
    }

    private JLabel createCard(String title, String defaultValue, Color color, JPanel parent) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(170, 75));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(Color.DARK_GRAY);

        JLabel valLbl = new JLabel(defaultValue, SwingConstants.CENTER);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valLbl.setForeground(color);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valLbl, BorderLayout.CENTER);
        parent.add(card);

        return valLbl;
    }

    private void initDetailTables() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        centerPanel.setOpaque(false);

        JLabel tableTitle = new JLabel("Top Products / Revenue Breakdown for Selected Period");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        centerPanel.add(tableTitle, BorderLayout.NORTH);

        topProductsModel = new DefaultTableModel(new String[]{"Rank", "Category", "Company", "Total Qty Sold", "Revenue (BDT)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        topProductsTable = new JTable(topProductsModel);
        ModernTheme.styleTable(topProductsTable);

        centerPanel.add(new JScrollPane(topProductsTable), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void generateReport() {
        int month = monthCombo.getSelectedIndex() + 1;
        int year = (Integer) yearCombo.getSelectedItem();
        String periodPrefix = String.format("%04d-%02d", year, month);

        double income = 0;
        double expense = 0;
        int txCount = 0;

        try (Connection conn = database.getConnection()) {
            if (conn == null) return;

            String txSql = "SELECT type, SUM(amount) AS total, COUNT(*) AS cnt " +
                    "FROM transactions WHERE TO_CHAR(date, 'YYYY-MM') = ? GROUP BY type";
            try (PreparedStatement ps = conn.prepareStatement(txSql)) {
                ps.setString(1, periodPrefix);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String type = rs.getString("type");
                    double amt = rs.getDouble("total");
                    int cnt = rs.getInt("cnt");
                    txCount += cnt;

                    if ("Income".equalsIgnoreCase(type)) {
                        income += amt;
                    } else if ("Expense".equalsIgnoreCase(type)) {
                        expense += amt;
                    }
                }
            }

            // Also include recorded expenses from expenses table
            String expSql = "SELECT SUM(amount) AS total_extra FROM expenses WHERE month = ? AND year = ?";
            try (PreparedStatement psExp = conn.prepareStatement(expSql)) {
                psExp.setInt(1, month);
                psExp.setInt(2, year);
                ResultSet rsExp = psExp.executeQuery();
                if (rsExp.next()) {
                    expense += rsExp.getDouble("total_extra");
                }
            }

            totalIncomeLabel.setText(String.format("%.2f BDT", income));
            totalExpenseLabel.setText(String.format("%.2f BDT", expense));
            double profit = income - expense;
            netProfitLabel.setText(String.format("%.2f BDT", profit));
            if (profit >= 0) {
                netProfitLabel.setForeground(new Color(46, 204, 113));
            } else {
                netProfitLabel.setForeground(new Color(231, 76, 60));
            }
            transactionsCountLabel.setText(String.valueOf(txCount));

            // Populate Top Selling Products
            topProductsModel.setRowCount(0);
            String prodSql = "SELECT category, COALESCE(company_name, '-') AS company, SUM(quantity) AS total_qty, SUM(amount) AS total_amt " +
                    "FROM transactions WHERE TO_CHAR(date, 'YYYY-MM') = ? AND UPPER(type) = 'INCOME' " +
                    "GROUP BY category, company_name ORDER BY total_amt DESC LIMIT 10";

            try (PreparedStatement psProd = conn.prepareStatement(prodSql)) {
                psProd.setString(1, periodPrefix);
                ResultSet rsProd = psProd.executeQuery();
                int rank = 1;
                while (rsProd.next()) {
                    topProductsModel.addRow(new Object[]{
                            rank++,
                            rsProd.getString("category"),
                            rsProd.getString("company"),
                            rsProd.getInt("total_qty"),
                            String.format("%.2f", rsProd.getDouble("total_amt"))
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(REPORTS_WINDOW::new);
    }
}
