package com.anonymouslyfast.basicCustomShop.data;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.hooks.SQLiteHook;
import com.anonymouslyfast.basicCustomShop.shop.Product;
import com.anonymouslyfast.basicCustomShop.shop.Shop;
import com.anonymouslyfast.basicCustomShop.shop.SubShop;
import org.bukkit.Material;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DataManager {

    private static boolean saveSubShop(SubShop subShop) {
        try {
            // Saving SubShop to table subshops
            PreparedStatement statement = SQLiteHook.getConnection().prepareStatement(
                    "INSERT OR REPLACE INTO subshops(name, uuid, icon_material) VALUES(?, ?, ?)"
            );

            statement.setString(1, subShop.getName());
            statement.setString(2, subShop.getUuid().toString());
            statement.setString(3, subShop.getIcon().toString());
            statement.execute();
            statement.close();


            // Saving Products to table products
            statement = SQLiteHook.getConnection().prepareStatement(
                    "INSERT OR REPLACE INTO products(subshop_uuid, uuid, material, buy_price, sell_price) VALUES (?, ?, ?, ?, ?)"
            );
            statement.setString(1, subShop.getUuid().toString());

            for (Product product : subShop.getProducts()) {
                statement.setString(2, product.getUuid().toString());
                statement.setString(3, product.getMaterial().toString());
                statement.setDouble(4, product.getPrice());
                if (product.getSellPrice() != null) statement.setDouble(5, product.getSellPrice());
                statement.execute();
            }
            statement.close();
            return true;
        } catch (SQLException e) {
            BasicCustomShops.getInstance().getLogger().log(Level.WARNING, "Failed to load save subshops.", e);
        }
        return false;
    }

    public static void saveSubShops() {
        // Looping through subshops and saving them.
        for (SubShop subShop : Shop.getSubShops()) {
            boolean completed = saveSubShop(subShop);
            if (completed) continue;
            BasicCustomShops.getInstance().getLogger().warning("Could not save subshop: " + subShop.getName());
        }
        BasicCustomShops.getInstance().getLogger().info("Saved " + Shop.getSubShops().size() + " subshops to database.");
    }


    public static void loadSubShops() {
        List<SubShop> subShops = new ArrayList<>();
        try {
            // Getting all subshop indexes and looping through them.
            ResultSet subShopResults = SQLiteHook.getConnection().prepareStatement("SELECT * FROM subshops").executeQuery();
            while (subShopResults.next()) {
                String subShopName = subShopResults.getString("name");
                UUID subShopUuid = UUID.fromString(subShopResults.getString("uuid"));
                Material subShopIcon = Material.getMaterial(subShopResults.getString("icon_material"));

                SubShop subShop = new SubShop(subShopName, subShopIcon, subShopUuid);

                // Getting all products of the subshop, and looping through them.
                PreparedStatement statement =
                        SQLiteHook.getConnection().prepareStatement("SELECT * FROM products WHERE subshop_uuid = ?");
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
                subShop.setProducts(products);
                resultSet.close();
                subShops.add(subShop);
            }
            subShopResults.close();
        } catch (SQLException e) {
            BasicCustomShops.getInstance().getLogger().log(Level.WARNING, "Failed to load subshops.", e);
            return;
        }
        Shop.setSubShops(subShops);
        BasicCustomShops.getInstance().getLogger().info("Loaded " + Shop.getSubShops().size() + " subshops from database.");
    }

    public static void removeSubShop(SubShop subShop) {
        try {
            // Deleting the subshop
            PreparedStatement preparedStatement =  SQLiteHook.getConnection().prepareStatement(
                    "DELETE FROM subshops WHERE uuid = ?"
            );
            preparedStatement.setString(1, subShop.getUuid().toString());
            preparedStatement.execute();
            preparedStatement.close();

            // Deleting the products
            preparedStatement =  SQLiteHook.getConnection().prepareStatement(
                    "DELETE FROM products WHERE subshop_uuid = ?"
            );
            preparedStatement.setString(1, subShop.getUuid().toString());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException exception) {
            BasicCustomShops.getInstance().getLogger().log(Level.WARNING,
                    "Failed to delete subshop:" + subShop.getName() + ".",
                    exception
            );
        }
    }

    public static void removeProduct(Product product) {
        try {
            PreparedStatement preparedStatement =  SQLiteHook.getConnection().prepareStatement(
                    "DELETE FROM products WHERE uuid = ?"
            );
            preparedStatement.setString(1, product.getUuid().toString());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException exception) {
            BasicCustomShops.getInstance().getLogger().log(Level.WARNING,
                    "Failed to delete product:" + product.getMaterial() + ".",
                    exception
            );
        }

    }


}
