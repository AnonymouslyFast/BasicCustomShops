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

    private static BasicCustomShops instance;

    private DataService dataService;
    public ShopManager shopManager;

    @Override
    public void onLoad() {
        getLogger().info("Loading CommandAPI..");
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        if (!CommandAPI.isLoaded()) {
            getLogger().severe("CommandAPI is not loaded, contact developer.");
            PluginManager pluginManager = getServer().getPluginManager();
            pluginManager.disablePlugin(this);
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        dataService = new SQLiteDataService(this, new SQLiteHook());
        shopManager = new ShopManager(this, dataService);

        // Vault Setup
        VaultHook.init();
        if (VaultHook.failedSetup) { // Checking if it failed
            getLogger().severe("Vault is either not on your server, or you don't have a vault economy plugin like Essentials to set up economy!");
            PluginManager pluginManager = getServer().getPluginManager();
            pluginManager.disablePlugin(this);
            return;
        }

        // Main plugin enabling/registering stuff
        if (CommandAPI.isLoaded()) {
            CommandAPI.onEnable();
            registerCommands();
            registerListeners();
            // Loading shops and Products from database
            dataService.loadShops();
        }


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
        // Saving Shops and Products to Database
        dataService.saveShops();
    }


    private void registerCommands() {
        CommandAPI.registerCommand(ShopCommand.class);
        CommandAPI.registerCommand(ShopManagerCommand.class);
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new MainShopClickListener(), this);
        pluginManager.registerEvents(new InventoryCloseListener(), this);
        pluginManager.registerEvents(new ShopClickListener(), this);
        pluginManager.registerEvents(new QuitListener(), this);
        pluginManager.registerEvents(new ShopCreatorListener(), this);
        pluginManager.registerEvents(new TransactionHandlerListener(), this);
        pluginManager.registerEvents(new ProductCreatorListener(), this);
    }


    public static BasicCustomShops getInstance() { return instance; }

    public String getMessagePrefix() { return getConfig().getString("message-prefix"); }

}
