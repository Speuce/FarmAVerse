package main.java.com.speuce.farmtopia.event.farm.crop;

import main.java.com.speuce.farmtopia.crop.CropType;
import main.java.com.speuce.farmtopia.crop.FarmSubplot;
import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.FarmPlot;
import org.bukkit.event.HandlerList;

/**
 * Event called when a crop is harvested that changes the
 * fertility of the land. (or the fertility is changed otherwise)
 * @author Matt Kwiatkowski
 */
public class FarmFertilityEvent extends FarmCropEvent {

    /* the amount (negative indicating fertility removal) that the fertility will be changed */
    int fertilityChange;

    public FarmFertilityEvent(Farm farm, FarmPlot plot, FarmSubplot subplot, CropType type, int fertilityChange) {
        super(farm, plot,subplot, type, false);
        this.fertilityChange = fertilityChange;
    }

    /**
     * @return the amount (negative indicating fertility removal) that the fertility will be changed
     */
    public int getFertilityChange() {
        return fertilityChange;
    }

    /**
     * @param fertilityChange the amount (negative indicating fertility removal) that the fertility will be changed
     */
    public void setFertilityChange(int fertilityChange) {
        this.fertilityChange = fertilityChange;
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
