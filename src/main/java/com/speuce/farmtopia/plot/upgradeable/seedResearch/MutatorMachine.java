package main.java.com.speuce.farmtopia.plot.upgradeable.seedResearch;

import main.java.com.speuce.farmtopia.resources.Resource;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link SeedMachine} that takes an essence as an input,
 * and gives an essence (possibily a different one) as an output.
 */
public class MutatorMachine extends SeedMachine{
    /**
     * Create a new SeedMachine object.
     *
     * @param seed      the seed currently in this machine (may be null or NOTHING)
     * @param startTime the time that this machine operation was started.
     */
    public MutatorMachine(@Nullable Resource seed,@Nullable Resource output, long startTime) {
        super(seed,output, startTime);
        super.setTimeMultiplier(5.0);
    }
}
