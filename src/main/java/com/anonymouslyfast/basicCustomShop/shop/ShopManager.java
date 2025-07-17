package com.anonymouslyfast.basicCustomShop.shop;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.data.DataService;
import com.anonymouslyfast.basicCustomShop.utils.MessageUtils;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public final class ShopManager {

    private final DataService dataService;
    private final BasicCustomShops plugin;

    private List<Shop> shops = new ArrayList<>();
    private final HashMap<String, Shop> shopNames = new HashMap<>();

    private final HashMap<UUID, Customer> customers = new HashMap<>();
    private final HashMap<UUID, ShopAdmin> admins = new HashMap<>();

    public ShopManager(BasicCustomShops plugin, DataService dataService) {
        this.plugin = plugin;
        this.dataService = dataService;
    }

    public void reloadDataService() {
        dataService.saveShops();
    }

    public boolean isShopEnabled() { return plugin.getConfig().getBoolean("shop-enabled"); }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
        shopNames.clear();
        for (Shop shop : shops) {
            shopNames.put(shop.getName(), shop);
        }
    }


    public void addShop(Shop shop) {
        shops.add(shop);
        shopNames.put(shop.getName(), shop);
    }

    public void addCustomer(Customer customer) {
        customers.put(customer.getPlayerUUID(), customer);
    }

    public void addAdmin(ShopAdmin shopAdmin) { admins.put(shopAdmin.getPlayer().getUniqueId(), shopAdmin); }


    public void removeShop(Shop shop) {
        shops.remove(shop);
        dataService.removeShop(shop);
        shopNames.remove(shop.getName());
    }

    public void removeProduct(Shop shop, Product product) {
        shop.removeProduct(product);
        dataService.removeProduct(product);
    }

    public void removeCustomer(Customer customer) {
        customers.remove(customer.getPlayerUUID());
    }

    public void removeAdmin(ShopAdmin shopAdmin) { admins.remove(shopAdmin.getPlayer().getUniqueId()); }


    public List<Shop> getShops() { return shops; }
    public Shop getShopFromName(String shopName) {
        return shopNames.get(shopName);
    }
    public Customer getCustomerByUUID(UUID uuid) {
        return customers.get(uuid);
    }
    public ShopAdmin getAdminByUUID(UUID uuid) { return admins.get(uuid); }

    public String getInventoryName(@Nullable Shop shop, int page) {
        if (shop == null) { // When user wants to get the main shop name.
            return MessageUtils.convertCodes(plugin.getConfig().getString("shop-title")) + " &7(" + page + ")";
        }
        return shop.getName() + " &7(" + page + ")";
    }

    public Inventory getMainInventory(UUID uuid, int page) {
        ShopInventoryBuilder builder = new ShopInventoryBuilder(uuid, getInventoryName(null, page));
        return builder.buildMainInventory(shops, page);
    }





















}
