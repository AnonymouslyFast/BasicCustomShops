package com.anonymouslyfast.basicCustomShop;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Customer {

    private final UUID playerUUID;
    private Inventory inventory;
    private Inventory previousInventory;

    private Integer page;

    private final HashMap<Integer, SubShop> subShopSlots = new HashMap<>();


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
        Objects.requireNonNull(Bukkit.getPlayer(playerUUID)).openInventory(inventory);
    }

    public SubShop getSubShopFromSlot(Integer slot) {
        return subShopSlots.get(slot);
    }

    public void addSubShopSlot(Integer slot, SubShop subShop) {
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
}
