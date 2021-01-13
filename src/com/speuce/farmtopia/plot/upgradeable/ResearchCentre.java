package com.speuce.farmtopia.plot.upgradeable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.google.common.primitives.Longs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;



import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.crop.CropType;
import com.speuce.farmtopia.crop.Family;
import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.farm.Tutorial;
import com.speuce.farmtopia.resources.Resource;
import com.speuce.farmtopia.util.RandomCollection;

public class ResearchCentre extends Upgradeable {
	private long extractStart = 0l, mutateStart = 0l;
	private Resource extracting = Resource.NOTHING, extractResult = Resource.NOTHING, mutateResult = Resource.NOTHING;
	private Family mutating = Family.NONE;
	// private ArmorStand extractorStand = null;
	private UUID extractorUUID = null, extractor2UUID = null, mutatorUUID = null, mutator2UUID = null;
	private Location extractorLoc = null;

	public ResearchCentre(Farm f, int lvl) {
		super("Seed Research Centre", new String[] { "rs1", "rs2" }, f, lvl);
	}

	public ResearchCentre(Farm f, int lvl, Long extractStart, Long mutateStart, Resource extracting, Family mutating,
			Resource extractResult, Resource mutateResult) {
		super("Seed Research Centre", new String[] { "rs1", "rs2" }, f, lvl);
		this.extractStart = extractStart;
		this.mutateStart = mutateStart;
		this.extracting = extracting;
		this.mutating = mutating;
		this.extractResult = extractResult;
	}

	@Override
	public void update() {

		if (this.extractorUUID != null) {
			if (this.getFarm().getOwner().getLocation().distanceSquared(this.extractorLoc) < 22500) {
				this.updateStand();
				// Bukkit.broadcastMessage("player IN distance");
			} else {
				// Bukkit.broadcastMessage("player out of distance");
			}

		} else {
			// Bukkit.broadcastMessage("stand is null"); //NEVER HAPPENS
		}
	}

	private void updateStand() {
		ArmorStand as = (ArmorStand) Bukkit.getEntity(this.extractorUUID);
		ArmorStand as2 = (ArmorStand) Bukkit.getEntity(this.extractor2UUID);
		// as.setCustomName(Math.random() + "");
		// Bukkit.broadcastMessage("update");
		if (this.extracting != null && this.extracting != Resource.NOTHING) {
			if (this.getExtractLeft() > 0) {
				// as.set
				as.setCustomName(ChatColor.GOLD.toString() + "Extracting");
				as2.setCustomName(this.extracting.getName() + ChatColor.RED.toString() + " - "
						+ Constant.milliSecondsToDisplay(this.getExtractLeft()));
				// Bukkit.broadcastMessage("set extractING");
			} else {
				as.setCustomName(ChatColor.GOLD.toString() + "Extracted");
				as2.setCustomName(this.extracting.getName());
				// Bukkit.broadcastMessage("set exctractED");
			}

		} else {
			// Bukkit.broadcastMessage("set exctractor");
			as.setCustomName(ChatColor.GOLD.toString() + "Extractor");
			as2.setCustomName(ChatColor.RESET.toString());
		}
		if (this.mutatorUUID != null) {
			ArmorStand as3 = (ArmorStand) Bukkit.getEntity(this.mutatorUUID);
			ArmorStand as4 = (ArmorStand) Bukkit.getEntity(this.mutator2UUID);
			// as.setCustomName(Math.random() + "");
			// Bukkit.broadcastMessage("update");
			if (this.mutating != null && this.mutating != Family.NONE) {
				if (this.getMutateLeft() > 0) {
					// as.set
					as3.setCustomName(ChatColor.GOLD.toString() + "Mutating");
					as4.setCustomName(this.mutating.getDisplay().getName() + ChatColor.RED.toString() + " - "
							+ Constant.milliSecondsToDisplay(this.getMutateLeft()));
					// Bukkit.broadcastMessage("set extractING");
				} else {
					as3.setCustomName(ChatColor.GOLD.toString() + "Mutated");
					as4.setCustomName(this.mutating.getDisplay().getName());
					// Bukkit.broadcastMessage("set exctractED");
				}

			} else {
				// Bukkit.broadcastMessage("set exctractor");
				as3.setCustomName(ChatColor.GOLD.toString() + "Mutator");
				as4.setCustomName(ChatColor.RESET.toString());
			}
		}

	}

