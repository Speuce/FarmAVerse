package main.java.com.speuce.farmtopia.plot.upgradeable.seedResearch;

import main.java.com.speuce.farmtopia.crop.CropType;
import main.java.com.speuce.farmtopia.crop.Family;
import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.Plot;
import main.java.com.speuce.farmtopia.resources.Resource;
import main.java.com.speuce.farmtopia.util.Constant;
import main.java.com.speuce.farmtopia.util.RandomCollection;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

/**
 * A {@link SeedMachine} that takes a seed as an input and gives an essence
 * as an output.
 * Can also take essence as an input and give a seed as an output.
 */
public class ExtractorMachine extends SeedMachine{
    /**
     * Create a new SeedMachine object.
     *
     * @param seed      the seed currently in this machine (may be null or NOTHING)
     * @param startTime the time that this machine operation was started.
     */
    public ExtractorMachine(@Nullable Resource seed,@Nullable Resource output, long startTime) {
        super(seed, output,startTime);
    }

    /**
     * Indicates whether a given resource is usable in this machine
     *
     * @param r the resource to check
     * @param p the plot that this is occuring on.
     * @return true if the resource can be used in this machine, false otherwise
     */
    @Override
    public boolean isUsable(Resource r, Plot p) {
        //see if at least one seed in this essence family is usable.
        if(Constant.isEssence(r)){
            List<CropType> ss = CropType.getAllInFamily(Family.getByResource(r));
            for (CropType ty : ss) {
                if (ty.getMinLvl() <= p.getFarm().getLevel()) {
                    return true;
                }
            }
           return false;
        }
        //or if its just a seed
        CropType type = CropType.getBySeed(r);
        return type != CropType.NULLIO;
    }

    /**
     * Calculates an output product given the input product
     * and any information about the plot
     *
     * @param r the resource that is being inputted
     * @param p the plot that this occurs on
     * @return the associated output Resource for this input
     */
    @Override
    protected Resource getProduct(Resource r, Plot p) {
        if (Constant.isEssence(r)) {
            return extractEssence(r, p.getFarm());
        } else {
            // Bukkit.broadcastMessage("seed");
            CropType ty = CropType.getBySeed(r);
             return ty.getFamily().getResource();
        }
    }

    /**
     * Processes the extraction result of an essence resource
     */
    private Resource extractEssence(Resource r, Plot plot){
        assert(isUsable(r, plot));
        RandomCollection<Resource> coll = new RandomCollection<Resource>();
        Family f = Constant.getFamilyOf(r);
        List<CropType> ss = CropType.getAllInFamily(f);
        for (CropType ty : ss) {
            if (ty.getMinLvl() <= plot.getFarm().getLevel()) {
                coll.add(ty.getWeight(), ty.getSeed());
            }
        }
        assert(coll.isEmpty());
        return coll.next(new Random());
    }
}
