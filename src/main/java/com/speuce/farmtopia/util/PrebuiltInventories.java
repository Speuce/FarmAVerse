package main.java.com.speuce.farmtopia.util;

import main.java.com.speuce.farmtopia.farm.Farm;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

/**
 * Place for static inventory-creating functions to be placed.
 * @author Matt Kwiatkowski
 */
public class PrebuiltInventories {

    /**
     * Build the farm main menu for the provided farm.
     * @param f the Farm whose menu will be built
     * @return an {@link Inventory} that will have options for the player realated to their farm
     */
    public static Inventory getFarmHomeMenu(Farm f){
        Inventory ret = Bukkit.createInventory(null, 9, ChatColor.DARK_PURPLE.toString() + "Farm Menu");

        ItemStack add = new ItemStack(Material.ANVIL, 1);
        ItemMeta am = add.getItemMeta();
        assert am != null;
        am.setDisplayName(ChatColor.AQUA.toString()+ ChatColor.BOLD.toString() + "Buy another Plot");
        am.setLore(Collections.singletonList(ChatColor.GREEN.toString() + "Cost: " + ChatColor.DARK_GREEN.toString() + main.java.com.speuce.farmtopia.util.Constant.format(f.getUpgradeCost())));
        add.setItemMeta(am);

        ItemStack a2dd = new ItemStack(Material.BOOK, 1);
        ItemMeta a2m = add.getItemMeta();
        a2m.setDisplayName(ChatColor.GREEN.toString() + "Jobs");
        a2m.setLore(Collections.singletonList(ChatColor.GOLD.toString() + "$$$"));
        a2dd.setItemMeta(a2m);

        ret.setItem(0, add);
        ret.setItem(1, a2dd);

        return ret;
    }
}
