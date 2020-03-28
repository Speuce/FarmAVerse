package main.java.com.speuce.farmtopia.util.chunk;

import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

/**
 * Contains Various Chunk Calculations
 * @author Matt Kwiatkowski
 */
public class ChunkUtil {

    /**
     * Given a specific chunk, an array of size 8
     * will be returned, each index representing a direction
     * according to the order of {@link Direction}
     * @param c the current chunk
     * @return an array of nearby chunks.
     */
    public static Chunk[] getNearbyChunks(Chunk c){
        Chunk[] ret = new Chunk[8];
        for(int i = 0; i < 8; i++){
            ret[i] = ChunkUtil.getNearby(c, Direction.fromInt(i));
        }
        return ret;
    }

    /**
     * Get the next chunk in the given direction
     * @param c the current chunk
     * @param d the direction to go
     * @return the nearby chunk
     */
    @NotNull
    public static Chunk getNearby(Chunk c, Direction d){
        return getNearby(c, d.getXoffset(), d.getZoffset());
    }

    /**
     * Get the next chunk in the given direction
     * @param c the current chunk
     * @param xoffset the number of chunks in the x direction to go
     * @param zoffset the number of chunks in the x direction to go
     * @return the nearby chunk
     */
    @NotNull
    public static Chunk getNearby(Chunk c, int xoffset, int zoffset){
        return c.getWorld().getChunkAt(c.getX() +xoffset, c.getZ() + zoffset);
    }

    /**
     * Gets the nth chunk in chunk counting
     * from the given base location
     * @param n n, where n=1 returns base.
     * @param base the base location
     * @return the chunk
     */
    @NotNull
    public static Chunk getNthChunk(int n, Chunk base){
        int v = (int)Math.sqrt(n-1.0);
        int q = (v*v) + v + 1;
        System.out.println("n: " + n+" v: " + v + " q:" + q);
        int x = -v;
        int z = -v;
        // Chunk ret = base.nearby(-v,-v);
        if(n < q){
            x += q-n;
        }else if(n > q){
            z += n-q;
        }
        return getNearby(base, x, z);
    }

    /**
     * Get the n in chunk counting for the given
     * chunk given the base chunk
     * @param c the chunk to find n of
     * @param base the base for counting (n=1 chunk)
     * @return the proper n-value, in chunk counting.
     */
    public static int getN(Chunk c, Chunk base){
        int xoff = base.getX() - c.getX();
        int zoff = base.getZ() - c.getZ();
        int v = Math.max(xoff, zoff);
        int q = (v*v) + v + 1;
        //Chunk cmp = base.nearby(-v,-v);
        if(xoff < v){
            return q-(v-xoff);
        }else{
            return q+(v-zoff);
        }
    }

//    /**
//     * Find the nearby chunk with the indicated direction
//     * @param c the Current chunk
//     * @param we flag indicating whether to search west or east.
//     *           True for west, false for east
//     * @return the nearest chunk
//     */
//    public static Chunk getNearbyWE(Chunk c, boolean we){
//        //Precondition: the given chunk is not null.
//        assert(c != null);
//        Chunk ret;
//        if(we){
//            ret = c.getWorld().getChunkAt(c.getX()+1, c.getZ());
//        }else{
//            ret = c.getWorld().getChunkAt(c.getX()-1, c.getZ());
//        }
//        //Post condition: the returned chunk is not null.
//        assert(ret != null);
//        return ret;
//    }
//
//    /**
//     * Find the nearby chunk with the indicated direction
//     * @param c the Current chunk
//     * @param ns flag indicating whether to search north or south.
//     *           True for north, false for south
//     * @return the nearest chunk
//     */
//    public static Chunk getNearbyNS(Chunk c,boolean ns){
//        //Precondition: the given chunk is not null.
//        assert(c != null);
//        Chunk ret;
//        if(ns){
//            ret = c.getWorld().getChunkAt(c.getX(), c.getZ()+1);
//        }else{
//            ret = c.getWorld().getChunkAt(c.getX(), c.getZ()-1);
//        }
//        //Post condition: the returned chunk is not null.
//        assert(ret != null);
//        return ret;
//    }

}
