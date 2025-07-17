package com.anonymouslyfast.basicCustomShop.listeners;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.shop.*;
import com.anonymouslyfast.basicCustomShop.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class ShopClickListener implements Listener {

    private final ShopManager shopManager = BasicCustomShops.getInstance().shopManager;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Customer customer = shopManager.getCustomerByUUID(player.getUniqueId());
        if (customer == null) return;

        Shop shop = customer.getOpenedShop();
        if (shop == null) return;

        String title = MessageUtils.convertCodes(shop.getName() + " &7(" + customer.getPage() + ")");
        if (!player.getOpenInventory().getTitle().equals(title)) { player.closeInventory(); return; }
        event.setCancelled(true);

        int backPageSlot = 45;
        int nextPageSlot = 53;


        if (event.getSlot() == backPageSlot) { // Previous page
            if (event.getClickedInventory().getItem(backPageSlot).getType() != Material.BARRIER) {
                String newTitle = shopManager.getInventoryName(shop, customer.getPage()-1);
                customer.switchInventory(new ShopInventoryBuilder(player.getUniqueId(), newTitle)
                        .buildShopInventory(shop, customer.getPage()-1)
                );
            } else { // Back To Shop
                customer.switchInventory(shopManager.getMainInventory(player.getUniqueId(), 1));
            }
            return;
        } else if (event.getSlot() == nextPageSlot) { // Next Page
            if (event.getClickedInventory().getItem(nextPageSlot).getType() != Material.ARROW) return;
            String newTitle = shopManager.getInventoryName(shop, customer.getPage()+1);
            customer.switchInventory(new ShopInventoryBuilder(player.getUniqueId(), newTitle)
                    .buildShopInventory(shop, customer.getPage()+1)
            );
            return;
        }


        Product product = customer.getProductSlots().get(event.getSlot());
        if (product == null) return;

        // Buying one product
        if (event.getClick() == ClickType.LEFT) {
            TransactionHandler.buy(customer, product);
        // Buying multiple product
        } else if (event.getClick() == ClickType.SHIFT_LEFT) {
            PlayerTracking.updatePlayerStatus(player.getUniqueId(), PlayerTracking.PlayerStatus.BUYINGMULTIPLE);
            customer.setProductInCart(product);
            player.closeInventory();
            player.sendMessage(MessageUtils.getMessage("&fPlease send a integer above 0 of how many " +
                    product.getMaterial() + " you would like to buy. Otherwise, enter &7`cancel` &for &7`exit` to leave this."));
        // Selling product
        } else if (event.getClick() == ClickType.RIGHT && product.isSellable()) {
            PlayerTracking.updatePlayerStatus(player.getUniqueId(), PlayerTracking.PlayerStatus.SELLING);
            customer.setProductInCart(product);
            player.closeInventory();
            player.sendMessage(MessageUtils.getMessage("&fPlease send a integer above 0 of how many " +
                    product.getMaterial() + " you would like to sell. Otherwise, enter &7`cancel` &for &7`exit` to leave this."));
        // ADMIN: deleting product
        } else if (event.getClick() == ClickType.SHIFT_RIGHT && player.hasPermission("BCS.shopmanager")) {
            shopManager.removeProduct(shop, product);
            player.sendMessage(MessageUtils.convertCodes("&fDeleted &7" + product.getMaterial().name() + "&f from &7" + shop.getName() + "&f."));
            String newTitle = shopManager.getInventoryName(shop, customer.getPage());
            customer.switchInventory(new ShopInventoryBuilder(player.getUniqueId(), newTitle)
                    .buildShopInventory(shop, customer.getPage())
            );
        }
    }
}
