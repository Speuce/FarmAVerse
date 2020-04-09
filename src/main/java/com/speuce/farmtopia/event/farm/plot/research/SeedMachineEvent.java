package main.java.com.speuce.farmtopia.event.farm.plot.research;

import main.java.com.speuce.farmtopia.event.farm.plot.FarmPlotEvent;
import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.Plot;
import main.java.com.speuce.farmtopia.plot.upgradeable.seedResearch.SeedMachine;

/**
 * An event indicating the interaction with a seed machine.
 * @author matt
 */
public abstract class SeedMachineEvent extends FarmPlotEvent {

    /**
     * The machine that this event is related to.
     */
    private SeedMachine machine;

    public SeedMachineEvent(Farm farm, Plot plot, SeedMachine machine) {
        super(farm, plot);
        this.machine = machine;
    }

    public SeedMachine getMachine() {
        return machine;
    }
}
