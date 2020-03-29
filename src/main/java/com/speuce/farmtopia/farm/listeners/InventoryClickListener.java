package main.java.com.speuce.farmtopia.farm.listeners;

import main.java.com.speuce.farmtopia.event.Events;
import main.java.com.speuce.farmtopia.event.farm.plot.FarmPlotPurchaseEvent;
import main.java.com.speuce.farmtopia.event.farm.plot.FarmPlotUpgradeEvent;
import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.farm.FarmManager;
import main.java.com.speuce.farmtopia.main.DebugLevel;
import main.java.com.speuce.farmtopia.main.FarmTopia;
import main.java.com.speuce.farmtopia.plot.EmptyPlot;
import main.java.com.speuce.farmtopia.plot.Plot;
import main.java.com.speuce.farmtopia.plot.PlotBuilder;
import main.java.com.speuce.farmtopia.plot.Plots;
import main.java.com.speuce.farmtopia.plot.upgradeable.ResearchCentre;
import main.java.com.speuce.farmtopia.plot.upgradeable.Upgradeable;
import main.java.com.speuce.farmtopia.util.Constant;
import main.java.com.speuce.farmtopia.util.Economy;
import main.java.com.speuce.schemetic.SchemeticManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handler for inventory click events pertaining to farm menu's etc.
 * @author matt
 */
public class InventoryClickListener implements Listener {

    private FarmManager fm;
    private SchemeticManager schem;

    public InventoryClickListener(FarmManager fm, SchemeticManager schem) {
        this.fm = fm;
        this.schem = schem;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null){
            return;
        }
        String title = e.getView().getTitle();
        if (title.contains("Harvested")) {
            handleHarvestMenuClick(e);
        }else if (title.equalsIgnoreCase(Constant.FARM_MENU_NAME)) {
            e.setCancelled(true);
            handleFarmMenuClick(e);
        } else if (e.getView().getTitle().startsWith("Upgrade")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
                handleUpgradeMenuClick(e);
            }
        } else if(title.equals(Constant.SET_PLOT_NAME)) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
                handleSetPlotClick(e);
            }
        }
    }

    /**
     * Handles an inventory click in a harvest menu
     */
    private void handleHarvestMenuClick(InventoryClickEvent e){
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

    /**
     * Handles an inventory click in the farm main menu.
     */
    private void handleFarmMenuClick(InventoryClickEvent e){
        if(e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()){
            return;
        }
        Player pl = (Player) e.getWhoClicked();
        assert(e.getCurrentItem().getItemMeta() != null);
        String name = e.getCurrentItem().getItemMeta().getDisplayName();
            if (name.contains("another Plot")) {
                Farm f = fm.getFarm(pl);
                double cost = f.getUpgradeCost();
                if (Economy.hasEnough(pl.getUniqueId(), cost)) {
                    Economy.subtractBal(pl.getUniqueId(), cost);
                    Plot ne = new EmptyPlot(f);

                    PlotBuilder pb = new PlotBuilder(ne,schem, f.addChunk());
                    pb.build(true);
                    f.buildWalls(ne, true);
                    f.addPlot(ne);

                    Events.call(new FarmPlotPurchaseEvent(f, ne));

                    pl.getWorld().playSound(pl.getLocation(), Sound.BLOCK_ANVIL_USE, 3.5F, 0F);
                    pl.sendMessage(ChatColor.GREEN.toString() + "Plot added!");
                    pl.closeInventory();
                } else {
                    pl.sendMessage(ChatColor.RED.toString() + "You don't have enough for that!");
                }
            } else if (name.contains("Jobs")) {
                Bukkit.dispatchCommand(pl, "j");
            }
    }

    /**
     * Handles a click in the upgrade menu of any given plot
     */
    private void handleUpgradeMenuClick(InventoryClickEvent e){
        assert(e.getCurrentItem() != null);
        assert(e.getCurrentItem().getItemMeta() != null);
        String name = e.getCurrentItem().getItemMeta().getDisplayName();
        if (name.contains("Cancel")) {
            e.getWhoClicked().closeInventory();
        } else if (name.contains("Buy")) {
            Player pl = (Player) e.getWhoClicked();
            Farm f = fm.getFarm(pl);
            Plot p = f.getPlot(e.getWhoClicked().getLocation().getChunk());
            if (p instanceof Upgradeable) {
                Upgradeable up = (Upgradeable) p;
                if (up.canPurchase()) {
                    int cost = up.getCost(up.getLv());
                    if (Economy.hasEnough(pl.getUniqueId(), (double) cost)) {
                        Economy.subtractBal(pl.getUniqueId(), (double) cost);
                        Events.call(new FarmPlotUpgradeEvent(f, p, up.getLv()+1));
                        up.upgrade();
                    } else {
                        pl.sendMessage(ChatColor.RED + "Sorry, you need $" + cost + " for that!");
                        pl.closeInventory();
                    }
                } else {
                    pl.playSound(pl.getLocation(), Sound.BLOCK_ANVIL_LAND, 2F, 0F);
                }
            }else{
                FarmTopia.getFarmTopia().debug(DebugLevel.SEMI, "Player tried to upgrade in a not upgradeable plot?");
            }
        }
    }

    /**
     * Handle when a player clicks in the "set plot" menu
     */
    private void handleSetPlotClick(InventoryClickEvent e){
        assert(e.getCurrentItem() != null);
        assert(e.getCurrentItem().getItemMeta() != null);
        double cost = findCost(e.getCurrentItem().getItemMeta().getLore());
        String name = e.getCurrentItem().getItemMeta().getDisplayName();
        if (cost > -1) {
            Class<? extends Plot> clazz = Plots.getFromName(ChatColor.stripColor(name));
            if (clazz == null) {
                throw new NullPointerException("Couldn't find plot: "
                        + ChatColor.stripColor(name));

            }
            if (Economy.hasEnough(e.getWhoClicked().getUniqueId(), cost)) {
                Economy.subtractBal(e.getWhoClicked().getUniqueId(), cost);
                fm.getFarm(((Player) e.getWhoClicked())).plotChange(clazz);
                e.getWhoClicked().sendMessage(ChatColor.GREEN.toString() + "Success!");
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().setVelocity(new Vector(0, 2, 0));
            } else {
                e.getWhoClicked().sendMessage(ChatColor.RED.toString() + "You need "
                        + NumberFormat.getCurrencyInstance().format(cost) + " to buy that!");
            }
        }
    }

    /**
     * Searches for a "cost xxx" line in the given list
     * and returns that cost. FREE is acceptable instead of 0
     * @return the cost, if found, -1 if not found.
     */
    private Double findCost(@Nullable List<String> lore){
        if(lore == null){
            return -1d;
        }
        double cost = -1d;
        for (String s : lore) {
            if (s.contains("Cost")) {
                if (s.contains("FREE")) {
                    cost = 0D;
                } else {
                    Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");
                    Matcher m = p.matcher(s);
                    if (m.find()) {
                        cost = Double.parseDouble(m.group(1));
                    }
                }
                break;
            }
        }
        return cost;
    }

}
