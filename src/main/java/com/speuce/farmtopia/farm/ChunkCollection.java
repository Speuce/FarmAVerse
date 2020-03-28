package main.java.com.speuce.farmtopia.farm;

import main.java.com.speuce.farmtopia.util.chunk.ChunkUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Represents a iterable collection of chunks,
 * originating from a base location
 * @author Matt Kwiatkowksi
 */
public class ChunkCollection implements Iterable<Chunk> {

    /**
     * The Location (and chunk) where this collection is based.
     */
    private Location baseLocation;

    /**
     * The Number of Chunks to count up to when iterating over this collection
     */
    private int size;


    public ChunkCollection(Location baseLocation, int size) {
        this.baseLocation = baseLocation;
        this.size = size;
    }



    /**
     * Get the location that this chunk collection is based from.
     */
    public Location getBaseLocation() {
        return baseLocation;
    }

    /**
     * Get the location that this chunk collection is based from.
     */
    public void setBaseLocation(Location baseLocation) {
        this.baseLocation = baseLocation;
    }

    public int getSize(){
        return this.size;
    }

    /**
     * Set the # of chunks that this chunkcollection will iterate to.
     */
    public void setSize(int size){
        this.size = size;
    }

    /**
     * Adds the next chunk to the collection, and returns it.
     * @return the next chunk in the Chunk sequence
     */
    public Chunk addChunk(){
        size++;
        Chunk c = baseLocation.getChunk();
        Iterator<Chunk> it = this.iterator();
        while(it.hasNext()){
            c = it.next();
        }
        return c;
    }



    /**
     * Returns the iterator for this chunk collection.
     * @return
     */
    @NotNull
    @Override
    public Iterator<Chunk> iterator() {
        return new Iterator<Chunk>() {
            //the current n-value
            private int n = 0;
            private Chunk base = baseLocation.getChunk();
            

            @Override
            public boolean hasNext() {
                return n < getSize();
            }

            @Override
            public Chunk next() {
                n++;
                return ChunkUtil.getNthChunk(n, base);

//                if(count <= 0){
//                    iter++;
//                    if(iter % 2 == 0){
//                        curr = ChunkUtil.getNearbyNS(curr, false);
//                    }else{
//                        curr = ChunkUtil.getNearbyWE(curr, false);
//                    }
//                    count = iter*2;
//                }else{
//                    if(count > iter){
//                        if(iter % 2 == 0){
//                            curr = ChunkUtil.getNearbyWE(curr, false);
//                        }else{
//                            curr = ChunkUtil.getNearbyNS(curr, false);
//                        }
//                    }else{
//                        if(iter % 2 == 0){
//                            curr = ChunkUtil.getNearbyNS(curr, true);
//                        }else{
//                            curr = ChunkUtil.getNearbyWE(curr, true);
//                        }
//                    }
//                    count--;
//                }
//                iterations++;
//                return curr;
            }
        };
    }
}
