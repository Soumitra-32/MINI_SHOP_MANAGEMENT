
package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import controller.database;

public class DASHBOARD_WINDOW extends JFrame {
    private java.util.List<String> currentExpenseProducts = new java.util.ArrayList<>();
    private java.util.List<Double> currentExpenses = new java.util.ArrayList<>();
    private double maxProductExpense = 0;

    private JComboBox<String> yearBox, monthBox, dayBox, viewTypeBox;
    private JLabel incomeLabel, expenseLabel;
    private JPanel chartPanel;
    private JTable employeeTable;
    private DefaultTableModel employeeModel;

    // Fix: Class fields for use in inner classes
    private double currentIncome, currentExpense, currentBudget;
    private java.util.List<String> currentProducts = new java.util.ArrayList<>();
    private java.util.List<Double> currentIncomes = new java.util.ArrayList<>();
    private double maxProductIncome = 0;

    public DASHBOARD_WINDOW() {
        ModernTheme.setupWindow(this, "Dashboard Analytics", ModernTheme.WIN_MAIN_W, ModernTheme.WIN_MAIN_H, JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(ModernTheme.createHeader("Dashboard Analytics", "Income vs Expense vs Budget + Staff & Low-Stock Overview", ModernTheme.PRIMARY, ModernTheme.VIOLET), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Two explicit rows. A single FlowLayout row reports a ONE-row preferred
        // height but actually wraps at this width, which pushed the trailing
        // buttons below the panel edge where they were clipped and unclickable.
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        topPanel.setBackground(ModernTheme.CARD_BG);
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterRow.setOpaque(false);
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        actionRow.setOpaque(false);

        viewTypeBox = new JComboBox<>(new String[]{"Day", "Month", "Year"});
        yearBox = new JComboBox<>(getYears());
        monthBox = new JComboBox<>(getMonths());
        dayBox = new JComboBox<>(getDays());

        java.time.LocalDate now = java.time.LocalDate.now();
        yearBox.setSelectedItem(String.valueOf(now.getYear()));
        monthBox.setSelectedItem(String.format("%02d", now.getMonthValue()));
        dayBox.setSelectedItem(String.format("%02d", now.getDayOfMonth()));

        filterRow.add(label("View:"));
        filterRow.add(viewTypeBox);
        filterRow.add(label("Year:"));
        filterRow.add(yearBox);
        filterRow.add(label("Month:"));
        filterRow.add(monthBox);
        filterRow.add(label("Day:"));
        filterRow.add(dayBox);

        incomeLabel = label("Income: 0", Color.GREEN.darker());
        expenseLabel = label("Expense: 0", Color.RED);

        actionRow.add(incomeLabel);
        actionRow.add(expenseLabel);


        JButton overviewChartBtn = ModernTheme.createButton("Overview Chart", ModernTheme.SKY);
        overviewChartBtn.addActionListener(e -> calculateAndDisplay());
        actionRow.add(overviewChartBtn);

        JButton expenseProductBtn = ModernTheme.createButton("Expense by Product", ModernTheme.DANGER);
        expenseProductBtn.addActionListener(e -> showExpenseByProductChart());
        actionRow.add(expenseProductBtn);

        JButton productIncomeBtn = ModernTheme.createButton("Income by Product", ModernTheme.SUCCESS);
        productIncomeBtn.addActionListener(e -> showIncomeByProductChart());
        actionRow.add(productIncomeBtn);

        topPanel.add(filterRow);
        topPanel.add(actionRow);

        content.add(topPanel, BorderLayout.NORTH);

        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(ModernTheme.CARD_BG);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1, true),
                new EmptyBorder(14, 14, 14, 14)));
        content.add(chartPanel, BorderLayout.CENTER);

        JPanel employeePanel = new JPanel(new BorderLayout(10, 10));
        employeePanel.setBackground(ModernTheme.CARD_BG);
        employeePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        employeePanel.setPreferredSize(new Dimension(300, 0));

        employeeModel = new DefaultTableModel(new String[]{"Employee Username"}, 0);
        employeeTable = new JTable(employeeModel);
        ModernTheme.styleTable(employeeTable);

        JLabel empTitle = new JLabel("Employees");
        empTitle.setFont(ModernTheme.FONT_SUBTITLE);
        empTitle.setHorizontalAlignment(SwingConstants.CENTER);

        employeePanel.add(empTitle, BorderLayout.NORTH);
        employeePanel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        JPanel lowStockPanel = new JPanel(new BorderLayout(10, 10));
        lowStockPanel.setBackground(ModernTheme.CARD_BG);
        lowStockPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        lowStockPanel.setPreferredSize(new Dimension(300, 220));

