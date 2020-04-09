package main.java.com.speuce.farmtopia.plot.upgradeable.seedResearch;

import com.google.common.primitives.Longs;
import main.java.com.speuce.farmtopia.event.Events;
import main.java.com.speuce.farmtopia.event.farm.plot.research.FarmResearchCollectEvent;
import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.Plot;
import main.java.com.speuce.farmtopia.resources.Resource;
import main.java.com.speuce.farmtopia.util.Constant;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents any object that takes a seed and has an output.
 * @author matt
 */
public abstract class SeedMachine {

    /**
     * Epoch time in ms that this machine was activated.
     */
    private long startTime;

    /**
     * The current seed inside of this machine
     */
    @NotNull
    private Resource seed;

    /**
     * The output of this machine for the current given input
     */
    @NotNull
    private Resource product;

    /**
     * The additional time that will be imposed with this type of machine
     */
    private double timeMultiplier = 1.0d;

    /**
     * Holds text info
     */
    private ArmourTextHolder textHolder;

    /**
     * The location of this seed machine
     */
    private Location location = null;

    /**
     * The type of this seed machine.
     */
    @NotNull
    private SeedMachineType type;


    /**
     * Create a new SeedMachine object.
     * @param type the type of this seedmachine
     * @param seed the seed currently in this machine (may be null or NOTHING)
     * @param output the output set for the given current seed in this machine (may be null or NOTHING)
     * @param startTime the time that this machine operation was started.
     */
    public SeedMachine(@NotNull SeedMachineType type, @NotNull Resource seed, @NotNull Resource output, long startTime) {
        this.type = type;
        this.startTime = startTime;
        this.seed = seed;
        this.product = output;
        this.textHolder = new ArmourTextHolder(2);
    }

    public ArmourTextHolder getTextHolder() {
        return textHolder;
    }


    public boolean begin(Resource input, Plot p){
        if(!isInUse() && isUsable(input, p)){
            this.product = getProduct(input, p);
            this.seed = input;

            return true;
        }
        return false;
    }

    /**
     * Indicates whether a given resource is usable in this machine
     * @param r the resource to check
     * @param p the plot that this is occuring on.
     * @return true if the resource can be used in this machine, false otherwise
     */
    public abstract boolean isUsable(Resource r, Plot p);

    /**
     * Calculates an output product given the input product
     * and any information about the plot
     * @param r the resource that is being inputted
     * @param p the plot that this occurs on
     * @return the associated output Resource for this input
     */
    protected abstract Resource getProduct(Resource r, Plot p);

    /**
     * Runs procedures such as setting armour stand text
     * when a machine is started.
     */
    protected abstract void onStart();

    /**
     * Does completion procedures. Completion is when a machine
     * operation is finished, AND a player collects the product
     */
    public void complete(){
        this.seed = Resource.NOTHING;
        this.product = Resource.NOTHING;
        this.startTime = 0L;
        this.timeMultiplier = 1.0d;
    }

    /**
     * Calls any update-related procedures.
     */
    public void update(){

    }

    /**
     * Called when completion is achieved.
     * Meant for subsequent subclass prodecures.
     */
    protected void onComplete(){}

    /**
     * @return The time left that this machine operation still needs to run before completion.
     */
    public long getTimeLeft() {
        //precondition: the current seed in this machine is not null
        assert(seed != null);
        long diff = System.currentTimeMillis() - this.startTime;
        long l = Math.round(Constant.getFamilyOf(this.seed).getTime()*timeMultiplier);
        // Bukkit.broadcastMessage("diff: " + diff + "-- l:" + l);
        if (diff > l) {
            return 0;
        } else {
            return l - diff;
        }
    }

    /**
     * Check to see if the machine operation is completed.
     * @return true if the machine operation is completed, false otherwise.
     */
    public boolean isComplete(){
        return this.getTimeLeft() == 0;
    }

    /**
     * Check to see if the machine is in use or not.
     * @return true if the machine is in use (or completed), false otherwise
     */
    public boolean isInUse(){
        return this.seed != null && this.seed != Resource.NOTHING;
    }

    /**
     * @return Epoch time in ms that this machine was activated.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime Epoch time in ms that this machine was activated.
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return The current seed inside of this machine
     */
    public Resource getSeed() {
        return seed;
    }

    /**
     * @param seed The current seed inside of this machine
     */
    public void setSeed(Resource seed) {
        this.seed = seed;
    }

    public double getTimeMultiplier() {
        return timeMultiplier;
    }

    public void setTimeMultiplier(double timeMultiplier) {
        this.timeMultiplier = timeMultiplier;
    }

    /**
     * @return The output of this machine for the current given input
     */
    public Resource getProduct() {
        return product;
    }

    /**
     * @param product The output of this machine for the current given input
     */
    public void setProduct(Resource product) {
        this.product = product;
    }

    /**
     * get the location of this seed machine, if set.
     */
    @Nullable
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the location of this seed machine.
     */
    public void setLocation(Location location) {
        textHolder.setLocation(location);
        this.location = location;
    }

    /**
     * Get the # of bytes required for serializing this object
     */
    public static int getBytes(){
        return 11;
    }

    /**
     * Serialize this object into a byte array.
     */
    public byte[] serialize(){
        byte[] ret = new byte[11];
        ret[0] = type.getId();
        ret[1] = seed.getId();
        ret[2] = product.getId();
        Constant.insertIn(ret, Longs.toByteArray(startTime), 3);
        return ret;
    }


}
