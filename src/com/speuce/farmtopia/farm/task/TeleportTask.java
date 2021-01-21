package com.speuce.farmtopia.farm.task;

import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.farm.manager.FarmManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The task responsible for teleporting players back to their farms
 * when the fall out of the world.
 */
public class TeleportTask extends BukkitRunnable {

    private FarmManager manager;

    public TeleportTask(FarmManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().getY() <= 70) {
                Farm f = manager.getFarm(p);
                if (f != null) {
                    manager.teleportTo(p, f);
                }
            }
        }
    }
}
