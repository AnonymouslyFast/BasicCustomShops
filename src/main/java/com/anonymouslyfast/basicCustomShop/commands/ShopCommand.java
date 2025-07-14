package com.anonymouslyfast.basicCustomShop.commands;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.shop.Customer;
import com.anonymouslyfast.basicCustomShop.shop.PlayerTracking;
import com.anonymouslyfast.basicCustomShop.shop.ShopManager;
import com.anonymouslyfast.basicCustomShop.tools.Messages;
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
            sender.sendMessage(Messages.getMessage("&7Shop is Disabled!"));
            return;
        }
        // Checking if console
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.getMessage("&cOnly players can use this command!"));
            return;
        }

        Customer customer = shopManager.getCustomerByUUID(player.getUniqueId());
        if (customer == null) new Customer(player.getUniqueId(), PlayerTracking.PlayerStatus.INMAINSHOPGUI);

        Inventory inventory = customer.getInventory();
        if (inventory == null) shopManager.getMainInventory(player.getUniqueId(), 1);

        customer.switchInventory(inventory);
        shopManager.addCustomer(customer);
    }
}
