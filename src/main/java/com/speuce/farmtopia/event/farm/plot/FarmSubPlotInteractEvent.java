package main.java.com.speuce.farmtopia.event.farm.plot;

import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.FarmPlot;
import main.java.com.speuce.farmtopia.plot.Plot;
import main.java.com.speuce.farmtopia.plot.subplot.FarmSubPlot;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Event called when a Sub-Plot is interacted upon
 */
public class FarmSubPlotInteractEvent extends FarmPlotInteractEvent{

    /* the subplot that was interacted on */
    FarmSubPlot fs;

    public FarmSubPlotInteractEvent(Farm farm, FarmPlot plot, PlayerInteractEvent e, FarmSubPlot fs) {
        super(farm, plot, e);
        this.fs = fs;
    }

    /**
     * @return the subplot that was interacted on
     */
    public FarmSubPlot getSubPlot() {
        return fs;
    }

    @Override
    public FarmPlot getPlot(){
        return (FarmPlot)super.getPlot();
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
