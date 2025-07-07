package com.anonymouslyfast.basicCustomShop.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ShopAdmin {

    private Product product;
    private Material material;
    private String shopName;
    private Shop shop;

    private final Player player;

    public ShopAdmin(Player player, Shop shop, Material material) {
        this.player = player;
        this.material = material;
        this.shop = shop;
    }

    public ShopAdmin(Player player, Shop shop, Product product) {
        this.player = player;
        this.product = product;
        this.shop = shop;
    }

    public ShopAdmin(Player player, String shopName) {
        this.player = player;
        this.shopName = shopName;
    }

    public Product getProduct() {
        return product;
    }

    public Material getMaterial() {
        return material;
    }

    public Shop getShop() {
        return shop;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


    public Player getPlayer() {
        return player;
    }

    public String getShopName() {
        return shopName;
    }
}
