package com.speuce.farmtopia.farm.handlers;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.farm.FarmManager;
import com.speuce.farmtopia.farm.Tutorial;
import com.speuce.farmtopia.main.FarmTopia;
import com.speuce.farmtopia.plot.EmptyPlot;
import com.speuce.farmtopia.plot.Plot;
import com.speuce.farmtopia.plot.PlotBuilder;
import com.speuce.farmtopia.plot.Plots;
import com.speuce.farmtopia.plot.upgradeable.ResearchCentre;
import com.speuce.farmtopia.plot.upgradeable.Upgradeable;
import com.speuce.farmtopia.util.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.util.Vector;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages iventory clicks for various farm inventories.
 */
public class InventoryHandler implements Listener {

    private FarmManager manager;

    public InventoryHandler(FarmManager manager) {
        this.manager = manager;
        FarmTopia.registerListener(this);
    }

    /**
     * Handles a click in the 'set plot type' menu
     * @param e
     */
    private void handleSetPlotMenuClick(InventoryClickEvent e){
        e.setCancelled(true);
        if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
            double cost = -1D;
            for (String s : e.getCurrentItem().getItemMeta().getLore()) {
                if (s.contains("Cost")) {
                    if (s.contains("FREE")) {
                        cost = 0D;
                        break;
                    } else {
                        Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");
                        Matcher m = p.matcher(s);
                        if (m.find()) {
                            cost = Double.parseDouble(m.group(1));
                        }
                        break;
                    }

                }
            }
            if (cost > -1) {
                Class<? extends Plot> clazz = Plots
                        .getFromName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                if (clazz == null) {
                    throw new NullPointerException("Couldn't find plot: "
                            + ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));

                }
                if (Economy.hasEnough(e.getWhoClicked().getUniqueId(), cost)) {
                    Economy.subtractBal(e.getWhoClicked().getUniqueId(), cost);
                    manager.getFarm(((Player) e.getWhoClicked())).plotChange(clazz);
                    e.getWhoClicked().sendMessage(ChatColor.GREEN.toString() + "Success!");
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().setVelocity(new Vector(0, 2, 0));
                } else {
                    e.getWhoClicked().sendMessage(ChatColor.RED.toString() + "You need "
                            + NumberFormat.getCurrencyInstance().format(cost) + " to buy that!");
                }
            }
        }
    }


    /**
     * Handles a click event in the upgrade building menu
     */
    private void handleUpgradeMenuClick(InventoryClickEvent e){
        e.setCancelled(true);
        if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
            if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Cancel")) {
                e.getWhoClicked().closeInventory();
            } else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Buy")) {
                Player pl = (Player) e.getWhoClicked();
                Farm f = manager.getFarm(pl);
                Plot p = f.getPlot(e.getWhoClicked().getLocation().getChunk());
                if (p instanceof Upgradeable) {
                    Upgradeable up = (Upgradeable) p;
                    // Bukkit.broadcastMessage("purchase: " +
                    // up.canPurchase());
                    if (up.canPurchase()) {
                        int cost = up.getCost(up.getLv());
                        if (Economy.hasEnough(pl.getUniqueId(), (double) cost)) {
                            Economy.subtractBal(pl.getUniqueId(), (double) cost);
                            up.upgrade();
                        } else {
                            pl.sendMessage(ChatColor.RED + "Sorry, you need $" + cost + " for that!");
                            pl.closeInventory();
                        }
                    } else {
                        pl.playSound(pl.getLocation(), Sound.BLOCK_ANVIL_LAND, 2F, 0F);
                    }

                }
            }
        }
    }

    /**
     * Handles a click in the farm menu
     */
    private void handleFarmMenuClick(InventoryClickEvent e){
        e.setCancelled(true);
        if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
            if (e.getCurrentItem().getItemMeta().getDisplayName().contains("another Plot")) {
                Player pl = (Player) e.getWhoClicked();
                Farm f = manager.getFarm(pl);
                double cost = f.getCostt();
                if (Economy.hasEnough(pl.getUniqueId(), cost)) {
                    Plot ne = new EmptyPlot(f);
                    PlotBuilder pb = new PlotBuilder(ne, FarmTopia.getFarmTopia().getSchem(), f.getCurrentChunk());
                    pb.build(true);
                    f.buildWalls(ne, true);
                    f.nextChunk();
                    f.addPlot(ne);
                    Tutorial.onNewPlotBuild((Player) e.getWhoClicked());
                    Economy.subtractBal(pl.getUniqueId(), cost);
                    pl.getWorld().playSound(pl.getLocation(), Sound.BLOCK_ANVIL_USE, 3.5F, 0F);
                    pl.sendMessage(ChatColor.GREEN.toString() + "Plot added!");
                    pl.closeInventory();
                } else {
                    pl.sendMessage(ChatColor.RED.toString() + "You don't have enough for that!");
                    return;
                }
            } else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Jobs")) {
                Player pl = (Player) e.getWhoClicked();
                Bukkit.dispatchCommand(pl, "j");
            }
        }
    }


    /**
     * Handles a click in the 'harvested' menu
     */
    private void handleHarvestedClick(InventoryClickEvent e){
        if (e.getCurrentItem() == null) {
            return;
        }
        e.setCancelled(true);
        if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) {
            return;
        }
        if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Ok")) {
            e.getWhoClicked().closeInventory();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        // Bukkit.broadcastMessage("clicked");
        if (e.getClickedInventory() != null) {
            if (e.getView().getTitle().contains("Harvested")) {
                handleHarvestedClick(e);
            } else if (e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_PURPLE.toString() + "Farm Menu")) {
                handleFarmMenuClick(e);
            } else if (e.getView().getTitle().startsWith("Upgrade")) {
                handleUpgradeMenuClick(e);
            } else if (e.getView().getTitle().equals(Constant.setPlotName)) {
                handleSetPlotMenuClick(e);
            }
        }
    }
}
