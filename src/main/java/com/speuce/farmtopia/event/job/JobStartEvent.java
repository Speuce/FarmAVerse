package main.java.com.speuce.farmtopia.event.job;

import main.java.com.speuce.farmtopia.jobs.Job;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Event when a new job is started
 * @author Matt
 */
public class JobStartEvent extends JobEvent {



    private static final HandlerList handlers = new HandlerList();

    public JobStartEvent(Player p, Job job) {
        super(p, job);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
