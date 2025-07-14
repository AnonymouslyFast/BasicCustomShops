package com.anonymouslyfast.basicCustomShop.listeners;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.shop.ShopAdmin;
import com.anonymouslyfast.basicCustomShop.shop.PlayerTracking;
import com.anonymouslyfast.basicCustomShop.shop.Product;
import com.anonymouslyfast.basicCustomShop.shop.ShopManager;
import com.anonymouslyfast.basicCustomShop.tools.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class ProductCreatorListener implements Listener {

    private final ShopManager shopManager = BasicCustomShops.getInstance().shopManager;

    private final static HashMap<UUID, Integer> stages = new HashMap<>();

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (PlayerTracking.getPlayerStatus(e.getPlayer().getUniqueId()) != PlayerTracking.PlayerStatus.CREATINGPRODUCT)
            return;

        Player player = e.getPlayer();
        ShopAdmin shopAdmin = shopManager.getAdminByUUID(player.getUniqueId());
        if (shopAdmin == null) return;
        if (shopAdmin.getProduct() != null) return;

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            e.setCancelled(true);
            if (e.getItem() == null) {
                player.sendMessage(Messages.getMessage("&cYou need to be holding a item in your hand."));
                return;
            }
            ItemStack item = e.getItem();
            Product product = new Product(item.getType());
            shopAdmin.setProduct(product);
            stages.put(player.getUniqueId(), 1);
            player.sendMessage(Messages.getMessage(" "));
            player.sendMessage(Messages.getMessage("&fYour current product is &a" + product.getMaterial() +
                    "&f. Please send a number to set as the buy price &7(You can send 0 for it to be free.)"));
            player.sendMessage(Messages.getMessage("&fSend &7`cancel` &for &7`exit` &fto exit out of this product creator."));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (PlayerTracking.getPlayerStatus(e.getPlayer().getUniqueId()) != PlayerTracking.PlayerStatus.CREATINGPRODUCT)
            return;

        Player player = e.getPlayer();
        ShopAdmin shopAdmin = shopManager.getAdminByUUID(player.getUniqueId());
        if (shopAdmin == null) return;
        if (shopAdmin.getProduct() == null) return;
        e.setCancelled(true);
        if (e.getMessage().equalsIgnoreCase("cancel") || e.getMessage().equalsIgnoreCase("exit")) {
            PlayerTracking.removePlayer(player.getUniqueId());
            stages.remove(player.getUniqueId());
            shopManager.removeAdmin(shopAdmin);
            player.sendMessage(Messages.getMessage("&aRemoved you from the product creator."));
        } else {
            if (stages.get(player.getUniqueId()) == 1) { // Setting Buy price
                String message = e.getMessage();
                try {
                    double parsedMessage = Double.parseDouble(message);
                    if (parsedMessage <= 0) {
                        player.sendMessage(Messages.getMessage("&cPlease enter a number that's greater than 0."));
                        return;
                    }
                    shopAdmin.getProduct().setPrice(parsedMessage);
                    player.sendMessage(Messages.getMessage(" "));
                    player.sendMessage(Messages.getMessage("&fYour current price is &2&l$&a" + shopAdmin.getProduct().getPrice()
                            + "&f. Please send a sell price, or send &7`complete` &f to complete the creation process."));
                    player.sendMessage(Messages.getMessage("&fSend &7`cancel` &for &7`exit` &fto exit out of this product creator."));
                    stages.remove(player.getUniqueId());
                    stages.put(player.getUniqueId(), 2);
                } catch (NumberFormatException ex) {
                    player.sendMessage(Messages.getMessage("&cPlease enter a number to set the buy price of this item, or enter &7`cancel` &c or &7`exit` &cto leave this creator."));
                }
            } else if (stages.get(player.getUniqueId()) == 2) {
                if (e.getMessage().equalsIgnoreCase("complete")) {
                    complete(player);
                } else {
                    String message = e.getMessage();
                    try {
                        double parsedMessage = Double.parseDouble(message);
                        if (parsedMessage <= 0) {
                            player.sendMessage(Messages.getMessage("&cPlease enter a number that's greater than 0."));
                            return;
                        }
                        shopAdmin.getProduct().setSellPrice(parsedMessage);
                        complete(player);
                    } catch (NumberFormatException ex) {
                        player.sendMessage(Messages.getMessage("&cPlease enter a number to set the sell price of this item, or enter &7`cancel` &c or &7`exit` &cto leave this creator."));
                    }
                }

            } else { e.setCancelled(false); }
        }
    }

    private void complete(Player player) {
        ShopAdmin shopAdmin = shopManager.getAdminByUUID(player.getUniqueId());
        Product product = shopAdmin.getProduct();

        player.sendMessage(Messages.getMessage("&aCreated a new product for shop &f" + shopAdmin.getShop().getName())
                + "\n&a" +  product.getMaterial() + "\n  &7Buy: " + product.getPrice() + "\n  &7Sell: " + product.getSellPrice()
        );

       shopAdmin.getShop().addProduct(product);

        stages.remove(player.getUniqueId());
        shopManager.removeAdmin(shopAdmin);
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        stages.remove(e.getPlayer().getUniqueId());
    }


}
