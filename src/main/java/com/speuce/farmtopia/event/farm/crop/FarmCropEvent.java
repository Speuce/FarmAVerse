package main.java.com.speuce.farmtopia.event.farm.crop;

import main.java.com.speuce.farmtopia.crop.CropType;
import main.java.com.speuce.farmtopia.plot.subplot.FarmSubPlot;
import main.java.com.speuce.farmtopia.event.farm.FarmEvent;
import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.FarmPlot;
import org.bukkit.event.Cancellable;

/**
 * An event which involves a Farm Crop
 * @author Matt Kwiatkowski
 */
public abstract class FarmCropEvent extends FarmEvent implements Cancellable {

    /* Flag indicating whether the event is cancelled*/
    private boolean cancelled;
    /* the plot on which this is occuring */
    private FarmPlot plot;
    /* the subplot on which this is occuring */
    private FarmSubPlot subplot;
    /* the latest {@link Croptype} for which this event occurs */
    private CropType type;

    public FarmCropEvent(Farm farm, FarmPlot plot, FarmSubPlot subplot, CropType type, boolean cancelled) {
        super(farm);
        this.cancelled = cancelled;
        this.plot = plot;
        this.type = type;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * the plot on which this is occurring
     */
    public FarmPlot getPlot() {
        return plot;
    }

    /**
     * Get the subplot on which this is occurring
     */
    public FarmSubPlot getSubplot() {
        return subplot;
    }

    /**
     * the latest {@link CropType} for which this event occurs
     */
    public CropType getType() {
        return type;
    }
}
