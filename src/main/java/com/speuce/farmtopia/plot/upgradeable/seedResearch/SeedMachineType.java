package main.java.com.speuce.farmtopia.plot.upgradeable.seedResearch;

import main.java.com.speuce.farmtopia.main.DebugLevel;
import main.java.com.speuce.farmtopia.main.FarmTopia;
import main.java.com.speuce.farmtopia.resources.Resource;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Enum for serializing/deserializing seed machine types
 */
public enum SeedMachineType {

    /**
     * Represents the lack of a machinetype, id=0;
     */
    NULL,

    /**
     * Other machine times. Id in the given order.
     */
    EXTRACTOR,
    MUTATOR;

    /**
     * Get the id of the given seed machine
     * @return 0 for NULL, >1 for any other value.
     */
    public byte getId(){
        return (byte)this.ordinal();
    }

    /**
     * Get a seedMachineType by its' id.
     * @return NULL if id <= 0 or id > values,
     * otherwise returns the proper machinetype
     */
    public static SeedMachineType getById(byte id){
        if(id <= 0 || id > SeedMachineType.values().length){
            return NULL;
        }else{
            return SeedMachineType.values()[id];
        }
    }

    /* Deserializing Utilities */

    /**
     * Maps all Machine Types to an appropriate constructor for deserialization.
     */
    private static HashMap<SeedMachineType, Constructor<? extends SeedMachine>> constructorMap = new HashMap<>();

    /**
     * Sets the constructor mapping for the given machine type
     * @param t the Machine type to set
     * @param clazz the class to instantiate with the given machine type.
     *              ***CLAZZ SHOULD HAVE THE CONSTRUCTOR (Resource, Resource, long)***
     */
    public static void setMappedClass(@NotNull  SeedMachineType t,@NotNull Class<? extends SeedMachine> clazz){
        try{
            Constructor<? extends SeedMachine> constr
            = clazz.getConstructor(Resource.class, Resource.class, long.class);
            constructorMap.put(t, constr);
        } catch (NoSuchMethodException e) {
            FarmTopia.getFarmTopia().debug(DebugLevel.MAJOR, "Error Setting mapping for: " + t.name());
            e.printStackTrace();
        }
    }

    /**
     * Constructs a seed machine from the given parameters
     * @return the associated {@link SeedMachine} if all went well, null otherwise
     */
    public static SeedMachine constructMachine(SeedMachineType type, Resource seed, Resource output, long started){
        //pre condition: the type has an associated constructor
        assert(constructorMap.containsKey(type));
        try {
            return constructorMap.get(type).newInstance(seed, output, started);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            FarmTopia.getFarmTopia().debug(DebugLevel.MAJOR, "Error instantiating type: " + type.name());
            e.printStackTrace();
            return null;
        }
    }
}
