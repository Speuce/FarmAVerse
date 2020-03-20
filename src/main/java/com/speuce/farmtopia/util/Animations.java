package main.java.com.speuce.farmtopia.util;

import main.java.com.speuce.farmtopia.main.FarmTopia;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Class containing all animation methods. Code
 * Can be rather cumbersome. Ideally all methods should
 * be static here.
 * @author Matt Kwiatkowski
 */
public class Animations {

    /**
     * Plays the Farm-Harvested item inventory animation
     * @param p the player to play the animation to
     * @param items the items in the animation
     * @param frequency the time in between each item change
     */
    public static void playHarvestAni(Player p, ItemStack[] items, long frequency){
        Inventory i = Bukkit.createInventory(null, 9, ChatColor.DARK_GREEN + "You Harvested: ");
        p.openInventory(i);
        BukkitRunnable br = new BukkitRunnable(){
            int ca = 1;
            int slot = 0;
            int amt = items[0].getAmount();
            @Override
            public void run() {
                p.getOpenInventory();
                if(!p.getOpenInventory().getTitle().contains("Harvested")){
                    p.openInventory(i);
                }
                if(slot < items.length){
                    ItemStack it = items[slot];
                    it.setAmount(ca);
                    i.setItem(slot, it);
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2F, 2F);
                    ca++;
                    if(ca > amt){
                        ca = 1;
                        slot++;
                        if(slot < items.length){
                            amt = items[slot].getAmount();
                        }
                    }
                }else{
                    i.setItem(8, main.java.com.speuce.farmtopia.util.Constant.getOk());
                    this.cancel();
                }

            }

        };
        br.runTaskTimer(FarmTopia.getFarmTopia(), 7L, frequency);
    }

}
