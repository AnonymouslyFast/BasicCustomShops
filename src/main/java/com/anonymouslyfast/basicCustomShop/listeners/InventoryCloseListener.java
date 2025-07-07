package com.anonymouslyfast.basicCustomShop.listeners;

import com.anonymouslyfast.basicCustomShop.shop.Customer;
import com.anonymouslyfast.basicCustomShop.shop.PlayerTracking;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.UUID;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Customer customer = Shop.getCustomer(player.getUniqueId());
        if (customer == null) return;
        if (customer.isSwitchingInventory()) {

        }
        if (aProductTransactionHandler.containsPlayer(player.getUniqueId())) return;
        Shop.remaoveCustomer(customer);
    }

}
