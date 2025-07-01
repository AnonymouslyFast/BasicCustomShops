package com.anonymouslyfast.basicCustomShop.shop;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import com.anonymouslyfast.basicCustomShop.tools.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Shop {
    private static final FileConfiguration config = BasicCustomShops.getInstance().getConfig();

    private static List<SubShop> subShops = new ArrayList<>();
    private final static HashMap<String, SubShop> subShopNames = new HashMap<>();

    private final static HashMap<UUID, Customer> currentCustomers = new HashMap<>();
    public static void addCustomer(Customer customer) {currentCustomers.put(customer.getPlayerUUID(), customer);}
    public static void removeCustomer(Customer customer) {currentCustomers.remove(customer.getPlayerUUID());}
    public static Customer getCustomer(UUID uuid) {return currentCustomers.get(uuid);}


    private final static List<Integer> subShopSlots = List.of(10, 12, 14, 16, 19, 21, 23, 25, 28, 30, 32, 34, 36, 39, 41, 43);

    private static void fillInventory(Inventory inventory) {
        for (int i = 0; i <  54; i++) {
            ItemStack fill = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = fill.getItemMeta();
            assert meta != null;
            meta.setDisplayName(Messages.convertCodes("&a "));
            fill.setItemMeta(meta);
            inventory.setItem(i, fill);
        }
    }

    private final static int maxProductsPerPage = 28;
    private final static int endOfContainer = 44;
    private final static int startOfContainer = 10;

    public static Inventory getSubShopInventory(Player player, SubShop subShop, int page) {
        String title = subShop.getName() + " &7(" + page + ")";
        Inventory inventory = Bukkit.createInventory(null, 54, Messages.convertCodes(title));
        Customer customer = currentCustomers.get(player.getUniqueId());
        fillInventory(inventory);
        if (!subShop.getProducts().isEmpty()) {
            int currentSlot = startOfContainer;
            int index = 0;
            for (Product product : subShop.getProducts()) {
                if (index >= maxProductsPerPage*(page-1)) {
                    if ((currentSlot + 1) % 9 == 0) currentSlot += 2;
                    if (currentSlot >= endOfContainer) break;

                    ItemStack item =  product.getItem();
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        List<String> lore = new ArrayList<>();
                        lore.add(Messages.convertCodes("&7Costs: &2&l$&f" + product.getPrice()));
                        if (product.getSellPrice() != null) lore.add(Messages.convertCodes("&7Sell: &2&l$&f" + product.getSellPrice()));
                        lore.add(Messages.convertCodes("&8&l&m-----------------"));
                        lore.add(Messages.convertCodes("&7Left Click &fto buy."));
                        lore.add(Messages.convertCodes("&7SHIFT + Left Click &fto buy multiple."));
                        if (product.getSellPrice() != null) lore.add(Messages.convertCodes("&7Right click &fto sell."));
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

        // Close
        ItemMeta meta;
        ItemStack itemStack;
        itemStack = new ItemStack(Material.BARRIER);
        meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Messages.convertCodes("&cGo back to shop."));
        meta.setLore(List.of(Messages.convertCodes("&7Click to go back to shop.")));
        itemStack.setItemMeta(meta);
        inventory.setItem(45, itemStack);

        // Back Page
        itemStack = new ItemStack(Material.STRUCTURE_VOID);
        meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Messages.convertCodes("&c&lBack"));
        meta.setLore(List.of(Messages.convertCodes("&7Click to go back to the previous page.")));
        itemStack.setItemMeta(meta);
        if (page > 1) {inventory.setItem(45, itemStack);}

        // Next page
        itemStack = new ItemStack(Material.ARROW);
        meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Messages.convertCodes("&a&lNext Page"));
        meta.setLore(List.of(Messages.convertCodes("&7Click to go to the next page.")));
        itemStack.setItemMeta(meta);
        if (subShop.getProducts().size() - (28*page) > 28) {inventory.setItem(54, itemStack);}

        // Info
        itemStack = new ItemStack(Material.NETHER_STAR);
        meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Messages.convertCodes("&a&l Your Information"));
        meta.setLore(List.of(Messages.convertCodes("&7Coming soon")));
        itemStack.setItemMeta(meta);
        inventory.setItem(49, itemStack);

        customer.setOpenedSubShop(subShop);

        return inventory;
    }

    public static Inventory getShopInventory(Player player, int page) {
        String title = config.getString("shop-title") + " &7(" + page + ")";
        if (title == null || title.isEmpty()) {title = "&a&lShop";}
        Inventory inventory = Bukkit.createInventory(null, 54, Messages.convertCodes(title));
        fillInventory(inventory);
        Shop.getCustomer(player.getUniqueId()).clearSubShopSlots();
        if (!subShops.isEmpty()) {
            int passedLoops = 0;
            int startIndex = subShops.size()*(page-1);
            for (int i = 0; i < subShops.size(); i++) {
                if (i >= startIndex && passedLoops < subShopSlots.size()) {
                    SubShop subShop = subShops.get(i);
                    ItemStack itemStack = new ItemStack(subShop.getIcon());
                    ItemMeta meta = itemStack.getItemMeta();
                    meta.setDisplayName(Messages.convertCodes(subShop.getName()));
                    meta.setLore(List.of(Messages.convertCodes("&7Click to open this subshop.")));
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                    itemStack.setItemMeta(meta);
                    inventory.setItem(subShopSlots.get(passedLoops), itemStack);
                    Shop.getCustomer(player.getUniqueId()).addSubShopSlot(subShopSlots.get(passedLoops), subShop.getName());
                    passedLoops++;
                }
            }
        }

        // Close
        ItemMeta meta;
        ItemStack itemStack;
        itemStack = new ItemStack(Material.BARRIER);
        meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Messages.convertCodes("&4&lExit Shop"));
        meta.setLore(List.of(Messages.convertCodes("&7Click to exit.")));
        itemStack.setItemMeta(meta);
        inventory.setItem(45, itemStack);

        // Back Page
        itemStack = new ItemStack(Material.STRUCTURE_VOID);
        meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Messages.convertCodes("&c&lBack"));
        meta.setLore(List.of(Messages.convertCodes("&7Click to go back to the previous page.")));
        itemStack.setItemMeta(meta);
        if (page > 1) {inventory.setItem(45, itemStack);}

        // Next page
        itemStack = new ItemStack(Material.ARROW);
        meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Messages.convertCodes("&a&lNext Page"));
        meta.setLore(List.of(Messages.convertCodes("&7Click to go to the next page.")));
        itemStack.setItemMeta(meta);
        if (subShops.size() - (subShopSlots.size()*page) > subShopSlots.size()) {inventory.setItem(54, itemStack);}

        // Info
        itemStack = new ItemStack(Material.NETHER_STAR);
        meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Messages.convertCodes("&a&l Your Information"));
        meta.setLore(List.of(Messages.convertCodes("&7Coming soon")));
        itemStack.setItemMeta(meta);
        inventory.setItem(49, itemStack);

        // Setting Customer stuff
        Customer customer = Shop.getCustomer(player.getUniqueId());
        if (customer == null) return inventory;
        customer.setPage(page);
        customer.setOpenedSubShop(null);
        customer.clearProductSlots();

        return inventory;
    }
    public static Inventory getShopInventory(Player player) { return getShopInventory(player, 1); }

    public static List<SubShop> getSubShops() {
        return subShops;
    }

    public static void removeSubShop(String name) {
        subShops.remove(getSubShopFromName(name));
    }

    public static boolean isSubshopNameTaken(String name) {
        return subShopNames.containsKey(name);
    }

    public static SubShop getSubShopFromName(String name) {
        return subShopNames.get(name);
    }


    public static void addSubShop(SubShop subShop) {
        subShops.add(subShop);
        subShopNames.put(subShop.getName(), subShop);
    }

    public static void setSubShops(List<SubShop> subShops) {
        Shop.subShops = subShops;
    }




}
