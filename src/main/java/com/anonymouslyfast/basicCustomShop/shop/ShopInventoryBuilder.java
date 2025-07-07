package com.anonymouslyfast.basicCustomShop.shop;


import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.hooks.VaultHook;
import com.anonymouslyfast.basicCustomShop.tools.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ShopInventoryBuilder {

    private final Inventory inventory;
    private final boolean isAdmin;
    private final UUID playerUUID;

    private final int closeInventorySlot = 45;
    private final int backPageSlot = 45;
    private final int informationSlot = 49;
    private final int nextPageSlot = 49;

    private void fillInventory(Inventory inventory) {
        for (int i = 0; i <  inventory.getSize(); i++) {
            ItemStack fill = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = fill.getItemMeta();
            assert meta != null;
            meta.setDisplayName(Messages.convertCodes("&a "));
            fill.setItemMeta(meta);
            inventory.setItem(i, fill);
        }
    }

    public ShopInventoryBuilder(UUID playerUUID, String title) {
        inventory = Bukkit.createInventory(null, 54, Messages.convertCodes(title));
        isAdmin = Bukkit.getPlayer(playerUUID).hasPermission("BCS.shopmanager");
        this.playerUUID = playerUUID;
        fillInventory(inventory);
        inventory.setItem(informationSlot, getInformationItem(VaultHook.getBalance(Bukkit.getPlayer(playerUUID))));
    }

    private final static int maxProductsPerPage = 28;
    private final static int endOfContainer = 44;
    private final static int startOfContainer = 10;

    public Inventory buildShopInventory(Shop shop, int page) {
        boolean requiresNextPage = (shop.getProducts().size() >= 1+(maxProductsPerPage*page));
        boolean requiresBackPage = page > 1;
        Customer customer = BasicCustomShops.plugin.shopManager.getCustomerByUUID(playerUUID);
        if (!shop.getProducts().isEmpty()) {
            int currentSlot = startOfContainer;
            int index = 0;
            for (Product product : shop.getProducts()) {
                if (index >= maxProductsPerPage*(page-1)) {
                    if ((currentSlot + 1) % 9 == 0) currentSlot += 2;
                    if (currentSlot >= endOfContainer) break;

                    ItemStack item = new ItemStack(product.getMaterial());
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        List<String> lore = new ArrayList<>();
                        lore.add(Messages.convertCodes("&7Costs: &2&l$&f" + product.getPrice()));
                        if (product.getSellPrice() != null) lore.add(Messages.convertCodes("&7Sell: &2&l$&f" + product.getSellPrice()));
                        lore.add(Messages.convertCodes("&8&l&m-----------------"));
                        lore.add(Messages.convertCodes("&7Left Click &fto buy."));
                        lore.add(Messages.convertCodes("&7SHIFT + Left Click &fto buy multiple."));
                        if (product.getSellPrice() != null || product.getSellPrice() == 0)
                            lore.add(Messages.convertCodes("&7Right click &fto sell."));
                        if (isAdmin) lore.add(Messages.convertCodes("&cShift + Right click to &ldelete&c."));
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }
                    inventory.setItem(currentSlot, item);
                    customer.addProductSlot(currentSlot, product);
                }
                index++;
                currentSlot++;
            }
        }
        // Setting the bottom menu
        if (requiresBackPage) {
            inventory.setItem(backPageSlot, getBackPageItem());
        } else { // Doesn't require back page, so adds the close inventory item
            inventory.setItem(closeInventorySlot, getCloseInventoryItem());
        }

        if (requiresNextPage) inventory.setItem(nextPageSlot, getNextPageItem());

        return inventory;
    }

    private final List<Integer> subShopSlots = List.of(10, 12, 14, 16, 19, 21, 23, 25, 28, 30, 32, 34, 36, 39, 41, 43);

    public Inventory buildMainInventory(List<Shop> shops, int page) {
        boolean requiresNextPage = (shops.size() >= subShopSlots.size()*page);
        boolean requiresBackPage = page > 1;
        if (!shops.isEmpty()) {
            int passedLoops = 0;
            int startIndex = shops.size()*(page-1);
            Customer customer = BasicCustomShops.plugin.shopManager.getCustomerByUUID(playerUUID);
            for (int i = 0; i < shops.size(); i++) {
                if (i >= startIndex && passedLoops < subShopSlots.size()) {
                    Shop shop = shops.get(i);
                    ItemStack itemStack = new ItemStack(shop.getIcon());
                    ItemMeta meta = itemStack.getItemMeta();
                    assert meta != null;
                    meta.setDisplayName(Messages.convertCodes(shop.getName()));
                    List<String> lore = new ArrayList<>();
                    lore.add(Messages.convertCodes("&7Click to open this subshop."));
                    if (isAdmin) lore.add(Messages.convertCodes("&cShift + Right click to &ldelete&c."));
                    meta.setLore(lore);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                    itemStack.setItemMeta(meta);
                    inventory.setItem(subShopSlots.get(passedLoops), itemStack);
                    customer.addShopSlot(subShopSlots.get(passedLoops), shop.getName());
                    passedLoops++;
                }
            }
        }

        // Adding the bottom menu
        if (requiresBackPage) {
            inventory.setItem(backPageSlot, getBackPageItem());
        } else { // Doesn't require back page, so adds the close inventory item
            inventory.setItem(closeInventorySlot, getCloseInventoryItem());
        }

        if (requiresNextPage) inventory.setItem(nextPageSlot, getNextPageItem());

        return inventory;
    }




    private ItemStack getNextPageItem() {
        ItemStack itemStack = new ItemStack(Material.ARROW);
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Messages.convertCodes("&a&lNext Page"));
        meta.setLore(List.of(Messages.convertCodes("&7Click to go to the next page.")));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private ItemStack getBackPageItem() {
        ItemStack itemStack = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Messages.convertCodes("&c&lBack"));
        meta.setLore(List.of(Messages.convertCodes("&7Click to go back to the previous page.")));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private ItemStack getInformationItem(Double playerBalance) {
        ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Messages.convertCodes("&a&l Your Information"));
        meta.setLore(List.of(Messages.convertCodes(" &2Balance: &2&l$&a" + playerBalance)));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private ItemStack getCloseInventoryItem() {
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Messages.convertCodes("&cGo back to shop."));
        meta.setLore(List.of(Messages.convertCodes("&7Click to go back to shop.")));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

}
