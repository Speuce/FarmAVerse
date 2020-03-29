package main.java.com.speuce.farmtopia.farm.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.world.StructureGrowEvent;

/**
 * Listeners that only disable events
 * without much thinking
 * @author matt
 */
public class DisableListeners implements Listener {

    /**
     * To disable vanilla crafting
     */
    @EventHandler
    public void onPrepareCraft(CraftItemEvent e) {
        // e.setResult(Result.DENY);
        e.setCancelled(true);
    }

    /**
     * To disable grass spread
     */
    @EventHandler
    public void onSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
    }

    /**
     * To treat one as a cat.
     * Cats don't take fall damage.
     */
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setCancelled(true);
            }
        }
    }

    /**
     * Screw Physics
     */
    @EventHandler
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

    /**
     * Crops shouldn't grow without me telling them to
     */
    @EventHandler
    public void onBlockGrow(BlockGrowEvent e) {
        e.setCancelled(true);
    }

    /**
     * This isn't desert farming.
     */
    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        e.setCancelled(true);
    }

    /**
     * This isn't minecraft.
     * You can't break blocks
     * (unless you're in creative of course)
     */
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            e.setCancelled(true);
        }
    }

    /**
     * Let saplings be saplings
     */
    @EventHandler
    public void onGrow(StructureGrowEvent e) {
        e.setCancelled(true);
    }

    /**
     * Again. This is not minecraft.
     * Don't place blocks unless creative.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            e.setCancelled(true);
        }
    }

    /**
     * Not desert farming.
     * No moisture changing.
     */
    @EventHandler
    public void onMoistureChange(MoistureChangeEvent e){
        e.setCancelled(true);
    }
}