	@Override
	public void setup() {
		// System.out.println("-----------Setup--------");
		boolean t = false;
		for (int x = 0; x < 15; x++) {
			for (int z = 0; z < 15; z++) {
				for (int y = Constant.baseY; y < Constant.baseY + 5; y++) {
					Block b = this.getChunk().getBlock(x, y, z);
					if (this.getChunk().getBlock(x, y, z).getType() == Material.DISPENSER) {
						// System.out.println("--------Spot--------");
						// TODO spawn armourstand
						ArmorStand s = (ArmorStand) b.getWorld().spawnEntity(b.getLocation().add(0.5, -0.25, 0.5),
								EntityType.ARMOR_STAND);
						Constant.setupStand(s);
						this.extractorLoc = s.getLocation();
						this.extractorUUID = s.getUniqueId();

						ArmorStand s2 = (ArmorStand) b.getWorld().spawnEntity(b.getLocation().add(0.5, -0.5, 0.5),
								EntityType.ARMOR_STAND);
						Constant.setupStand(s2);
						// this.extractorLoc = s.getLocation();
						this.extractor2UUID = s2.getUniqueId();
						// this.updateStand();
						if (t || this.getLv() == 0) {
							break;
						}
						t = true;
					} else if (this.getChunk().getBlock(x, y, z).getType() == Material.OBSERVER) {
						// System.out.println("--------Spot--------");
						// TODO spawn armourstand
						ArmorStand s = (ArmorStand) b.getWorld().spawnEntity(b.getLocation().add(0.5, -0.25, 0.5),
								EntityType.ARMOR_STAND);
						Constant.setupStand(s);
						this.mutatorUUID = s.getUniqueId();

						ArmorStand s2 = (ArmorStand) b.getWorld().spawnEntity(b.getLocation().add(0.5, -0.5, 0.5),
								EntityType.ARMOR_STAND);
						Constant.setupStand(s2);
						// this.extractorLoc = s.getLocation();
						this.mutator2UUID = s2.getUniqueId();
						// this.updateStand();
						if (t) {
							break;
						}
						t = true;
					}

				}
			}
		}
		this.updateStand();
	}

	@Override
	public byte[] serialize() {
		byte[] ret = new byte[21];
		ret[0] = (byte) this.getLv();
		ret[1] = (byte) this.extracting.ordinal();
		Constant.insertIn(ret, Longs.toByteArray(extractStart), 2);
		ret[10] = (byte) this.mutating.ordinal();
		Constant.insertIn(ret, Longs.toByteArray(this.mutateStart), 11);
		ret[19] = (byte) this.extractResult.ordinal();
		ret[20] = (byte) this.mutateResult.ordinal();
		return ret;
	}

	public void takeExtractProduct() {
		// Bukkit.broadcastMessage("result is no more.");
		this.extractResult = Resource.NOTHING;
	}

	@Override
	public int getCost(int currentlv) {
		return 100;
	}

	public static int getBytes(int protocol) {
		if (protocol == Constant.PROTOCOL_V3) {
			return 21;
		} else {
			return 21;
		}
	}

	public long getExtractLeft() {
		long diff = System.currentTimeMillis() - this.extractStart;
		long l = Constant.getFamilyOf(this.extracting).getTime();
		// Bukkit.broadcastMessage("diff: " + diff + "-- l:" + l);
		if (diff > l) {
			return 0;
		} else {
			return l - diff;
		}
	}

	public long getMutateLeft() {
		long diff = System.currentTimeMillis() - this.mutateStart;
		long l = this.mutating.getTime() * 5;
		// Bukkit.broadcastMessage("diff: " + diff + "-- l:" + l);
		if (diff > l) {
			return 0;
		} else {
			return l - diff;
		}
	}

