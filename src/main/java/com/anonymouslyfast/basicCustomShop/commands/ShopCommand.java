package com.anonymouslyfast.basicCustomShop.commands;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.shop.Customer;
import com.anonymouslyfast.basicCustomShop.shop.PlayerTracking;
import com.anonymouslyfast.basicCustomShop.shop.ShopManager;
import com.anonymouslyfast.basicCustomShop.utils.MessageUtils;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@Command("shop")
public class ShopCommand {

    private static final ShopManager shopManager = BasicCustomShops.getInstance().shopManager;

    @Default
    public static void shop(CommandSender sender) {
        // Checking if shop is enabled for normal players.
        if (!shopManager.isShopEnabled() && !sender.hasPermission("BCS.shopmanager")) {
            sender.sendMessage(MessageUtils.getMessage("&7Shop is Disabled!"));
            return;
        }
        // Checking if console
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtils.getMessage("&cOnly players can use this command!"));
            return;
        }

        Customer customer = shopManager.getCustomerByUUID(player.getUniqueId());
        if (customer == null) {
            customer = new Customer(player.getUniqueId(), PlayerTracking.PlayerStatus.INMAINSHOPGUI);
            shopManager.addCustomer(customer);
        }

        Inventory inventory = customer.getInventory();
        if (inventory == null) {
            inventory = shopManager.getMainInventory(player.getUniqueId(), 1);
            customer.setPage(1);
            customer.switchInventory(inventory);
        } else {
            player.openInventory(inventory);
        }
    }
}
