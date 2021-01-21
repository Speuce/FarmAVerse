package com.speuce.farmtopia.farm.manager;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.main.FarmTopia;
import com.speuce.farmtopia.plot.BuildQueue;
import com.speuce.farmtopia.plot.Plot;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * Manages Farm Locations in a given world.
 */
public class FarmLocationManager {

    // TODO support more worlds
    private World main;

    private ConcurrentLinkedQueue<Location> availableLocs;

    private FarmManager manager;

    /**
     * Cached farm locations for currently built farms.
     * For easy lookup
     */
    private Map<Location, Farm> lookup;

    public FarmLocationManager(FarmManager manager) {
        this.lookup = new HashMap<Location, Farm>();
        this.manager = manager;
        try{
            this.main = Bukkit.getWorld("world");
        }catch (Exception e){
            System.out.println("world is incorrectly named!");
            e.printStackTrace();
        }

        this.availableLocs = new ConcurrentLinkedQueue<Location>();
        this.getLocUpdater().runTaskTimer(FarmTopia.getFarmTopia(), 20L, 40L);
    }

    /**
     * Rounds a block location to the closest 500
     * Used for base location (location around which a farm is built at)
     * calculation.
     */
    public int nearest500(int val) {
        return (int) Math.ceil(val / 500D) * 500;
    }



    /**
     * Gets the farm nearest to the given location.
     * @param l the location to search
     * @return the {@link Farm} that the location is likely in, or {@code null}
     *      if the location is out of bounds of where a farm would be
     */
    public Farm getFarmAtLocation(Location l){
        Location pl = new Location(l.getWorld(),
                nearest500(l.getBlockX() - 16), Constant.baseY,
                nearest500(l.getBlockZ() - 16));
        return lookup.get(pl);
    }

    public void removeLocationLookup(Location l){
        lookup.remove(l);
    }

    /**
     * Adds the given farm to the 'nearest500' lookup table.
     */
    public void addLocationLookup(Location l, Farm f){
        lookup.put(l, f);
    }

    /**
     * Get the main world where farms are built.
     */
    public World getMainWorld(){
        return main;
    }

    /**
     * Get the task that updates the list of available farm locations.
     * Additionally, teleports players who fell off the world for whatever
     * reason back to their farm.
     */
    private BukkitRunnable getLocUpdater() {
        return new BukkitRunnable() {

            @Override
            public void run() {

                int x = 500;
                int y = 500;
                while (availableLocs.size() < 5) {
                    if (y < 10000) {
                        y += 500;
                    } else {
                        y = 500;
                        x += 500;
                    }
                    Block l = main.getBlockAt(x, Constant.baseY, y);
                    if (l.getType() == Material.AIR) {
                        availableLocs.add(l.getLocation());
                    }
                }

            }

        };
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

    /**
     * Cleans up a farm location for later use.
     */
    public void cleanFarm(Farm f) {
        for (Plot p : f.getAllPlots()) {
            if (p.getChunk() != null) {
                p.cleanup();
                BuildQueue.queue(manager.getClear().def(p.getChunk().getBlock(0, Constant.baseY, 0), FarmTopia.getFarmTopia()));
                Plot[] nearby = f.getNearbyPlots(p);
                if(nearby[0] == null){
                    Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX(), p.getChunk().getZ()-1);
                    BuildQueue.queue(manager.getClear().def(c.getBlock(0, Constant.baseY, 0), FarmTopia.getFarmTopia()));
                }
                if(nearby[1] == null){
                    Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX()+1,
                            p.getChunk().getZ()-1);
                    BuildQueue.queue(manager.getClear().def(c.getBlock(0, Constant.baseY, 0), FarmTopia.getFarmTopia()));
                }
                if(nearby[2] == null){
                    Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX()+1, p.getChunk().getZ());
                    BuildQueue.queue(manager.getClear().def(c.getBlock(0, Constant.baseY, 0), FarmTopia.getFarmTopia()));
                }
                if(nearby[3] == null){
                    Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX()+1, p.getChunk().getZ()+1);
                    BuildQueue.queue(manager.getClear().def(c.getBlock(0, Constant.baseY, 0), FarmTopia.getFarmTopia()));
                }
                if(nearby[4] == null){
                    Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX(), p.getChunk().getZ()+1);
                    BuildQueue.queue(manager.getClear().def(c.getBlock(0, Constant.baseY, 0), FarmTopia.getFarmTopia()));
                }
                if(nearby[5] == null){
                    Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX()-1, p.getChunk().getZ()+1);
                    BuildQueue.queue(manager.getClear().def(c.getBlock(0, Constant.baseY, 0), FarmTopia.getFarmTopia()));
                }
                if(nearby[6] == null){
                    Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX()-1, p.getChunk().getZ());
                    BuildQueue.queue(manager.getClear().def(c.getBlock(0, Constant.baseY, 0), FarmTopia.getFarmTopia()));
                }
                if(nearby[7] == null){
                    Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX()-1, p.getChunk().getZ()-1);
                    BuildQueue.queue(manager.getClear().def(c.getBlock(0, Constant.baseY, 0), FarmTopia.getFarmTopia()));
                }
            }
        }
    }

    /**
     * Requests and Finds a suitable location for the player's
     * farm to be built.
     * @param p the player who is making the request (for debug info)
     * @param callback will be called with the provided location once a suitable location has been found.
     */

    public void requestLocation(@Nullable Player p, Consumer<Location> callback){
        BukkitRunnable br = new BukkitRunnable() {
            Location l = availableLocs.poll();
            @Override
            public void run() {

                if (l == null) {
                    if(p != null){
                        p.sendMessage(ChatColor.RED + "Trying to find a location to place your farm..");
                    }
                    l = availableLocs.poll();
                }
                if(l != null){
                    callback.accept(l);
                    cancel();
                }
            }
        };
        br.runTaskTimer(FarmTopia.getFarmTopia(), 10L, 1200L);
    }
}
