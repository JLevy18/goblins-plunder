package com.levthedev.mc.managers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;

import com.levthedev.mc.GoblinsPlunder;
import com.levthedev.mc.coordinators.DatabaseCoordinator;
import com.levthedev.mc.dao.Plunder;
import com.levthedev.mc.dao.PlunderState;
import com.levthedev.mc.utility.PlunderCallback;
import com.levthedev.mc.utility.Serializer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseManager {
    
    private static final GoblinsPlunder plugin = GoblinsPlunder.getInstance();
    private static DatabaseManager instance;
    private Logger logger = plugin.getLogger();
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

            if (plugin.getConfig().getString("datasource.databaseName").isEmpty()){
                setupDataSource(plugin.getConfig());
            } else { // User provided a databaseName
                setupDataSourceCustom(plugin.getConfig());
                createTables();
            }


        } catch (Exception e) {

            // If database not found, create it
            if (isDatabaseNotFound(e)){

                // Failed to find specified database - throw error
                if (!plugin.getConfig().getString("datasource.databaseName").isEmpty()){
                    throw new RuntimeException("Database connection failed", e);
                }

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

    private void setupDataSourceCustom(FileConfiguration pluginConfig) throws Exception{
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + pluginConfig.getString("datasource.host") + ":" + pluginConfig.getInt("datasource.port") + "/"+ pluginConfig.getString("datasource.databaseName"));
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
                           "location VARCHAR(255) NOT NULL," +
                           "world_name VARCHAR(255) NOT NULL," +
                           "ignore_restock BOOLEAN NOT NULL DEFAULT FALSE," +
                           "block_type VARCHAR(255) NOT NULL," +
                           "loot_table_key VARCHAR(255), " +
                           "contents BLOB, " +
                           "UNIQUE (location, world_name));";
            stmt.executeUpdate(query);
            
            logger.log(Level.INFO, this.getClass().getSimpleName() + ": Successfully created plunder_blocks table");
    
        } catch (SQLException e) {
            logger.log(Level.SEVERE, this.getClass().getSimpleName() + ": Failed to create plunder_blocks table", e);   
        }
    }

    private void createPlunderStateTable(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String query = "CREATE TABLE plunder_state (" +
                           "player_uuid VARCHAR(36) NOT NULL," +
                           "pb_id VARCHAR(36) NOT NULL," +
                           "world_name VARCHAR(255) NOT NULL," +
                           "ignore_restock BOOLEAN NOT NULL DEFAULT FALSE," +
                           "state BLOB NOT NULL," +
                           "PRIMARY KEY (player_uuid, pb_id)," +
                           "FOREIGN KEY (pb_id) REFERENCES plunder_blocks(id) ON DELETE CASCADE);";
            stmt.executeUpdate(query);

            logger.log(Level.INFO, this.getClass().getSimpleName() + ": Successfully created plunder_state table");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, this.getClass().getSimpleName() + ": Failed to create plunder_state table", e);        
        }
    }

    public void resetPlunderStateTableAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "DELETE FROM plunder_state WHERE ignore_restock = FALSE;";
    
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                stmt.executeUpdate(sql);

                logger.log(Level.INFO, this.getClass().getSimpleName() + ": Deleted all entries in plunder_state table | NOTE: only if (ignore_restock = false)");
    
            } catch (SQLException e) {
                logger.log(Level.SEVERE, this.getClass().getSimpleName() + ": Failed to delete data in plunder_state", e);
            }
        });
    }


    // ##########################
    // ##    DB CREATE/GET     ##
    // ##########################


    public void createPlunderDataAsync(String blockId, String worldName, boolean ignore_restock, String location, String blockType, String loot_table_key, byte[] contents, Player player) {


        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT INTO plunder_blocks (id, world_name, ignore_restock, location, block_type, loot_table_key, contents) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, blockId);
                stmt.setString(2, worldName);
                stmt.setBoolean(3, ignore_restock);
                stmt.setString(4, location);
                stmt.setString(5, blockType);
                stmt.setString(6, loot_table_key);
                stmt.setBytes(7, contents);
                stmt.executeUpdate();
                

                // Success Messages

                if (player != null) {
                    player.sendMessage(ConfigManager.getInstance().getPrefix() + ChatColor.DARK_GREEN + "Plunder successfully created:\n" + ChatColor.RESET + "" + ChatColor.GREEN + blockId);
                }

                if (ConfigManager.getInstance().isDebug()){
                    logger.log(Level.INFO, this.getClass().getSimpleName() + ": " + blockType + " added to plunder_blocks table \nblockId: " + blockId + "\nworld: " + worldName + "\nlocation: " + location + "\nlootTable: " + loot_table_key);

                } else {
                    logger.log(Level.INFO, this.getClass().getSimpleName() + ": " + blockType + " added to plunder_blocks table (" + blockId + ")"); 
                }

            } catch (SQLException e) {
                logger.log(Level.SEVERE, this.getClass().getSimpleName() + ": Failed to create data in plunder_blocks", e);
            }
        });

    }

    public void createPlunderStateAsync(String playerUuid, String pbId, String worldName, Boolean ignore_restock, ItemStack[] contents) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            // Database operation
            String sql = "INSERT INTO plunder_state (player_uuid, pb_id, world_name, ignore_restock, state) VALUES (?, ?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE state = ?;"; // Adjust SQL as needed
            
            // Serialize the state of the inventory
            byte[] state = null;
            try {
                state = Serializer.toBase64(contents);
            } catch (IOException e) {
                logger.log(Level.SEVERE, this.getClass().getSimpleName() + ": Failed to serialize inventory contents", e);
                return;
            }
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, playerUuid);
                stmt.setString(2, pbId);
                stmt.setString(3, worldName);
                stmt.setBoolean(4, ignore_restock != null ? ignore_restock : false);
                stmt.setBytes(5, state);
                stmt.setBytes(6, state); // For the ON DUPLICATE KEY clause
                stmt.executeUpdate();
                
                // Remove from open map after success
                PlunderManager.getInstance().removeOpenPlunder(UUID.fromString(playerUuid));

                if (ConfigManager.getInstance().isDebug()){
                    logger.log(Level.INFO, this.getClass().getSimpleName() + ": Saved interaction into plunder_state\nplayerUuid: " + playerUuid + "\nplunderBlockId: " + pbId);
                } 

            } catch (SQLException e) {

                // Couldn't save interaction, lock the chest.
                PlunderManager.getInstance().getPlunder(UUID.fromString(playerUuid)).setLocked(true);

                logger.log(Level.SEVERE, this.getClass().getSimpleName() + ": Failed to save interaction in plunder_state \n {\r\n   Player: " + Bukkit.getPlayer(UUID.fromString(playerUuid)).getName() + "("+ playerUuid +")" + ", \r\n   blockId: " + pbId + ", \r\n   worldName: " + worldName + ", \r\n   contents:" + formatItemStackArray(contents) + " }\nCause: ", e);
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
                    plunder = new Plunder(rs.getString("id"), rs.getString("location"), rs.getString("block_type"), rs.getString("loot_table_key"), rs.getString("world_name"), rs.getBoolean("ignore_restock"), rs.getBytes("contents"), null,false);
                }

            } catch (SQLException e) {
                logger.log(Level.SEVERE, this.getClass().getSimpleName() + ": Failed to fetch data in plunder_blocks", e);
            }



            // Callback to the main thread
            final Plunder response = plunder;
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
                    plunderState = new PlunderState(playerUuid, blockId, rs.getBytes("state"), rs.getBoolean("ignore_restock"));
                }
    
            } catch (SQLException e) {
                logger.log(Level.SEVERE, this.getClass().getSimpleName() + ": Failed to fetch data from plunder_state", e);
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

                // Success Messages
                if (ConfigManager.getInstance().isDebug()){
                    logger.log(Level.INFO, this.getClass().getSimpleName() + ": Deleted " + (index-1) + " entries from plunder_blocks table \n" + String.join(", " , blockIds));

                } else {
                    logger.log(Level.INFO, this.getClass().getSimpleName() + ": Deleted " + (index-1) + " entries from plunder_blocks table");
                }
    
            } catch (SQLException e) {
                logger.log(Level.SEVERE, this.getClass().getSimpleName() + ": Failed to delete data from plunder_blocks", e);
            }
        });
    }

    public void deletePlunderBlocksByWorldAsync(String worldName, CommandSender sender){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "DELETE FROM plunder_blocks WHERE world_name = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, worldName);
                stmt.executeUpdate();


                // Success Messages
                sender.sendMessage(ConfigManager.getInstance().getPrefix() + ChatColor.GREEN + "Deleted all entries in " + worldName);

                logger.log(Level.INFO, this.getClass().getSimpleName() + ": Deleted all entries for " + worldName + " in plunder_blocks table");
    
            } catch (SQLException e) {

                logger.log(Level.SEVERE, this.getClass().getSimpleName() + ": Failed to delete entries from plunder_blocks for world: " + worldName, e);
            }
        });
    }

    public void deletePlunderStateByWorldAsync(String worldName) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "DELETE FROM plunder_state WHERE world_name = ? AND ignore_restock = FALSE;";
    
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, worldName);
                stmt.executeUpdate();

                logger.log(Level.INFO, this.getClass().getSimpleName() + ": Deleted all entries for " + worldName + " in plunder_state table");
    
            } catch (SQLException e) {
                logger.log(Level.SEVERE, this.getClass().getSimpleName() + ": Failed to delete entries from plunder_state for world: " + worldName, e);
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

    public String formatItemStackArray(ItemStack[] items) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\r\n\r");
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item != null) {
                String itemName = item.getType().toString();
                int amount = item.getAmount();
                sb.append("      Position ").append(i).append(": ").append(itemName).append(" x").append(amount).append("\n");
            } else {
                sb.append("      Position ").append(i).append(": Empty\n");
            }
        }
        sb.append("   }\n");
        return sb.toString();
    }


    // ############################
    // ##   EXCEPTION HANDLING   ##
    // ############################

    private boolean isDatabaseNotFound(Exception e){
        return e.getMessage().contains("Unknown database");
    }

}
