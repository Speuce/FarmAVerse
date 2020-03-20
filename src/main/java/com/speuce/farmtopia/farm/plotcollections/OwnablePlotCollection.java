package main.java.com.speuce.farmtopia.farm.plotcollections;


import main.java.com.speuce.farmtopia.farm.Tutorial;
import main.java.com.speuce.farmtopia.farm.plotcollections.PlotCollection;
import main.java.com.speuce.farmtopia.resources.Resource;
import main.java.com.speuce.farmtopia.util.Constant;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * A plot collection that can be owned by a Player.
 * Also has XP-related functionality
 */
public abstract class OwnablePlotCollection extends PlotCollection {

    //the owner
    private Player owner;
    //the level attained with xp
    private int lvl;
    //the progress to the next level attained with xp
    private byte progress;

    public OwnablePlotCollection(Location baseLocation, int size, Player owner, int lvl, byte progress) {
        super(baseLocation, size);
        this.owner = owner;
        this.lvl = lvl;
        this.progress = progress;
    }

    /**
     * Get the owner of this collection of plots.
     */
    public Player getOwner(){
        return this.owner;
    }

    /**
     * Get the current Xp-based level of this plot collection
     */
    public int getLevel() {
        return lvl;
    }

    public int getProgress() {
        return this.progress&255;
    }

    /**
     * Adds Farm xp to the given owner of this farm
     * @param xp the amount of xp to add.
     */
    public void addExp(int xp){
        int add = (int) Math.round((xp)/((1 + lvl)*0.2));
        int val =  add+ (progress&255);
        if(val > 0){
            getOwner().sendMessage(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD.toString()  + "+ " + add + " exp");
        }
        while(val >= 255){
            val -= 255;

            lvl++;
            Constant.forceGive(getOwner(), Resource.MAGIC_DUST.toItemStack(5));
            Tutorial.onLevelUp(getOwner(), lvl);
            getOwner().sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "LEVEL UP: " + ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + lvl);
            getOwner().playSound(getOwner().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 1.6F);
        }
        progress = (byte)val;
        //Bukkit.broadcastMessage("prog: " + progress);
        getOwner().setLevel(lvl);
        getOwner().setExp(this.getProgress()/255F);
    }

    /**
     * Removes xp to the given owner of this plot collection
     * @param xp the amount of xp to remove.
     */
    public void subtractExp(int xp){
        int val =  (progress&255) - xp;
        //Bukkit.broadcastMessage("subtract: "+ xp);
        getOwner().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString()  + "- " + xp + " exp");

        while(val < 0){
            if(lvl == 0){
                progress = (byte)0;
                getOwner().setLevel(lvl);
                getOwner().setExp(this.getProgress()/255F);
                return;
            }
            val += 255;
            lvl--;
            getOwner().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "LEVEL DOWN: " + ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + lvl);
            getOwner().playSound(getOwner().getLocation(), Sound.ENTITY_PLAYER_BURP, 2F, 0F);
        }
        progress = (byte)val;

        //Bukkit.broadcastMessage("prog: " + progress);
        getOwner().setLevel(lvl);
        getOwner().setExp(this.getProgress()/255F);
    }
}
