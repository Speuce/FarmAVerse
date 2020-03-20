package main.java.com.speuce.farmtopia.event.farm.plot;

import main.java.com.speuce.farmtopia.event.farm.FarmEvent;
import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.Plot;

/**
 * Represents an event that relates to the modification of a plot
 * @author Matt Kwiatkowski
 */
public abstract class FarmPlotEvent extends FarmEvent {

    //The plot that this event relates to.
    Plot plot;

    public FarmPlotEvent(Farm farm, Plot plot) {
        super(farm);
        this.plot = plot;
    }

    /**
     * @return The {@link Plot} that this event relates to.
     */
    public Plot getPlot() {
        return plot;
    }
}
