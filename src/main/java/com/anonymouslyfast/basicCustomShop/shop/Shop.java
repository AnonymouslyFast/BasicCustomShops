package com.anonymouslyfast.basicCustomShop.shop;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Shop {

    private final Material icon;
    private final String name;
    private final UUID uuid;
    private boolean isEnabled = true;

    private List<Product> products = new ArrayList<>();

    public Shop(String name, Material icon, UUID uuid) {
        this.name = name;
        this.icon = icon;
        this.uuid = uuid;
    }

    public Shop(String name, Material icon) {
        this.name = name;
        this.icon = icon;
        this.uuid = UUID.randomUUID();
    }

    public String getName() {return name;}
    public UUID getUuid() {return uuid;}
    public Material getIcon() {return icon;}

    public List<Product> getProducts() {return products;}
    public void setProducts(List<Product> products) {this.products = products;}
    public void addProduct(Product product) {this.products.add(product);}

    public void removeProduct(Product product) {this.products.remove(product);}

    public boolean isEnabled() {return isEnabled;}
    public void setEnabled(boolean enabled) {this.isEnabled = enabled;}

}
