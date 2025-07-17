package com.anonymouslyfast.basicCustomShop.data;

import com.anonymouslyfast.basicCustomShop.shop.Product;
import com.anonymouslyfast.basicCustomShop.shop.Shop;

public interface DataService {
    void loadShops();
    void saveShops();
    boolean saveShop(Shop shop);
    void removeShop(Shop shop);
    void removeProduct(Product product);
}
