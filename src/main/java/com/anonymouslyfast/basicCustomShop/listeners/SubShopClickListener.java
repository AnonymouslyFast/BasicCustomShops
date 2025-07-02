package com.anonymouslyfast.basicCustomShop.listeners;

import com.anonymouslyfast.basicCustomShop.shop.*;
import com.anonymouslyfast.basicCustomShop.tools.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SubShopClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Customer customer = Shop.getCustomer(player.getUniqueId());
        if (customer == null) return;

        SubShop subShop = customer.getOpenedSubShop();
        if (subShop == null) return;

        String title = Messages.convertCodes(subShop.getName() + " &7(" + customer.getPage() + ")");
        if (!player.getOpenInventory().getTitle().equals(title)) { player.closeInventory(); return; }
        event.setCancelled(true);

        int backPageSlot = 45;
        int nextPageSlot = 53;

        if (event.getSlot() == backPageSlot) { // Previous page / Back to Shop
            if (event.getClickedInventory().getItem(backPageSlot).getType() != Material.BARRIER) {
                customer.switchInventory(Shop.getSubShopInventory(player, subShop, customer.getPage()-1));
            } else {
                customer.switchInventory(Shop.getShopInventory(player));
            }
            return;
        } else if (event.getSlot() == nextPageSlot) { // Next Page
            if (event.getClickedInventory().getItem(nextPageSlot).getType() != Material.ARROW) return;
            customer.switchInventory(Shop.getSubShopInventory(player, subShop,customer.getPage()+1));
            return;
        }


        Product product = customer.getProductSlots().get(event.getSlot());
        if (product == null) return;

        // Buying one product
        if (event.getClick() == ClickType.LEFT) {
            ProductTransactionHandler.buy(customer, product);
        // Buying multiple product
        } else if (event.getClick() == ClickType.SHIFT_LEFT) {
            ProductTransactionHandler.addPlayer(player, ProductTransactionHandler.TransactionType.BUYING, product);
            player.closeInventory();
        // Selling product
        } else if (event.getClick() == ClickType.RIGHT) {
            ProductTransactionHandler.addPlayer(player, ProductTransactionHandler.TransactionType.SELLING, product);
            player.closeInventory();
        // ADMIN: deleting product
        } else if (event.getClick() == ClickType.SHIFT_RIGHT && player.hasPermission("BCS.shopmanager")) {
            subShop.removeProduct(product);
            player.sendMessage(Messages.convertCodes("&fDeleted &7" + product.getMaterial().name() + "&f from &7" + subShop.getName() + "&f."));
            customer.switchInventory(Shop.getSubShopInventory(player, subShop, customer.getPage()));
        }
    }
}
