package com.anonymouslyfast.basicCustomShop.shop;

import java.util.HashMap;
import java.util.UUID;

public final class PlayerTracking {

    public enum PlayerStatus {
        CREATINGSHOP,
        CREATINGPRODUCT,
        BUYINGMULTIPLE,
        SELLING,
        INSHOPGUI,
        INMAINSHOPGUI,
        SWITCHINGINVENTORY
    }


    private static final HashMap<UUID, PlayerStatus> playerStatuses = new HashMap<>();

    public static void updatePlayerStatus(UUID uuid, PlayerStatus playerStatus) {
        removePlayer(uuid);
        addPlayer(uuid, playerStatus);
    }

    public static void addPlayer(UUID uuid, PlayerStatus playerStatus) {
        playerStatuses.put(uuid, playerStatus);
    }
    public static void removePlayer(UUID uuid) {
        playerStatuses.remove(uuid);
    }
    public static PlayerStatus getPlayerStatus(UUID uuid) {
        return playerStatuses.get(uuid);
    }

}
