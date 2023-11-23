package com.levthedev.mc.coordinators;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariDataSource;

public class DatabaseCoordinatorImpl implements DatabaseCoordinator {
    private final HikariDataSource dataSource;

    public DatabaseCoordinatorImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void setup() {
        
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

    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        java.sql.DatabaseMetaData dbm = conn.getMetaData();
        try(ResultSet tables = dbm.getTables(null,null,tableName,null)) {
            return tables.next();
        }
    }

    private void createPlunderBlocksTable(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String query = "CREATE TABLE plunder_blocks (" +
                           "id VARCHAR(36) PRIMARY KEY," +
                           "location VARCHAR(255) NOT NULL," +
                           "block_type VARCHAR(255) NOT NULL);";
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

}
