package com.levthedev.mc.managers;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.levthedev.mc.GoblinsPlunder;
import com.levthedev.mc.coordinators.DatabaseCoordinator;
import com.levthedev.mc.dao.Plunder;
import com.levthedev.mc.utility.PlunderCallback;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseManager {
    
    private static GoblinsPlunder plugin = GoblinsPlunder.getInstance();
    private static DatabaseManager instance;
    private HikariDataSource dataSource;
    private final DatabaseCoordinator databaseCoordinator;


    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseManager not initialized");
        }

        return instance;
    }

    public DatabaseManager() {



        // Setup datasource 
        try {
            setupDataSource(plugin.getConfig());
        } catch (Exception e) {


            // If database not found, create it
            if (DatabaseNotFoundException(e)){
                createDatabase(plugin.getConfig());
                try {
                    setupDataSource(plugin.getConfig());
                    createTables();
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to establish a database connection after creation", ex);
                }
            } else {
                throw new RuntimeException("Database connection failed", e);
            }
        }
        

        // Setup coordinator
        this.databaseCoordinator = new DatabaseCoordinator(this, plugin);

    }


    public static void initialize() {
        instance = new DatabaseManager();
    }


    private void setupDataSource(FileConfiguration pluginConfig) throws Exception{
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + pluginConfig.getString("datasource.host") + ":" + pluginConfig.getInt("datasource.port") + "/goblins_plunder");
        config.setUsername(pluginConfig.getString("datasource.username"));
        config.setPassword(pluginConfig.getString("datasource.password"));

        this.dataSource = new HikariDataSource(config);
    }

        
    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }


    // ###########################
    // ##   GETTERS & SETTERS   ##
    // ###########################


    public DatabaseCoordinator getDatabaseCoordinator() {
        return databaseCoordinator;
    }


    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }


    // #############################
    // ##   DATABASE OPERATIONS   ##
    // #############################

    private void createDatabase(FileConfiguration pluginConfig) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://" + pluginConfig.getString("datasource.host") + ":" + pluginConfig.getInt("datasource.port"), pluginConfig.getString("datasource.username"), pluginConfig.getString("datasource.password"));
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS goblins_plunder");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create database", e);
        }


    }

    private void createTables() {
        try (Connection conn = dataSource.getConnection()) {
            if (!tableExists(conn, "plunder_blocks")) {
                createPlunderBlocksTable(conn);
            }

            if (!tableExists(conn, "plunder_state")) {
                createPlunderStateTable(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPlunderBlocksTable(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String query = "CREATE TABLE plunder_blocks (" +
                           "id VARCHAR(36) PRIMARY KEY," +
                           "location VARCHAR(255) NOT NULL UNIQUE," +
                           "block_type VARCHAR(255) NOT NULL," +
                           "loot_table_key VARCHAR(255), " +
                           "contents BLOB);";
            stmt.executeUpdate(query);
        }
    }

    private void createPlunderStateTable(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String query = "CREATE TABLE plunder_state (" +
                           "player_uuid VARCHAR(36) NOT NULL," +
                           "pb_id VARCHAR(36) NOT NULL," +
                           "PRIMARY KEY (player_uuid, pb_id)," +
                           "FOREIGN KEY (pb_id) REFERENCES plunder_blocks(id));";
            stmt.executeUpdate(query);
        }
    }


    // ##########################
    // ##   CRUD OPERATIONS    ##
    // ##########################


    public void createPlunderData(String blockId, String location, String blockType, String loot_table_key, Blob contents, Player player) {


        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT INTO plunder_blocks (id, location, block_type, loot_table_key, contents) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, blockId);
                stmt.setString(2, location);
                stmt.setString(3, blockType);
                stmt.setString(4, loot_table_key);
                stmt.setBlob(5, contents);
                stmt.executeUpdate();

                player.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "Plunder successfully created:\n" + ChatColor.RESET + "" + ChatColor.GREEN + blockId);
            } catch (SQLException e) {

                player.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_RED + "Database Error: " + ChatColor.RESET + "" + ChatColor.RED + e.getMessage());
            }
        });

    }

    public void getPlunderDataByIdAsync(String blockId, PlunderCallback callback) {


        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Plunder plunder = null;
            String sql = "SELECT * FROM plunder_blocks WHERE id = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, blockId);
                ResultSet rs = stmt.executeQuery();
        
                if (rs.next()) {
                    plunder = new Plunder(rs.getString("id"), rs.getString("location"), rs.getString("block_type"), rs.getString("loot_table_key"), rs.getBlob("contents"), "");
                }

            } catch (SQLException e) {
                String error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Database Error]: " + e.getMessage();
                plunder = new Plunder(null,null,null,null,null, error);
            }

            final Plunder response = plunder;
            Bukkit.getScheduler().runTask(GoblinsPlunder.getInstance(), () -> callback.onQueryFinish(response));

        });

    }
    

    // ############################
    // ##    HELPER FUNCTIONS    ##
    // ############################


    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        java.sql.DatabaseMetaData dbm = conn.getMetaData();
        try(ResultSet tables = dbm.getTables(null,null,tableName,null)) {
            return tables.next();
        }
    }


    // ############################
    // ##   EXCEPTION HANDLING   ##
    // ############################

    private boolean DatabaseNotFoundException(Exception e){
        return e.getMessage().contains("Unknown database");
    }

}
