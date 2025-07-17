package com.anonymouslyfast.basicCustomShop.shop;

import com.anonymouslyfast.basicCustomShop.hooks.VaultHook;
import com.anonymouslyfast.basicCustomShop.utils.MessageUtils;
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
            player.sendMessage(MessageUtils.getMessage("&cYou don't have a economy account, please contact admins!"));
            return;
        }

        double price = product.getPrice()*amount;
        if (VaultHook.getBalance(player) < price) {
            player.sendMessage(MessageUtils.getMessage("&cYou do not have enough money!"));
            return;
        }

        ItemStack itemStack = new ItemStack(product.getMaterial(), amount);

        EconomyResponse economyResponse = VaultHook.withdrawPlayer(player, price);
        if (!economyResponse.transactionSuccess()) {
            player.sendMessage(MessageUtils.getMessage(economyResponse.errorMessage));
            return;
        }

        player.sendMessage(MessageUtils.getMessage("&aYou have successfully purchased &7" + amount + " &aof &7" +
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
            player.sendMessage(MessageUtils.getMessage("&cYou don't have a economy account, please contact admins!"));
            return;
        }
        if (!product.isSellable()) return;

        double price = product.getSellPrice()*amount;

        if (!player.getInventory().contains(product.getMaterial(), amount)) {
            player.sendMessage(MessageUtils.getMessage("&cYou do not have enough items to sell that much!"));
            return;
        }

        boolean success = removeItems(customer, amount);
        if (!success) {
            player.sendMessage(MessageUtils.getMessage("&cYou do not have enough items to sell that much!"));
            return;
        }

        EconomyResponse economyResponse = VaultHook.depositPlayer(player, price);
        if (!economyResponse.transactionSuccess()) {
            player.sendMessage(MessageUtils.getMessage(economyResponse.errorMessage));
            return;
        }
        player.sendMessage(MessageUtils.getMessage("&aYou have successfully sold &7" + amount + " &aof &7" +
                product.getMaterial() + " &aand made &2&l" + price + "&a! &fYour new balance is: &2&l$" + economyResponse.balance));
    }
    public static void sell(Customer customer, Product product) { sell(customer, product, 1); }


    private static boolean removeItems(Customer customer, int amount) {
        Player player = Bukkit.getPlayer(customer.getPlayerUUID());
        Product product = customer.getProductInCart();
        assert player != null;
        int amountToRemove = amount;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack == null || itemStack.getType() != product.getMaterial()) continue;

            if (itemStack.getAmount() <= amountToRemove) {
                player.getInventory().clear(i);
                amountToRemove -= itemStack.getAmount();
            } else {
                itemStack.setAmount(itemStack.getAmount() - amountToRemove);
                player.getInventory().setItem(i, itemStack);
                amountToRemove = 0;
            }
            if (amountToRemove == 0) return true;
        }
        return false;
    }


}
