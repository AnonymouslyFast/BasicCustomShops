package com.anonymouslyfast.basicCustomShop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class Customer {

    private final Player player;
    private Inventory inventory;
    private Inventory previousInventory;

    private Integer page;

    private final HashMap<Integer, SubShop> subShopSlots = new HashMap<>();


    public Customer(Player player) {
        this.player = player;
        this.inventory = Shop.getShopInventory(player);
    }

    public Customer(Player player, Inventory inventory) {
        this.player = player;
        this.inventory = inventory;
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void switchInventory(Inventory inventory) {
        this.inventory = inventory;
        previousInventory = this.inventory;
        player.openInventory(inventory);
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
