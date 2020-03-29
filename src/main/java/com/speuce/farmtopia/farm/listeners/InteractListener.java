package main.java.com.speuce.farmtopia.farm.listeners;

import main.java.com.speuce.farmtopia.event.Events;
import main.java.com.speuce.farmtopia.event.farm.plot.FarmPlotInteractEvent;
import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.farm.FarmManager;
import main.java.com.speuce.farmtopia.plot.Plot;
import main.java.com.speuce.farmtopia.resources.Resource;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Random;

/**
 * Listener for interact events occuring on farms
 * @author matt
 */
public class InteractListener implements Listener {

    private FarmManager fm;
    private Random r;

    public InteractListener(FarmManager fm) {
        this.fm = fm;
        this.r = new Random();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.EXPERIENCE_BOTTLE
                && e.getAction().toString().contains("RIGHT")) {
            Resource s = Resource.getByItem(e.getPlayer().getInventory().getItemInMainHand());
            if (s != null) {
                if (s == Resource.XP_BOTTLE_SMALL) {
                    fm.getFarm(e.getPlayer()).addExp(r.nextInt(10) + 10);
                    int amt = e.getPlayer().getInventory().getItemInMainHand().getAmount();
                    if (amt > 1) {
                        e.getPlayer().getInventory().getItemInMainHand().setAmount(amt - 1);
                    } else {
                        e.getPlayer().getInventory().setItemInMainHand(null);
                    }
                }
            }
            e.setCancelled(true);
            return;
        }
        if ((e.getHand() == EquipmentSlot.HAND) && (e.getClickedBlock() != null)) {
            Farm f = fm.getFarm(e.getClickedBlock().getLocation());
            if (f != null) {
                Plot plot = f.getPlot(e.getClickedBlock().getChunk());
                if(plot != null){
                    Events.call(new FarmPlotInteractEvent(f, plot, e));
                    if (f.getOwner() == e.getPlayer()) {
                        plot.onInteractOwner(e);
                    } else {
                        e.getPlayer().sendMessage(ChatColor.RED + "This is not your farm!");
                        e.setCancelled(true);
                    }
                }
            }
        } else if (e.getAction() == Action.PHYSICAL) {
            if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.FARMLAND) {
                e.setCancelled(true);
            }
        }
    }
}
