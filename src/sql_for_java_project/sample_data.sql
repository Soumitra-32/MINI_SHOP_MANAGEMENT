-- ============================================================================
--  Mini Shop & Expense Management System - Demo / Seed Data
--  Engine : PostgreSQL 12+
--  Usage  : psql -U postgres -d expense_tracker_db -f src/sql_for_java_project/sample_data.sql
--  Run this AFTER src/sql_for_java_project/sql_commands (the schema script).
--
--  Idempotent: every statement is guarded (ON CONFLICT / NOT EXISTS), so the
--  script can be executed repeatedly without creating duplicate rows.
--
--  To wipe the demo data again and start from an empty shop:
--    TRUNCATE transactions, expenses RESTART IDENTITY;
--    DELETE FROM companies;
--    DELETE FROM categories;
--    DELETE FROM settings WHERE setting_key LIKE 'budget_%';
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 1. Monthly budgets - current month plus the two previous months.
--    Stored as settings rows: budget_YYYY_MM = <amount>
--    Computed from CURRENT_DATE so the script keeps working in later months.
-- ---------------------------------------------------------------------------
INSERT INTO settings (setting_key, value)
SELECT 'budget_' || to_char(date_trunc('month', CURRENT_DATE) - (m.months_ago * INTERVAL '1 month'), 'YYYY_MM'),
       m.amount
FROM (VALUES (0, '500000'),
             (1, '450000'),
             (2, '400000')) AS m(months_ago, amount)
ON CONFLICT (setting_key) DO NOTHING;

-- Global display currency
INSERT INTO settings (setting_key, value)
VALUES ('currency', 'BDT')
ON CONFLICT (setting_key) DO NOTHING;

-- ---------------------------------------------------------------------------
-- 2. Product categories
-- ---------------------------------------------------------------------------
INSERT INTO categories (name, quantity) VALUES
 ('Rice & Grains', 0),
 ('Cooking Oil', 0),
 ('Beverages', 0),
 ('Snacks', 0),
 ('Dairy', 0),
 ('Household', 0)
ON CONFLICT (name) DO NOTHING;

-- ---------------------------------------------------------------------------
-- 3. Supplier companies / products (stock, buying price, selling price)
-- ---------------------------------------------------------------------------
INSERT INTO companies (company_name, category_name, quantity, price, selling_price) VALUES
 ('Fresh Mills', 'Rice & Grains', 400, 65.00, 75.00),
 ('Golden Grain Co', 'Rice & Grains', 250, 70.00, 82.00),
 ('Pure Drop Oil', 'Cooking Oil', 300, 160.00, 185.00),
 ('Sundarban Oil', 'Cooking Oil', 180, 150.00, 172.00),
 ('CocaCola Distributor', 'Beverages', 500, 28.00, 35.00),
 ('Fresh Juice Corner', 'Beverages', 220, 40.00, 55.00),
 ('Munch Foods', 'Snacks', 600, 12.00, 20.00),
 ('Crunchy Bites', 'Snacks', 350, 15.00, 25.00),
 ('Milky Way Dairy', 'Dairy', 200, 85.00, 100.00),
 ('Farm Fresh Dairy', 'Dairy', 150, 90.00, 110.00),
 ('CleanHome Supplies', 'Household', 180, 120.00, 150.00),
 ('Sparkle Care', 'Household', 90, 95.00, 125.00)
ON CONFLICT (category_name, company_name) DO NOTHING;

-- Sync category totals
UPDATE categories c SET quantity = sub.total
FROM (SELECT category_name, SUM(quantity) AS total FROM companies GROUP BY category_name) sub
WHERE c.name = sub.category_name;

