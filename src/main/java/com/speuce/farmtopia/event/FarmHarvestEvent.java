package main.java.com.speuce.farmtopia.event;

import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.FarmPlot;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Event called when a subplot inside of an {@link FarmPlot} is harvested
 * PROTOTYPE. NOT COMPLETE OR USED.
 * @author Matt Kwiatkowski
 */
public class FarmHarvestEvent extends FarmEvent implements Cancellable {

    private boolean cancelled;

    public FarmHarvestEvent(Farm farm) {
        super(farm);
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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
