package com.anonymouslyfast.basicCustomShop;

import com.anonymouslyfast.basicCustomShop.commands.ShopCommand;
import com.anonymouslyfast.basicCustomShop.commands.ShopManagerCommand;
import com.anonymouslyfast.basicCustomShop.data.DataManager;
import com.anonymouslyfast.basicCustomShop.hooks.SQLiteHook;
import com.anonymouslyfast.basicCustomShop.hooks.VaultHook;
import com.anonymouslyfast.basicCustomShop.listeners.InventoryCloseListener;
import com.anonymouslyfast.basicCustomShop.listeners.QuitListener;
import com.anonymouslyfast.basicCustomShop.listeners.ShopClickListener;
import com.anonymouslyfast.basicCustomShop.listeners.SubShopClickListener;
import com.anonymouslyfast.basicCustomShop.shop.ProductCreation;
import com.anonymouslyfast.basicCustomShop.shop.ProductTransactionHandler;
import com.anonymouslyfast.basicCustomShop.shop.SubShopCreation;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BasicCustomShops extends JavaPlugin {

    private static BasicCustomShops instance;
    public static BasicCustomShops getInstance() {return instance;}

    public String messagePrefix = getConfig().getString("message-prefix");


    public void customReloadConfig() {
        saveDefaultConfig();
        reloadConfig();

        messagePrefix = getConfig().getString("message-prefix");
    }

    public boolean shopIsEnabled;
    public void changeShopBoolean(boolean bool) {
        shopIsEnabled = bool;
        getConfig().set("shop-enabled", bool);
        saveConfig();
        reloadConfig();
    }




    @Override
    public void onLoad() {
        getLogger().info("Loading CommandAPI..");
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        if (!CommandAPI.isLoaded()) {
            getLogger().severe("CommandAPI is not loaded, contact developer.");
            PluginManager pm = getServer().getPluginManager();
            pm.disablePlugin(this);
        }
    }

    @Override
    public void onEnable() {
        customReloadConfig();
        instance = this;

        shopIsEnabled = getConfig().getBoolean("shop-enabled");

        // Database Setup
        SQLiteHook.Init();
        if (SQLiteHook.failedSetup()) { // Checking if it failed
            getLogger().severe("Something went wrong initializing SQLite hook!");
            PluginManager pm = getServer().getPluginManager();
            pm.disablePlugin(this);
            return;
        }

        // Vault Setup
        VaultHook.init();
        if (VaultHook.failedSetup) { // Checking if it failed
            getLogger().severe("Vault is either not on your server, or you don't have a vault economy plugin like Essentials to set up economy!");
            PluginManager pm = getServer().getPluginManager();
            pm.disablePlugin(this);
            return;
        }

        // Main plugin enabling/registering stuff
        if (CommandAPI.isLoaded()) {
            CommandAPI.onEnable();
            registerCommands();
            registerListeners();
            // Loading Subshops and Products from database
            DataManager.loadSubShops();
        }


    }

    private void registerCommands() {
        CommandAPI.registerCommand(ShopCommand.class);
        CommandAPI.registerCommand(ShopManagerCommand.class);
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new SubShopCreation(), this);
        pm.registerEvents(new InventoryCloseListener(), this);
        pm.registerEvents(new ShopClickListener(), this);
        pm.registerEvents(new QuitListener(), this);
        pm.registerEvents(new ProductCreation(), this);
        pm.registerEvents(new SubShopClickListener(), this);
        pm.registerEvents(new ProductTransactionHandler(), this);
    }


    @Override
    public void onDisable() {
        if (CommandAPI.isLoaded()) {
            CommandAPI.getRegisteredCommands().forEach(command ->
                    CommandAPI.unregister(command.commandName())
            );
            getLogger().info("Unloaded registered commands. Disabling CommandAPI...");
            CommandAPI.onDisable();
        }
        // Saving SubShops and Products to Database
        DataManager.saveSubShops();
    }


}
