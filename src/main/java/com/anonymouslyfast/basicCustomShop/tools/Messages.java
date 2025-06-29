package com.anonymouslyfast.basicCustomShop.tools;

import com.anonymouslyfast.basicCustomShop.BasicCustomShops;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messages {

    public static String convertCodes(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String getMessage(String msg) {
        return convertCodes(BasicCustomShops.getInstance().messagePrefix + " " + msg);
    }

}
