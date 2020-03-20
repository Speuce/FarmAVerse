package main.java.com.speuce.farmtopia.event;

import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.Plot;
import org.bukkit.Chunk;
import org.bukkit.event.HandlerList;

/**
 * An event called when the plot in a farm is changed.
 * @author Matt Kwiatkowski
 */
public class FarmPlotChangeEvent extends FarmEvent {



    /* Represent the plot types before the change and after the change, respectively */
    private Class<? extends Plot> from, to;

    /* the chunk that contains the plot being changed */
    private Chunk chunk;

    /**
     *
     * @param farm the farm in which this event is occuring
     * @param from the initial type of the plot
     * @param to the final type of the plot
     * @param chunk the chunk containing the plot being changed.
     */
    public FarmPlotChangeEvent(Farm farm, Class<? extends Plot> from, Class<? extends Plot> to, Chunk chunk) {
        super(farm);
        this.from = from;
        this.to = to;
        this.chunk = chunk;
    }


    /**
     * @return the initial type of the plot
     */
    public Class<? extends Plot> getIntialPlotType() {
        return from;
    }

    /**
     * @return the final type of the plot
     */
    public Class<? extends Plot> getFinalPlotType() {
        return to;
    }

    /**
     * @return the chunk containing the plot being changed.
     */
    public Chunk getChunk() {
        return chunk;
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
