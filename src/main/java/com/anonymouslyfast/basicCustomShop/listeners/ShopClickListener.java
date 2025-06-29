package com.anonymouslyfast.basicCustomShop.listeners;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.Customer;
import com.anonymouslyfast.basicCustomShop.Shop;
import com.anonymouslyfast.basicCustomShop.SubShop;
import com.anonymouslyfast.basicCustomShop.tools.Messages;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;


public class ShopClickListener implements Listener {

    private final FileConfiguration config = BasicCustomShops.getInstance().getConfig();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = config.getString("shop-title");
        if (!event.getView().getTitle().equals(Messages.convertCodes(title))) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        Customer customer = Shop.getCustomer(player);
        if (customer == null) return;

        if (event.getSlot() == 44) {
            if (event.getClickedInventory().getItem(44).getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            } else {
                customer.switchInventory(Shop.getShopInventory(player, customer.getPage()-1));
                return;
            }
        } else if (event.getSlot() == 54) {
            if (event.getClickedInventory().getItem(54).getType() != Material.ARROW) return;
            customer.switchInventory(Shop.getShopInventory(player, customer.getPage()+1));
            return;
        }

        SubShop clickedSubShop = customer.getSubShopFromSlot(event.getSlot());
        if (clickedSubShop == null) return;
        customer.switchInventory(Shop.getSubShopInventory(clickedSubShop, 1));

    }


}
