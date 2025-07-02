package com.anonymouslyfast.basicCustomShop.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Product {

    private final Material material;
    private double price;
    private Double sellPrice;

    private UUID uuid;

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

    public UUID getUuid() {
        return uuid;
    }
}
