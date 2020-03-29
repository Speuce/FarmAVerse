package main.java.com.speuce.farmtopia.farm;

import main.java.com.speuce.farmtopia.commands.ExpCommand;
import main.java.com.speuce.farmtopia.commands.FarmCommand;
import main.java.com.speuce.farmtopia.event.farm.FarmLoadedEvent;
import main.java.com.speuce.farmtopia.farm.listeners.*;
import main.java.com.speuce.farmtopia.jobs.JobManager;
import main.java.com.speuce.farmtopia.main.FarmTopia;
import main.java.com.speuce.farmtopia.plot.BuildQueue;
import main.java.com.speuce.farmtopia.plot.Plot;
import main.java.com.speuce.farmtopia.util.Constant;
import main.java.com.speuce.farmtopia.util.Debug;
import main.java.com.speuce.farmtopia.util.SC;
import main.java.com.speuce.farmtopia.util.chunk.ChunkUtil;
import main.java.com.speuce.farmtopia.util.chunk.Direction;
import main.java.com.speuce.schemetic.RunnablePredefinedSchem;
import main.java.com.speuce.schemetic.Schematic;
import main.java.com.speuce.schemetic.SchemeticManager;
import main.java.com.speuce.sql.SQLManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;


public class FarmManager implements Listener, CommandExecutor {
	private Map<Player, Farm> loadedFarms;
	private Map<Location, Farm> lookup;
	private Schematic clear;
	private FarmTopia pl;
	private ConcurrentLinkedQueue<Location> availableLocs;
	private Random r = new Random();
	private FarmSQL sql;
	//private BlockPopulator blockpop;
	// private Set<Chunk> keep;

	// TODO support more worlds
	private World main;

	public FarmManager(FarmTopia main, SQLManager sql) {
		SchemeticManager schem = main.getSchem();
		this.availableLocs = new ConcurrentLinkedQueue<Location>();
		this.pl = main;
		this.sql = new FarmSQL(sql, this);
		// this.keep = new HashSet<Chunk>();
		this.main = Bukkit.getWorld("world");
		this.loadedFarms = new HashMap<Player, Farm>();
		this.lookup = new HashMap<Location, Farm>();
		this.clear = schem.getSchemetic("clear");

		//register commands
		Objects.requireNonNull(main.getCommand("farm")).setExecutor(new FarmCommand(this));
		Objects.requireNonNull(main.getCommand("exp")).setExecutor(new ExpCommand(this));
		Objects.requireNonNull(main.getCommand("purge")).setExecutor(this);

		//register listeners
		PluginManager pm = main.getServer().getPluginManager();
		pm.registerEvents(this, main);
		pm.registerEvents(new CustomEntityInteractListener(this), main);
		pm.registerEvents(new CustomPickupListener(), main);
		pm.registerEvents(new CustomStackingListener(), main);
		pm.registerEvents(new DisableListeners(), main);
		pm.registerEvents(new InteractListener(this), main);
		pm.registerEvents(new InventoryClickListener(this, schem), main);
		pm.registerEvents(new SneakListener(this), main);


		//Start updater tasks.
		Updaters.getLocationUpdater(this).runTaskTimer(pl, 20L, 20L);
		Updaters.getFarmPlotUpdater(this).runTaskTimerAsynchronously(main, 200L, 200L);
		Updaters.getPlotUpdater(this).runTaskTimer(main, 400L, 400L);

	}

	/**
	 * @return a Queue where each entry is an available position (500x,500z)
	 * that a farm can be built at.
	 */
	public ConcurrentLinkedQueue<Location> getAvailableLocations() {
		return availableLocs;
	}

	/**
	 * Returns the main world that farms are being built in.
	 */
	public World getMainWorld(){
		return main;
	}



	public byte getLevel(Player pl) {
		if (getLoadedFarms().containsKey(pl)) {
			return (byte) getLoadedFarms().get(pl).getLevel();
		}
		return 0;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if(farmLoaded(e.getEntity())){
			this.teleportTo(e.getEntity(), getLoadedFarms().get(e.getEntity()));
		}else{
			e.getEntity().teleport(getMainWorld().getSpawnLocation());
		}

	}

