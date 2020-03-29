package main.java.com.speuce.farmtopia.farm;

import main.java.com.speuce.farmtopia.plot.BuildQueue;
import main.java.com.speuce.farmtopia.plot.FarmPlot;
import main.java.com.speuce.farmtopia.plot.Plot;
import main.java.com.speuce.farmtopia.util.Constant;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Class for building and returning BukkitRunnable's for various update tasks
 * @author matt
 */
public class Updaters {

    /**
     * The task responsible for sending updates to non-farm plots.
     */
    public static BukkitRunnable getPlotUpdater(final FarmManager fm) {
        return new BukkitRunnable() {

            @Override
            public void run() {
                for (Farm f : fm.getLoadedFarms().values()) {
                    for (Plot p : f.getPlots()) {
                        if (p != null && !(p instanceof FarmPlot)) {
                            p.update();
                        }
                    }
                }
            }
        };
    }

    /**
     * The task responsible for sending updates to farm plots.
     */
    public static BukkitRunnable getFarmPlotUpdater(final FarmManager fm) {
        return new BukkitRunnable() {
            Random r = new Random();

            @Override
            public void run() {
                for (Farm f : fm.getLoadedFarms().values()) {
                    for (Plot p : f.getPlots()) {
                        if (p instanceof FarmPlot) {
                            FarmPlot fp = (FarmPlot) p;
                            fp.updateStages(true);
                        }
                    }
                }
                if (r.nextInt(20) == 0) {
                    BuildQueue.test();
                }
            }

        };
    }

    /**
     * The task responsible for filling the queue of available farm
     * locations, as well as teleporting people back to their farm when they fall off.
     */
    public static BukkitRunnable getLocationUpdater(final FarmManager fm) {
        return new BukkitRunnable() {

            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getLocation().getY() <= 70) {
                        Farm f = fm.getFarm(p);
                        if (f != null) {
                            fm.teleportTo(p, f);
                        }
                    }
                }
                int x = 500;
                int y = 500;
                while (fm.getAvailableLocations().size() < 5) {
                    if (y < 10000) {
                        y += 500;
                    } else {
                        y = 500;
                        x += 500;
                    }
                    Block l = fm.getMainWorld().getBlockAt(x, Constant.baseY, y);
                    if (l.getType() == Material.AIR) {
                        fm.getAvailableLocations().add(l.getLocation());
                    }
                }

            }

        };
    }
}
