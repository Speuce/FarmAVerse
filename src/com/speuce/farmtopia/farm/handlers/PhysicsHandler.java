package com.speuce.farmtopia.farm.handlers;

import com.speuce.farmtopia.farm.manager.FarmManager;
import com.speuce.farmtopia.main.FarmTopia;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.world.StructureGrowEvent;

/**
 * Handles all physics events
 * Includes placing blocks, growing, natural physics, etc
 */
public class PhysicsHandler implements Listener {

    /**
     * The farm manager instance
     */
    private FarmManager fm;

    public PhysicsHandler(FarmManager fm){
        this.fm = fm;
        FarmTopia.registerListener(this);

    }


    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onGrow(StructureGrowEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockDecay(LeavesDecayEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockDropEvbent(BlockDropItemEvent e){
        e.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            e.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPhysics(BlockPhysicsEvent e) {
        // if(e.getChangedType() == Material.CROPS || e.getBlock().getType() ==
        // Material.CROPS
        // || e.getChangedType() == Material.WHEAT || e.getBlock().getType() ==
        // Material.WHEAT
        // ||e.getChangedType() == Material.LONG_GRASS
        // || e.getBlock().getType() == Material.LONG_GRASS
        // || e.getChangedType() == Material.RED_ROSE
        // || e.getBlock().getType() == Material.RED_ROSE
        // || e.getBlock().getType() == Material.TORCH
        // || e.getChangedType() == Material.TORCH){
        // e.setCancelled(true);
        // }
        e.setCancelled(true);
        // Bukkit.broadcastMessage(e.getBlock().getType().toString() + ":" +
        // e.getChangedType().toString());
    }


    @EventHandler
    public void onMoistureChange(MoistureChangeEvent e){
        e.setCancelled(true);
    }



}
