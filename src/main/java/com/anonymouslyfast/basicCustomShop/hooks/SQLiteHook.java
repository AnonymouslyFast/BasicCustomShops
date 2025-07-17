package com.anonymouslyfast.basicCustomShop.hooks;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class SQLiteHook {

    private boolean failedSetup;

    private Connection connection;

    // Getting/Creating Database, Connection, and Tables
    public void init(BasicCustomShops plugin) {
        plugin.getLogger().info("Initializing SQLite");
        try {
            File dbFile = new File(BasicCustomShops.getInstance().getDataFolder() + "/database.db");

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getPath());
            Statement statement = connection.createStatement();

            String subshopQuery = """
                CREATE TABLE IF NOT EXISTS `shops` (
                    name TEXT NOT NULL,
                    is_enabled BOOLEAN NOT NULL,
                    uuid TEXT NOT NULL PRIMARY KEY,
                    icon_material TEXT NOT NULL
                );""";
            String productsQuery = """
                CREATE TABLE IF NOT EXISTS `products` (
                    shop_uuid TEXT NOT NULL,
                    uuid TEXT NOT NULL PRIMARY KEY,
                    material TEXT NOT NULL,
                    buy_price REAL NOT NULL,
                    sell_price REAL,
                    FOREIGN KEY(shop_uuid) REFERENCES shops(uuid)
                );""";

            statement.executeUpdate(subshopQuery);
            statement.executeUpdate(productsQuery);
            statement.closeOnCompletion();
        } catch (SQLException e) {
            failedSetup = true;
            plugin.getLogger().log(Level.SEVERE, "FAILED INITIALIZING DATABASE", e);
        }
    }

    public boolean failedSetup() {
        return failedSetup;
    }

    public Connection getConnection() {
        return connection;
    }

}
