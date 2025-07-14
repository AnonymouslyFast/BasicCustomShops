package com.anonymouslyfast.basicCustomShop.listeners;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.shop.Customer;
import com.anonymouslyfast.basicCustomShop.shop.PlayerTracking;
import com.anonymouslyfast.basicCustomShop.shop.ShopManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    private final ShopManager shopManager =  BasicCustomShops.getInstance().shopManager;

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Customer customer = shopManager.getCustomerByUUID(event.getPlayer().getUniqueId());
        if (customer == null) return;
        PlayerTracking.PlayerStatus status = PlayerTracking.getPlayerStatus(customer.getPlayerUUID());
        if (customer.isSwitchingInventory()) {
            if (customer.getOpenedShop() == null) {
                PlayerTracking.updatePlayerStatus(customer.getPlayerUUID(), PlayerTracking.PlayerStatus.INMAINSHOPGUI);
            } else {
                PlayerTracking.updatePlayerStatus(customer.getPlayerUUID(), PlayerTracking.PlayerStatus.INSHOPGUI);
            }
            return;
        }
        if (status == PlayerTracking.PlayerStatus.BUYINGMULTIPLE ||
                status == PlayerTracking.PlayerStatus.SELLING) return;
        shopManager.removeCustomer(customer);
        PlayerTracking.removePlayer(event.getPlayer().getUniqueId());
    }
}
