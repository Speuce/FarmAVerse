package com.speuce.farmtopia.farm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.speuce.farmtopia.commands.ExpCommand;
import com.speuce.farmtopia.commands.FarmCommand;
import com.speuce.farmtopia.commands.PurgeCommand;
import com.speuce.farmtopia.farm.handlers.PhysicsHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.jobs.JobManager;
import com.speuce.farmtopia.main.FarmTopia;
import com.speuce.farmtopia.plot.BuildQueue;
import com.speuce.farmtopia.plot.EmptyPlot;
import com.speuce.farmtopia.plot.FarmPlot;
import com.speuce.farmtopia.plot.Plot;
import com.speuce.farmtopia.plot.PlotBuilder;
import com.speuce.farmtopia.plot.Plots;
import com.speuce.farmtopia.plot.upgradeable.ResearchCentre;
import com.speuce.farmtopia.plot.upgradeable.Upgradeable;
import com.speuce.farmtopia.resources.Resource;
import com.speuce.farmtopia.util.Debug;
import com.speuce.farmtopia.util.Economy;
import com.speuce.farmtopia.util.SC;
import com.speuce.schemetic.RunnablePredefinedSchem;
import com.speuce.schemetic.Schematic;
import com.speuce.schemetic.SchemeticManager;
import com.speuce.sql.DataType;
import com.speuce.sql.SQLManager;
import com.speuce.sql.TableCheck;
import com.speuce.sql.booleanQuery;

public class FarmManager implements Listener{
	private Map<Player, Farm> loadedFarms;
	private Map<Location, Farm> lookup;
	private SchemeticManager schem;
	private Schematic clear;
	private SQLManager sql;
	private FarmTopia pl;
	private ConcurrentLinkedQueue<Location> availableLocs;
	private Random r = new Random();
	private JobManager jobs;
	//private BlockPopulator blockpop;
	// private Set<Chunk> keep;

	// TODO support more worlds
	private World main;

