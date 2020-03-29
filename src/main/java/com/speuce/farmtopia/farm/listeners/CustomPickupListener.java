package main.java.com.speuce.farmtopia.farm.listeners;

import main.java.com.speuce.farmtopia.util.Constant;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for Ground item pickup.
 * This is specifically for custom items.
 * @author Matt
 */
public class CustomPickupListener implements Listener {

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (e.getEntityType() == EntityType.PLAYER && e.getItem().getItemStack().getType() == Material.BOW) {
            ItemStack s = e.getItem().getItemStack();
            Player p = (Player) e.getEntity();
            Constant.forceGive(p, s);
            Constant.playsound(p, Sound.ENTITY_ITEM_PICKUP, 1F);
            e.setCancelled(true);
            e.getItem().remove();
        }
    }
}
