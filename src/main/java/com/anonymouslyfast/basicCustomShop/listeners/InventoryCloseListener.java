package com.anonymouslyfast.basicCustomShop.listeners;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.shop.Customer;
import com.anonymouslyfast.basicCustomShop.shop.PlayerTracking;
import com.anonymouslyfast.basicCustomShop.shop.ShopManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {

    private final ShopManager shopManager = BasicCustomShops.getInstance().shopManager;

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Customer customer = shopManager.getCustomerByUUID(player.getUniqueId());
        if (customer == null) return;
        if (PlayerTracking.getPlayerStatus(player.getUniqueId()) == PlayerTracking.PlayerStatus.BUYINGMULTIPLE ||
                PlayerTracking.getPlayerStatus(player.getUniqueId()) == PlayerTracking.PlayerStatus.SELLING) return;
        if (customer.isSwitchingInventory()) {
            PlayerTracking.PlayerStatus status;
            if (customer.getOpenedShop() != null) {
                status = PlayerTracking.PlayerStatus.INSHOPGUI;
            } else { status = PlayerTracking.PlayerStatus.INMAINSHOPGUI; }
            PlayerTracking.updatePlayerStatus(player.getUniqueId(), status);
            return;
        }

        if (PlayerTracking.getPlayerStatus(customer.getPlayerUUID()) == null) return;
        PlayerTracking.removePlayer(player.getUniqueId());
        shopManager.removeCustomer(customer);
    }

}
