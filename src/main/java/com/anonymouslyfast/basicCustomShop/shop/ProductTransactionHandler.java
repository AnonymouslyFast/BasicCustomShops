package com.anonymouslyfast.basicCustomShop.shop;

import com.anonymouslyfast.basicCustomShop.hooks.VaultHook;
import com.anonymouslyfast.basicCustomShop.tools.Messages;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;


public class ProductTransactionHandler implements Listener {


    public enum TransactionType {
        BUYING,
        SELLING
    }

    private final static HashMap<UUID, Product> currentBuyingPlayers = new HashMap<>();
    private final static HashMap<UUID, Product> currentSellingPlayers = new HashMap<>();

    public static boolean containsPlayer(UUID uuid) {
        if (currentBuyingPlayers.containsKey(uuid)) return true;
        return currentSellingPlayers.containsKey(uuid);
    }

    public static void addPlayer(Player player, TransactionType type, Product product) {
        if (type == TransactionType.BUYING) {
            currentBuyingPlayers.put(player.getUniqueId(), product);
            player.sendMessage(Messages.getMessage("&fPlease send the number of &7" +
                    product.getItem().getItemMeta().getDisplayName() + " &fyou would like to buy." +
                    " If you would like to exit, please type &7`exit` &for &7`cancel` &finstead."));
        } else if (type == TransactionType.SELLING) {
            currentSellingPlayers.put(player.getUniqueId(), product);
            player.sendMessage(Messages.getMessage("&fPlease send the number of &7" +
                    product.getItem().getItemMeta().getDisplayName() + " &fyou would like to sell." +
                    " If you would like to exit, please type &7`exit` &for &7`cancel` &finstead."));
        }
    }


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

        ItemStack itemStack = product.getItem();
        itemStack.setAmount(amount);

        EconomyResponse economyResponse = VaultHook.withdrawPlayer(player, price);
        if (!economyResponse.transactionSuccess()) {
            player.sendMessage(Messages.getMessage(economyResponse.errorMessage));
            return;
        }

        player.sendMessage(Messages.getMessage("&aYou have successfully purchased &7" + amount + " &aof &7" +
                itemStack.getType().name() + " &afor &2&l&" + price + "&a! &fYour new balance is: &2&l$" + economyResponse.balance));

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

        ItemStack itemStack = product.getItem();
        itemStack.setAmount(amount);

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
                itemStack.getType().name() + " &aand made &2&l&" + price + "&a! &fYour new balance is: &2&l$" + economyResponse.balance));
        player.getInventory().remove(itemStack);
    }
    public static void sell(Customer customer, Product product) { sell(customer, product, 1); }

    // Buying and Selling multiple
    @Nullable
    private Integer parseMessage(Player player, String message) {
        try {
            if (Double.parseDouble(message) > 0) return Integer.parseInt(message);
        } catch (NumberFormatException ignored) {}
        player.sendMessage(Messages.getMessage("&cPlease enter a valid number above 0"));
        return null;
    }

    private boolean triedToCancel(Player player, TransactionType type, String message) {
        if (message.equalsIgnoreCase("cancel") || message.equalsIgnoreCase("exit")) {
            player.openInventory(Shop.getCustomer(player.getUniqueId()).getInventory());
            player.sendMessage(Messages.getMessage("&cYou have cancelled the transaction."));

            if (type == TransactionType.BUYING) currentBuyingPlayers.remove(player.getUniqueId());
            else currentSellingPlayers.remove(player.getUniqueId());

            return true;
        }
        return false;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        Customer customer = Shop.getCustomer(player.getUniqueId());
        if (customer == null) return;

        // Buying
        if (currentBuyingPlayers.containsKey(player.getUniqueId())) {
            e.setCancelled(true);
            if (triedToCancel(player, TransactionType.BUYING, e.getMessage())) return;

            Integer parsedMessage = parseMessage(player, e.getMessage());
            if  (parsedMessage == null) return;

            ProductTransactionHandler.buy(customer, currentBuyingPlayers.get(player.getUniqueId()), parsedMessage);
            player.openInventory(Shop.getCustomer(player.getUniqueId()).getInventory());
            currentBuyingPlayers.remove(player.getUniqueId());

        // Selling
        } else if (currentSellingPlayers.containsKey(player.getUniqueId())) {
            e.setCancelled(true);
            if (triedToCancel(player, TransactionType.SELLING, e.getMessage())) return;

            Integer parsedMessage = parseMessage(player, e.getMessage());
            if  (parsedMessage == null) return;

            ProductTransactionHandler.sell(customer, currentSellingPlayers.get(player.getUniqueId()), parsedMessage);
            player.openInventory(Shop.getCustomer(player.getUniqueId()).getInventory());
            currentSellingPlayers.remove(player.getUniqueId());
        }
    }





}