        DefaultTableModel stockModel = new DefaultTableModel(new String[]{"Product", "Quantity"}, 0);
        JTable stockTable = new JTable(stockModel);
        ModernTheme.styleTable(stockTable);

        JLabel stockTitle = new JLabel("Low Stock (<100)");
        stockTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        stockTitle.setHorizontalAlignment(SwingConstants.CENTER);

        lowStockPanel.add(stockTitle, BorderLayout.NORTH);
        lowStockPanel.add(new JScrollPane(stockTable), BorderLayout.CENTER);
        employeePanel.add(lowStockPanel, BorderLayout.SOUTH);

        content.add(employeePanel, BorderLayout.EAST);
        add(content, BorderLayout.CENTER);

        ActionListener filterListener = e -> calculateAndDisplay();
        viewTypeBox.addActionListener(filterListener);
        yearBox.addActionListener(filterListener);
        monthBox.addActionListener(filterListener);
        dayBox.addActionListener(filterListener);

        calculateAndDisplay();
        loadEmployees();
        loadLowStockProducts(stockModel);

        setVisible(true);
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private JLabel label(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        return label;
    }
    private JButton createStyledButton(String text, Color bgColor) {
        return ModernTheme.createButton(text, bgColor);
    }

    private void styleTable(JTable table) {
        ModernTheme.styleTable(table);
    }

    private String[] getYears() {
        int currentYear = java.time.LocalDate.now().getYear();
        String[] years = new String[7];
        for (int i = 0; i < 7; i++) {
            years[i] = String.valueOf(currentYear - 3 + i);
        }
        return years;
    }

    private String[] getMonths() {
        return new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    }

