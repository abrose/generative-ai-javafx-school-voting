// ABOUTME: Manages database connections and initialization for the voting system
// ABOUTME: Provides connection pooling and transaction support for SQLite database

package com.school.voting.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DB_NAME = "school_voting.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
            
            // Enable foreign keys
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            
            // Check if tables exist
            if (!tablesExist()) {
                createTables();
            }
            
            logger.info("Database initialized successfully");
        } catch (SQLException e) {
            logger.error("Failed to initialize database", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private boolean tablesExist() throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, "voting_sessions", null)) {
            return rs.next();
        }
    }

    private void createTables() throws SQLException {
        try {
            String schema = Files.readString(
                Path.of(getClass().getResource("/db/schema.sql").toURI()),
                StandardCharsets.UTF_8
            );
            
            try (Statement stmt = connection.createStatement()) {
                for (String sql : schema.split(";")) {
                    if (!sql.trim().isEmpty()) {
                        stmt.execute(sql.trim());
                    }
                }
            }
            
            logger.info("Database tables created successfully");
        } catch (Exception e) {
            logger.error("Failed to create database tables", e);
            throw new SQLException("Failed to create database tables", e);
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
            
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
        }
        return connection;
    }

    public void beginTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    public void commitTransaction() throws SQLException {
        try {
            getConnection().commit();
        } finally {
            getConnection().setAutoCommit(true);
        }
    }

    public void rollbackTransaction() throws SQLException {
        try {
            getConnection().rollback();
        } finally {
            getConnection().setAutoCommit(true);
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed");
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }

    public void resetDatabase() throws SQLException {
        logger.warn("Resetting database - all data will be lost!");
        
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS votes");
            stmt.execute("DROP TABLE IF EXISTS parents");
            stmt.execute("DROP TABLE IF EXISTS voting_sessions");
        }
        
        createTables();
    }
}