-- ---------------------------------------------------------------------------
-- 4. Demo transactions (sales + restocks over the last couple of months)
--    Guarded by NOT EXISTS so re-running the script never duplicates rows.
-- ---------------------------------------------------------------------------
INSERT INTO transactions (type, category, company_name, quantity, amount, currency, date, notes, name, status)
SELECT v.type, v.category, v.company_name, v.quantity, v.amount, v.currency, v.date, v.notes, v.name, v.status
FROM (VALUES
 ('Income', 'Rice & Grains', 'Fresh Mills', 10, 750.00, 'BDT', CURRENT_DATE - 1, 'Retail sale - daily', 'employee1', 'Employee'),
 ('Income', 'Cooking Oil', 'Pure Drop Oil', 5, 925.00, 'BDT', CURRENT_DATE - 1, 'Retail sale - daily', 'employee1', 'Employee'),
 ('Income', 'Beverages', 'CocaCola Distributor', 24, 840.00, 'BDT', CURRENT_DATE - 2, 'Bulk order canteen', 'admin', 'Admin'),
 ('Income', 'Snacks', 'Munch Foods', 40, 800.00, 'BDT', CURRENT_DATE - 3, 'School tiffin bulk', 'employee1', 'Employee'),
 ('Income', 'Dairy', 'Milky Way Dairy', 8, 800.00, 'BDT', CURRENT_DATE - 4, 'Morning delivery', 'employee1', 'Employee'),
 ('Income', 'Household', 'CleanHome Supplies', 4, 600.00, 'BDT', CURRENT_DATE - 5, 'Walk-in sale', 'admin', 'Admin'),
 ('Expense', 'Rice & Grains', 'Golden Grain Co', 50, 3500.00, 'BDT', CURRENT_DATE - 6, 'Restock rice sacks', 'admin', 'Admin'),
 ('Expense', 'Cooking Oil', 'Sundarban Oil', 30, 4500.00, 'BDT', CURRENT_DATE - 7, 'Restock oil cartons', 'admin', 'Admin'),
 ('Income', 'Beverages', 'Fresh Juice Corner', 15, 825.00, 'BDT', CURRENT_DATE - 8, 'Weekend sales', 'employee1', 'Employee'),
 ('Income', 'Snacks', 'Crunchy Bites', 30, 750.00, 'BDT', CURRENT_DATE - 9, 'Evening rush', 'employee1', 'Employee'),
 ('Income', 'Rice & Grains', 'Fresh Mills', 20, 1500.00, 'BDT', CURRENT_DATE - 12, 'Monthly ration order', 'admin', 'Admin'),
 ('Income', 'Cooking Oil', 'Pure Drop Oil', 8, 1480.00, 'BDT', CURRENT_DATE - 15, 'Restaurant order', 'employee1', 'Employee'),
 ('Income', 'Dairy', 'Farm Fresh Dairy', 12, 1320.00, 'BDT', CURRENT_DATE - 18, 'Hotel supply', 'employee1', 'Employee'),
 ('Income', 'Beverages', 'CocaCola Distributor', 48, 1680.00, 'BDT', CURRENT_DATE - 22, 'Event catering', 'admin', 'Admin'),
 ('Expense', 'Snacks', 'Munch Foods', 100, 1200.00, 'BDT', CURRENT_DATE - 25, 'Restock chips', 'admin', 'Admin'),
 ('Expense', 'Dairy', 'Milky Way Dairy', 40, 3400.00, 'BDT', CURRENT_DATE - 28, 'Restock milk packs', 'admin', 'Admin'),
 ('Income', 'Household', 'Sparkle Care', 6, 750.00, 'BDT', CURRENT_DATE - 32, 'Monthly cleaning supply', 'employee1', 'Employee'),
 ('Income', 'Rice & Grains', 'Golden Grain Co', 15, 1230.00, 'BDT', CURRENT_DATE - 40, 'Wholesale buyer', 'admin', 'Admin'),
 ('Expense', 'Household', 'CleanHome Supplies', 25, 3000.00, 'BDT', CURRENT_DATE - 45, 'Restock detergents', 'admin', 'Admin'),
 ('Income', 'Snacks', 'Munch Foods', 60, 1200.00, 'BDT', CURRENT_DATE - 50, 'Festival stall', 'employee1', 'Employee')
) AS v(type, category, company_name, quantity, amount, currency, date, notes, name, status)
WHERE NOT EXISTS (SELECT 1 FROM transactions);

-- ---------------------------------------------------------------------------
-- 5. Monthly expense book entries (current month + previous month)
--    Only inserted when the expenses table is still empty, so re-runs are safe.
-- ---------------------------------------------------------------------------
INSERT INTO expenses (expense_name, amount, month, year)
SELECT e.expense_name,
       e.amount,
       EXTRACT(MONTH FROM CURRENT_DATE - (e.months_ago * INTERVAL '1 month'))::int,
       EXTRACT(YEAR  FROM CURRENT_DATE - (e.months_ago * INTERVAL '1 month'))::int
FROM (VALUES
 ('Shop Rent', 15000.00, 0),
 ('Electricity Bill', 3200.00, 0),
 ('Staff Salary - Helper', 12000.00, 0),
 ('Transport / Delivery', 2500.00, 0),
 ('Shop Rent', 15000.00, 1),
 ('Electricity Bill', 2900.00, 1),
 ('Staff Salary - Helper', 12000.00, 1),
 ('Packaging Materials', 1800.00, 1)
) AS e(expense_name, amount, months_ago)
WHERE NOT EXISTS (SELECT 1 FROM expenses);

-- ---------------------------------------------------------------------------
-- Verification (optional) - uncomment to inspect the seeded row counts
-- ---------------------------------------------------------------------------
-- SELECT 'categories'   AS table_name, COUNT(*) AS rows FROM categories
-- UNION ALL SELECT 'companies',    COUNT(*) FROM companies
-- UNION ALL SELECT 'transactions', COUNT(*) FROM transactions
-- UNION ALL SELECT 'expenses',     COUNT(*) FROM expenses
-- UNION ALL SELECT 'settings',     COUNT(*) FROM settings;
