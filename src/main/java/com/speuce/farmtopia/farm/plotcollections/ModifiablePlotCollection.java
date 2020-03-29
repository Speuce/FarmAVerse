package main.java.com.speuce.farmtopia.farm.plotcollections;

import main.java.com.speuce.farmtopia.farm.Tutorial;
import main.java.com.speuce.farmtopia.main.FarmTopia;
import main.java.com.speuce.farmtopia.plot.FarmPlot;
import main.java.com.speuce.farmtopia.plot.Plot;
import main.java.com.speuce.farmtopia.plot.PlotBuilder;
import main.java.com.speuce.farmtopia.plot.upgradeable.ResearchCentre;
import main.java.com.speuce.farmtopia.plot.upgradeable.Shop;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a Plot collection that can be owned, as well as expanded
 * and modified
 * @author Matt Kwiatkowski
 */
public class ModifiablePlotCollection extends OwnablePlotCollection {

    public ModifiablePlotCollection(@Nullable Location baseLocation, int size, Player owner, int lvl, byte progress) {
        super(baseLocation, size, owner, lvl, progress);
    }

    /**
     * Calculates the cost to add an additional plot to this plotcollection
     * @return the cost to add an additonal plot
     */
    public double getUpgradeCost(){
        if(this.getSize() <= 1){
            return 0;
        }
        return Math.pow((this.getSize() * 3), 1.4);
    }



    /**
     * Changes the given chunk to the set plot.
     * Also executes building tasks.
     * @param c the chunk to change
     * @param f the Plot to set the chunk as.
     */
    public void setBuildPlot(Chunk c, Plot f){
        if(f instanceof FarmPlot){
            Tutorial.onFarmPlotSet(getOwner());
        }
        if(f instanceof ResearchCentre){
            Tutorial.onResearchCentreBuild(getOwner());
        }
        if(f instanceof Shop){
            Tutorial.onShopBuild(getOwner());
        }
        this.setPlot(this.getN(c), f);
        //BuildQueue.queue(this.getFm().getClear().def(f.getChunk().getBlock(0, Constant.baseY - 1, 0), this.getFm().getPlugin()));
        PlotBuilder bl = new PlotBuilder(f, FarmTopia.getFarmTopia().getSchem(), c);
        bl.build(false);
//		bl.build(false, new Runnable(){
//
//			@Override
//			public void run() {
//				buildWalls(f);
//
//			}
//
//		});

    }

}
