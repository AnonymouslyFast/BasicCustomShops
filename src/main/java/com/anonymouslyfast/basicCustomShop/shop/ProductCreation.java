package com.anonymouslyfast.basicCustomShop.shop;

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

public class ProductCreation implements Listener {

    private final static HashMap<Player, String> currentPlayers = new HashMap<>();
    private final static HashMap<Player, Product> currentProducts = new HashMap<>();
    private final static HashMap<Player, Integer> stages = new HashMap<>();

    public static void addPlayer(Player player, String subShop) {
        currentPlayers.put(player, subShop);
        stages.put(player, 1);
        player.sendMessage(Messages.getMessage("&fPlease &7right click while holding a item &fthat your would like to sell in this subshop"));
        player.sendMessage(Messages.getMessage("&fSend &7`cancel` &for &7`exit` &fto exit out of this product creator."));
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!currentPlayers.containsKey(player)) return;
        if (currentProducts.containsKey(player)) return;
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            e.setCancelled(true);
            if (e.getItem() == null) {
                player.sendMessage(Messages.getMessage("&cYou need to be holding a item in your hand."));
                return;
            }
            ItemStack item = e.getItem();
            Product product = new Product(item);
            currentProducts.put(player, product);
            stages.remove(player);
            stages.put(player, 2);
            player.sendMessage(Messages.getMessage(" "));
            player.sendMessage(Messages.getMessage("&fYour current product is &a" + product.getItem().getType() +
                    "&f. Please send a number to set as the buy price &7(You can send 0 for it to be free.)"));
            player.sendMessage(Messages.getMessage("&fSend &7`cancel` &for &7`exit` &fto exit out of this product creator."));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!currentPlayers.containsKey(player)) return;
        e.setCancelled(true);
        if (e.getMessage().equalsIgnoreCase("cancel") || e.getMessage().equalsIgnoreCase("exit")) {
            currentPlayers.remove(player);
            currentProducts.remove(player);
            stages.remove(player);
            player.sendMessage(Messages.getMessage("&aRemoved you from the product creator."));
        } else {
            if (stages.get(player) == 2) { // Setting Buy price
                String message = e.getMessage();
                try {
                    Double parsedMessage = Double.parseDouble(message);
                    if (parsedMessage <= 0) {
                        player.sendMessage(Messages.getMessage("&cPlease enter a number that's greater than 0."));
                        return;
                    }
                    currentProducts.get(player).setPrice(parsedMessage);
                    player.sendMessage(Messages.getMessage(" "));
                    player.sendMessage(Messages.getMessage("&fYour current price is &2&l$&a" + currentProducts.get(player).getPrice()
                            + "&f. Please send a sell price, or send &7`complete` &f to complete the creation process."));
                    player.sendMessage(Messages.getMessage("&fSend &7`cancel` &for &7`exit` &fto exit out of this product creator."));
                    stages.remove(player);
                    stages.put(player, 3);
                } catch (NumberFormatException ex) {
                    player.sendMessage(Messages.getMessage("&cPlease enter a number to set the buy price of this item, or enter &7`cancel` &c or &7`exit` &cto leave this creator."));
                }
            } else if (stages.get(player) == 3) {
                if (e.getMessage().equalsIgnoreCase("complete")) {
                    complete(player);
                } else {
                    String message = e.getMessage();
                    try {
                        Double parsedMessage = Double.parseDouble(message);
                        if (parsedMessage == null || parsedMessage <= 0) {
                            player.sendMessage(Messages.getMessage("&cPlease enter a number that's greater than 0."));
                            return;
                        }
                        currentProducts.get(player).setSellPrice(parsedMessage);
                        complete(player);
                    } catch (NumberFormatException ex) {
                        player.sendMessage(Messages.getMessage("&cPlease enter a number to set the sell price of this item, or enter &7`cancel` &c or &7`exit` &cto leave this creator."));
                    }
                }

            } else { e.setCancelled(false); }
        }
    }

    private static void complete(Player player) {
        Product product = currentProducts.get(player);

        player.sendMessage(Messages.getMessage("&aCreated a new product for subshop &f" + currentPlayers.get(player)
        + "\n&a" +  product.getItem().getType() + "\n  &7Buy: " + product.getPrice() + "\n  &7Sell: " + product.getSellPrice()
        ));

        Shop.getSubShopFromName(currentPlayers.get(player)).addProduct(product);

        currentPlayers.remove(player);
        currentProducts.remove(player);
        stages.remove(player);
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (currentPlayers.containsKey(e.getPlayer())) {
            currentPlayers.remove(e.getPlayer());
            currentProducts.remove(e.getPlayer());
            stages.remove(e.getPlayer());
        }
    }





}
