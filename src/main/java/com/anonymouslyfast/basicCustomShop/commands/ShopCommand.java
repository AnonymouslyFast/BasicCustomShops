package com.anonymouslyfast.basicCustomShop.commands;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.shop.Customer;
import com.anonymouslyfast.basicCustomShop.tools.Messages;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@Command("shop")
public class ShopCommand {

    @Default
    public static void shop(CommandSender sender) {
        // Checking if shop is enabled for normal players.
        if (!BasicCustomShops.getInstance().shopIsEnabled && !sender.hasPermission("BCS.shopmanager")) {
            sender.sendMessage(Messages.getMessage("&7Shop is Disabled!"));
            return;
        }
        // Checking if console
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.getMessage("&cOnly players can use this command!"));
            return;
        }

        Customer customer;
        if (Shop.getCustomer(player.getUniqueId()) != null) customer = Shop.getCustomer(player.getUniqueId());
        else customer = new Customer(player.getUniqueId());

        Shop.addCustomer(customer);
        Inventory inventory = Shop.getShopInventory(player);
        customer.switchInventory(inventory);
    }

}
