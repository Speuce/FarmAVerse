package com.speuce.farmtopia.farm.handlers;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.farm.FarmManager;
import com.speuce.farmtopia.main.FarmTopia;
import com.speuce.farmtopia.plot.FarmPlot;
import com.speuce.farmtopia.plot.Plot;
import com.speuce.farmtopia.resources.Resource;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * Handles different types of interactions that may occur on a
 * players farm.
 */
public class PhysicalInteractionHandler implements Listener {

    private FarmManager manager;

    private Random r;


    public PhysicalInteractionHandler(FarmManager manager) {
        this.manager = manager;
        FarmTopia.registerListener(this);
        r = new Random();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.EXPERIENCE_BOTTLE
                && e.getAction().toString().contains("RIGHT")) {
            Resource s = Resource.getByItem(e.getPlayer().getInventory().getItemInMainHand());
            if (s != null) {
                if (s == Resource.XP_BOTTLE_SMALL) {
                    manager.getFarm(e.getPlayer()).addExp(r.nextInt(10) + 10);
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
        if ((e.getClickedBlock() != null) && e.getHand() != null && e.getHand().equals(EquipmentSlot.HAND)) {
            // Bukkit.broadcastMessage("Interact");
            Farm f = manager.getFarmAtLocation(e.getClickedBlock().getLocation());
            if (f != null) {
                if (f.getOwner() == e.getPlayer() || e.getPlayer().hasPermission("farm.click.others")) {
                    Plot plot = f.getPlot(e.getClickedBlock().getChunk());
                    if (plot != null) {
                        plot.onInteractOwner(e);
                    }
                } else {
                    e.getPlayer().sendMessage(ChatColor.RED + "This is not your farm!");
                    e.setCancelled(true);
                }

            }
        } else if (e.getAction() == Action.PHYSICAL) {
            if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.FARMLAND) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (e.getEntityType() == EntityType.PLAYER && e.getItem().getItemStack().getType() == Material.BOW) {
            ItemStack s = e.getItem().getItemStack();
            Player p = (Player) e.getEntity();
            Constant.forceGive(p, s);
            // Bukkit.broadcastMessage(s.getAmount() + "");
            Constant.playsound(p, Sound.ENTITY_ITEM_PICKUP, 1F);
            e.setCancelled(true);
            // e.getItem().setItemStack(new ItemStack(Material.AIR));
            e.getItem().remove();
            // e.setCancelled(true);
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
        if (e.isSneaking()) {
            Farm f = manager.getFarmAtLocation(e.getPlayer().getLocation());
            if (f != null) {
                Plot plot = f.getPlot(e.getPlayer().getLocation().getChunk());
                if (plot instanceof FarmPlot) {
                    FarmPlot fp = (FarmPlot) plot;
                    fp.onShift(e.getPlayer().getLocation());
                }

            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        Farm f = manager.getFarmAtLocation(e.getRightClicked().getLocation());
        Plot plot = f.getPlot(e.getRightClicked().getLocation().getChunk());
        if (plot != null) {
            plot.onEntityInteract(e);
        }

    }
}
