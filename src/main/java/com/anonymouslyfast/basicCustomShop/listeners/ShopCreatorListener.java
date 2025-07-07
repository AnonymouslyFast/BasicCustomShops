package com.anonymouslyfast.basicCustomShop.listeners;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.shop.ShopAdmin;
import com.anonymouslyfast.basicCustomShop.shop.PlayerTracking;
import com.anonymouslyfast.basicCustomShop.shop.Shop;
import com.anonymouslyfast.basicCustomShop.tools.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ShopCreatorListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (PlayerTracking.getPlayerStatus(e.getPlayer().getUniqueId()) != PlayerTracking.PlayerStatus.CREATINGSHOP)
            return;

        Player player = e.getPlayer();
        ShopAdmin shopAdmin = BasicCustomShops.plugin.shopManager.getAdminByUUID(player.getUniqueId());
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
            BasicCustomShops.plugin.shopManager.addShop(shop);
            BasicCustomShops.plugin.shopManager.removeAdmin(shopAdmin);
            PlayerTracking.removePlayer(player.getUniqueId());
            player.sendMessage(Messages.getMessage("&aCreated the shop &f" + shop.getName() + "&a."));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (PlayerTracking.getPlayerStatus(e.getPlayer().getUniqueId()) != PlayerTracking.PlayerStatus.CREATINGSHOP)
            return;

        Player player = e.getPlayer();
        ShopAdmin shopAdmin = BasicCustomShops.plugin.shopManager.getAdminByUUID(player.getUniqueId());
        if (shopAdmin == null) return;
        if (shopAdmin.getShopName() == null) return;
        if (e.getMessage().equalsIgnoreCase("cancel") || e.getMessage().equalsIgnoreCase("exit")) {
            e.setCancelled(true);
            BasicCustomShops.plugin.shopManager.removeAdmin(shopAdmin);
            PlayerTracking.removePlayer(player.getUniqueId());
            player.sendMessage(Messages.getMessage("&aRemoved you from the shop creator."));
        }
    }

}
