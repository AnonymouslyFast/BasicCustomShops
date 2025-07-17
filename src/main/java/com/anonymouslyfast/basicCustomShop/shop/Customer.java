package com.anonymouslyfast.basicCustomShop.shop;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public class Customer {

    private final UUID playerUUID;
    private Inventory inventory;

    private Product productInCart;

    private Integer page;

    private Shop openedShop;

    private final HashMap<Integer, String> shopSlots = new HashMap<>();
    private final HashMap<Integer, Product> productSlots = new HashMap<>();


    public Customer(UUID playerUUID, PlayerTracking.PlayerStatus playerStatus) {
        this.playerUUID = playerUUID;
        PlayerTracking.addPlayer(playerUUID, playerStatus);
    }

    public Customer(UUID playerUUID, Inventory inventory, PlayerTracking.PlayerStatus playerStatus) {
        this.playerUUID = playerUUID;
        this.inventory = inventory;
        PlayerTracking.addPlayer(playerUUID, playerStatus);
    }



    public void switchInventory(Inventory inventory) {
        this.inventory = inventory;
        PlayerTracking.updatePlayerStatus(playerUUID, PlayerTracking.PlayerStatus.SWITCHINGINVENTORY);
        Bukkit.getScheduler().callSyncMethod(BasicCustomShops.getInstance(), () -> Bukkit.getPlayer(playerUUID).openInventory(inventory));
    }

    public boolean isSwitchingInventory() {
        return PlayerTracking.getPlayerStatus(playerUUID) == PlayerTracking.PlayerStatus.SWITCHINGINVENTORY;
    }


    public void addShopSlot(Integer slot, String shopName) {
        shopSlots.put(slot, shopName);
    }

    public void clearSubShopSlots() {
        shopSlots.clear();
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setOpenedShop(@Nullable Shop openedShop) {
        this.openedShop = openedShop;
    }

    public void addProductSlot(Integer slot, Product product) {
        productSlots.put(slot, product);
    }

    public void clearProductSlots() {
        productSlots.clear();
    }

    public void setProductInCart(Product productInCart) {
        this.productInCart = productInCart;
    }

    public void clearCart() {
        this.productInCart = null;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }
    public Inventory getInventory() {
        return inventory;
    }
    public Shop getShopFromSlot(Integer slot) { return BasicCustomShops.getInstance().shopManager.getShopFromName(shopSlots.get(slot)); }
    public Integer getPage() { return page; }
    public Shop getOpenedShop() {
        return openedShop;
    }
    public HashMap<Integer, Product> getProductSlots() {
        return productSlots;
    }
    public Product getProductInCart() {
        return productInCart;
    }
}
