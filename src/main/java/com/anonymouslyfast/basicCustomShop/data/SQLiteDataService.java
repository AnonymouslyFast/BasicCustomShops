package com.anonymouslyfast.basicCustomShop.data;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.hooks.SQLiteHook;
import com.anonymouslyfast.basicCustomShop.shop.Product;
import com.anonymouslyfast.basicCustomShop.shop.Shop;
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
    public void loadShops() {
        List<Shop> shops = new ArrayList<>();
        try {
            // Getting all subshop indexes and looping through them.
            ResultSet shopResults = sqLiteHook.getConnection().prepareStatement("SELECT * FROM shops").executeQuery();
            while (shopResults.next()) {
                String shopName = shopResults.getString("name");
                boolean isEnabled = shopResults.getBoolean("is_enabled");
                UUID shopUuid = UUID.fromString(shopResults.getString("uuid"));
                Material shopIcon = Material.getMaterial(shopResults.getString("icon_material"));

                Shop shop = new Shop(shopName, shopIcon, shopUuid);
                shop.setEnabled(isEnabled);

                // Getting all products of the subshop, and looping through them.
                PreparedStatement statement =
                        sqLiteHook.getConnection().prepareStatement("SELECT * FROM products WHERE shop_uuid = ?");
                statement.setString(1, shopUuid.toString());
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
            shopResults.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load shops.", e);
            return;
        }
        plugin.shopManager.setShops(shops);
        plugin.getLogger().info("Loaded " + shops.size() + " shops from database.");
    }

    @Override
    public void saveShops() {
        for (Shop shop : plugin.shopManager.getShops()) {
            boolean completed = saveShop(shop);
            if (completed) continue;
            plugin.getLogger().warning("Could not save shop: " + shop.getName());
        }
        plugin.getLogger().info("Saved " + plugin.shopManager.getShops().size() + " shops to database.");
    }

    @Override
    public boolean saveShop(Shop shop) {
        try {
            // Saving Shop to table shops
            PreparedStatement statement = sqLiteHook.getConnection().prepareStatement(
                    "INSERT OR REPLACE INTO shops(name, is_enabled, uuid, icon_material) VALUES(?, ?, ?, ?)"
            );

            statement.setString(1, shop.getName());
            statement.setBoolean(2, shop.isEnabled());
            statement.setString(3, shop.getUuid().toString());
            statement.setString(4, shop.getIcon().toString());
            statement.execute();
            statement.close();


            // Saving Products to table products
            statement = sqLiteHook.getConnection().prepareStatement(
                    "INSERT OR REPLACE INTO products(shop_uuid, uuid, material, buy_price, sell_price) VALUES (?, ?, ?, ?, ?)"
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
           plugin.getLogger().log(Level.WARNING, "Failed to load save shops.", e);
        }
        return false;
    }

    @Override
    public void removeShop(Shop shop) {
        try {
            // Deleting the shop
            PreparedStatement preparedStatement =  sqLiteHook.getConnection().prepareStatement(
                    "DELETE FROM shops WHERE uuid = ?"
            );
            preparedStatement.setString(1, shop.getUuid().toString());
            preparedStatement.execute();
            preparedStatement.close();

            // Deleting the products
            preparedStatement =  sqLiteHook.getConnection().prepareStatement(
                    "DELETE FROM products WHERE shop_uuid = ?"
            );
            preparedStatement.setString(1, shop.getUuid().toString());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException exception) {
            plugin.getLogger().log(
                    Level.WARNING,
                    "Failed to delete shop:" + shop.getName() + ".",
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
