package com.anonymouslyfast.basicCustomShop.listeners;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.shop.*;
import com.anonymouslyfast.basicCustomShop.tools.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;


public class MainShopClickListener implements Listener {

    private final ShopManager shopManager = BasicCustomShops.getInstance().shopManager;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Customer customer = shopManager.getCustomerByUUID(event.getWhoClicked().getUniqueId());
        if (customer == null) return;
        String title = shopManager.getInventoryName(null, customer.getPage());
        if (!event.getView().getTitle().equals(Messages.convertCodes(title))) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if (event.getSlot() == 45) { // Previous page / Back to Shop
            if (event.getClickedInventory().getItem(45).getType() != Material.BARRIER) {
                customer.switchInventory(shopManager.getMainInventory(player.getUniqueId(), customer.getPage()));
            } else {
                player.closeInventory();
                shopManager.removeCustomer(customer);
                PlayerTracking.removePlayer(player.getUniqueId());
            }
            return;
        } else if (event.getSlot() == 53) { // Next Page
            if (event.getClickedInventory().getItem(53).getType() != Material.ARROW) return;
            customer.switchInventory(shopManager.getMainInventory(player.getUniqueId(), customer.getPage()+1));
            return;
        }

        Shop clickedShop = customer.getShopFromSlot(event.getSlot());
        if (clickedShop == null) return;
        // Deleting Shop
        if (event.getClick() == ClickType.SHIFT_RIGHT && player.hasPermission("BCS.shopmanager")) {
            shopManager.removeShop(clickedShop);
            player.sendMessage(Messages.convertCodes("&fDeleted 77" + clickedShop.getName() + "&f."));
            customer.switchInventory(shopManager.getMainInventory(player.getUniqueId(), customer.getPage()));
            return;
        }
        // Clicked on a Shop.
        String newTitle = shopManager.getInventoryName(clickedShop, 1);
        Inventory shopInventory = new ShopInventoryBuilder(player.getUniqueId(), newTitle)
                .buildShopInventory(clickedShop, 1);
        customer.switchInventory(shopInventory);
    }


}
