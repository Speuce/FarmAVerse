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
import com.speuce.farmtopia.util.Economy;
import com.speuce.farmtopia.util.SC;
import com.speuce.schemetic.RunnablePredefinedSchem;
import com.speuce.schemetic.Schematic;
import com.speuce.schemetic.SchemeticManager;
import com.speuce.sql.DataType;
import com.speuce.sql.SQLManager;
import com.speuce.sql.TableCheck;
import com.speuce.sql.booleanQuery;

public class FarmManager implements Listener, CommandExecutor {
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
		this.jobs = jobs;
		this.schem = main.getSchem();
		this.availableLocs = new ConcurrentLinkedQueue<Location>();
		this.sql = sql;
		this.pl = main;
		// this.keep = new HashSet<Chunk>();
		this.main = Bukkit.getWorld("blank");
		this.loadedFarms = new HashMap<Player, Farm>();
		this.lookup = new HashMap<Location, Farm>();
		this.clear = schem.getSchemetic("clear");
		main.getCommand("farm").setExecutor(this);
		main.getCommand("exp").setExecutor(this);
		main.getCommand("purge").setExecutor(this);
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

	public Farm getFarm(Player p) {
		return this.loadedFarms.get(p);
	}

	public FarmTopia getPlugin() {
		return this.pl;
	}

