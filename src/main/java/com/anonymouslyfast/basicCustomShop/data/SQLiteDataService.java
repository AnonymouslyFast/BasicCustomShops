package com.anonymouslyfast.basicCustomShop.data;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.hooks.SQLiteHook;
import com.anonymouslyfast.basicCustomShop.shop.Product;
import com.anonymouslyfast.basicCustomShop.shop.Shop;
import com.anonymouslyfast.basicCustomShop.shop.ShopManager;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class SQLiteDataService implements DataService {

    private final SQLiteHook sqLiteHook;
    private final BasicCustomShops plugin;

    public SQLiteDataService(BasicCustomShops plugin, SQLiteHook sqLiteHook) {
        this.plugin = plugin;
        this.sqLiteHook = sqLiteHook;
        sqLiteHook.init(plugin);
        // Checking if it failed
        if (sqLiteHook.failedSetup()) {
            plugin.getLogger().severe("Something went wrong initializing SQLite hook!");
            PluginManager pm = plugin.getServer().getPluginManager();
            pm.disablePlugin(plugin);
        }
    }

    @Override
    public void loadSubShops() {
        List<Shop> shops = new ArrayList<>();
        try {
            // Getting all subshop indexes and looping through them.
            ResultSet subShopResults = sqLiteHook.getConnection().prepareStatement("SELECT * FROM subshops").executeQuery();
            while (subShopResults.next()) {
                String subShopName = subShopResults.getString("name");
                UUID subShopUuid = UUID.fromString(subShopResults.getString("uuid"));
                Material subShopIcon = Material.getMaterial(subShopResults.getString("icon_material"));

                Shop shop = new Shop(subShopName, subShopIcon, subShopUuid);

                // Getting all products of the subshop, and looping through them.
                PreparedStatement statement =
                        sqLiteHook.getConnection().prepareStatement("SELECT * FROM products WHERE subshop_uuid = ?");
                statement.setString(1, subShopUuid.toString());
                ResultSet resultSet = statement.executeQuery();

                List<Product> products = new ArrayList<>();
                while (resultSet.next()) {
                    Material material = Material.getMaterial(resultSet.getString("material"));
                    double buy_price = resultSet.getDouble("buy_price");
                    Double sell_price = resultSet.getDouble("sell_price");
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    products.add(new Product(material, buy_price, sell_price, uuid));
                }
                shop.setProducts(products);
                resultSet.close();
                shops.add(shop);
            }
            subShopResults.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load subshops.", e);
            return;
        }
        plugin.shopManager.setShops(shops);
        plugin.getLogger().info("Loaded " + shops.size() + " subshops from database.");
    }

    @Override
    public void saveSubShops() {
        for (Shop shop : plugin.shopManager.getShops()) {
            boolean completed = saveSubShop(shop);
            if (completed) continue;
            plugin.getLogger().warning("Could not save subshop: " + shop.getName());
        }
        plugin.getLogger().info("Saved " + plugin.shopManager.getShops().size() + " subshops to database.");
    }

    @Override
    public boolean saveSubShop(Shop shop) {
        try {
            // Saving Shop to table subshops
            PreparedStatement statement = sqLiteHook.getConnection().prepareStatement(
                    "INSERT OR REPLACE INTO subshops(name, uuid, icon_material) VALUES(?, ?, ?)"
            );

            statement.setString(1, shop.getName());
            statement.setString(2, shop.getUuid().toString());
            statement.setString(3, shop.getIcon().toString());
            statement.execute();
            statement.close();


            // Saving Products to table products
            statement = sqLiteHook.getConnection().prepareStatement(
                    "INSERT OR REPLACE INTO products(subshop_uuid, uuid, material, buy_price, sell_price) VALUES (?, ?, ?, ?, ?)"
            );
            statement.setString(1, shop.getUuid().toString());

            for (Product product : shop.getProducts()) {
                statement.setString(2, product.getUuid().toString());
                statement.setString(3, product.getMaterial().toString());
                statement.setDouble(4, product.getPrice());
                if (product.getSellPrice() != null) statement.setDouble(5, product.getSellPrice());
                statement.execute();
            }
            statement.close();
            return true;
        } catch (SQLException e) {
           plugin.getLogger().log(Level.WARNING, "Failed to load save subshops.", e);
        }
        return false;
    }

    @Override
    public void removeSubShop(Shop shop) {
        try {
            // Deleting the subshop
            PreparedStatement preparedStatement =  sqLiteHook.getConnection().prepareStatement(
                    "DELETE FROM subshops WHERE uuid = ?"
            );
            preparedStatement.setString(1, shop.getUuid().toString());
            preparedStatement.execute();
            preparedStatement.close();

            // Deleting the products
            preparedStatement =  sqLiteHook.getConnection().prepareStatement(
                    "DELETE FROM products WHERE subshop_uuid = ?"
            );
            preparedStatement.setString(1, shop.getUuid().toString());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException exception) {
            plugin.getLogger().log(
                    Level.WARNING,
                    "Failed to delete subshop:" + shop.getName() + ".",
                    exception
            );
        }
    }

    @Override
    public void removeProduct(Product product) {
        try {
            PreparedStatement preparedStatement =  sqLiteHook.getConnection().prepareStatement(
                    "DELETE FROM products WHERE uuid = ?"
            );
            preparedStatement.setString(1, product.getUuid().toString());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException exception) {
            plugin.getLogger().log(
                    Level.WARNING,
                    "Failed to delete product:" + product.getMaterial() + ".",
                    exception
            );
        }
    }
}