    private String[] getDays() {
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = String.format("%02d", i);
        }
        return days;
    }

    private void showExpenseByProductChart() {
        chartPanel.removeAll();
        currentExpenseProducts.clear();
        currentExpenses.clear();
        maxProductExpense = 0;

        try (Connection con = database.getConnection();
             PreparedStatement stmt = con.prepareStatement(
                     "SELECT category AS product_name, SUM(amount) AS total_expense FROM transactions WHERE type='Expense' GROUP BY category")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("product_name");
                double expense = rs.getDouble("total_expense");
                currentExpenseProducts.add(name);
                currentExpenses.add(expense);
                if (expense > maxProductExpense) maxProductExpense = expense;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        JPanel expenseChart = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                int w = getWidth(), h = getHeight();

                int barWidth = 40;
                int spacing = 20;
                int baseY = h - 80;
                int chartHeight = h - 150;
                int startX = 50;

                for (int i = 0; i < currentExpenseProducts.size(); i++) {
                    int barHeight = (int) ((currentExpenses.get(i) / maxProductExpense) * chartHeight);
                    int x = startX + i * (barWidth + spacing);

                    g2.setColor(new Color(255, 153, 153)); // soft red
                    g2.fillRoundRect(x, baseY - barHeight, barWidth, barHeight, 10, 10);
                    g2.setColor(Color.BLACK);

                    String label = currentExpenseProducts.get(i);
                    if (label.length() > 6) label = label.substring(0, 6) + "...";

                    g2.drawString(label, x, baseY + 15);
                    g2.drawString(String.format("%.0f", currentExpenses.get(i)), x, baseY - barHeight - 10);
                }
            }
        };

        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(expenseChart, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }


    private void calculateAndDisplay() {
        String viewType = (String) viewTypeBox.getSelectedItem();
        String year = (String) yearBox.getSelectedItem();
        String month = (String) monthBox.getSelectedItem();
        String day = (String) dayBox.getSelectedItem();

        currentIncome = 0;
        currentExpense = 0;
        currentBudget = 0;

        try (Connection con = database.getConnection()) {
            String sql;
            if ("Day".equals(viewType)) {
                sql = "SELECT type, amount FROM transactions WHERE date = ?::date";
            } else if ("Month".equals(viewType)) {
                sql = "SELECT type, amount FROM transactions WHERE TO_CHAR(date, 'YYYY-MM') = ?";
            } else {
                sql = "SELECT type, amount FROM transactions WHERE TO_CHAR(date, 'YYYY') = ?";
            }

            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                if ("Day".equals(viewType)) {
                    stmt.setString(1, year + "-" + month + "-" + day);
                } else if ("Month".equals(viewType)) {
                    stmt.setString(1, year + "-" + month);
                } else {
                    stmt.setString(1, year);
                }

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String type = rs.getString("type");
                    double amount = rs.getDouble("amount");
                    if ("Income".equalsIgnoreCase(type)) currentIncome += amount;
                    else if ("Expense".equalsIgnoreCase(type)) currentExpense += amount;
                }
            }

            // Budget key format: budget_YYYY_MM
            String budgetKey = String.format("budget_%s_%s", year, month);
            try (PreparedStatement budgetStmt = con.prepareStatement("SELECT value FROM settings WHERE setting_key = ?")) {
                budgetStmt.setString(1, budgetKey);
                ResultSet budgetRs = budgetStmt.executeQuery();
                if (budgetRs.next()) {
                    try {
                        currentBudget = Double.parseDouble(budgetRs.getString("value"));
                    } catch (Exception ignored) {}
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        incomeLabel.setText(String.format("Income: %.2f", currentIncome));
        expenseLabel.setText(String.format("Expense: %.2f", currentExpense));
        displayChart();
    }

    private void displayChart() {
        chartPanel.removeAll();

        JPanel barChart = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                int w = getWidth(), h = getHeight();

                double max = Math.max(currentIncome, Math.max(currentExpense, currentBudget));
                int barWidth = 80, spacing = 70;
                int baseX = 70, baseY = h - 80, chartHeight = h - 150;

                int iBar = (int) ((currentIncome / max) * chartHeight);
                int eBar = (int) ((currentExpense / max) * chartHeight);
                int bBar = (int) ((currentBudget / max) * chartHeight);

                drawBar(g2, "Income", baseX, iBar, baseY, barWidth, Color.BLUE, currentIncome);
                drawBar(g2, "Expense", baseX + spacing + barWidth, eBar, baseY, barWidth, Color.RED, currentExpense);
                drawBar(g2, "Budget", baseX + 2 * (spacing + barWidth), bBar, baseY, barWidth, Color.GREEN.darker(), currentBudget);
            }

            void drawBar(Graphics2D g2, String label, int x, int height, int baseY, int width, Color color, double value) {
                g2.setColor(color);
                g2.fillRoundRect(x, baseY - height, width, height, 10, 10);
                g2.setColor(Color.BLACK);
                g2.drawString(label, x + 10, baseY + 20);
                g2.drawString(String.format("%.0f", value), x + 10, baseY - height - 10);
            }
        };

        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(barChart, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void showIncomeByProductChart() {
        chartPanel.removeAll();
        currentProducts.clear();
        currentIncomes.clear();
        maxProductIncome = 0;

        try (Connection con = database.getConnection();
             PreparedStatement stmt = con.prepareStatement(
                     "SELECT category AS product_name, SUM(amount) AS total_income FROM transactions WHERE type='Income' GROUP BY category")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("product_name");
                double income = rs.getDouble("total_income");
                currentProducts.add(name);
                currentIncomes.add(income);
                if (income > maxProductIncome) maxProductIncome = income;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        JPanel productChart = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                int w = getWidth(), h = getHeight();

                int barWidth = 40;
                int spacing = 20;
                int baseY = h - 80;
                int chartHeight = h - 150;
                int startX = 50;

                for (int i = 0; i < currentProducts.size(); i++) {
                    int barHeight = (int) ((currentIncomes.get(i) / maxProductIncome) * chartHeight);
                    int x = startX + i * (barWidth + spacing);

                    g2.setColor(new Color(102, 204, 255));
                    g2.fillRoundRect(x, baseY - barHeight, barWidth, barHeight, 10, 10);
                    g2.setColor(Color.BLACK);

                    String label = currentProducts.get(i);
                    if (label.length() > 6) label = label.substring(0, 6) + "...";

                    g2.drawString(label, x, baseY + 15);
                    g2.drawString(String.format("%.0f", currentIncomes.get(i)), x, baseY - barHeight - 10);
                }
            }
        };

        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(productChart, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void loadEmployees() {
        employeeModel.setRowCount(0);
        try (Connection con = database.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username FROM employee")) {
            while (rs.next()) {
                employeeModel.addRow(new Object[]{rs.getString("username")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadLowStockProducts(DefaultTableModel stockModel) {
        stockModel.setRowCount(0);
        try (Connection con = database.getConnection();
             PreparedStatement stmt = con.prepareStatement(
                     "SELECT CONCAT(category_name, ' (', company_name, ')') AS product_name, quantity " +
                             "FROM companies WHERE quantity < 100 ORDER BY quantity ASC")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stockModel.addRow(new Object[]{rs.getString("product_name"), rs.getInt("quantity")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(DASHBOARD_WINDOW::new);
    }

}