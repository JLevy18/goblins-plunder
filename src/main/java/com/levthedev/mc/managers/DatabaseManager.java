package com.levthedev.mc.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import com.levthedev.mc.coordinators.DatabaseCoordinator;
import com.levthedev.mc.coordinators.DatabaseCoordinatorImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseManager {
    
    private static Plugin plugin;
    private static DatabaseManager instance;
    private HikariDataSource dataSource;
    private final DatabaseCoordinator databaseCoordinator;

    public DatabaseManager() {

        try {
            setupDataSource(plugin.getConfig());
        } catch (Exception e) {
            if (DatabaseNotFoundException(e)){
                createDatabase(plugin.getConfig());
                try {
                    setupDataSource(plugin.getConfig());
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to establish a database connection after creation", ex);
                }
            } else {
                throw new RuntimeException("Database connection failed", e);
            }
        }
        

        // Coordinator
        this.databaseCoordinator = new DatabaseCoordinatorImpl(dataSource, plugin);

    }


    public static void initialize(Plugin pluginInstance) {
        plugin = pluginInstance;
        instance = new DatabaseManager();
        instance.getDatabaseCoordinator().setup();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseManager not initialized");
        }

        return instance;
    }

    public DatabaseCoordinator getDatabaseCoordinator() {
        return databaseCoordinator;
    }


    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Remember to close your dataSource when the plugin is disabled
    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private void createDatabase(FileConfiguration pluginConfig) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://" + pluginConfig.getString("datasource.host") + ":" + pluginConfig.getInt("datasource.port"), pluginConfig.getString("datasource.username"), pluginConfig.getString("datasource.password"));
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS goblins_plunder");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create database", e);
        }
    }

    private void setupDataSource(FileConfiguration pluginConfig) throws Exception{
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + pluginConfig.getString("datasource.host") + ":" + pluginConfig.getInt("datasource.port") + "/goblins_plunder");
        config.setUsername(pluginConfig.getString("datasource.username"));
        config.setPassword(pluginConfig.getString("datasource.password"));

        this.dataSource = new HikariDataSource(config);
    }

    private boolean DatabaseNotFoundException(Exception e){
        return e.getMessage().contains("Unknown database");
    }

}
