package main.java.com.speuce.farmtopia.event.farm.crop;

import main.java.com.speuce.farmtopia.crop.CropType;
import main.java.com.speuce.farmtopia.crop.FarmSubplot;
import main.java.com.speuce.farmtopia.event.farm.FarmEvent;
import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.FarmPlot;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Event called when a subplot inside of an {@link FarmPlot} is harvested
 * @author Matt Kwiatkowski
 */
public class FarmHarvestEvent extends FarmCropEvent {

    private boolean cancelled;

    /* the amount that the crop yield is to be multiplied */
    private int multiplier;

    public FarmHarvestEvent(Farm farm, FarmPlot plot, FarmSubplot subplot, CropType type, int multiplier) {
        super(farm, plot, subplot, type, false);
        this.multiplier = multiplier;
    }

    /**
     * the amount that the crop yield is to be multiplied.
     */
    public int getMultiplier() {
        return multiplier;
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
