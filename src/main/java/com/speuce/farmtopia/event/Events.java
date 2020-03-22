package main.java.com.speuce.farmtopia.event;

import main.java.com.speuce.farmtopia.main.FarmTopia;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class Events {

    /**
     * Quickly broadcast an event to the server.
     */
    public static void call(Event e){
        FarmTopia.getFarmTopia().getServer().getPluginManager().callEvent(e);
    }

    /**
     * Quickly calls a cancellable event
     * @param c the Cancellable event to call
     * @return true is the event is not to be cancelled, false otherwise.
     */
    public static boolean isCancelled(Cancellable c){
        if(c instanceof Event){
            call((Event )c);
            return c.isCancelled();
        }
        return false;
    }
}
