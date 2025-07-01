package com.anonymouslyfast.basicCustomShop.listeners;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.shop.Customer;
import com.anonymouslyfast.basicCustomShop.shop.Shop;
import com.anonymouslyfast.basicCustomShop.shop.SubShop;
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
        Customer customer = Shop.getCustomer(event.getWhoClicked().getUniqueId());
        if (customer == null) return;
        String title = config.getString("shop-title") + " &7(" + customer.getPage() + ")";
        if (!event.getView().getTitle().equals(Messages.convertCodes(title))) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if (event.getSlot() == 45) { // Previous page / Back to Shop
            if (event.getClickedInventory().getItem(45).getType() != Material.BARRIER) {
                customer.switchInventory(Shop.getShopInventory(player, customer.getPage()-1));
            } else {
                player.closeInventory();
                Shop.removeCustomer(customer);
            }
            return;
        } else if (event.getSlot() == 53) { // Next Page
            if (event.getClickedInventory().getItem(53).getType() != Material.ARROW) return;
            customer.switchInventory(Shop.getShopInventory(player, customer.getPage()+1));
            return;
        }

        SubShop clickedSubShop = customer.getSubShopFromSlot(event.getSlot());
        if (clickedSubShop == null) return;
        customer.switchInventory(Shop.getSubShopInventory(player, clickedSubShop, 1)); // Clicked on subshop

    }


}
