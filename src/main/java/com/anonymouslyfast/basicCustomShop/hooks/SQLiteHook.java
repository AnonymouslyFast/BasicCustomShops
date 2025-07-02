package com.anonymouslyfast.basicCustomShop.hooks;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteHook {

    private final static BasicCustomShops basicCustomShops = BasicCustomShops.getInstance();

    private static boolean failedSetup;

    private static Connection connection;

    // Getting/Creating Database, Connection, and Tables
    public static void Init() {
        basicCustomShops.getLogger().info("Initializing SQLite");
        try {
            File dbFile = new File(BasicCustomShops.getInstance().getDataFolder() + "/database.db");

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getPath());
            Statement statement = connection.createStatement();

            String subshopQuery = """
                CREATE TABLE IF NOT EXISTS `subshops` (
                    name TEXT NOT NULL,
                    uuid TEXT NOT NULL PRIMARY KEY,
                    icon_material TEXT NOT NULL
                );""";
            String productsQuery = """
                CREATE TABLE IF NOT EXISTS `products` (
                    subshop_uuid TEXT NOT NULL,
                    uuid TEXT NOT NULL PRIMARY KEY,
                    material TEXT NOT NULL,
                    buy_price REAL NOT NULL,
                    sell_price REAL,
                    FOREIGN KEY(subshop_uuid) REFERENCES subshops(uuid)
                );""";

            statement.executeUpdate(subshopQuery);
            statement.executeUpdate(productsQuery);
            statement.closeOnCompletion();
        } catch (SQLException e) {
            basicCustomShops.getLogger().severe("FAILED INITIALIZING DATABASE");
            failedSetup = true;
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static boolean failedSetup() {
        return failedSetup;
    }

}
