package main.java.com.speuce.farmtopia.main;

import main.java.com.speuce.farmtopia.event.farm.plot.FarmPlotInteractEvent;
import main.java.com.speuce.farmtopia.event.farm.plot.FarmSubPlotInteractEvent;
import main.java.com.speuce.farmtopia.plot.subplot.FarmSubPlot;
import main.java.com.speuce.farmtopia.resources.Resource;
import main.java.com.speuce.farmtopia.util.Constant;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handles Dev-related features
 * @author matt
 */
public class DevTools implements Listener {

    public DevTools(){

    }

    @EventHandler
    public void onPlotInteract(FarmSubPlotInteractEvent e){
        if(e.getPlayer().isOp()){
             if(Resource.getByItem(e.getItem()).equals(Resource.DEV_WAND)){
                 FarmSubPlot sb = e.getSubPlot();
                 if (!sb.max()) {
                     boolean bl = sb.hasActualPlant();
                     if (bl) {
                         sb.dev();
                         Constant.playsound(e.getPlayer(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 0F);
                         e.getPlot().updateStages(true);
                     } else {
                         e.getPlayer().sendMessage(ChatColor.RED.toString() + "There's nothing to apply magic to!");
                     }
                 } else {
                     e.getPlayer().sendMessage(ChatColor.RED + "This Crop is already fully grown");
                 }
             } else if(e.getPlayer().isSneaking() && e.getOriginatingEvent().getAction() == Action.RIGHT_CLICK_BLOCK){
                 e.getPlayer().sendMessage(ChatColor.AQUA.toString() + "Fertility: " + ChatColor.YELLOW.toString()
                         + e.getSubPlot().getFertility());
             }
        }

    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (e.getPlayer().isOp() && e.getPlayer().isSneaking()) {
            ItemStack s = e.getPlayer().getInventory().getItemInMainHand();
            if (Resource.getByItem(s).equals(Resource.DEV_WAND)) {
                if (Constant.hasDebug(e.getPlayer())) {
                    Constant.removeDebug(e.getPlayer());
                    e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE.toString() + "Debug Text Disabled.");
                } else {
                    Constant.addDebug(e.getPlayer());
                    e.getPlayer().sendMessage(ChatColor.GREEN.toString() + "Debug Text Enabled.");
                }
            }
        }
    }

}