	public FarmManager(FarmTopia main, SQLManager sql, JobManager jobs) {
	    new PhysicsHandler(this);
		this.jobs = jobs;
		this.schem = main.getSchem();
		this.availableLocs = new ConcurrentLinkedQueue<Location>();
		this.sql = sql;
		this.pl = main;
		// this.keep = new HashSet<Chunk>();
		this.main = Bukkit.getWorld("world");
		this.loadedFarms = new HashMap<Player, Farm>();
		this.lookup = new HashMap<Location, Farm>();
		this.clear = schem.getSchemetic("clear");
		main.getCommand("farm").setExecutor(new FarmCommand(this));
		main.getCommand("exp").setExecutor(new ExpCommand(this));
		main.getCommand("purge").setExecutor(new PurgeCommand(this));
		main.getServer().getPluginManager().registerEvents(this, main);
		sql.Query(new TableCheck("farms", new booleanQuery() {

			@Override
			public void onReturn(boolean b) {
				if (b == false) {
					makeTable();
				}

			}
		}));

		this.getLocUpdater().runTaskTimer(pl, 20L, 20L);
		this.getFarmUpdater().runTaskTimerAsynchronously(main, 200L, 200L);
		this.getPlotUpdater().runTaskTimer(main, 400L, 400L);

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

	public FarmTopia getPlugin() {
		return this.pl;
	}



	private BukkitRunnable getPlotUpdater() {
		return new BukkitRunnable() {

			@Override
			public void run() {
				for (Farm f : loadedFarms.values()) {
					for (Plot p : f.getAllPlots()) {
						if (p instanceof FarmPlot) {
							continue;
						} else {
							if (p != null) {
								p.update();
							}

						}
					}
				}

			}

		};
	}

	@EventHandler
	public void onPrepareCraft(CraftItemEvent e) {
		// e.setResult(Result.DENY);
		e.setCancelled(true);
	}

	private BukkitRunnable getFarmUpdater() {
		return new BukkitRunnable() {

			@Override
			public void run() {
				for (Farm f : loadedFarms.values()) {
					for (Plot p : f.getAllPlots()) {
						if (p instanceof FarmPlot) {
							FarmPlot fp = (FarmPlot) p;
							fp.updateStages(true);
						}
						// else{
						// if(p != null){
						// p.update();
						// }
						//
						// }
					}
				}
				// if(r.nextInt(3) == 1){
				// for(Player p: Bukkit.getOnlinePlayers()){
				// if(p.getOpenInventory() != null &&
				// p.getOpenInventory().getTopInventory() != null){
				// if(p.getOpenInventory().getTitle().equals(Constant.seedExtractorName)){
				// Farm f = loadedFarms.get(p);
				// ((ResearchCentre)f.getFirstPlot(ResearchCentre.class)).openUpgradeInventory(p);
				// }
				// }
				// }
				// }

				if (r.nextInt(20) == 0) {
					BuildQueue.test();
				}
			}

		};
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

	private BukkitRunnable getLocUpdater() {
		return new BukkitRunnable() {

			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.getLocation().getY() <= 70) {
						Farm f = loadedFarms.get(p);
						if (f != null && p != null) {
							teleportTo(p, f);
						}
					}
				}
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

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (this.loadedFarms.containsKey(e.getPlayer())) {
			this.teleportTo(e.getPlayer(), this.loadedFarms.get(e.getPlayer()));
		} else {
			e.getPlayer().teleport(main.getSpawnLocation());
			e.getPlayer().sendMessage(ChatColor.GREEN + "Loading your farm..");
			final Player pl = e.getPlayer();
			this.loadFarm(e.getPlayer(), main, new FarmReady() {

				@Override
				public void onFinish(Farm f) {
					teleportTo(pl, f);
					e.getPlayer().setExp(f.getProgress() / 255F);
					e.getPlayer().setLevel(f.getLevel());
					// Bukkit.broadcastMessage("leve " + f.getLevel());
					pl.sendMessage(ChatColor.GREEN + "Welcome to your Farm!");
				}

			});
		}

	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getCause() == DamageCause.FALL) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		// TODO optimize so it doesnt clear farm until minutes after
		if (this.loadedFarms.containsKey(e.getPlayer())) {
			final Farm fl = this.loadedFarms.get(e.getPlayer());
			cleanEntities(fl);
			cleanFarm(fl);
			BukkitRunnable br = new BukkitRunnable() {

				@Override
				public void run() {
					saveFarm(fl, e.getPlayer());

					lookup.remove(fl.getBaseLocation());
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
		for (Plot p : f.getAllPlots()) {
			p.cleanup();
		}
	}

	public void teleportTo(Player p, Farm f) {
	    Plot plot = f.getPlot(0);
		p.teleport(plot.getChunk().getBlock(7, Constant.baseY + 3, 7).getLocation());
        p.setVelocity(new Vector(0, 1, 0));
	}

	private void makeTable() {
		Map<String, DataType> columns = new HashMap<String, DataType>();
		columns.put("uuid", DataType.UUID);
		columns.put("farm", DataType.BLOB);
		columns.put("inv", DataType.BLOB);
		columns.put("bal", DataType.DOUBLE);
		columns.put("jobs", DataType.BLOB);
		columns.put("tut", DataType.INT);
		sql.CreateTable("farms", columns, "uuid");
	}
	public Schematic getClear(){
		return clear;
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
	public void cleanFarm(Farm f) {
		for (Plot p : f.getAllPlots()) {
			if (p.getChunk() != null) {
				p.cleanup();
				BuildQueue.queue(clear.def(p.getChunk().getBlock(0, Constant.baseY, 0), pl));
				Plot[] nearby = f.getNearbyPlots(p);
				if(nearby[0] == null){
					Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX(), p.getChunk().getZ()-1);
					BuildQueue.queue(clear.def(c.getBlock(0, Constant.baseY, 0), pl));
				}
				if(nearby[1] == null){
					Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX()+1,
							p.getChunk().getZ()-1);
					BuildQueue.queue(clear.def(c.getBlock(0, Constant.baseY, 0), pl));
				}
				if(nearby[2] == null){
					Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX()+1, p.getChunk().getZ());
					BuildQueue.queue(clear.def(c.getBlock(0, Constant.baseY, 0), pl));
				}
				if(nearby[3] == null){
					Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX()+1, p.getChunk().getZ()+1);
					BuildQueue.queue(clear.def(c.getBlock(0, Constant.baseY, 0), pl));
				}
				if(nearby[4] == null){
					Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX(), p.getChunk().getZ()+1);
					BuildQueue.queue(clear.def(c.getBlock(0, Constant.baseY, 0), pl));
				}
				if(nearby[5] == null){
					Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX()-1, p.getChunk().getZ()+1);
					BuildQueue.queue(clear.def(c.getBlock(0, Constant.baseY, 0), pl));
				}
				if(nearby[6] == null){
					Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX()-1, p.getChunk().getZ());
					BuildQueue.queue(clear.def(c.getBlock(0, Constant.baseY, 0), pl));
				}
				if(nearby[7] == null){
					Chunk c = p.getChunk().getWorld().getChunkAt(p.getChunk().getX()-1, p.getChunk().getZ()-1);
					BuildQueue.queue(clear.def(c.getBlock(0, Constant.baseY, 0), pl));
				}
			}
		}
		return;
	}

	public void loadFarm(Player p, World w, FarmReady fr) {
		p.getInventory().clear();
		BukkitRunnable br = new BukkitRunnable() {

			@Override
			public void run() {
				PreparedStatement ps = null;
				Connection c = null;
				ResultSet rs = null;
				try {
					c = sql.getConnection();
					ps = c.prepareStatement("SELECT * FROM farms WHERE uuid=?");
					ps.setString(1, p.getUniqueId().toString());
					rs = ps.executeQuery();
					if (!rs.next()) {
						newPlayer(p, w, fr);
					} else {
						jobs.loadData(p, rs.getBytes("jobs"));

						byte[] data = rs.getBytes("farm");
						Location l = availableLocs.poll();
						while (l == null) {
							p.sendMessage(ChatColor.RED + "Trying to find a location to place your farm..");
							Thread.sleep(1200);
							l = availableLocs.poll();
						}

						Farm f = Farm.deserialize(data, l, p, getInstance());
						FarmBuilder fb = new FarmBuilder(pl);
						fb.build(f, fr);
						loadedFarms.put(p, f);
						lookup.put(l, f);
						Score(p, rs.getDouble("bal"));
						byte[] inv = rs.getBytes("inv");
						Tutorial.newPlayer(p, rs.getInt("tut"));
						for (int x = 0; x < inv.length; x += 2) {
							ItemStack r = Resource.deserialize(Arrays.copyOfRange(inv, x, x + 2),
									Constant.currentProtocol);
							if (r != null && r.getType() != Material.AIR) {
								p.getInventory().setItem(x, r);
							}

						}

					}
				} catch (SQLException | InterruptedException e) {
					e.printStackTrace();
				} finally {
					sql.close(ps);
					sql.close(rs);
					sql.close(c);

				}

			}

		};
		br.runTaskAsynchronously(pl);
	}

	private void Score(Player p, Double bal) {
		BukkitRunnable br = new BukkitRunnable() {

			@Override
			public void run() {
				Economy.setBalance(p.getUniqueId(), bal);
				SC.setLine(p, 0, ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "Money");
			}

		};
		br.runTask(this.pl);
	}

	public void newPlayer(Player p, World w, FarmReady fr) throws InterruptedException {
		Resource r = Resource.WHEAT_SEEDS;
		ItemStack i = r.toItemStack(5);
		p.getInventory().addItem(i);
		p.getInventory().addItem(Resource.MAGIC_DUST.toItemStack(15));
		// Bukkit.broadcastMessage("NEW PLAYERRR");
		p.sendMessage(ChatColor.GREEN.toString() + "Welcome!");
		Connection c = null;
		PreparedStatement ps = null;
		Location l = availableLocs.poll();
		while (l == null) {
			p.sendMessage(ChatColor.RED + "Trying to find a location to place your farm..");
			Thread.sleep(1200);
			l = availableLocs.poll();
		}
		Farm f = new Farm(l, p, this, 0, (byte) 0);
		f.addPlot(new FarmPlot(f));
		FarmBuilder fb = new FarmBuilder(pl);
		fb.build(f, fr);
		loadedFarms.put(p, f);
		lookup.put(l, f);
		// Economy.setBalance(p.getUniqueId(),0D);
		BukkitRunnable br = new BukkitRunnable() {

			@Override
			public void run() {
				SC.newScoreboard(p);

			}

		};
		br.runTask(this.pl);
		try {
			c = sql.getConnection();
			ps = c.prepareStatement("INSERT INTO farms (uuid, farm, inv, bal, jobs, tut) VALUES (?,?,?,?,?,?)");
			ps.setString(1, p.getUniqueId().toString());
			ps.setBytes(2, f.FINALserialize(Constant.currentProtocol));
			ps.setBytes(3, new byte[2]);
			ps.setDouble(4, 0);
			byte[] job = JobManager.newPlayer();
			ps.setBytes(5, job);
			ps.setInt(6, 0);
			jobs.loadData(p, job);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			sql.close(ps);
			sql.close(c);
		}
		Score(p, 0D);
		Tutorial.newPlayer(p, 0);
	}

	private void saveFarm(Farm f, Player p) {
		Connection c = null;
		PreparedStatement ps = null;
		final byte[] inv = this.SerializeInventory(p.getInventory());
		try {
			c = sql.getConnection();
			ps = c.prepareStatement("UPDATE farms SET farm=?, inv=?, bal=?, jobs=?, tut=? WHERE uuid=?");
			ps.setString(6, p.getUniqueId().toString());
			byte[] ftr = f.FINALserialize(Constant.currentProtocol);
			// Bukkit.broadcastMessage("saving::: " + Hex.encodeHexString(ftr));
			ps.setBytes(1, ftr);
			ps.setBytes(2, inv);
			ps.setDouble(3, Economy.getRemoveBalance(p.getUniqueId()));
			ps.setBytes(4, jobs.saveData(p));
			ps.setInt(5, Tutorial.getDelProgress(p));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			sql.close(ps);
			sql.close(c);
		}
	}

	public void deleteData(Player p) {
        Farm f = getFarm(p);
        lookup.remove(f.getBaseLocation());
        loadedFarms.remove(f);



		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = sql.getConnection();
			ps = c.prepareStatement("DELETE FROM farms WHERE uuid=?");
			ps.setString(1, p.getUniqueId().toString());
			// Bukkit.broadcastMessage("query: " + ps.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			sql.close(ps);
			sql.close(c);
		}
	}

	private byte[] SerializeInventory(Inventory i) {
		byte[] ret = new byte[i.getSize() * 2];
		for (int x = 0; x < i.getSize(); x++) {
			if (i.getItem(x) != null) {
				byte[] t = Resource.serialize(i.getItem(x));
				ret[x * 2] = t[0];
				ret[(x * 2) + 1] = t[1];

			}
		}
		return ret;
	}

	public int nearest500(int val) {
		return (int) Math.ceil(val / 500D) * 500;
	}


	public void db(String s) {
		Debug.getInstance().log(Debug.Type.FARM, s);
	}
}