	@Override
	public void onUpgrade() {
		BukkitRunnable br = new BukkitRunnable() {

			@Override
			public void run() {
				for (int x = 0; x < 15; x++) {
					for (int z = 0; z < 15; z++) {
						for (int y = Constant.baseY + 1; y < Constant.baseY + 3; y++) {
							Block b = getChunk().getBlock(x, y, z);
							if (getChunk().getBlock(x, y, z).getType() == Material.OBSERVER) {
								// System.out.println("--------Spot--------");
								// TODO spawn armourstand
								ArmorStand s = (ArmorStand) b.getWorld()
										.spawnEntity(b.getLocation().add(0.5, -0.25, 0.5), EntityType.ARMOR_STAND);
								Constant.setupStand(s);
								mutatorUUID = s.getUniqueId();

								ArmorStand s2 = (ArmorStand) b.getWorld()
										.spawnEntity(b.getLocation().add(0.5, -0.5, 0.5), EntityType.ARMOR_STAND);
								Constant.setupStand(s2);
								// this.extractorLoc = s.getLocation();
								mutator2UUID = s2.getUniqueId();
								// this.updateStand();
								break;
							}

						}
					}
				}
				updateStand();
			}
		};
		br.runTaskLater(this.getFarm().getFm().getPlugin(), 20L);

	}

	@Override
	public void cleanup() {
		// if(this.extractorStand != null){
		// this.extractorStand.remove();
		// this.extractorStand = null;
		// }
		if (this.extractorUUID != null) {
			if (this.extractorLoc != null) {
				if (!this.extractorLoc.getChunk().isLoaded()) {
					this.extractorLoc.getChunk().load();
				}
			}
			ArmorStand as = (ArmorStand) Bukkit.getEntity(this.extractorUUID);
			as.remove();
			this.extractorUUID = null;
			ArmorStand as2 = (ArmorStand) Bukkit.getEntity(this.extractor2UUID);
			as2.remove();
			this.extractor2UUID = null;
			if (this.mutatorUUID != null) {
				ArmorStand as3 = (ArmorStand) Bukkit.getEntity(this.mutatorUUID);
				as3.remove();
				this.mutatorUUID = null;
				ArmorStand as4 = (ArmorStand) Bukkit.getEntity(this.mutator2UUID);
				as4.remove();
				this.mutator2UUID = null;
			}

		}
	}

	public static ResearchCentre deserialize(byte[] dat, int protocol, Farm f) {
		if (protocol == Constant.PROTOCOL_V3) {
			if (dat.length != 21) {
				throw new IllegalArgumentException("Dat lenght must be 20!");
			}
			Resource extracting = Resource.values()[dat[1]];
			Long extractingStart = Longs.fromByteArray(Arrays.copyOfRange(dat, 2, 10));
			Family mutating = Family.values()[dat[10]];
			Long mutatingStart = Longs.fromByteArray(Arrays.copyOfRange(dat, 11, 19));
			Resource ex = Resource.values()[dat[19]];
			Resource mu = Resource.values()[dat[20]];

			return new ResearchCentre(f, (int) dat[0], extractingStart, mutatingStart, extracting, mutating, ex, mu);
		} else {
			throw new IllegalArgumentException("Protocol version: " + protocol + " not supported!");
		}
	}

