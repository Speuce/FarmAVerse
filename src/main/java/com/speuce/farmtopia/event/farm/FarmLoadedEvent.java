package main.java.com.speuce.farmtopia.event.farm;

import main.java.com.speuce.farmtopia.farm.Farm;
import org.bukkit.event.HandlerList;

/**
 * Event called when a farm is loaded and ready for the player to be
 * teleported to.
 * @author matt
 */
public class FarmLoadedEvent extends FarmEvent {


    private static final HandlerList handlers = new HandlerList();

    public FarmLoadedEvent(Farm farm) {
        super(farm);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
