package com.anonymouslyfast.basicCustomShop.shop;

import org.bukkit.Material;

import java.util.UUID;

public class Product {

    private final Material material;
    private double price;
    private Double sellPrice;

    private final UUID uuid;

    public Product(Material material, double price, Double sellPrice, UUID uuid) {
        this.sellPrice = sellPrice;
        this.material = material;
        this.price = price;
        this.uuid = uuid;
    }

    public Product(Material material, double price, Double sellPrice) {
        this.sellPrice = sellPrice;
        this.material = material;
        this.price = price;
        this.uuid = UUID.randomUUID();
    }

    public Product(Material material, double price) {
        this.material = material;
        this.price = price;
        this.uuid = UUID.randomUUID();
    }

    public Product(Material material) {
        this.material = material;
        this.uuid = UUID.randomUUID();
    }


    public Material getMaterial() {return material;}

    public double getPrice() {return price;}
    public void setPrice(double price) {this.price = price;}

    public void setSellPrice(Double sellPrice) {this.sellPrice = sellPrice;}
    public Double getSellPrice() {return sellPrice;}
    public boolean isSellable() {
        boolean sellable = getSellPrice() != null;
        if (sellable) sellable = getSellPrice() != 0d;
        return sellable;
    }

    public UUID getUuid() {
        return uuid;
    }
}
