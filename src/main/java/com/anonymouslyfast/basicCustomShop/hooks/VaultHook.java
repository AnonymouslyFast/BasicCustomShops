package com.anonymouslyfast.basicCustomShop.hooks;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private static Economy economy;

    public static boolean failedSetup = false;

    public static void init() {
        if (BasicCustomShops.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            failedSetup = true;
            return;
        }
        RegisteredServiceProvider<Economy> rsp =
                BasicCustomShops.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            failedSetup = true;
            return;
        }
        economy = rsp.getProvider();
        failedSetup = false;
    }


    public static Double getBalance(OfflinePlayer offlinePlayer) {
        return economy.getBalance(offlinePlayer);
    }

    public static boolean hasAccount(OfflinePlayer offlinePlayer) {
        return economy.hasAccount(offlinePlayer);
    }

    public static EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        return economy.withdrawPlayer(offlinePlayer, amount);
    }

    public static EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        return economy.depositPlayer(offlinePlayer, amount);
    }


}
