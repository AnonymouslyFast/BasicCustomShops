package com.anonymouslyfast.basicCustomShop;

import com.anonymouslyfast.basicCustomShop.commands.ShopCommand;
import com.anonymouslyfast.basicCustomShop.commands.ShopManagerCommand;
import com.anonymouslyfast.basicCustomShop.data.DataService;
import com.anonymouslyfast.basicCustomShop.data.SQLiteDataService;
import com.anonymouslyfast.basicCustomShop.hooks.SQLiteHook;
import com.anonymouslyfast.basicCustomShop.hooks.VaultHook;
import com.anonymouslyfast.basicCustomShop.listeners.*;
import com.anonymouslyfast.basicCustomShop.shop.ShopManager;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BasicCustomShops extends JavaPlugin {

    public String messagePrefix = getConfig().getString("message-prefix");

    public static BasicCustomShops plugin;

    private DataService dataService;
    public ShopManager shopManager;


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
        plugin = this;
        customReloadConfig();
        shopIsEnabled = getConfig().getBoolean("shop-enabled");

        dataService = new SQLiteDataService(this, new SQLiteHook());
        shopManager = new ShopManager(this, dataService);

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
            dataService.loadSubShops();
        }


    }

    private void registerCommands() {
        CommandAPI.registerCommand(ShopCommand.class);
        CommandAPI.registerCommand(ShopManagerCommand.class);
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new MainShopClickListener(), this);
        pm.registerEvents(new InventoryCloseListener(), this);
        pm.registerEvents(new ShopClickListener(), this);
        pm.registerEvents(new QuitListener(), this);
        pm.registerEvents(new ShopCreatorListener(), this);
        pm.registerEvents(new TransactionHandlerListener(), this);
        pm.registerEvents(new ProductCreatorListener(), this);
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
        dataService.saveSubShops();
    }



}
