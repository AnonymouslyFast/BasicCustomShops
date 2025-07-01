package com.anonymouslyfast.basicCustomShop.listeners;

import com.anonymouslyfast.basicCustomShop.shop.Customer;
import com.anonymouslyfast.basicCustomShop.shop.Shop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Customer customer = Shop.getCustomer(event.getPlayer().getUniqueId());
        if (customer == null) return;
        Shop.removeCustomer(customer);
    }
}
