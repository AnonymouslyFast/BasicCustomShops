package com.anonymouslyfast.basicCustomShop.commands;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.shop.PlayerTracking;
import com.anonymouslyfast.basicCustomShop.shop.Shop;
import com.anonymouslyfast.basicCustomShop.shop.ShopAdmin;
import com.anonymouslyfast.basicCustomShop.shop.ShopCreator;
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
                  &8- &f/shopmanager saveshops &7- saves all shops and products to database\s
                  &8- &f/shopmanager createshop [ShopName] &7- Starts the shop creation process.\s
                  &8- &f/shopmanager createproduct [ShopName] &7- Starts the product creation process.\s
                  &8- &f/shopmanager deleteshop [ShopName] &7- Deletes the shop and the products- To delete a product, go to the shop gui and shift + right click the product.""";
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

    @Subcommand("saveshops")
    public static void reloadDatabase(CommandSender sender) {
        BasicCustomShops.getInstance().shopManager.reloadDataService();
        sender.sendMessage(Messages.getMessage("&fSaved all shops and products to database!"));
    }

    @Subcommand("createshop")
    public static void createSubShop(CommandSender sender, @AStringArgument String name) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Messages.getMessage("&cOnly players can use this command!"));
            return;
        }
        if (BasicCustomShops.getInstance().shopManager.getShopFromName(name) != null) {
            sender.sendMessage(Messages.getMessage("&cThis name is taken! Please use another name."));
            return;
        }
        Player player = (Player) sender;
        ShopAdmin shopAdmin = new ShopAdmin(player, name);
        BasicCustomShops.getInstance().shopManager.addAdmin(shopAdmin);
        ShopCreator.getInstance().addPlayer(player, ShopCreator.CreatingType.SHOP);
        PlayerTracking.updatePlayerStatus(player.getUniqueId(), PlayerTracking.PlayerStatus.CREATINGSHOP);
    }

    @Subcommand("createproduct")
    public static void createProduct(CommandSender sender, @AStringArgument String shopName) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Messages.getMessage("&cOnly players can use this command!"));
            return;
        }
        if (BasicCustomShops.getInstance().shopManager.getShopFromName(shopName) != null) {
            sender.sendMessage(Messages.getMessage("&cThis name is not a name of a subshop!"));
            return;
        }
        Player player = (Player) sender;
        ShopAdmin shopAdmin = new ShopAdmin(player, shopName);
        BasicCustomShops.getInstance().shopManager.addAdmin(shopAdmin);
        ShopCreator.getInstance().addPlayer(player, ShopCreator.CreatingType.PRODUCT);
        PlayerTracking.updatePlayerStatus(player.getUniqueId(), PlayerTracking.PlayerStatus.CREATINGPRODUCT);
    }

    @Subcommand("deleteshop")
    public static void deleteSubShop(CommandSender sender, @AStringArgument String shopName) {
        Shop shop = BasicCustomShops.getInstance().shopManager.getShopFromName(shopName);
        if (shop == null) {
            sender.sendMessage(Messages.getMessage("&cThis name is not a name of a subshop!"));
            return;
        }
        BasicCustomShops.getInstance().shopManager.removeShop(shop);
        sender.sendMessage(Messages.getMessage("&fDeleted &c" + shop.getName() + "&f."));
    }


}
