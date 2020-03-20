package main.java.com.speuce.farmtopia.event.farm.plot.research;

import main.java.com.speuce.farmtopia.event.farm.plot.FarmPlotEvent;
import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.Plot;
import main.java.com.speuce.farmtopia.plot.upgradeable.ResearchCentre;

/**
 * An event dealing with the Research Centre
 * @author Matt Kwiatkowski
 */
public abstract class FarmResearchEvent extends FarmPlotEvent {

    //the type of research being conducted
    private ResearchType type;

    public FarmResearchEvent(Farm farm, ResearchCentre plot, ResearchType type) {
        super(farm, plot);
    }

    /**
     * @return the type of research being conducted
     */
    public ResearchType getType() {
        return type;
    }

    /**
     * Get the Research Centre associated with this event
     */
    @Override
    public ResearchCentre getPlot(){
        return (ResearchCentre) super.getPlot();
    }
}
