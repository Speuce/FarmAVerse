package com.speuce.farmtopia.farm.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import com.speuce.farmtopia.commands.ExpCommand;
import com.speuce.farmtopia.commands.FarmCommand;
import com.speuce.farmtopia.commands.PurgeCommand;
import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.farm.handlers.InventoryHandler;
import com.speuce.farmtopia.farm.handlers.JoinQuitHandler;
import com.speuce.farmtopia.farm.handlers.PhysicsHandler;
import com.speuce.farmtopia.farm.handlers.StackingHandler;
import com.speuce.farmtopia.farm.task.FarmPlotUpdater;
import com.speuce.farmtopia.farm.task.PlotUpdater;
import com.speuce.farmtopia.farm.task.TeleportTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.jobs.JobManager;
import com.speuce.farmtopia.main.FarmTopia;
import com.speuce.farmtopia.plot.BuildQueue;
import com.speuce.farmtopia.plot.FarmPlot;
import com.speuce.farmtopia.plot.Plot;
import com.speuce.farmtopia.util.Debug;
import com.speuce.farmtopia.util.Economy;
import com.speuce.farmtopia.util.SC;
import com.speuce.schemetic.Schematic;
import com.speuce.schemetic.SchemeticManager;
import com.speuce.sql.DataType;
import com.speuce.sql.SQLManager;
import com.speuce.sql.TableCheck;
import com.speuce.sql.booleanQuery;
import org.jetbrains.annotations.Nullable;

public class FarmManager implements Listener{
	private final Map<Player, Farm> loadedFarms;

	private final SchemeticManager schem;
	private final Schematic clear;
	private final SQLManager sql;
	private final FarmTopia pl;

	private final Random r = new Random();
	private JobManager jobs;
	private final FarmIO iomanager;
	private final FarmLocationManager locationManager;

	//private BlockPopulator blockpop;
	// private Set<Chunk> keep;



	public FarmManager(FarmTopia main, SQLManager sql, JobManager jobs) {

        this.sql = sql;
        this.pl = main;

	    new PhysicsHandler(this);
	    new InventoryHandler(this);
	    new PhysicsHandler(this);
	    new StackingHandler(this);
	    new JoinQuitHandler(this);


	    iomanager = new FarmIO(this, sql);
	    locationManager = new FarmLocationManager(this);
		this.jobs = jobs;
		this.schem = main.getSchem();

		this.loadedFarms = new HashMap<Player, Farm>();

		this.clear = schem.getSchemetic("clear");

		main.getCommand("farm").setExecutor(new FarmCommand(this));
		main.getCommand("exp").setExecutor(new ExpCommand(this));
		main.getCommand("purge").setExecutor(new PurgeCommand(this));
		main.getServer().getPluginManager().registerEvents(this, main);



		new FarmPlotUpdater(this).runTaskTimerAsynchronously(main, 200L, 200L);
		new TeleportTask(this).runTaskTimer(main, 20L, 20L);
		new PlotUpdater(this).runTaskTimer(main, 400L, 400L);

	}

    /**
     * Get the class responsible for handling farm locations.
     * @return
     */
	public FarmLocationManager getLocationManager(){
	    return this.locationManager;
    }


    /**
     * Get the farm of the given player
     * @param p the player to get the farm of
     * @return the {@link Farm} if loaded, {@code null} otherwise.
     */
	public Farm getFarm(Player p) {
		return this.loadedFarms.get(p);
	}

    /**
     * Checks whether a farm for the given player has been loaded into the cache
     * @param p the player to check
     * @return true if the farm is loaded, false otherwise.
     */
	public boolean hasFarm(Player p){
	    return this.loadedFarms.containsKey(p);
    }


	public FarmTopia getPlugin() {
		return this.pl;
	}


    /**
     * @return a collection of all farms loaded into the farm cache.
     */
	public Collection<Farm> getAllLoadedFarms(){
	    return loadedFarms.values();
    }


	@EventHandler
	public void onPrepareCraft(CraftItemEvent e) {
		// e.setResult(Result.DENY);
		e.setCancelled(true);
	}


	public byte getLevel(Player pl) {
		if (this.loadedFarms.containsKey(pl)) {
			return (byte) this.loadedFarms.get(pl).getLevel();
		}
		return 0;
	}



	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		this.teleportTo(e.getEntity(), this.loadedFarms.get(e.getEntity()));
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
	}



	public void buildSchem(String name, Block place) {
		Schematic sc = schem.getSchemetic(name);
		if (sc != null) {
			BuildQueue.queue(sc.def(place, this.pl));
		}
	}

	public void buildSchemOpt(String name, Block place) {
		Schematic sc = schem.getSchemetic(name);
		if (sc != null) {
			sc.buildOptimized(place, this.pl);
		}
	}

	public FarmManager getInstance() {
		return this;
	}


	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e) {

		// Location l = e.getChunk().getBlock(0, Constant.baseY,
		// 0).getLocation();
		// for(Farm f: this.loadedFarms.values()){
		// if(l.distanceSquared(f.getBaseLocation()) < 6400){
		// e.setCancelled(true);
		// //Bukkit.broadcastMessage("cancelload");
		// return;
		// }
		// }
		// if(this.keep.contains(e.getChunk())){
		// e.setCancelled(true);
		// }
	}
    public FarmIO getIomanager(){
        return iomanager;
    }




	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getCause() == DamageCause.FALL) {
				e.setCancelled(true);
			}
		}
	}


	public void teleportTo(Player p, Farm f) {
	    Plot plot = f.getPlot(0);
		p.teleport(plot.getChunk().getBlock(7, Constant.baseY + 3, 7).getLocation());
        p.setVelocity(new Vector(0, 1, 0));
	}


	public Schematic getClear(){
		return clear;
	}


    /**
     * Adds the given farm to the farm cache
     */
    public void addCachedFarm(Player p, Farm f){
       loadedFarms.put(p, f);
    }



    /**
     * Removes a farm from internal caches.
     */
    public void removeCachedFarm(Farm f){
        loadedFarms.remove(f.getOwner());
        locationManager.removeLocationLookup(f.getBaseLocation());
    }

	public void updateBalance(Player p, Double bal) {
        Economy.setBalance(p.getUniqueId(), bal);
        SC.setLine(p, 0, ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "Money");
	}



	public void db(String s) {
		Debug.getInstance().log(Debug.Type.FARM, s);
	}
}
