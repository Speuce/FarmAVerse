package main.java.com.speuce.farmtopia.event.farm.plot;

import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.Plot;
import org.bukkit.event.HandlerList;

/**
 * An event called when a new Plot is purchased
 * @author Matt Kwiatkowski
 */
public class FarmPlotPurchaseEvent extends FarmPlotEvent{

    private static final HandlerList handlers = new HandlerList();

    public FarmPlotPurchaseEvent(Farm farm, Plot plot) {
        super(farm, plot);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
