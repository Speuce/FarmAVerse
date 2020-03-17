package main.java.com.speuce.farmtopia.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Extended JavaPlugin to handle Debug-Realated messaging
 * @author Matt Kwiatkowski
 */
public abstract class DebugMessager extends JavaPlugin {
    private DebugLevel listeningfor;

    /**
     * Send a debug message at the given level
     * @param d the {@link DebugLevel} assosciated with this message
     * @param s the Message
     */
    public void debug(DebugLevel d, String s){
        if(this.listeningfor.getValue() <= d.getValue()){
            Bukkit.broadcastMessage(s);
        }
    }

    /**
     * Sends a debug message at the SPAM level
     * @param s the message to send.
     */
    public void spamDebug(String s){
        this.debug(DebugLevel.SPAM, s);
    }

    /**
     * Sends a debug message at the SEMI Level.
     * Semi meaning semi-important
     * @param s the message to send
     */
    public void semiDebug(String s){
        this.debug(DebugLevel.SEMI, s);
    }

    /**
     * Get the current lowest-diplay level of debug messages
     */
    public DebugLevel getListeningfor() {
        return listeningfor;
    }

    /**
     * Set the current lowest-diplay level of debug messages
     */
    public void setListeningfor(DebugLevel listeningfor) {
        this.listeningfor = listeningfor;
    }
}
