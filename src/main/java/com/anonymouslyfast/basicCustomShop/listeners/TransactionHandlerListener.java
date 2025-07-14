package com.anonymouslyfast.basicCustomShop.listeners;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.shop.Customer;
import com.anonymouslyfast.basicCustomShop.shop.PlayerTracking;
import com.anonymouslyfast.basicCustomShop.shop.ShopManager;
import com.anonymouslyfast.basicCustomShop.shop.TransactionHandler;
import com.anonymouslyfast.basicCustomShop.tools.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import javax.annotation.Nullable;

public class TransactionHandlerListener implements Listener {

    private final BasicCustomShops instance = BasicCustomShops.getInstance();

    private final ShopManager shopManager = instance.shopManager;

    @Nullable
    private Integer parseMessage(Player player, String message) {
        try {
            if (Double.parseDouble(message) > 0) return Integer.parseInt(message);
        } catch (NumberFormatException ignored) {}
        player.sendMessage(Messages.getMessage("&cPlease enter a valid number above 0"));
        return null;
    }

    private boolean triedToCancel(Player player, String message) {
        if (message.equalsIgnoreCase("cancel") || message.equalsIgnoreCase("exit")) {
            Customer customer = shopManager.getCustomerByUUID(player.getUniqueId());
            player.openInventory(customer.getInventory());
            player.sendMessage(Messages.getMessage("&cYou have cancelled the transaction."));
            PlayerTracking.updatePlayerStatus(player.getUniqueId(), PlayerTracking.PlayerStatus.INSHOPGUI);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        Customer customer = shopManager.getCustomerByUUID(player.getUniqueId());
        if (customer == null) return;

        // Buying Multiple
        if (PlayerTracking.getPlayerStatus(player.getUniqueId()) ==  PlayerTracking.PlayerStatus.BUYINGMULTIPLE) {
            e.setCancelled(true);
            if (triedToCancel(player, e.getMessage())) return;

            Integer parsedMessage = parseMessage(player, e.getMessage());
            if  (parsedMessage == null) return;

            TransactionHandler.buy(customer, customer.getProductInCart(), parsedMessage);
            Bukkit.getScheduler().callSyncMethod(instance, () -> player.openInventory(customer.getInventory()));
            PlayerTracking.updatePlayerStatus(player.getUniqueId(), PlayerTracking.PlayerStatus.INSHOPGUI);

        // Selling
        } else if (PlayerTracking.getPlayerStatus(player.getUniqueId()) ==  PlayerTracking.PlayerStatus.SELLING) {
            e.setCancelled(true);
            if (triedToCancel(player, e.getMessage())) return;

            Integer parsedMessage = parseMessage(player, e.getMessage());
            if  (parsedMessage == null) return;

            TransactionHandler.sell(customer, customer.getProductInCart(), parsedMessage);
            Bukkit.getScheduler().callSyncMethod(instance, () -> player.openInventory(customer.getInventory()));
            PlayerTracking.updatePlayerStatus(player.getUniqueId(), PlayerTracking.PlayerStatus.INSHOPGUI);
        }
    }


}
