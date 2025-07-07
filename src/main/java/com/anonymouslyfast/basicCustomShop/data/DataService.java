package com.anonymouslyfast.basicCustomShop.data;

import com.anonymouslyfast.basicCustomShop.shop.Product;
import com.anonymouslyfast.basicCustomShop.shop.Shop;

public interface DataService {
    void loadSubShops();
    void saveSubShops();
    boolean saveSubShop(Shop shop);
    void removeSubShop(Shop shop);
    void removeProduct(Product product);
}
