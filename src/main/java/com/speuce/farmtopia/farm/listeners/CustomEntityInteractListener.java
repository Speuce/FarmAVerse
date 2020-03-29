package main.java.com.speuce.farmtopia.farm.listeners;

import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.farm.FarmManager;
import main.java.com.speuce.farmtopia.plot.Plot;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Listener for events where the player clicks an entity
 * @author matt
 */
public class CustomEntityInteractListener implements Listener {

    private FarmManager fm;

    public CustomEntityInteractListener(FarmManager fm) {
        this.fm = fm;
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        Location l = e.getRightClicked().getLocation();
        Farm f = fm.getFarm(l);
        if(f != null) {
            Plot plot = f.getPlot(l.getChunk());
            if (plot != null) {
                plot.onEntityInteract(e);
            }
        }
    }
}
