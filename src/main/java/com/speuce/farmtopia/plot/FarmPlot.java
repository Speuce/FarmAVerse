package main.java.com.speuce.farmtopia.plot;

import java.util.Arrays;

import main.java.com.speuce.farmtopia.crop.CropType;
import main.java.com.speuce.farmtopia.crop.FarmSubplot;
import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.farm.FarmManager;
import main.java.com.speuce.farmtopia.farm.Tutorial;
import main.java.com.speuce.farmtopia.resources.Resource;
import main.java.com.speuce.farmtopia.util.Constant;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class FarmPlot extends Plot {
	private final int height = Constant.baseY;
	private FarmSubplot[] subPlots;
	private Block[] farmSpots;
	private int[] stage;

	public FarmPlot(Farm f) {
		super("Farm Plot", "Farm", f);
		this.subPlots = new FarmSubplot[] { new FarmSubplot(CropType.BARREN, 0, (byte) 0),
				new FarmSubplot(CropType.BARREN, 0, (byte) 0), new FarmSubplot(CropType.BARREN, 0, (byte) 0),
				new FarmSubplot(CropType.BARREN, 0, (byte) 0) };
		this.stage = new int[4];
	}

	@Override
	public void setChunk(Chunk c) {
		super.setChunk(c);
		this.farmSpots = new Block[] { this.getFarmSpot1(), this.getFarmSpot2(), this.getFarmSpot3(),
				this.getFarmSpot4() };
	}

	private Block getFarmSpot1() {
		return this.getChunk().getBlock(0, height, 0);
	}

	private Block getFarmSpot2() {
		return this.getChunk().getBlock(0, height, 9);
	}

	private Block getFarmSpot3() {
		return this.getChunk().getBlock(9, height, 0);
	}

	private Block getFarmSpot4() {
		return this.getChunk().getBlock(9, height, 9);
	}

	public FarmSubplot getSubplot(int index) {
		return subPlots[index];
	}

	public void plantCrop(int index, CropType c) {
		this.subPlots[index].plantNewCrop(c);
	}

	public void setFarmSubPlot(int index, FarmSubplot set) {
		this.subPlots[index] = set;
	}

	public Block getFarmSpot(int index) {
		return this.farmSpots[index];
	}

	// public Crop[] getCrops(){
	// return this.crops;
	// }
	public void updateStages(boolean build) {
		if (this.farmSpots == null) {
			return;
		}
		for (int x = 0; x < 4; x++) {
			FarmSubplot c = subPlots[x];
			int stg = c.getStage();
			if (stage[x] != stg) {
				if (build) {
					rebuild(x);
				}
				stage[x] = stg;
			}
		}
	}

	public void rebuild(int x) {
		FarmSubplot c = subPlots[x];
		// this.getFarm().getFm().buildSchem("miniclear", farmSpots[x]);
		if (c.currentSchem().equalsIgnoreCase("barren1")) {
			this.getFarm().getFm().buildSchemOpt(c.currentSchem(), farmSpots[x]);
			// Bukkit.broadcastMessage("Built Optimized");
			return;
		}
		try {
			Farm f = this.getFarm();
			FarmManager fm = f.getFm();
			String schem = c.currentSchem();
			Block b = farmSpots[x];
			fm.buildSchem(schem, b);
		} catch (NullPointerException e) {
			Bukkit.broadcastMessage("ERROR");
			e.printStackTrace();
			System.out.println("CropType:" + c.getCurrentCrop().toString());
			System.out.println("CurrentSchem:" + c.currentSchem());
		}

	}

	public void onShift(Location l) {
		int fs = farmSpot(l.getBlock());
		if (fs >= 0) {
			FarmSubplot sb = this.subPlots[fs];
			if (sb.hasActualPlant() && !sb.max()) {
				sb.subtractSeconds(2);
			}
		}

	}

	@Override
	public void onInteractOwner(PlayerInteractEvent e) {
		int fs = farmSpot(e.getClickedBlock());
		if (fs >= 0) {
			if (!this.subPlots[fs].hasActualPlant()) {
				if (e.getPlayer().getInventory().getItemInMainHand() != null
						&& e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) {
					Resource c = Resource.getByTitle(
							e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName());

					if (c != null && c != Resource.NOTHING) {
						// Bukkit.broadcastMessage(c.getName());
						CropType crop = CropType.getBySeed(c);
						if (crop != null && crop != CropType.NULLIO && crop != CropType.BARREN) {
							if (crop == CropType.MAGIC) {
								if (this.subPlots[fs].getFertility() > 0) {
									e.getPlayer().sendMessage(
											ChatColor.RED.toString() + "This Spot is still infused with magic!");
									return;
								}
							}
							// Bukkit.broadcastMessage(crop.toString());
							if (c.getLevel() <= this.getFarm().getLevel()) {
								this.plantCrop(fs, crop);
								this.rebuild(fs);
								if (crop == CropType.WHEAT) {
									Tutorial.onSeedPlant(e.getPlayer());
								}
								int amt = e.getPlayer().getInventory().getItemInMainHand().getAmount();
								if (amt <= 0) {
									e.getPlayer().getInventory().setItemInMainHand(null);
								} else {
									e.getPlayer().getInventory().getItemInMainHand().setAmount(amt - 1);
								}
							} else {
								e.getPlayer().sendMessage(ChatColor.RED.toString() + "You must be at least level "
										+ c.getLevel() + " to plant this!");
							}
							return;
						}
					}
				}

			} else {
				FarmSubplot sb = this.subPlots[fs];
				if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
					if (sb.hasActualPlant()) {
						if (sb.max()) {
							if (sb.getCurrentCrop() == CropType.MAGIC) {
								if (sb.getFertility() < 8) {
									sb.setFertility((byte) 8);
								}
							}
							int mult = 1;
							if (sb.getCurrentCrop().getFertilityNeeded() > 0) {
								if (sb.getFertility() >= sb.getCurrentCrop().getFertilityNeeded()) {
									sb.setFertility(
											(byte) (sb.getFertility() - sb.getCurrentCrop().getFertilityNeeded()));
									mult = 2;
								} else {
									sb.setFertility((byte) 0);
								}
							}
							if (sb.getCurrentCrop() == CropType.WHEAT) {
								Tutorial.onWheatHarvest(e.getPlayer());
							}
							this.getFarm().harvest(sb.getCurrentCrop(), e.getPlayer(), mult);

							this.plantCrop(fs, CropType.BARREN);
							// TODO
							// c.onHarvest(e.getClickedBlock().getLocation());
							this.rebuild(fs);
							this.updateStages(false);
						} else {
							e.getPlayer()
									.sendMessage(ChatColor.RED.toString() + "This Crop is not ready to harvest yet!");
							return;
						}
					} else {
						e.getPlayer().sendMessage(ChatColor.RED + "This Land is barren, nothing to harvest.");
						return;
					}
				} else if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.GLOWSTONE_DUST) {
					ItemStack s = e.getPlayer().getInventory().getItemInMainHand();
					if (s.hasItemMeta() && s.getItemMeta().getDisplayName().equals(Resource.MAGIC_DUST.getName())) {
						if (!sb.max()) {
							boolean bl = sb.hasActualPlant();
							// Constant.debug(bl + ":::");
							if (bl) {
								// Constant.debug("still");
								int amt = s.getAmount() - 1;
								if (amt > 0) {
									e.getPlayer().getInventory()
											.setItemInMainHand(Resource.MAGIC_DUST.toItemStack(amt));
								} else {
									e.getPlayer().getInventory().setItemInMainHand(null);
								}
								sb.boost();
								Tutorial.onMagicDustUse(e.getPlayer());
								e.getPlayer().updateInventory();
								e.getPlayer().getWorld().playSound(e.getClickedBlock().getLocation(),
										Sound.ENTITY_PLAYER_LEVELUP, 2F, 0F);
								this.updateStages(true);
								return;
							} else {
								e.getPlayer()
										.sendMessage(ChatColor.RED.toString() + "There's nothing to apply magic to!");
								return;
							}

						} else {
							e.getPlayer().sendMessage(ChatColor.RED + "This Crop is already fully grown");
							return;
						}
					}
					return;
				} else if (e.getPlayer().isOp() && Resource.getByItem(e.getItem()).equals(Resource.DEV_WAND)) {
					if (!sb.max()) {
						boolean bl = sb.hasActualPlant();
						// Constant.debug(bl + ":::");
						if (bl) {
							sb.dev();
							Constant.playsound(e.getPlayer(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 0F);
							this.updateStages(true);
							return;
						} else {
							e.getPlayer().sendMessage(ChatColor.RED.toString() + "There's nothing to apply magic to!");
							return;
						}

					} else {
						e.getPlayer().sendMessage(ChatColor.RED + "This Crop is already fully grown");
						return;
					}

				}
				if (e.getPlayer().isOp() && e.getPlayer().isSneaking() && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					e.getPlayer().sendMessage(ChatColor.AQUA.toString() + "Fertility: " + ChatColor.YELLOW.toString()
							+ this.subPlots[fs].getFertility());
				}
			}
		}
	}

	private int farmSpot(Block b) {
		int x = b.getX() % 16;
		int z = b.getZ() % 16;
		int cx = this.aorb(x);
		int cz = this.aorb(z);
		if (cx >= 0 && cz >= 0) {
			if (cx == 0) {
				if (cz == 0) {
					return 0;
				} else {
					return 1;
				}
			} else {
				if (cz == 0) {
					return 2;
				} else {
					return 3;
				}
			}
		} else {
			return -1;
		}
	}

	@Override
	public byte[] serialize() {
		byte[] ret = new byte[FarmPlot.getBytes(Constant.currentProtocol)];
		int sp = 0;
		// int cb = FarmSubplot.getBytes(Constant.currentProtocol);
		for (int i = 0; i < 4; i++) {
			byte[] ins = this.subPlots[i].serialize();
			for (int y = 0; y < ins.length; y++) {
				ret[sp] = ins[y];
				sp++;
			}
		}
		return ret;
	}

	public static int getBytes(int version) {
		return (FarmSubplot.getBytes(version) * 4);
	}

	private int aorb(int val) {
		if (val != 7 && val != 8) {
			if (val >= 9) {
				return 9;
			} else {
				return 0;
			}
		} else {
			return -1;
		}

	}

	public static FarmPlot deserialize(byte[] data, int version, Farm fa) {
		if (data.length == FarmPlot.getBytes(version)) {
			FarmPlot f = new FarmPlot(fa);
			int crop = FarmSubplot.getBytes(version);
			int index = 0;
			int cro = 0;
			while (index < data.length) {
				byte[] dat = Arrays.copyOfRange(data, index, index + crop);
				index += crop;
				f.setFarmSubPlot(cro, FarmSubplot.deserialize(version, dat));
				cro++;
			}
			return f;
		} else {
			Bukkit.broadcastMessage(ChatColor.RED.toString()
					+ "ERROR: MISMATCHED BYTE ARRAY LENGTH AND NEEDED BYTE ARRAY LENGTH WHILE USING FARMPLOT.DESERIALZE. PLEASE CHECK OUT.");
			return null;
		}
	}

}
