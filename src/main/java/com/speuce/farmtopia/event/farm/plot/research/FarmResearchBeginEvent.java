package main.java.com.speuce.farmtopia.event.farm.plot.research;

import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.Plot;
import main.java.com.speuce.farmtopia.plot.upgradeable.seedResearch.SeedMachine;
import main.java.com.speuce.farmtopia.plot.upgradeable.seedResearch.SeedResearchCentre;
import main.java.com.speuce.farmtopia.resources.Resource;
import org.bukkit.event.HandlerList;

/**
 * Event called when crop research is started
 * @author Matt
 */
public class FarmResearchBeginEvent extends FarmResearchEvent{

    private Resource resource;

    public FarmResearchBeginEvent(Farm farm, Plot plot, SeedMachine machine, Resource resource) {
        super(farm, plot, machine);
        this.resource = resource;
    }

    /**
     * @return the resource used to initiate this research
     */
    public Resource getResource() {
        return resource;
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
