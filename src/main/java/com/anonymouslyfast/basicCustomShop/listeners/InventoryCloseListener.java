package com.anonymouslyfast.basicCustomShop.listeners;

import com.anonymouslyfast.basicCustomShop.Customer;
import com.anonymouslyfast.basicCustomShop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Customer customer = Shop.getCustomer(player);
        if (customer == null) return;
        Shop.removeCustomer(customer);
    }

}
