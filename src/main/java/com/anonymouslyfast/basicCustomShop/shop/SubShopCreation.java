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

public class SubShopCreation implements Listener {
    private final static HashMap<Player, String> currentPlayers = new HashMap<>();

    public static void addPlayer(Player player, String subshopName) {
        currentPlayers.put(player, subshopName);
        player.sendMessage(Messages.getMessage("&fPlease &7right click &fwith a item to set as a icon for the subshop. &7(Only takes the material of the item.)"));
        player.sendMessage(Messages.getMessage("&fIn chat say &7`cancel` &for &7`exit` &fto exit from this creator."));
    }



    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!currentPlayers.containsKey(player)) {return;}
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            e.setCancelled(true);
            if (e.getItem() == null) {
                player.sendMessage(Messages.getMessage("&cYou need to be holding a item in your hand."));
                return;
            }
            ItemStack item = e.getItem();
            SubShop subShop = new SubShop(currentPlayers.get(player), item.getType());
            Shop.addSubShop(subShop);
            currentPlayers.remove(player);
            player.sendMessage(Messages.getMessage("&aCreated the subshop &f" + subShop.getName() + "&a."));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!currentPlayers.containsKey(player)) {return;}
        if (e.getMessage().equalsIgnoreCase("cancel") || e.getMessage().equalsIgnoreCase("exit")) {
            e.setCancelled(true);
            currentPlayers.remove(player);
            player.sendMessage(Messages.getMessage("&aRemoved you from the subshop creator."));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        currentPlayers.remove(e.getPlayer());
    }



}
