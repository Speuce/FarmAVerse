package main.java.com.speuce.farmtopia.event.job;

import main.java.com.speuce.farmtopia.jobs.Job;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Any event associated with a Job
 * @author Matt
 */
public abstract class JobEvent extends Event {

    /* the player associated with this job */
    private Player p;

    /* the Job associated with this event */
    private Job job;

    public JobEvent(Player p, Job job) {
        this.p = p;
        this.job = job;
    }

    /**
     * @return the player associated with this job.
     */
    public Player getPlayer() {
        return p;
    }

    /**
     * @return the Job associated with this event
     */
    public Job getJob() {
        return job;
    }

}
