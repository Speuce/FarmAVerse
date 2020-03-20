package main.java.com.speuce.farmtopia.event.farm.plot;

import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.Plot;
import org.bukkit.event.HandlerList;

/**
 * Event when a plot is upgraded
 * @author Matt
 */
public class FarmPlotUpgradeEvent extends FarmPlotEvent {

    int newlvl;

    public FarmPlotUpgradeEvent(Farm farm, Plot plot, int newlvl) {
        super(farm, plot);
        this.newlvl = newlvl;
    }

    /**
     * @return the level that this plot was upgraded to.
     */
    public int getNewLevel() {
        return newlvl;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
