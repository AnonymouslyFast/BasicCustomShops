package com.anonymouslyfast.basicCustomShop.shop;

import com.anonymouslyfast.basicCustomShop.hooks.VaultHook;
import com.anonymouslyfast.basicCustomShop.tools.Messages;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public final class TransactionHandler {

    public static void buy(Customer customer, Product product, int amount) {
        Player player = Bukkit.getPlayer(customer.getPlayerUUID());
        if (player == null) return;
        if (!VaultHook.hasAccount(player)) {
            player.sendMessage(Messages.getMessage("&cYou don't have a economy account, please contact admins!"));
            return;
        }

        double price = product.getPrice()*amount;
        if (VaultHook.getBalance(player) < price) {
            player.sendMessage(Messages.getMessage("&cYou do not have enough money!"));
            return;
        }

        ItemStack itemStack = new ItemStack(product.getMaterial(), amount);

        EconomyResponse economyResponse = VaultHook.withdrawPlayer(player, price);
        if (!economyResponse.transactionSuccess()) {
            player.sendMessage(Messages.getMessage(economyResponse.errorMessage));
            return;
        }

        player.sendMessage(Messages.getMessage("&aYou have successfully purchased &7" + amount + " &aof &7" +
                itemStack.getType().name() + " &afor &2&l" + price + "&a! &fYour new balance is: &2&l$" + economyResponse.balance));

        HashMap<Integer, ItemStack> cantHold = player.getInventory().addItem(itemStack);
        if (!cantHold.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), cantHold.get(0));
        }
    }
    public static void buy(Customer customer, Product product) { buy(customer, product, 1); }

    public static void sell(Customer customer, Product product, int amount) {
        Player player = Bukkit.getPlayer(customer.getPlayerUUID());
        if (player == null) return;
        if (!VaultHook.hasAccount(player)) {
            player.sendMessage(Messages.getMessage("&cYou don't have a economy account, please contact admins!"));
            return;
        }
        if (product.getSellPrice() == null) return;

        double price = product.getSellPrice()*amount;

        ItemStack itemStack = new ItemStack(product.getMaterial(), amount);

        if (!player.getInventory().contains(itemStack)) {
            player.sendMessage(Messages.getMessage("&cYou don't have the items to sell this much!"));
            return;
        }

        EconomyResponse economyResponse = VaultHook.depositPlayer(player, price);
        if (!economyResponse.transactionSuccess()) {
            player.sendMessage(Messages.getMessage(economyResponse.errorMessage));
            return;
        }
        player.sendMessage(Messages.getMessage("&aYou have successfully sold &7" + amount + " &aof &7" +
                itemStack.getType().name() + " &aand made &2&l" + price + "&a! &fYour new balance is: &2&l$" + economyResponse.balance));
        player.getInventory().remove(itemStack);
    }
    public static void sell(Customer customer, Product product) { sell(customer, product, 1); }


}
