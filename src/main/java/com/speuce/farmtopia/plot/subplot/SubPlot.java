package main.java.com.speuce.farmtopia.plot.subplot;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a part of a plot with a defined size
 * and anchor position.
 * @author Matt
 */
public class SubPlot {

    /**
     * The size (nxn) of the subplot
     */
    private int size;

    /**
     * The location in the subplot representing the
     * lowest x,z coordinates in the plot,
     * and the "ground level" height
     */
    @Nullable
    private Location anchor;

    /**
     * Construct a new Subplot
     * @param anchor The location in the subplot representing the
     *               lowest x,z coordinates in the plot,
     *               and the "ground level" height
     * @param size The size (nxn) of the subplot
     */
    public SubPlot(Location anchor,int size) {
        this.size = size;
        this.anchor = anchor;
    }

    /**
     * @return The size (nxn) of the subplot
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size The size (nxn) of the subplot
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return The location in the subplot representing the
     *         lowest x,z coordinates in the plot,
     *         and the "ground level" height
     */
    public Location getAnchor() {
        return anchor;
    }

    /**
     * @return the anchor location, as a block
     */
    public Block getAnchorBlock(){
        assert(anchor != null);
        return anchor.getBlock();
    }

    /**
     * @param anchor The location in the subplot representing the
     *      lowest x,z coordinates in the plot,
     *      and the "ground level" height.
     */
    public void setAnchor(Location anchor) {
        this.anchor = anchor;
    }
}
