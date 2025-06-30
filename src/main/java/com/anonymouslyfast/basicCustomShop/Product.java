package com.anonymouslyfast.basicCustomShop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class Product {

    private final ItemStack item;
    private double price;
    private Double sellPrice;

    public Product(ItemStack item, double price) {
        this.item = item;
        this.price = price;
    }

    public Product(ItemStack item) {
        this.item = item;
    }


    public ItemStack getItem() {return item;}

    public double getPrice() {return price;}
    public void setPrice(double price) {this.price = price;}

    public void setSellPrice(Double sellPrice) {this.sellPrice = sellPrice;}
    public Double getSellPrice() {return sellPrice;}

}
