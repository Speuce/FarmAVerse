package com.speuce.farmtopia.farm.task;

import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.farm.manager.FarmManager;
import com.speuce.farmtopia.plot.BuildQueue;
import com.speuce.farmtopia.plot.FarmPlot;
import com.speuce.farmtopia.plot.Plot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Task responsible for updating all farmplots in a farm.
 */
public class FarmPlotUpdater extends BukkitRunnable {

    private FarmManager manager;

    private Random r;

    public FarmPlotUpdater(FarmManager manager) {
        this.manager = manager;
        r = new Random();
    }

    @Override
    public void run() {
        for (Farm f : manager.getAllLoadedFarms()) {
            for (Plot p : f.getAllPlots()) {
                if (p instanceof FarmPlot) {
                    FarmPlot fp = (FarmPlot) p;
                    fp.updateStages(true);
                }
                // else{
                // if(p != null){
                // p.update();
                // }
                //
                // }
            }
        }
        // if(r.nextInt(3) == 1){
        // for(Player p: Bukkit.getOnlinePlayers()){
        // if(p.getOpenInventory() != null &&
        // p.getOpenInventory().getTopInventory() != null){
        // if(p.getOpenInventory().getTitle().equals(Constant.seedExtractorName)){
        // Farm f = loadedFarms.get(p);
        // ((ResearchCentre)f.getFirstPlot(ResearchCentre.class)).openUpgradeInventory(p);
        // }
        // }
        // }
        // }

        if (r.nextInt(20) == 0) {
            BuildQueue.test();
        }
    }
}
