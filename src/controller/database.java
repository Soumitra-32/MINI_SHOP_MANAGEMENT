package controller;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class database {

    private static String URL = "jdbc:postgresql://localhost:5432/expense_tracker_db";
    private static String USER = "postgres";
    private static String PASSWORD = "admin";

    static {
        try {
            // Load configurations from db.properties if available
            Properties props = new Properties();
            InputStream in = database.class.getResourceAsStream("/db.properties");
            if (in == null) {
                // Try from local file system as fallback
                java.io.File f = new java.io.File("src/db.properties");
                if (f.exists()) {
                    try (java.io.FileInputStream fis = new java.io.FileInputStream(f)) {
                        props.load(fis);
                    }
                }
            } else {
                props.load(in);
            }

            if (props.getProperty("db.url") != null) {
                URL = props.getProperty("db.url").trim();
            }
            if (props.getProperty("db.user") != null) {
                USER = props.getProperty("db.user").trim();
            }
            if (props.getProperty("db.password") != null) {
                PASSWORD = props.getProperty("db.password").trim();
            }

            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            System.err.println("Database initialization notice: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            Connection c = DriverManager.getConnection(URL, USER, PASSWORD);
            try (java.sql.Statement s = c.createStatement()) {
                s.execute("SET search_path TO public");
            } catch (Exception ignored) {}
            return c;
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /** Show a friendly message when a window gets a null connection. Returns false if null. */
    public static boolean requireConnection(java.awt.Component parent, Connection conn) {
        if (conn == null) {
            javax.swing.JOptionPane.showMessageDialog(parent,
                    "Cannot connect to database.\nURL: " + URL + "\nCheck src/db.properties and PostgreSQL service.",
                    "Database Offline", javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static String getUrl() {
        return URL;
    }
}

