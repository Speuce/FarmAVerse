package main.java.com.speuce.farmtopia.event.farm.crop;

import main.java.com.speuce.farmtopia.crop.CropType;
import main.java.com.speuce.farmtopia.plot.subplot.FarmSubPlot;
import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.FarmPlot;
import org.bukkit.event.HandlerList;

/**
 * Event when a player plants a crop on a {@link FarmPlot}
 * @author Matt Kwiatkowski
 */
public class FarmPlantEvent extends FarmCropEvent {

    private static final HandlerList handlers = new HandlerList();

    /**
     * The type of crop that is being attempted to be planted.
     */
    private CropType newtype;

    public FarmPlantEvent(Farm farm, FarmPlot plot, FarmSubPlot subplot, CropType currtype, CropType newtype) {
        super(farm, plot, subplot, currtype, false);
        this.newtype = newtype;
    }

    /**
     * @return The type of crop that is being attempted to be planted.
     */
    public CropType getNewtype() {
        return newtype;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
