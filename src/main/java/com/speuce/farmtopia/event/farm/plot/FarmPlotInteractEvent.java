package main.java.com.speuce.farmtopia.event.farm.plot;

import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.Plot;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Event called when a specific Plot is interacted on
 * @author Matt Kwiatkowski
 */
public class FarmPlotInteractEvent extends FarmPlotEvent {

    //the associated interact event
    private PlayerInteractEvent e;

    public FarmPlotInteractEvent(Farm farm, Plot plot, PlayerInteractEvent e) {
        super(farm, plot);
        this.e = e;
    }

    public PlayerInteractEvent getOriginatingEvent() {
        return e;
    }

    /**
     * @return the Player who clicked
     */
    public Player getPlayer(){
        return e.getPlayer();
    }

    /**
     * @return the block that was clicked
     */
    public Block getClickedBlock(){
        return e.getClickedBlock();
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
