package main.java.com.speuce.farmtopia.farm.listeners;

import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.farm.FarmManager;
import main.java.com.speuce.farmtopia.plot.FarmPlot;
import main.java.com.speuce.farmtopia.plot.Plot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Listener for sneak-related things on a farm
 * @author matt
 */
public class SneakListener implements Listener {

    private FarmManager fm;

    public SneakListener(FarmManager fm) {
        this.fm = fm;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            Farm f = fm.getFarm(e.getPlayer().getLocation());
            if (f != null) {
                Plot plot = f.getPlot(e.getPlayer().getLocation().getChunk());
                if (plot instanceof FarmPlot) {
                    FarmPlot fp = (FarmPlot) plot;
                    fp.onShift(e.getPlayer().getLocation());
                }
            }
        }
    }
}
