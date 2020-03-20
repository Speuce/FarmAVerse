package main.java.com.speuce.farmtopia.util.chunk;

import org.bukkit.Chunk;

/**
 * Contains Various Chunk Calculations
 * @author Matt Kwiatkowski
 */
public class ChunkUtil {

    /**
     * Find the nearby chunk with the indicated direction
     * @param c the Current chunk
     * @param we flag indicating whether to search west or east.
     *           True for west, false for east
     * @return the nearest chunk
     */
    public static Chunk getNearbyWE(Chunk c, boolean we){
        //Precondition: the given chunk is not null.
        assert(c != null);
        Chunk ret;
        if(we){
            ret = c.getWorld().getChunkAt(c.getX()+1, c.getZ());
        }else{
            ret = c.getWorld().getChunkAt(c.getX()-1, c.getZ());
        }
        //Post condition: the returned chunk is not null.
        assert(ret != null);
        return ret;
    }

    /**
     * Find the nearby chunk with the indicated direction
     * @param c the Current chunk
     * @param ns flag indicating whether to search north or south.
     *           True for north, false for south
     * @return the nearest chunk
     */
    public static Chunk getNearbyNS(Chunk c,boolean ns){
        //Precondition: the given chunk is not null.
        assert(c != null);
        Chunk ret;
        if(ns){
            ret = c.getWorld().getChunkAt(c.getX(), c.getZ()+1);
        }else{
            ret = c.getWorld().getChunkAt(c.getX(), c.getZ()-1);
        }
        //Post condition: the returned chunk is not null.
        assert(ret != null);
        return ret;
    }

}
