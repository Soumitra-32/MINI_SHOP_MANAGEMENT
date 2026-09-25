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
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String getUrl() {
        return URL;
    }
}

