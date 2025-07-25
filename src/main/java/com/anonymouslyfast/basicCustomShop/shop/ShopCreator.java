package com.anonymouslyfast.basicCustomShop.shop;

import com.anonymouslyfast.basicCustomShop.utils.MessageUtils;
import org.bukkit.entity.Player;

public final class ShopCreator {
    private final static ShopCreator instance = new ShopCreator();

    public static ShopCreator getInstance() {
        return instance;
    }

    public enum CreatingType {
        PRODUCT,
        SHOP
    }

    public void addPlayer(Player player, CreatingType creatingType) {
        PlayerTracking.PlayerStatus status = null;
        if (creatingType == CreatingType.PRODUCT) {
            status = PlayerTracking.PlayerStatus.CREATINGPRODUCT;
            player.sendMessage(MessageUtils.getMessage("&fPlease &7right click while holding a item &fthat your would like to sell in this shop"));
        } else if (creatingType == CreatingType.SHOP) {
            status = PlayerTracking.PlayerStatus.CREATINGSHOP;
            player.sendMessage(MessageUtils.getMessage("&fPlease &7right click &fwith a item to set as a icon for the shop. &7(Only takes the material of the item.)"));
        }
        player.sendMessage(MessageUtils.getMessage("&fSend &7`cancel` &for &7`exit` &fto exit out of this creator."));
        PlayerTracking.updatePlayerStatus(player.getUniqueId(), status);
    }





}
