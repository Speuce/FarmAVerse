package main.java.com.speuce.farmtopia.farm.plotcollections;

import main.java.com.speuce.farmtopia.farm.ChunkCollection;
import main.java.com.speuce.farmtopia.plot.Plot;
import main.java.com.speuce.farmtopia.util.chunk.ChunkUtil;
import main.java.com.speuce.farmtopia.util.chunk.Direction;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an navigable collection of plots.
 * @author Matt Kwiatkowski
 */
public abstract class PlotCollection extends ChunkCollection {
    //the list of all plots in this plotcollection
    private ArrayList<Plot> plots;

    public PlotCollection(Location baseLocation, int size) {
        super(baseLocation, size);
        this.plots = new ArrayList<Plot>();
    }

    /**
     * Get the list of plots in this collection
     * @return
     */
    public List<Plot> getPlots() {
        return plots;
    }

    /**
     * Set the list of plots in this collection
     * @param plots
     */
    public void setPlots(ArrayList<Plot> plots) {
        this.plots = plots;
    }

    /**
     * Adds the plot to the PlotCollection
     * @param p the plot to add
     */
    public void addPlot(Plot p){
        plots.add(p);
    }


    public boolean hasPlot(String plotname){
        for(Plot f: this.plots){
            if(f.getName().equalsIgnoreCase(plotname)){
                return true;
            }
        }
        return false;
    }
    public int getPlots(String type){
        int count = 0;
        for(Plot f: this.plots){
            if(f.getName().equalsIgnoreCase(type)){
                count++;
            }
        }
        return count;
    }
    public Plot getFirstPlot(Class<? extends Plot> type){
        for(Plot pl: this.plots){
            if(pl.getClass().equals(type)){
                return pl;
            }
        }
        return null;
    }

    public Plot getPlot(int x){
        return this.plots.get(x);
    }

    //FORMAT
    // 0 = NORTH
    // 1 = NORTHEAST
    // 2 = EAST
    // 3 = SOUTHEAST
    // 4 = SOUTH
    // 5 = SOUTHWEST
    // 6 = WEST
    // 7 = NORTHWEST
    public Plot[] getNearbyPlots(Plot pl){
        assert(pl != null);
        Chunk[] c = ChunkUtil.getNearbyChunks(pl.getChunk());
        Plot[] ret = new Plot[8];
        for(int i = 0; i < 8; i++){
            ret[i] = getPlot(c[i]);
        }
        return ret;
    }

    /**
     * Get the plot in the given direction
     * @param curr the plot to search from
     * @param d the direction to search
     * @return the {@link Plot} if found, null otherwise.
     */
    @Nullable
    public Plot getNearbyPlot(Plot curr, Direction d){
        Chunk c = ChunkUtil.getNearby(curr.getChunk(), d);
        return getPlot(c);
    }

    /**
     * Calculates the n-value for, and returns the plot
     * of the given chunk, if it is within this collection
     * @param c the chunk to search for
     * @return the associated {@link Plot} if found, null otherwise.
     */
    @Nullable
    public Plot getPlot(Chunk c){
        int n = getN(c);
        if(n > -1){
            return getPlot(n-1);
        }
        return null;
    }

    /**
     * Changes the given type of the plot # given
     */
    public void setPlot(int x, Plot p){
        this.plots.set(x, p);
    }
}
