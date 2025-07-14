package com.anonymouslyfast.basicCustomShop.listeners;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.shop.ShopAdmin;
import com.anonymouslyfast.basicCustomShop.shop.PlayerTracking;
import com.anonymouslyfast.basicCustomShop.shop.Shop;
import com.anonymouslyfast.basicCustomShop.shop.ShopManager;
import com.anonymouslyfast.basicCustomShop.tools.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ShopCreatorListener implements Listener {

    private final ShopManager shopManager = BasicCustomShops.getInstance().shopManager;

    private boolean playerIsNotCreatingShop(UUID playerUUID) {
        return PlayerTracking.getPlayerStatus(playerUUID) != PlayerTracking.PlayerStatus.CREATINGSHOP;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (playerIsNotCreatingShop(e.getPlayer().getUniqueId())) return;

        Player player = e.getPlayer();
        ShopAdmin shopAdmin = shopManager.getAdminByUUID(player.getUniqueId());
        if (shopAdmin == null) return;
        if (shopAdmin.getShopName() == null) return;

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            e.setCancelled(true);
            if (e.getItem() == null) {
                player.sendMessage(Messages.getMessage("&cYou need to be holding a item in your hand."));
                return;
            }

            ItemStack item = e.getItem();
            Shop shop = new Shop(shopAdmin.getShopName(), item.getType());
            shopManager.addShop(shop);
            shopManager.removeAdmin(shopAdmin);
            PlayerTracking.removePlayer(player.getUniqueId());
            player.sendMessage(Messages.getMessage("&aCreated the shop &f" + shop.getName() + "&a."));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (playerIsNotCreatingShop(e.getPlayer().getUniqueId())) return;

        Player player = e.getPlayer();
        ShopAdmin shopAdmin = shopManager.getAdminByUUID(player.getUniqueId());
        if (shopAdmin == null) return;
        if (shopAdmin.getShopName() == null) return;
        if (e.getMessage().equalsIgnoreCase("cancel") || e.getMessage().equalsIgnoreCase("exit")) {
            e.setCancelled(true);
            shopManager.removeAdmin(shopAdmin);
            PlayerTracking.removePlayer(player.getUniqueId());
            player.sendMessage(Messages.getMessage("&aRemoved you from the shop creator."));
        }
    }

}
