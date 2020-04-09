package main.java.com.speuce.farmtopia.event.farm.plot.research;

import main.java.com.speuce.farmtopia.event.farm.plot.FarmPlotEvent;
import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.Plot;
import main.java.com.speuce.farmtopia.plot.upgradeable.seedResearch.SeedMachine;
import main.java.com.speuce.farmtopia.plot.upgradeable.seedResearch.SeedResearchCentre;

/**
 * An event dealing with the Research Centre
 * @author Matt Kwiatkowski
 */
public abstract class FarmResearchEvent extends SeedMachineEvent {

    public FarmResearchEvent(Farm farm, Plot plot, SeedMachine machine) {
        super(farm, plot, machine);
    }

    /**
     * Get the Research Centre associated with this event
     */
    @Override
    public SeedResearchCentre getPlot(){
        return (SeedResearchCentre) super.getPlot();
    }
}
