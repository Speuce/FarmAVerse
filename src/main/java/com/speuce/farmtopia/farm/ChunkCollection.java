package main.java.com.speuce.farmtopia.farm;

import main.java.com.speuce.farmtopia.util.chunk.ChunkUtil;
import org.bukkit.Chunk;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * Represents a iterable collection of chunks,
 * originating from a base Chunk
 * @author Matt Kwiatkowksi
 */
public class ChunkCollection implements Iterable<Chunk> {

    /**
     * The Chunk (and chunk) where this collection is based.
     */
    private Chunk baseChunk;

    /**
     * The Number of Chunks to count up to when iterating over this collection
     */
    private int size;


    public ChunkCollection(@Nullable  Location baseLoc, int size) {
        if(baseLoc != null){
            this.baseChunk = baseLoc.getChunk();
        }
        this.size = size;
    }

    /**
     * Gets the n-value for the given chunk
     * returns -1 if the n value is outside the range
     * of this collection
     * @param c the Chunk to calculate for.
     * @return n, if the n-value is found and <= size; -1 otherwise.
     */
    public int getN(Chunk c){
        int n = ChunkUtil.getN(c, this.getBaseChunk());
        return (n <= getSize()) ? n:-1;
    }

    /**
     * Get the Chunk that this chunk collection is based from.
     */
    public Chunk getBaseChunk() {
        return baseChunk;
    }

    /**
     * Get the Chunk that this chunk collection is based from.
     */
    public void setBaseChunk(Chunk baseChunk) {
        this.baseChunk = baseChunk;
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
        return ChunkUtil.getNthChunk(size, baseChunk);
    }

    /**
     * Returns the iterator for this chunk collection.
     */
    @NotNull
    @Override
    public Iterator<Chunk> iterator() {
        return new Iterator<Chunk>() {
            //the current n-value
            private int n = 0;
            private Chunk base = baseChunk;
            

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
