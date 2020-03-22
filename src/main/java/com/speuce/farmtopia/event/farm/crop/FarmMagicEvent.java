package main.java.com.speuce.farmtopia.event.farm.crop;

import main.java.com.speuce.farmtopia.crop.CropType;
import main.java.com.speuce.farmtopia.crop.FarmSubplot;
import main.java.com.speuce.farmtopia.event.farm.FarmEvent;
import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.FarmPlot;
import org.bukkit.event.HandlerList;

/**
 * An event called when "Magic Dust" is used on a farm subplot
 * @author Matt Kwiatkowski
 */
public class FarmMagicEvent extends FarmCropEvent {

    private static final HandlerList handlers = new HandlerList();

    public FarmMagicEvent(Farm farm, FarmPlot plot, FarmSubplot subplot, CropType type) {
        super(farm, plot,subplot, type, false);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