	@EventHandler
	public void onPickup(EntityPickupItemEvent e) {
		if (e.getEntityType() == EntityType.PLAYER && e.getItem().getItemStack().getType() == Material.BOW) {
			ItemStack s = e.getItem().getItemStack();
			Player p = (Player) e.getEntity();
			Constant.forceGive(p, s);
			// Bukkit.broadcastMessage(s.getAmount() + "");
			Constant.playsound(p, Sound.ENTITY_ITEM_PICKUP, 1F);
			e.setCancelled(true);
			// e.getItem().setItemStack(new ItemStack(Material.AIR));
			e.getItem().remove();
			// e.setCancelled(true);
		}

	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void itemStackManager(InventoryClickEvent e) {

		// Bukkit.broadcastMessage("click1");

		if (e.getCurrentItem() != null && e.getCursor() != null && e.getCurrentItem().getType() == Material.BOW
				&& e.getCursor().getType() == Material.BOW && e.getAction() != InventoryAction.COLLECT_TO_CURSOR) {
			if (e.getCurrentItem().getDurability() == e.getCursor().getDurability()) {
				Constant.debug(e.getAction().toString());
				Constant.debug("CurrentItem: " + Constant.itemInfo(e.getCurrentItem()));
				Constant.debug("Cursor: " + Constant.itemInfo(e.getCursor()));
				Constant.debug("Clicktype: " + e.getClick());
				// if (!(e.getAction().toString().contains("PICKUP") ||
				// (e.getAction().equals(InventoryAction.PICKUP_SOME) &&
				// e.getClick() == ClickType.LEFT))) {
				// if(e.getCurrentItem().getType() == Material.BOW &&
				// e.getCursor().getType() == Material.BOW){
				// if(e.getCurrentItem().getDurability() ==
				// e.getCursor().getDurability()){

				// Bukkit.broadcastMessage(e.getCurrentItem().toString()
				// + ":" + e.getCursor().toString());
				if (e.getClick() == ClickType.LEFT) {
					if (e.getCurrentItem().getAmount() < 64) {

						int spaceLeft = 64 - e.getCurrentItem().getAmount();
						if (spaceLeft >= e.getCursor().getAmount()) {
							 Constant.debug("Click2");
							e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() + e.getCursor().getAmount());
							e.setCursor(null);
							((Player) e.getWhoClicked()).updateInventory();
							// Bukkit.broadcastMessage("yes5");
						} else {
							// Bukkit.broadcastMessage("click3");
							 Constant.debug("Click3");
							e.getCurrentItem().setAmount(64);
							ItemStack s = e.getCursor();
							s.setAmount(s.getAmount() - spaceLeft);
							e.setCursor(s);
							// e.getCursor().setAmount(e.getCursor().getAmount()
							// - spaceLeft);
							((Player) e.getWhoClicked()).updateInventory();
							// Bukkit.broadcastMessage("yes6");
						}
						e.setCancelled(true);

					}else{
						Constant.debug("ret");
						e.setCancelled(true);
						((Player) e.getWhoClicked()).updateInventory();
						return;
					}
				} else if (e.getClick() == ClickType.RIGHT) {
					// Constant.debug("rightt");
					if (e.getCurrentItem().getAmount() < 64) {
						e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() + 1);
						if (e.getCursor().getAmount() == 1) {
							e.setCursor(null);
							((Player) e.getWhoClicked()).updateInventory();
						} else {
							e.getCursor().setAmount(e.getCursor().getAmount() - 1);
						}
						// int spaceLeft = 64 - e.getCurrentItem().getAmount();
						e.setCancelled(true);

					}
				}

			}

		} else if (e.getCursor() != null && e.getCursor().getType() == Material.BOW
				&& e.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
			//Constant.debug("work: " + e.getClickedInventory().getSize());
			
			int slotcheck = 0;
			while (e.getCursor().getAmount() < 64 && slotcheck < e.getClickedInventory().getSize()) {
				ItemStack check = e.getClickedInventory().getItem(slotcheck);
				//Constant.debug("Check: " + Constant.itemInfo(check));
				if (check != null && check.getType() == Material.BOW
						&& check.getDurability() == e.getCursor().getDurability()) {
					int maxamt = 64 - e.getCursor().getAmount();
					if (check.getAmount() > maxamt) {
						e.getCursor().setAmount(64);
						check.setAmount(check.getAmount() - maxamt);
					} else {
						e.getCursor().setAmount(e.getCursor().getAmount() + check.getAmount());
						e.getClickedInventory().setItem(slotcheck, null);
					}
				}
				slotcheck++;
			}
			((Player) e.getWhoClicked()).updateInventory();
		}
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
	@EventHandler
	public void onMoistureChange(MoistureChangeEvent e){
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
				// if(p.getOpenInventory().getTopInventory().getName().equals(Constant.seedExtractorName)){
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
	public void onSpread(BlockSpreadEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		this.teleportTo(e.getEntity(), this.loadedFarms.get(e.getEntity()));
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		// Bukkit.broadcastMessage("clicked");
		if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null) {
			if (e.getClickedInventory().getName().contains("Harvested")) {
				if (e.getCurrentItem() == null) {
					return;
				}
				e.setCancelled(true);
				if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) {
					return;
				}
				if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Ok")) {
					e.getWhoClicked().closeInventory();
					return;
				}
			} else if (e.getClickedInventory().getName()
					.equalsIgnoreCase(ChatColor.DARK_PURPLE.toString() + "Farm Menu")) {
				e.setCancelled(true);
				if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
					if (e.getCurrentItem().getItemMeta().getDisplayName().contains("another Plot")) {
						Player pl = (Player) e.getWhoClicked();
						Farm f = this.loadedFarms.get(pl);
						double cost = f.getCostt();
						if (Economy.hasEnough(pl.getUniqueId(), cost)) {
							Plot ne = new EmptyPlot(f);
							PlotBuilder pb = new PlotBuilder(ne, this.schem, f.getCurrentChunk());
							pb.build(true);
							f.buildWalls(ne, true);
							f.nextChunk();
							f.addPlot(ne);
							Tutorial.onNewPlotBuild((Player) e.getWhoClicked());
							Economy.subtractBal(pl.getUniqueId(), cost);
							pl.getWorld().playSound(pl.getLocation(), Sound.BLOCK_ANVIL_USE, 3.5F, 0F);
							pl.sendMessage(ChatColor.GREEN.toString() + "Plot added!");
							pl.closeInventory();
						} else {
							pl.sendMessage(ChatColor.RED.toString() + "You don't have enough for that!");
							return;
						}
					} else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Jobs")) {
						Player pl = (Player) e.getWhoClicked();
						Bukkit.dispatchCommand(pl, "j");
					}
				}
			} else if (e.getClickedInventory().getName().startsWith("Upgrade")) {
				e.setCancelled(true);
				if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
					if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Cancel")) {
						e.getWhoClicked().closeInventory();
						return;
					} else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Buy")) {
						Player pl = (Player) e.getWhoClicked();
						Farm f = this.loadedFarms.get(pl);
						Plot p = f.getPlot(e.getWhoClicked().getLocation().getChunk());
						if (p != null && p instanceof Upgradeable) {
							Upgradeable up = (Upgradeable) p;
							// Bukkit.broadcastMessage("purchase: " +
							// up.canPurchase());
							if (up.canPurchase()) {
								int cost = up.getCost(up.getLv());
								if (Economy.hasEnough(pl.getUniqueId(), (double) cost)) {
									Economy.subtractBal(pl.getUniqueId(), (double) cost);
									up.upgrade();
								} else {
									pl.sendMessage(ChatColor.RED + "Sorry, you need $" + cost + " for that!");
									pl.closeInventory();
								}
							} else {
								pl.playSound(pl.getLocation(), Sound.BLOCK_ANVIL_LAND, 2F, 0F);
								return;
							}

						}
					}
				}
			} else if (e.getClickedInventory().getName().equals(Constant.setPlotName)) {
				e.setCancelled(true);
				if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
					Double cost = -1D;
					for (String s : e.getCurrentItem().getItemMeta().getLore()) {
						if (s.contains("Cost")) {
							if (s.contains("FREE")) {
								cost = 0D;
								break;
							} else {
								Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");
								Matcher m = p.matcher(s);
								if (m.find()) {
									cost = Double.parseDouble(m.group(1));
								}
								break;
							}

						}
					}
					if (cost > -1) {
						Class<? extends Plot> clazz = Plots
								.getFromName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
						if (clazz == null) {
							throw new NullPointerException("Couldn't find plot: "
									+ ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));

						}
						if (Economy.hasEnough(e.getWhoClicked().getUniqueId(), cost)) {
							Economy.subtractBal(e.getWhoClicked().getUniqueId(), cost);
							this.loadedFarms.get(((Player) e.getWhoClicked())).plotChange(clazz);
							e.getWhoClicked().sendMessage(ChatColor.GREEN.toString() + "Success!");
							e.getWhoClicked().closeInventory();
							e.getWhoClicked().setVelocity(new Vector(0, 2, 0));
							return;
						} else {
							e.getWhoClicked().sendMessage(ChatColor.RED.toString() + "You need "
									+ NumberFormat.getCurrencyInstance().format(cost) + " to buy that!");
							return;
						}
					}
				}
			} else if (e.getView().getTopInventory() != null
					&& e.getView().getTopInventory().getName().equalsIgnoreCase(Constant.seedExtractorName)
					&& e.isShiftClick()) {
				e.setCancelled(true);
				return;
			} else if (e.getClickedInventory().getName().equals(Constant.seedExtractorName)) {
				// Bukkit.broadcastMessage("cli");
				if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
					// Bukkit.broadcastMessage("current item isn't null");
					if (e.getCurrentItem().getType() == Material.GOLDEN_SHOVEL) {
						e.setCancelled(true);
					} else if (e.getSlot() == 1) {
						// STOP
						e.setCancelled(true);
					} else if (e.getSlot() == 7) {
						if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {
							// Bukkit.broadcastMessage("where it should be
							// going.");
							e.setCancelled(false);
							Plot pl = this.loadedFarms.get((Player) e.getWhoClicked())
									.getFirstPlot(ResearchCentre.class);
							if (pl != null && pl instanceof ResearchCentre) {
								ResearchCentre rs = (ResearchCentre) pl;
								rs.takeExtractProduct();
								// Bukkit.broadcastMessage("take");
							}
						} else {
							e.setCancelled(true);
						}
					} else {
						e.setCancelled(true);
					}

				} else {
					if (e.getSlot() != 1) {
						e.setCancelled(true);
					}
					// else{
					// if(e.getCursor() != null && e.getCursor().getType() !=
					// Material.AIR){
					// //START
					// Resource r = Resource.getByItem(e.getCursor());
					// if(r != null && r != Resource.NOTHING){
					// //REAL start
					// //Bukkit.broadcastMessage("strt");
					// Farm farm =
					// this.loadedFarms.get((Player)e.getWhoClicked());
					// Plot pl = farm.getFirstPlot(ResearchCentre.class);
					// if(pl != null && pl instanceof ResearchCentre){
					// //Bukkit.broadcastMessage("testexc");
					// if(Constant.canExtract(r)){
					// //Bukkit.broadcastMessage("can extract");
					// //TODO take seeds
					//
					// ResearchCentre rs = (ResearchCentre) pl;
					//
					// e.getWhoClicked().sendMessage(ChatColor.GREEN.toString()
					// + "Extraction Started.");
					// e.setCancelled(true);
					// if(e.getCursor().getAmount() == 1){
					// e.setCursor(null);
					// }else{
					// ItemStack s = e.getCursor();
					// s.setAmount(s.getAmount()-1);
					// e.setCursor(null);
					// Constant.forceGive((Player)e.getWhoClicked(), s);
					// }
					// rs.startExtract(r);
					// ((Player)e.getWhoClicked()).updateInventory();
					// rs.sendSeedInventory((Player)e.getWhoClicked());
					// return;
					// }else{
					// e.getWhoClicked().sendMessage(ChatColor.RED.toString() +
					// "That Item Cannot Be Extracted!");
					// }
					//
					// }
					// }
					// }
					// //Bukkit.broadcastMessage("canc");
					// e.setCancelled(true);
					// ((Player)e.getWhoClicked()).updateInventory();
					// }

				}
			}
		}
	}

	@EventHandler
	public void onGrow(StructureGrowEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlace(BlockPlaceEvent e) {
		if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			e.setCancelled(true);
		}
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
						if (f != null) {
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
	public void onInteract(PlayerInteractEvent e) {
		if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.EXPERIENCE_BOTTLE
				&& e.getAction().toString().contains("RIGHT")) {
			Resource s = Resource.getByItem(e.getPlayer().getInventory().getItemInMainHand());
			if (s != null) {
				if (s == Resource.XP_BOTTLE_SMALL) {
					this.loadedFarms.get(e.getPlayer()).addExp(r.nextInt(10) + 10);
					int amt = e.getPlayer().getInventory().getItemInMainHand().getAmount();
					if (amt > 1) {
						e.getPlayer().getInventory().getItemInMainHand().setAmount(amt - 1);
					} else {
						e.getPlayer().getInventory().setItemInMainHand(null);
					}
				}
			}
			e.setCancelled(true);
			return;
		}
		if ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK)
				&& e.getHand().equals(EquipmentSlot.HAND)) {
			// Bukkit.broadcastMessage("Interact");
			Location pl = new Location(e.getClickedBlock().getWorld(), this.nearest500(e.getClickedBlock().getX() - 16),
					Constant.baseY, this.nearest500(e.getClickedBlock().getZ() - 16));
			if (this.lookup.containsKey(pl)) {
				Farm f = this.lookup.get(pl);
				if (f.getOwner() == e.getPlayer()) {
					Plot plot = f.getPlot(e.getClickedBlock().getChunk());
					if (plot != null) {
						plot.onInteract(e);
					}
				} else {
					e.getPlayer().sendMessage(ChatColor.RED + "This is not your farm!");
					e.setCancelled(true);
					return;
				}

			}
		} else if (e.getAction() == Action.PHYSICAL) {
			if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.FARMLAND) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e) {
		if (e.getPlayer().isOp() && e.getPlayer().isSneaking()) {
			ItemStack s = e.getPlayer().getInventory().getItemInMainHand();

			if (s != null && Resource.getByItem(s).equals(Resource.DEV_WAND)) {
				if (Constant.hasDebug(e.getPlayer())) {
					Constant.removeDebug(e.getPlayer());
					e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE.toString() + "Debug Text Disabled.");
				} else {
					Constant.addDebug(e.getPlayer());
					e.getPlayer().sendMessage(ChatColor.GREEN.toString() + "Debug Text Enabled.");
				}
			}
		}
		if (e.isSneaking()) {
			Location pl = new Location(e.getPlayer().getWorld(),
					this.nearest500(e.getPlayer().getLocation().getBlockX() - 16), Constant.baseY,
					this.nearest500(e.getPlayer().getLocation().getBlockZ() - 16));
			if (this.lookup.containsKey(pl)) {
				Farm f = this.lookup.get(pl);
				Plot plot = f.getPlot(e.getPlayer().getLocation().getChunk());
				if (plot != null && plot instanceof FarmPlot) {
					FarmPlot fp = (FarmPlot) plot;
					fp.onShift(e.getPlayer().getLocation());
				}

			}
		}
	}

	@EventHandler
	public void onBlockGrow(BlockGrowEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockFade(BlockFadeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (e.getPlayer() != null && !e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPhysics(BlockPhysicsEvent e) {
		// if(e.getChangedType() == Material.CROPS || e.getBlock().getType() ==
		// Material.CROPS
		// || e.getChangedType() == Material.WHEAT || e.getBlock().getType() ==
		// Material.WHEAT
		// ||e.getChangedType() == Material.LONG_GRASS
		// || e.getBlock().getType() == Material.LONG_GRASS
		// || e.getChangedType() == Material.RED_ROSE
		// || e.getBlock().getType() == Material.RED_ROSE
		// || e.getBlock().getType() == Material.TORCH
		// || e.getChangedType() == Material.TORCH){
		// e.setCancelled(true);
		// }
		e.setCancelled(true);
		// Bukkit.broadcastMessage(e.getBlock().getType().toString() + ":" +
		// e.getChangedType().toString());
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

	private void teleportTo(Player p, Farm f) {
		p.setVelocity(new Vector(0, 1, 0));
		p.teleport(f.getPlot(0).getChunk().getBlock(7, Constant.baseY + 3, 7).getLocation());
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

	private void newPlayer(Player p, World w, FarmReady fr) throws InterruptedException {
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

	private void deleteData(Player p) {
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

	private int nearest500(int val) {
		return (int) Math.ceil(val / 500D) * 500;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] arg3) {
		if (cmd.getName().equalsIgnoreCase("farm")) {
			if (sender instanceof Player) {
				Player pl = (Player) sender;
				if (this.loadedFarms.containsKey(pl)) {
					pl.openInventory(Farm.getMenu(this.loadedFarms.get(pl)));
					return true;
				} else {
					pl.sendMessage(ChatColor.RED
							+ "Sorry, your farm isn't loaded yet! (try relogging if you keep seeing this)");
					return true;
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("exp")) {
			if (sender.isOp()) {
				if (arg3.length == 2) {
					Player target = Constant.getPlayer(arg3[0]);
					int amt = Constant.getInt(arg3[1]);
					if (amt == 0 || target == null) {
						sender.sendMessage(ChatColor.RED.toString() + "Bad number or bad player!");
						return true;
					} else {

						if (amt > 0) {
							this.loadedFarms.get(target).addExp(amt);
							return true;
						} else {
							this.loadedFarms.get(target).subtractExp(Math.abs(amt));
							// sender.sendMessage(ChatColor.RED.toString() +
							// "Took " + amt );
							return true;
						}

					}

				} else {
					return false;
				}
			} else {
				sender.sendMessage(ChatColor.RED.toString() + "Nice try..");
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("purge")) {
			if (sender.isOp() && arg3.length == 1) {
				Player target = Constant.getPlayer(arg3[0]);
				if (target != null) {
					target.sendMessage(ChatColor.RED.toString() + "Your data has been purged..");
					target.getInventory().clear();
					Farm f = this.loadedFarms.get(target);
					target.teleport(new Location(target.getWorld(), 0, Constant.baseY + 5, 0));
					cleanFarm(f);
					this.deleteData(target);
					lookup.remove(f.getBaseLocation());
					loadedFarms.remove(target);
					try {
						this.newPlayer(target, target.getWorld(), new FarmReady() {

							@Override
							public void onFinish(Farm f) {
								teleportTo(target, f);
								target.setExp(f.getProgress() / 255F);
								target.setLevel(f.getLevel());
								target.sendMessage(ChatColor.GREEN + "Welcome to your Farm!");
							}
						});
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
}
