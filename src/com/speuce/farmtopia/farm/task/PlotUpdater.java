package com.speuce.farmtopia.farm.task;

import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.farm.manager.FarmManager;
import com.speuce.farmtopia.plot.FarmPlot;
import com.speuce.farmtopia.plot.Plot;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task responsible for providing update ticks to all non farm-plot plots.
 */
public class PlotUpdater extends BukkitRunnable {

    private FarmManager manager;

    public PlotUpdater(FarmManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        for (Farm f : manager.getAllLoadedFarms()) {
            for (Plot p : f.getAllPlots()) {
                if (!(p instanceof FarmPlot)) {
                    if (p != null) {
                        p.update();
                    }

                }
            }
        }

    }
}
