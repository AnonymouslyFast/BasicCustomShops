# Welcome to BasicCustomShops
Hello! This is a project that I made for practice, this isn't really meant to be useful in anyway, but you're free to use it at your own risk of bugs. 

<b>This plugin depends on the plugin Vault, you will also need a plugin to setup Economy on vault like the plugin Essentials since this plugin just uses the Economy.</b>

> This plugin is meant for Minecraft 1.21 as of current release, again please don't try this plugin on a production server!

## Commands

This plugin is mainly configured by commands to do stuff like reloading, and creating subshops and products. To access the shopmanager you would have to have the permission `BCS.shopmanager` or be oped.

```
/shop - Opens the shop GUI where players can buy and sell goods
/shopmanager - Requires permission `BCS.shopmanager` or op to use aswell as it's subcommands
/shopmanager help - Brings up a help message
/shopmanager saveshops - saves all shops and products into the database.
/shopmanager createshop <String> - creates a shop with the given name
/shopmanager createproduct <String> - puts you in a product creation tool that will guide you through creating a product for the given shop.
/subshop deleteshop <String> - deletes the given shop
/shopmanager togglevisibility <String> &7- Toggles whether the given shop is visable or hidden to the public.
```

## GUI Actions
This plugin uses guis to visualize the shops and have the players buy and sell products. Players with the permission `BCS.shopmanager` or ops can delete subshops and products in the gui aswell.

# For Developers
Hey! This plugin is currently opensource and you have free will to do anything you want with this code. Please just note that this is a poorly made shop plugin as I don't have much experience in plugin making.

You're free to contribute or fork this plugin, idrc, this code *should* be easy to work with aswell in another plugin aswell.