	public void onInteractOwner(PlayerInteractEvent e) {

		if (e.getClickedBlock().getType() == Material.DISPENSER) {
			e.setCancelled(true);
			// this.sendSeedInventory(e.getPlayer());
			if (this.extracting != null && this.extracting != Resource.NOTHING) {
				if (this.getExtractLeft() > 0) {
					if(e.getPlayer().isOp() && Resource.getByItem(e.getItem()).equals(Resource.DEV_WAND)){
						this.extractStart = 100L;
						Constant.playsound(e.getPlayer(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 0F);
						this.update();
						return;
					}else{
						e.getPlayer().sendMessage(ChatColor.RED.toString() + "That item is still being extracted!");
						return;
					}

				} else {
					// Bukkit.broadcastMessage("ready");
					// Bukkit.broadcastMessage(this.extracting.toString());
					if (Constant.isEssence(this.extracting)) {
						// Bukkit.broadcastMessage("essence");
						RandomCollection<Resource> coll = new RandomCollection<Resource>();
						Family f = Constant.getFamilyOf(this.extracting);
						List<CropType> ss = CropType.getAllInFamily(f);
						// if(ss.isEmpty()){
						// // Bukkit.broadcastMessage("empty for: " +
						// f.toString());
						// }
						for (CropType ty : ss) {
							// Bukkit.broadcastMessage("for: " + ty.toString() +
							// " lvl: " + this.getFarm().getLevel() + " minL: "
							// + ty.getMinLvl());
							if (ty.getMinLvl() <= this.getFarm().getLevel()) {
								coll.add(ty.getWeight(), ty.getSeed());
							}

						}
						if (!coll.isEmpty()) {
							this.extractResult = coll.next(new Random());
						} else {
							Constant.forceGive(e.getPlayer(), this.extracting.toItemStack(1));
							this.extracting = Resource.NOTHING;
							this.extractResult = Resource.NOTHING;
							e.getPlayer()
									.sendMessage(Constant.trans("&cSorry, "
											+ "an error occured, you shouldn't have been able to extract that in the"
											+ " first place. Item Returned."));
							this.update();
							return;
						}

					} else {
						// Bukkit.broadcastMessage("seed");
						CropType ty = CropType.getBySeed(this.extracting);
						this.extractResult = ty.getFamily().getDisplay();
					}
					Tutorial.onExtractorFinish(e.getPlayer(), this.extracting);
					this.extracting = Resource.NOTHING;
					Constant.forceGive(e.getPlayer(), this.extractResult.toItemStack(1));
					Constant.playsound(e.getPlayer(), Sound.ENTITY_ITEM_PICKUP, 0F);
					this.extractResult = Resource.NOTHING;

					this.update();
				}
			} else {
				if (e.getPlayer().getInventory().getItemInMainHand() != null) {
					Resource r = Resource.getByItem(e.getPlayer().getInventory().getItemInMainHand());
					if (r != null && r != Resource.NOTHING) {
						if (Constant.canExtract(r)) {
							if (r.getLevel() > this.getFarm().getLevel()) {
								e.getPlayer().sendMessage(Constant
										.trans("&cSorry, you must be level " + r.getLevel() + " to extract that."));
								return;
							}
							e.getPlayer().sendMessage(ChatColor.GREEN.toString() + "Extraction Started.");
							if (e.getPlayer().getInventory().getItemInMainHand().getAmount() == 1) {
								e.getPlayer().getInventory().setItemInMainHand(null);
							} else {
								ItemStack s = e.getPlayer().getInventory().getItemInMainHand();
								s.setAmount(s.getAmount() - 1);
								e.getPlayer().getInventory().setItemInMainHand(s);
							}
							startExtract(r);
							Constant.playsound(e.getPlayer(), Sound.ENTITY_SPIDER_DEATH, 0F);
							Tutorial.onExtractorStart(e.getPlayer(), r);
							// ((Player)e.getWhoClicked()).updateInventory();
							// rs.sendSeedInventory((Player)e.getWhoClicked());
							return;
						} else {
							e.getPlayer().sendMessage(ChatColor.RED.toString() + "That Item Cannot Be Extracted!");
						}

					} else {
						e.getPlayer().sendMessage(ChatColor.RED.toString()
								+ "Right click the extractor with some seeds or some essence in your hand.");
						return;
					}
				} else {
					e.getPlayer().sendMessage(ChatColor.RED.toString() + "You have nothing in your hand!");
				}
			}
		} else if (e.getClickedBlock().getType() == Material.ENCHANTING_TABLE) {
			e.setCancelled(true);
			this.openUpgradeInventory(e.getPlayer());
		} else if (e.getClickedBlock().getType() == Material.OBSERVER && this.getLv() > 0) {
			e.setCancelled(true);
			if (this.mutating != null && this.mutating != Family.NONE) {
				if (this.getMutateLeft() <= 0) {
					Resource retrieve = this.mutating.mutate();
					Family newFamily = Family.getByResource(retrieve);
					if (newFamily == null || newFamily == Family.NONE) {
						Constant.forceGive(e.getPlayer(), this.mutating.getDisplay().toItemStack(1));
						e.getPlayer()
								.sendMessage(ChatColor.RED.toString() + "An internal error occured. "
										+ "There were no families defined for family: " + this.mutating.toString()
										+ ". Error code:" + "5478");
						return;
					}
					this.mutating = Family.NONE;
					Constant.forceGive(e.getPlayer(), retrieve.toItemStack(1));
					Constant.playsound(e.getPlayer(), Sound.ENTITY_ITEM_PICKUP, 0F);
					this.update();
					return;

				} else {
					if(e.getPlayer().isOp() && Resource.getByItem(e.getItem()).equals(Resource.DEV_WAND)){
						this.mutateStart = 100L;
						Constant.playsound(e.getPlayer(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 0F);
						this.update();
						return;
					}else{
						e.getPlayer().sendMessage(ChatColor.RED.toString() + "That item is still being mutated!");
						return;
					}

				}
			} else {
				Resource r = Resource.getByItem(e.getPlayer().getInventory().getItemInMainHand());
				if (r != null) {
					Family f = Family.getByResource(r);
					if (f != null && f != Family.NONE) {
						e.getPlayer().sendMessage(ChatColor.GREEN.toString() + "Mutation Started.");
						if (e.getPlayer().getInventory().getItemInMainHand().getAmount() == 1) {
							e.getPlayer().getInventory().setItemInMainHand(null);
						} else {
							ItemStack s = e.getPlayer().getInventory().getItemInMainHand();
							s.setAmount(s.getAmount() - 1);
							e.getPlayer().getInventory().setItemInMainHand(s);
						}
						Constant.playsound(e.getPlayer(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2F);
						startMutate(f);
						return;
					} else {
						e.getPlayer().sendMessage(ChatColor.RED.toString() + "Only essence can be mutated!");
						return;
					}
				} else {
					e.getPlayer().sendMessage(
							ChatColor.RED.toString() + "Right click the mutator with some essence in your hand.");
					return;
				}
			}
		}
		e.setCancelled(true);

	}

	public void startExtract(Resource f) {
		this.extracting = f;
		this.extractStart = System.currentTimeMillis();
		this.update();
	}

	public void startMutate(Family f) {
		this.mutating = f;
		this.mutateStart = System.currentTimeMillis();
		this.update();
	}

	public Long getExtractStart() {
		return extractStart;
	}

	public Long getMutateStart() {
		return mutateStart;
	}

	public Resource getExtracting() {
		return extracting;
	}

	public Family getMutating() {
		return mutating;
	}

	@Override
	public int getLevelReqToUpgrade(int next) {
		if (next == 0) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int getThLvToUpgrade(int next) {
		if (next == 1) {
			return 1;
		} else {
			return 0;
		}
	}
	// public void sendSeedInventory(Player p){
	// Inventory i = Bukkit.createInventory(null, 9,
	// Constant.seedExtractorName);
	// i.setItem(0, Constant.getItem(Material.BOW, (byte)8, ""));
	// if(this.extracting != null && this.extracting != Resource.NOTHING){
	// if(this.getExtractLeft() > 0){
	// ItemStack light = Constant.getItem(Material.GOLD_HOE, (byte)1, "");
	// ItemStack dark = Constant.getItem(Material.GOLD_HOE, (byte)2, "");
	// i.setItem(1, this.extracting.toItemStack(1));
	// long l = this.getExtractLeft();
	// long tot = Constant.getFamilyOf(this.extracting).getTime();
	// long sub = tot/6L;
	// l = tot-l;
	// for(int x = 2; x < 7; x++){
	// if(l >= sub){
	// l-= sub;
	// i.setItem(x, light);
	// }else{
	// i.setItem(x, dark);
	// }
	// }
	// }else{
	// //Bukkit.broadcastMessage("ready");
	// //Bukkit.broadcastMessage(this.extracting.toString());
	// if(Constant.isEssence(this.extracting)){
	// //Bukkit.broadcastMessage("essence");
	// RandomCollection<Resource> coll = new RandomCollection<Resource>();
	// Family f = Constant.getFamilyOf(this.extracting);
	// List<CropType> ss = CropType.getAllInFamily(f);
	// if(ss.isEmpty()){
	// // Bukkit.broadcastMessage("empty for: " + f.toString());
	// }
	// for(CropType ty: ss){
	// coll.add(ty.getWeight(), ty.getSeed());
	// }
	// this.extractResult = coll.next(new Random());
	// }else{
	// // Bukkit.broadcastMessage("seed");
	// CropType ty = CropType.getBySeed(this.extracting);
	// this.extractResult = ty.getFamily().getDisplay();
	// }
	// this.extracting = Resource.NOTHING;
	//
	// }
	// }
	// if(this.extractResult != null && this.extractResult != Resource.NOTHING){
	// i.setItem(7, this.extractResult.toItemStack(1));
	// }else{
	// i.setItem(7, new ItemStack(Material.AIR));
	// }
	// p.openInventory(i);
	//
	// }

}
