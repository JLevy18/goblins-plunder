package com.levthedev.mc.managers;

import java.io.ObjectInputFilter.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;

import com.levthedev.mc.GoblinsPlunder;
import com.levthedev.mc.coordinators.DatabaseCoordinator;
import com.levthedev.mc.dao.PlunderDAO;
import com.levthedev.mc.dao.PlunderState;
import com.levthedev.mc.utility.PlunderCallback;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseManager {
    
    private static final GoblinsPlunder plugin = GoblinsPlunder.getInstance();
    private static DatabaseManager instance;
    private HikariDataSource dataSource;
    private DatabaseCoordinator databaseCoordinator;


    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseManager not initialized");
        }

        return instance;
    }

    private DatabaseManager() {

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

    }


    public static synchronized void initialize() {
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

    public void setDatabaseCoordinator(DatabaseCoordinator coordinator){
        this.databaseCoordinator = coordinator;
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
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create database tables", e);
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
                           "worldName VARCHAR(255) NOT NULL," +
                           "state BLOB NOT NULL," +
                           "PRIMARY KEY (player_uuid, pb_id)," +
                           "FOREIGN KEY (pb_id) REFERENCES plunder_blocks(id) ON DELETE CASCADE);";
            stmt.executeUpdate(query);
        }
    }

    public void resetPlunderStateTableAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "DELETE FROM plunder_state";
    
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                stmt.executeUpdate(sql);
                System.out.println("plunder_state table has been reset.");
    
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error resetting plunder_state table: " + e.getMessage());
            }
        });
    }


    // ##########################
    // ##    DB CREATE/GET     ##
    // ##########################


    public void createPlunderDataAsync(String blockId, String location, String blockType, String loot_table_key, byte[] contents, Player player) {


        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT INTO plunder_blocks (id, location, block_type, loot_table_key, contents) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, blockId);
                stmt.setString(2, location);
                stmt.setString(3, blockType);
                stmt.setString(4, loot_table_key);
                stmt.setBytes(5, contents);
                stmt.executeUpdate();
                
                if (player != null) {
                    player.sendMessage(ConfigManager.getInstance().getPrefix() + ChatColor.DARK_GREEN + "Plunder successfully created:\n" + ChatColor.RESET + "" + ChatColor.GREEN + blockId);
                }


                if (ConfigManager.getInstance().isDebug()){
                    System.out.println( "Plunder Added:\n"
                                        + blockId + "\n" 
                                        + loot_table_key + "\n"
                                        + location + "\n");
                }

            } catch (SQLException e) {
                if (player != null) {
                    player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.DARK_RED + "Database Error: " + ChatColor.RESET + "" + ChatColor.RED + e.getMessage());
                }
            }
        });

    }

    public void createPlunderStateAsync(String playerUuid, String pbId, String worldName, byte[] state){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            // Database operation
            String sql = "INSERT INTO plunder_state (player_uuid, pb_id, worldName, state) VALUES (?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE state = ?;"; // Adjust SQL as needed
    
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, playerUuid);
                stmt.setString(2, pbId);
                stmt.setString(3, worldName);
                stmt.setBytes(4, state);
                stmt.setBytes(5, state); // For the ON DUPLICATE KEY clause
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Failed to save interaction to the database");
            }
        });
    }

    public void getPlunderDataByIdAsync(String blockId, PlunderCallback callback) {


        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlunderDAO plunder = null;
            String sql = "SELECT * FROM plunder_blocks WHERE id = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, blockId);
                ResultSet rs = stmt.executeQuery();
        
                if (rs.next()) {
                    plunder = new PlunderDAO(rs.getString("id"), rs.getString("location"), rs.getString("block_type"), rs.getString("loot_table_key"), rs.getBytes("contents"), "");
                }

            } catch (SQLException e) {
                String error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Database Error]: " + e.getMessage();
                plunder = new PlunderDAO(null,null,null,null,null, error);
            }



            // Callback to the main thread

            final PlunderDAO response = plunder;
            Bukkit.getScheduler().runTask(GoblinsPlunder.getInstance(), () -> callback.onQueryFinish(response));

        });

    }
    
    public void getPlunderStateByIdAsync(UUID playerUuid, String blockId, Consumer<PlunderState> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlunderState plunderState = null;
            String sql = "SELECT * FROM plunder_state WHERE player_uuid = ? AND pb_id = ?";
    
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, playerUuid.toString());
                stmt.setString(2, blockId);
                ResultSet rs = stmt.executeQuery();
    
                if (rs.next()) {
                    byte[] stateData = rs.getBytes("state");
                    // Assuming PlunderState is a class you've created to hold this data
                    plunderState = new PlunderState(playerUuid, blockId, stateData);
                }
    
            } catch (SQLException e) {
                e.printStackTrace();
            }
    
            // Callback to the main thread
            final PlunderState response = plunderState;
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(response));
        });
    }

    public void deletePlunderBlocksByIdsAsync(List<String> blockIds) {
        if (blockIds == null || blockIds.isEmpty()) {
            return; // Nothing to delete
        }
    
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "DELETE FROM plunder_blocks WHERE id IN (" + String.join(",", Collections.nCopies(blockIds.size(), "?")) + ")";
    
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                int index = 1;
                for (String id : blockIds) {
                    stmt.setString(index++, id);
                }
                
                stmt.executeUpdate();
                System.out.println("Deleted " + blockIds.size() + " plunder_blocks entries.");
    
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error deleting plunder_blocks entries: " + e.getMessage());
            }
        });
    }

    public void deletePlunderStateByWorldAsync(String worldName) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "DELETE FROM plunder_state WHERE worldName = ?";
    
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, worldName);
                stmt.executeUpdate();
                System.out.println("plunder_state entries for world " + worldName + " have been deleted.");
    
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error deleting plunder_state entries for world " + worldName + ": " + e.getMessage());
            }
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
