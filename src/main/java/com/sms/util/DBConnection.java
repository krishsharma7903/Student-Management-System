package com.sms.util;

import com.sms.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton database connectivity utility providing managed JDBC connections.
 * Reads database configuration securely from db.properties with zero hardcoded credentials.
 * 
 * // [JDBC] Classes for database operations: DBConnection singleton
 * // [JDBC] Reads db.properties, closes resources properly
 */
public final class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static final String PROPERTIES_FILE = "db.properties";

    private static volatile DBConnection instance;

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String dbDriver;

    private DBConnection() {
        loadConfiguration();
        loadDriver();
    }

    /**
     * Thread-safe double-checked locking Singleton accessor.
     * 
     * // [CONCURRENCY] Thread-safe singleton
     */
    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Loads database configuration properties from classpath or project root.
     */
    private void loadConfiguration() {
        Properties properties = new Properties();
        boolean loaded = false;

        // 1. Try loading from thread context class loader
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            try (InputStream in = cl.getResourceAsStream(PROPERTIES_FILE)) {
                if (in != null) {
                    properties.load(in);
                    loaded = true;
                    LOGGER.info("Successfully loaded db.properties from Thread context ClassLoader.");
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed loading db.properties from context ClassLoader", e);
            }
        }

        // 2. Try loading from this class's class loader
        if (!loaded) {
            try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
                if (in != null) {
                    properties.load(in);
                    loaded = true;
                    LOGGER.info("Successfully loaded db.properties from DBConnection ClassLoader.");
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed loading db.properties from DBConnection ClassLoader", e);
            }
        }

        // 3. Fallback to external file if present in working directory
        if (!loaded) {
            Path externalPath = Paths.get(PROPERTIES_FILE);
            if (Files.exists(externalPath)) {
                try (InputStream in = Files.newInputStream(externalPath)) {
                    properties.load(in);
                    loaded = true;
                    LOGGER.info("Successfully loaded external db.properties from: " + externalPath.toAbsolutePath());
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Failed loading external db.properties", e);
                }
            }
        }

        if (!loaded) {
            throw new DatabaseException("Failed to find db.properties in classpath or working directory.");
        }

        this.dbUrl = properties.getProperty("db.url");
        this.dbUsername = properties.getProperty("db.username");
        this.dbPassword = properties.getProperty("db.password");
        this.dbDriver = properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

        if (this.dbUrl == null || this.dbUsername == null) {
            throw new DatabaseException("Database URL or username missing in db.properties.");
        }
    }

    private void loadDriver() {
        try {
            Class.forName(this.dbDriver);
            LOGGER.info("Registered JDBC Driver: " + this.dbDriver);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("JDBC Driver class not found: " + this.dbDriver, e);
        }
    }

    /**
     * Obtains an active JDBC Connection. Callers must always wrap connections in try-with-resources.
     * 
     * @return active java.sql.Connection
     * @throws DatabaseException if connection acquisition fails
     */
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(this.dbUrl, this.dbUsername, this.dbPassword);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to database at: " + this.dbUrl, e);
            throw new DatabaseException("Unable to establish database connection. Please verify MySQL service and credentials.", e);
        }
    }

    /**
     * Validates database connectivity with a 3-second timeout.
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && conn.isValid(3);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Database ping failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Closes AutoCloseable resources safely without throwing checked exceptions.
     */
    public static void close(AutoCloseable... resources) {
        if (resources == null) return;
        for (AutoCloseable res : resources) {
            if (res != null) {
                try {
                    res.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }
}