	/**
	 * Finds the nearest farm to the given location
	 * @param l the given location
	 * @return the farm if found. null otherwise
	 */
	@Nullable
	public Farm getFarm(Location l){
		Location pl = new Location(l.getWorld(),
				this.nearest500(l.getBlockX() - 16), Constant.baseY,
				this.nearest500(l.getBlockZ() - 16));
		if(lookup.containsKey(pl)){
			return lookup.get(pl);
		}
		return null;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (getLoadedFarms().containsKey(e.getPlayer())) {
			this.teleportTo(e.getPlayer(), getLoadedFarms().get(e.getPlayer()));
		} else {
			e.getPlayer().teleport(main.getSpawnLocation());
			e.getPlayer().sendMessage(ChatColor.GREEN + "Loading your farm..");
			final Player pl = e.getPlayer();
			sql.loadFarm(e.getPlayer(), main);
		}

	}


	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		// TODO optimize so it doesnt clear farm until minutes after
		if (getLoadedFarms().containsKey(e.getPlayer())) {
			final Farm fl = getLoadedFarms().get(e.getPlayer());
			cleanEntities(fl);
			cleanFarm(fl);
			BukkitRunnable br = new BukkitRunnable() {

				@Override
				public void run() {
					sql.saveFarm(fl, e.getPlayer());
					lookup.remove(fl.getBaseChunk());
					loadedFarms.remove(e.getPlayer());
					if (Bukkit.getServer().getOnlinePlayers().isEmpty()) {
						BuildQueue.queue(new RunnablePredefinedSchem(new Runnable() {
							@Override
							public void run() {
								System.out.println("Server is ready for shutdown.");
							}
						}));
					}
				}

			};
			br.runTaskLaterAsynchronously(pl, 5L);
		}
		SC.logoff(e.getPlayer());
	}

	private void cleanEntities(Farm f) {
		for (Plot p : f.getPlots()) {
			p.cleanup();
		}
	}

	public void teleportTo(Player p, Farm f) {
		p.setVelocity(new Vector(0, 1, 0));
		p.teleport(f.getPlot(0).getChunk().getBlock(7, Constant.baseY + 3, 7).getLocation());
	}

	/**
	 * Cleans the farm out, removing all plots and walls
	 * @param f the farm to clean.
	 */
	public void cleanFarm(Farm f) {
		for(Plot p: f.getPlots()){
			p.cleanup();
			clearChunk(p.getChunk());
			for(Direction d: Direction.values()){
				Chunk c = ChunkUtil.getNearby(p.getChunk(), d);
				if(f.getPlot(c) == null){
					clearChunk(c);
				}
			}
		}
	}

	/**
	 * Clears the given chunk of all build schems
	 * @param c the chunk to clear.
	 */
	private void clearChunk(Chunk c){
		BuildQueue.queue(clear.def(c.getBlock(0, Constant.baseY, 0)));
	}

	@EventHandler
	public void onFarmLoad(FarmLoadedEvent e){
		Farm f = e.getFarm();
		Player pl = e.getFarm().getOwner();
		teleportTo(pl, f);
		pl.setExp(f.getProgress() / 255F);
		pl.setLevel(f.getLevel());
		pl.sendMessage(ChatColor.GREEN + "Welcome to your Farm!");
	}

	/**
	 * Executes sequence of commands to find a location for a farm
	 * and building the farm. As well, adds the farm to the lookup tables
	 *  *** MUST BE RUN FROM AN ASYNCHRONOUS THREAD ***
	 * @param p the Player whose farm is loaded
	 * @param f the farm that has been loaded
	 */
	public void onFarmLoaded(Player p, Farm f){
		Location l = availableLocs.poll();
		while (l == null) {
			p.sendMessage(ChatColor.RED + "Trying to find a location to place your farm..");
			try {
				Thread.sleep(1200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			l = availableLocs.poll();
			f.setBaseChunk(l.getChunk());
			FarmBuilder fb = new FarmBuilder(f);
			fb.build();
			loadedFarms.put(p, f);
			lookup.put(l, f);
		}
	}


	/**
	 * Rounds the value UP to the nearest 500
	 * @param val the value unrounded
	 */
	private int nearest500(int val) {
		return (int) Math.ceil(val / 500D) * 500;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] arg3) {
		 if (cmd.getName().equalsIgnoreCase("purge")) {
			if (sender.isOp() && arg3.length == 1) {
				Player target = Constant.getPlayer(arg3[0]);
				if (target != null) {
					target.sendMessage(ChatColor.RED.toString() + "Your data has been purged..");
					target.getInventory().clear();
					Farm f = getLoadedFarms().get(target);
					target.teleport(new Location(target.getWorld(), 0, Constant.baseY + 5, 0));
					cleanFarm(f);
					sql.deleteData(target);
					lookup.remove(f.getBaseChunk());
					loadedFarms.remove(target);
					try {
						sql.newPlayer(target, target.getWorld());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					sender.sendMessage(ChatColor.GREEN.toString() + "Success!");
					return true;
				} else {
					sender.sendMessage(ChatColor.RED.toString() + "Target not found!");
					return true;
				}
			}
		}
		return false;
	}
	public void db(String s) {
		Debug.getInstance().log(Debug.Type.FARM, s);
	}

	/**
	 * @return The map of loaded farms, indexed by each farm's owner
	 */
	public Map<Player, Farm> getLoadedFarms() {
		return loadedFarms;
	}

	/**
	 * Get the farm owned by a specific player
	 * @param p the player who owns the farm we wanna find
	 * @return the associated {@link Farm}, if found.
	 */
	public Farm getFarm(Player p) {
		return getLoadedFarms().get(p);
	}

	public FarmTopia getPlugin() {
		return this.pl;
	}

	/**
	 * Checks if the given player's farm is loaded yet
	 */
	public boolean farmLoaded(Player p){
		return loadedFarms.containsKey(p);
	}
}
