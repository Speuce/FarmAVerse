package main.java.com.speuce.farmtopia.event.farm.plot.research;

import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.upgradeable.ResearchCentre;
import main.java.com.speuce.farmtopia.resources.Resource;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player collects the product of research
 * @author Matt Kwiatkowski
 */
public class FarmResearchCollectEvent extends FarmResearchEvent{
    private Resource result;

    public FarmResearchCollectEvent(Farm farm, ResearchCentre plot, ResearchType type, Resource result) {
        super(farm, plot, type);
        this.result = result;
    }

    /**
     * @return the result of the research
     */
    public Resource getResult() {
        return result;
    }

    /**
     * @param result the result of the research
     */
    public void setResult(Resource result) {
        this.result = result;
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
