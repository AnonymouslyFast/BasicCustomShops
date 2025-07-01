package com.anonymouslyfast.basicCustomShop.shop;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Customer {

    private final UUID playerUUID;
    private Inventory inventory;
    private Inventory previousInventory;


    private Integer page;

    private SubShop openedSubShop;

    private final HashMap<Integer, String> subShopSlots = new HashMap<>();
    private final HashMap<Integer, Product> productSlots = new HashMap<>();

    private boolean isSwitchingInventory = false;


    public Customer(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public Customer(UUID playerUUID, Inventory inventory) {
        this.playerUUID = playerUUID;
        this.inventory = inventory;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void switchInventory(Inventory inventory) {
        this.inventory = inventory;
        previousInventory = this.inventory;
        isSwitchingInventory = true;
        Bukkit.getScheduler().callSyncMethod(BasicCustomShops.getInstance(), () -> Objects.requireNonNull(Bukkit.getPlayer(playerUUID)).openInventory(inventory));
    }

    public SubShop getSubShopFromSlot(Integer slot) {
        return Shop.getSubShopFromName(subShopSlots.get(slot));
    }

    public void addSubShopSlot(Integer slot, String subShop) {
        subShopSlots.put(slot, subShop);
    }

    public void clearSubShopSlots() {
        subShopSlots.clear();
    }

    public Inventory getPreviousInventory() {
        return previousInventory;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public SubShop getOpenedSubShop() {
        return openedSubShop;
    }

    public void setOpenedSubShop(@Nullable SubShop openedSubShop) {
        this.openedSubShop = openedSubShop;
    }

    public HashMap<Integer, Product> getProductSlots() {
        return productSlots;
    }

    public void addProductSlot(Integer slot, Product product) {
        productSlots.put(slot, product);
    }

    public void clearProductSlots() {
        productSlots.clear();
    }

    public boolean isSwitchingInventory() {
        return isSwitchingInventory;
    }
    public void setSwitchingInventory(boolean isSwitchingInventory) {
        this.isSwitchingInventory = isSwitchingInventory;
    }
}
