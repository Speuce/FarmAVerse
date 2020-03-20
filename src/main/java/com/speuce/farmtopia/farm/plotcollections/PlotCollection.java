package main.java.com.speuce.farmtopia.farm.plotcollections;

import main.java.com.speuce.farmtopia.farm.ChunkCollection;
import main.java.com.speuce.farmtopia.plot.Plot;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an navigable collection of plots.
 * @author Matt Kwiatkowski
 */
public abstract class PlotCollection extends ChunkCollection {
    //the list of all plots in this plotcollection
    private List<Plot> plots;

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
    public void setPlots(List<Plot> plots) {
        this.plots = plots;
    }

    /**
     * Adds the plot to the PlotCollection
     * @param p the plot to add
     */
    public void addPlot(Plot p){
        plots.add(p);
    }

    public int getPlotSlot(Chunk c){
        for(int x = 0; x < plots.size(); x++){
            Plot p = plots.get(x);
            if(p.getChunk().getX() == c.getX() && p.getChunk().getZ() == c.getZ()){
                return x;
            }
        }
        return -1;
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
        Chunk c = pl.getChunk();
        Plot[] ret = new Plot[8];
        ret[0]= getPlot(c.getWorld().getChunkAt(c.getX(), c.getZ()-1));
        ret[1]= getPlot( c.getWorld().getChunkAt(c.getX()+1, c.getZ()-1));
        ret[2]= getPlot( c.getWorld().getChunkAt(c.getX()+1, c.getZ()));
        ret[3]= getPlot(c.getWorld().getChunkAt(c.getX()+1, c.getZ()+1));
        ret[4]= getPlot(c.getWorld().getChunkAt(c.getX(), c.getZ()+1));
        ret[5]= getPlot(c.getWorld().getChunkAt(c.getX()-1, c.getZ()+1));
        ret[6]= getPlot(c.getWorld().getChunkAt(c.getX()-1, c.getZ()));
        ret[7]= getPlot(c.getWorld().getChunkAt(c.getX()-1, c.getZ()-1));
        return ret;
    }

    @Deprecated
    public Plot getPlot(Chunk c){
        for(Plot p: plots){
            if(p.getChunk().getX() == c.getX() && p.getChunk().getZ() == c.getZ()){
                return p;
            }
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
