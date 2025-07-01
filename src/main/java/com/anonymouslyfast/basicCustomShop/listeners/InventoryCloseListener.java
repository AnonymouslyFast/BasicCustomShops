package com.anonymouslyfast.basicCustomShop.listeners;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.shop.Customer;
import com.anonymouslyfast.basicCustomShop.shop.ProductTransactionHandler;
import com.anonymouslyfast.basicCustomShop.shop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Customer customer = Shop.getCustomer(player.getUniqueId());
        if (customer == null) return;
        if (customer.isSwitchingInventory()) {customer.setSwitchingInventory(false); return;}
        if (ProductTransactionHandler.containsPlayer(player.getUniqueId())) return;
        BasicCustomShops.getInstance().getLogger().info("removed on closed!");
        Shop.removeCustomer(customer);
    }

}
