# Mini Shop & Expense Management System

A desktop Point of Sale (POS), Inventory Control, and Financial Expense Management application built with **Java Swing** and powered by **PostgreSQL**. Designed with a modern, responsive flat UI theme and an MVC architecture suitable for small-to-medium retail shops, supermarkets, and trading businesses.

---

## 📋 Table of Contents
1. [Overview](#overview)
2. [Key Features](#key-features)
3. [System Architecture](#system-architecture)
4. [Database Schema](#database-schema)
5. [Prerequisites](#prerequisites)
6. [Getting Started & Installation](#getting-started--installation)
7. [Default User Credentials](#default-user-credentials)
8. [Application Usage Guide](#application-usage-guide)
9. [Project Structure](#project-structure)
10. [Technologies Used](#technologies-used)

---

## 🌟 Overview

The **Mini Shop & Expense Management System** addresses the operational and financial needs of modern storefronts. It unifies:
* Real-time sales logging and stock deduction.
* Supplier / company product catalog and inventory restock workflows.
* Operating expenditure logging compared against preset monthly budgets.
* Comprehensive analytics (visual revenue charts, profit/loss calculations, top-selling product breakdowns).

---

## 🚀 Key Features

### 1. Dual Role-Based Portals
* **Admin Portal**: Full access to dashboard analytics, inventory management, category creation, selling price setup, budget configuration, transaction auditing, and financial reporting.
* **Employee Portal**: Streamlined interface for cashiers and staff to record transactions, view sales history, monitor store inventory, and review summary reports.

### 2. Transaction & Sales Engine
* Record transactions categorized as **Income** (sales) or **Expense**.
* Dynamic price lookup: Automatically computes total prices based on configured selling prices.
* **Real-time stock deduction**: Validates existing inventory before completing a transaction; automatically decrements company inventory and updates category totals.
* Search, filter by transaction type, and remove obsolete transactions.

### 3. Inventory & Category Management
* Group products by categories and supplier companies.
* Set buying price and retail selling price per item.
* Restock products with budget verification against the current month's budget.
* Automatic low-stock warnings for items with fewer than 100 units in stock.

### 4. Financial & Expense Tracking
* Define monthly expenditure budgets (in BDT or any custom currency).
* Log operational expenses (rent, utilities, salaries, maintenance).
* Visual bar charts comparing daily, monthly, and yearly income vs. expense.
* Product-wise revenue and expenditure breakdown charts.

### 5. Reports & Analytics
* Monthly financial metrics: **Total Income**, **Total Expenses**, **Net Profit/Loss**, and **Transaction Volume**.
* Top 10 selling products table ranked by quantity sold and total revenue.


---

## 🏗️ System Architecture

The project follows a clean **MVC (Model-View-Controller)** architectural pattern:

```
┌────────────────────────────────────────────────────────┐
│                   Presentation Layer                   │
│             (Java Swing UI & ModernTheme)              │
│  • homepage             • admin / employee Dashboard   │
│  • ADD_TRANSACTIONS     • VIEW_TRANSACTIONS            │
│  • INVENTORY_WINDOW     • REPORTS_WINDOW               │
│  • MANAGE_CATEGORIES    • SETTINGS_WINDOW              │
└───────────────────────────▲────────────────────────────┘
                            │ User Interactions & Events
┌───────────────────────────▼────────────────────────────┐
│                    Controller Layer                    │
│                 (controller.database)                  │
│  • Connection Pooling & Driver Initialization          │
│  • db.properties configuration parser                  │
│  • Transaction safety (ACID rollbacks & commits)       │
└───────────────────────────▲────────────────────────────┘
                            │ JDBC SQL Queries (PreparedStatements)
┌───────────────────────────▼────────────────────────────┐
│                    Persistence Layer                   │
│               (PostgreSQL 12+ / Cloud DB)              │
│  • admin, employee, categories, companies              │
│  • transactions, expenses, settings                    │
└────────────────────────────────────────────────────────┘
```

* **UI Layer (`src/gui/`)**: Implements graphical frames using Java Swing. Styled via `ModernTheme.java` with a slate and royal blue palette, rounded input fields, card layouts, and alternating zebra-striped tables.
* **Controller Layer (`src/controller/`)**: Manages database connectivity, reads external connection strings, and ensures atomic transaction operations.
* **Database Layer (`src/sql_for_java_project/`)**: Relational PostgreSQL tables supporting both local instances and cloud-hosted databases (Supabase, Neon, AWS RDS, Render, ElephantSQL).

---

## 🗄️ Database Schema

The system uses 7 relational tables:

| Table | Purpose |
| :--- | :--- |
| `admin` | Admin credentials (`name`, `password`, `admin_key`) |
| `employee` | Employee sign-in accounts (`username`, `password`) |
| `categories` | Product categories and aggregate item quantities |
| `companies` | Supplier products linked to category, with quantity, purchase price, and selling price |
| `transactions`| Records of sales and expenditures with type, date, amount, currency, and notes |
| `expenses` | Recorded business overhead costs tied to month and year |
| `settings` | Key-value store for monthly budgets (`budget_YYYY_MM`) and global currency |


---

## 🛠️ Getting Started & Installation

### 1. Clone the Repository
```bash
git clone https://github.com/Soumitra-32/MINI_SHOP_MANAGEMENT.git
cd MINI_SHOP_MANAGEMENT
```

### 2. Setup the PostgreSQL Database
Open your PostgreSQL terminal (`psql`) or a GUI tool like pgAdmin / DBeaver:

```sql
CREATE DATABASE expense_tracker_db;
```

Execute the database script located at `src/sql_for_java_project/sql_commands`:
```bash
psql -U postgres -d expense_tracker_db -f src/sql_for_java_project/sql_commands
```

### 2b. (Optional) Load Demo / Seed Data
To instantly populate the app with realistic sample content — 6 product categories,
12 supplier products with stock & prices, ~20 sales/restock transactions spread over
3 months, monthly expense-book entries (rent, electricity, salary), and monthly
budgets:

```bash
psql -U postgres -d expense_tracker_db -f src/sql_for_java_project/sample_data.sql
```

The script is **idempotent** — every statement is guarded with `ON CONFLICT DO NOTHING`
or `NOT EXISTS`, and the budgets/expense rows are derived from `CURRENT_DATE` rather than
hard-coded — so it is safe to run repeatedly and keeps working in later months. After
loading, the Dashboard, Reports, Inventory and Settings windows open with meaningful
data instead of empty tables.

> Without this step the app still runs, but all tables start empty and you must add
> categories, companies and prices manually before recording any transaction.

To wipe the demo content again and start from an empty shop:

```sql
TRUNCATE transactions, expenses RESTART IDENTITY;
DELETE FROM companies;
DELETE FROM categories;
DELETE FROM settings WHERE setting_key LIKE 'budget_%';
```

### 3. Configure Database Credentials
Edit `src/db.properties` with your database details:

```properties
# Local PostgreSQL
db.url=jdbc:postgresql://localhost:5432/expense_tracker_db
db.user=postgres
db.password=your_password_here

# OR Cloud PostgreSQL (e.g., Supabase / Neon / Render)
# db.url=jdbc:postgresql://ep-example.region.aws.neon.tech/expense_tracker_db?sslmode=require
# db.user=your_cloud_user
# db.password=your_cloud_password
```

### 4. Build and Run

#### Option A (Recommended): One-Click `run.bat` (Windows)

The project ships with `run.bat` at the repository root, which compiles every source
file and launches the application in a single step — no IDE and no manual classpath
setup required.

```powershell
# from the project root
.\run.bat
```

What the script does, in order:

| Step | Action |
| :--- | :--- |
| 1 | Locates a JDK — first `%JDK_BIN%`, then `javac` on your `PATH`, then known installs (`%USERPROFILE%\.jdks\openjdk-23.0.2\bin`, `C:\Program Files\Java\jdk-21\bin`, `jdk-17\bin`). `java.exe` is always taken from the **same folder as `javac.exe`**, so an older JDK that happens to sit earlier on `PATH` is never used. |
| 2 | Deletes any previous `out/` and recreates it, so no stale `.class` files can survive a rename or deletion. |
| 3 | Compiles all of `src/**/*.java` with the PostgreSQL JDBC driver on the classpath (`-encoding UTF-8`). Every path is quoted, so a project folder containing spaces works. |
| 4 | Copies the runtime resources (`db.properties` and `gui/money_bg.jpg`) into `out/`. |
| 5 | Launches `gui.Main` with `out;lib/postgresql-42.7.3.jar` as the classpath. |

**If no JDK is found**, either install a JDK 17+ (and make sure it is on `PATH`),
or point the script at your JDK explicitly before running it:

```powershell
set JDK_BIN=C:\Program Files\Java\jdk-21\bin
.\run.bat
```

> `run.bat` is a Windows batch file. On Linux/macOS or Git Bash, use the manual
> terminal commands in Option C below instead.

#### Option B: Running with an IDE (IntelliJ IDEA / Eclipse)
1. Open the project folder in IntelliJ IDEA.
2. Ensure the JDK is set to **Java 17+** (`File` → `Project Structure` → `Project SDK`).
3. Ensure `lib/postgresql-42.7.3.jar` is included under `Libraries` (already configured in `.iml`).
4. Run `src/gui/Main.java`.

#### Option C: Running from Terminal (PowerShell / Bash)
```powershell
# Compile all source files with the PostgreSQL JDBC driver
javac -cp "lib/postgresql-42.7.3.jar" -d bin (Get-ChildItem -Path "src" -Recurse -Filter "*.java").FullName

# Copy properties and assets to runtime bin directory
Copy-Item "src/db.properties" -Destination "bin"
Copy-Item "src/gui/money_bg.jpg" -Destination "bin/gui"

# Launch Application
java -cp "bin;lib/postgresql-42.7.3.jar" gui.Main
```

---

## 🔑 Default User Credentials

Pre-seeded accounts are provided in the SQL schema:

| Role | Username | Password | Admin Key |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | `admin@123` |
| **Employee** | `employee1` | `emp123` | *N/A* |


---

## 📖 Application Usage Guide

### Admin Workflow
1. **Sign In**: Launch the app, select **Admin Login**, and enter your admin credentials and key.
2. **Setup Categories & Products**:
   * Navigate to **Manage Categories**.
   * Add a product category (e.g., `Electronics`, `Groceries`).
   * Select the category and add supplier companies with buying prices.
3. **Configure Pricing & Budget**:
   * Navigate to **Settings**.
   * Enter the retail selling price for each product.
   * Define the current month's operational budget and save currency.
4. **Purchase / Restock Inventory**:
   * Navigate to **Inventory**. Double-click a category to view items.
   * Input the quantity to buy and click **Buy Products**. The purchase cost is verified against available budget and added to inventory.
5. **Monitor Performance**:
   * Use **Dashboard** to view interactive Income vs. Expense bar charts.
   * Use **Reports & Analytics** to examine net profits and identify top-performing products.

### Employee Workflow
1. **Sign In**: Click **Employee Login** and input your username and password.
2. **Process a Sale**:
   * Click **Add Transaction**.
   * Select Category and Company product. The unit price and total amount calculate automatically.
   * Enter quantity and optional notes, then click **Add Transaction**. Stock is verified and deducted in real time.
3. **Review Transactions**:
   * Click **View Transactions** to search sales records by customer, product, or date.
4. **Inspect Inventory**:
   * Open **Inventory Stock** to verify available quantities before confirming orders with customers.

---

## 📂 Project Structure

```
MINI_SHOP_MANAGEMENT/
├── lib/
│   └── postgresql-42.7.3.jar        # PostgreSQL JDBC driver
├── src/
│   ├── controller/
│   │   └── database.java            # Connection factory & config loader
│   ├── gui/
│   │   ├── Main.java                # Application entry point
│   │   ├── ModernTheme.java         # Centralized UI theme & styling utility
│   │   ├── homepage.java            # Welcome & navigation portal
│   │   ├── adminloginpage.java      # Admin authentication screen
│   │   ├── employeeloginpage.java   # Employee authentication screen
│   │   ├── registerpage.java        # Employee account registration
│   │   ├── admin.java               # Admin command dashboard
│   │   ├── employee.java            # Employee sales workspace
│   │   ├── DASHBOARD_WINDOW.java    # Visual charts & financial stats
│   │   ├── ADD_TRANSACTIONS_WINDOW.java # Transaction & sales checkout
│   │   ├── VIEW_TRANSACTIONS_WINDOW.java # Filterable transactions table
│   │   ├── INVENTORY_WINDOW.java    # Stock levels & restocking dialog
│   │   ├── MANAGE_CATEGORIES_WINDOW.java # Category & company catalog
│   │   ├── MANAGE_EXPENSE_WINDOW.java # Monthly expense recording
│   │   ├── SETTINGS_WINDOW.java     # Pricing & budget configuration
│   │   ├── REPORTS_WINDOW.java      # Profit/loss analytics & top sellers
│   │   ├── ABOUT_WINDOW.java        # Application information dialog
│   │   ├── BackgroundImagePanel.java # Fallback-safe background component
│   │   └── PRODUCT.java             # Product base domain model
│   ├── sql_for_java_project/
│   │   ├── sql_commands             # PostgreSQL schema (tables + default users)
│   │   └── sample_data.sql          # Demo/seed data (categories, stock, transactions)
│   └── db.properties                # External database configuration
├── run.bat                          # One-click build & run script (Windows)
├── demo final.iml                   # IntelliJ module file
└── README.md                        # Project documentation
```

---

## 🛠️ Technologies Used

* **Language**: Java 17+ / Java 23
* **GUI Framework**: Java Swing & AWT (System Look & Feel, Custom Modern Flat Design)
* **Database**: PostgreSQL 12+ (local or cloud)
* **Database Driver**: PostgreSQL JDBC Driver 42.7.3
* **Architecture**: Model-View-Controller (MVC)

> 💡 *New employee accounts can be registered directly through the **Register** button on the home screen.*

---

## 📦 Prerequisites

* **Java Development Kit (JDK)**: JDK 17, 21, or 23 installed.
  Verify with `javac -version`. If `javac` is not on your `PATH`, you can still use
  `run.bat` by setting `JDK_BIN` to your JDK's `bin` folder first.
* **PostgreSQL Database**: PostgreSQL 12 or newer (local installation or cloud instance).
* **PostgreSQL CLI (`psql`)** — needed to load the schema and the optional seed data.
* **IDE (Optional)**: IntelliJ IDEA, Eclipse, NetBeans, or VS Code.

