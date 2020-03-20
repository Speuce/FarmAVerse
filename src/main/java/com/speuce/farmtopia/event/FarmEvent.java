package main.java.com.speuce.farmtopia.event;


import main.java.com.speuce.farmtopia.farm.Farm;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * An event which is assosciated with a specific given farm.
 * @author Matt Kwiatkowski
 */
public abstract class FarmEvent extends Event {

    /* The farm Associated with this event */
    private Farm farm;

    public FarmEvent(Farm farm) {
        this.farm = farm;
    }

    /**
     * Get the farm associated with this event
     * @return the {@link Farm} associated with this event.
     */
    public Farm getFarm() {
        return farm;
    }

}
