package com.anonymouslyfast.basicCustomShop.commands;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.data.DataManager;
import com.anonymouslyfast.basicCustomShop.shop.ProductCreation;
import com.anonymouslyfast.basicCustomShop.shop.Shop;
import com.anonymouslyfast.basicCustomShop.shop.SubShopCreation;
import com.anonymouslyfast.basicCustomShop.tools.Messages;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.Subcommand;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@Command("shopmanager")
@Permission("BCS.shopmanager")
public class ShopManagerCommand {

    private static void sendHelp(CommandSender sender) {
        String helpMessage = """
                &8=== &a&lShop &2&lManager &8===\s
                  &8- &f/shopmanager help &7- Shows this message.\s
                  &8- &f/shopmanager reload &7- Reloads config\s
                  &8- &f/shopmanager reloaddatabase &7- saves all subshops and products to database\s
                  &8- &f/shopmanager toggleshop &7- Enables/Disables the shop\s
                  &8- &f/shopmanager createsubshop [SubShopName] &7- Starts the subshop creation process.\s
                  &8- &f/shopmanager createproduct [SubShopName] &7- Starts the product creation process.\s
                  &8- &f/shopmanager deletesubshop [SubShopName] &7- Deletes the subshop and the products from shop. To delete a product, go to the subshop gui and shift + right click the product.""";
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', helpMessage));
    }

    @Default
    public static void shopManager(CommandSender sender) {
        sendHelp(sender);
    }
    @Subcommand("help")
    public static void help(CommandSender sender) {
        sendHelp(sender);
    }

    @Subcommand("reload")
    public static void reload(CommandSender sender) {
        BasicCustomShops.getInstance().customReloadConfig();
        sender.sendMessage(Messages.getMessage("&fReloaded Config!"));
    }

    @Subcommand("reloaddatabase")
    public static void reloadDatabase(CommandSender sender) {
        DataManager.saveSubShops();
        sender.sendMessage(Messages.getMessage("&fSaved all subshops and products to database!"));
    }

    @Subcommand("toggleshop")
    public static void toggleShop(CommandSender sender) {
        if (BasicCustomShops.getInstance().shopIsEnabled) {
            BasicCustomShops.getInstance().changeShopBoolean(false);
            sender.sendMessage(Messages.getMessage("&fToggled shop to &cDisabled&f."));
        } else {
            BasicCustomShops.getInstance().changeShopBoolean(true);
            sender.sendMessage(Messages.getMessage("&fToggled shop to &aEnabled&f."));
        }
    }

    @Subcommand("createsubshop")
    public static void createSubShop(CommandSender sender, @AStringArgument String name) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Messages.getMessage("&cOnly players can use this command!"));
            return;
        }
        if (Shop.isSubshopNameTaken(name)) {
            sender.sendMessage(Messages.getMessage("&cThis name is taken! Please use another name."));
            return;
        }
        Player player = (Player) sender;
        SubShopCreation.addPlayer(player, name);
    }

    @Subcommand("createproduct")
    public static void createProduct(CommandSender sender, @AStringArgument String SubShopName) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Messages.getMessage("&cOnly players can use this command!"));
            return;
        }
        if (!Shop.isSubshopNameTaken(SubShopName)) {
            sender.sendMessage(Messages.getMessage("&cThis name is not a name of a subshop!"));
            return;
        }
        Player player = (Player) sender;
        ProductCreation.addPlayer(player, SubShopName);
    }

    @Subcommand("deletesubshop")
    public static void deleteSubShop(CommandSender sender, @AStringArgument String SubShopName) {
        if (!Shop.isSubshopNameTaken(SubShopName)) {
            sender.sendMessage(Messages.getMessage("&cThis name is not a name of a subshop!"));
            return;
        }
        Shop.removeSubShop(SubShopName);
        sender.sendMessage(Messages.getMessage("&fDeleted &c" + SubShopName + "&f."));
    }


}
