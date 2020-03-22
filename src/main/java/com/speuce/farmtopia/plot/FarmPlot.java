package main.java.com.speuce.farmtopia.plot;

import java.util.Arrays;

import com.sun.org.apache.bcel.internal.Const;
import main.java.com.speuce.farmtopia.crop.CropType;
import main.java.com.speuce.farmtopia.crop.FarmSubplot;
import main.java.com.speuce.farmtopia.event.Events;
import main.java.com.speuce.farmtopia.event.farm.crop.FarmFertilityEvent;
import main.java.com.speuce.farmtopia.event.farm.crop.FarmHarvestEvent;
import main.java.com.speuce.farmtopia.event.farm.crop.FarmMagicEvent;
import main.java.com.speuce.farmtopia.event.farm.crop.FarmPlantEvent;
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
import org.bukkit.entity.Player;
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
			updateStage(x, build);
		}
	}

	/**
	 * Updates the growth stage of the given subplot
	 * @param x the # of the subplot to update
	 * @param build a flag indicating whether or not the subplot
	 *              should be rebuild (if its ready for it)
	 */
	public void updateStage(int x, boolean build){
		FarmSubplot c = subPlots[x];
		int stg = c.getStage();
		if (stage[x] != stg) {
			if (build) {
				rebuild(x);
			}
			stage[x] = stg;
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

	/**
	 * Executes sequence for when a subplot is harvested
	 * @param sb the associated {@link FarmSubplot}
	 * @param p the Player harvesting
	 * @param fs the # of the subplot
	 */
	private void harvest(FarmSubplot sb, Player p, int fs){
		if(!sb.max()){
			p.sendMessage(ChatColor.RED.toString() + "This Crop is not ready to harvest yet!");
			return;
		}
		CropType crop = sb.getCurrentCrop();
		int mult = 1;
		//handle multiplied harvest BEFORE calling harvest event.
		if(sb.getCurrentCrop().getFertilityNeeded() > 0 &&
				sb.getFertility() >= sb.getCurrentCrop().getFertilityNeeded()){
			mult = 2;
		}
		//handle harvest event
		if(Events.isCancelled(new FarmHarvestEvent(getFarm(), this, sb, sb.getCurrentCrop(), mult))){
			return;
		}
		//handle harvesting of magic crop
		//TODO move to event handler
		if (sb.getCurrentCrop() == CropType.MAGIC) {
			if (sb.getFertility() < 8) {
				sb.setFertility((byte) 8);
			}
		}
		//handle depletion of fertility
		int change = 0;
		if(mult > 1){
			change = - sb.getCurrentCrop().getFertilityNeeded();
		}else if(sb.getCurrentCrop().getFertilityNeeded() > 0){
			change = -sb.getFertility();
		}
		if(change != 0){
			FarmFertilityEvent ev = new FarmFertilityEvent(getFarm(), this,sb, crop, change);
			if(!Events.isCancelled(ev)){
				sb.setFertility((byte) (sb.getFertility() + ev.getFertilityChange()));
			}
		}
		//pass it to the farm and subplot
		this.getFarm().harvest(sb.getCurrentCrop(), p, mult);
		sb.plantNewCrop(CropType.BARREN);
		// TODO
		// c.onHarvest(e.getClickedBlock().getLocation());
		this.rebuild(fs);
		this.updateStage(fs,false);
	}

	/**
	 * Handles sequence of events when a crop is planted on a subplot
	 * @param crop the {@link CropType} planted
	 * @param fs the spot # of the farm subplot planted on
	 * @param p the Player planting.
	 */
	private void plant(CropType crop, int fs, Player p){
		FarmSubplot sub = subPlots[fs];
		if (crop == CropType.MAGIC) {
			if (sub.getFertility() > 0) {
				p.sendMessage(ChatColor.RED.toString() + "This Spot is still infused with magic!");
				return;
			}
		}
		if (crop.getSeed().getLevel() <= this.getFarm().getLevel()) {
			if(Events.isCancelled(new FarmPlantEvent(this.getFarm(), this,subPlots[fs], sub.getCurrentCrop(), crop))){
				return;
			}
			sub.plantNewCrop(crop);
			this.rebuild(fs);
			if (crop == CropType.WHEAT) {
				Tutorial.onSeedPlant(p);
			}
			Constant.takeOne(p);
		} else {
			p.sendMessage(ChatColor.RED.toString() + "You must be at least level "
					+ crop.getSeed().getLevel() + " to plant this!");
		}

	}

	private void magicDust(FarmSubplot sb, ItemStack i, Player p, int fs){
		if (!sb.max()) {
			if (sb.hasActualPlant()) {
				//execute Magic event
				if(Events.isCancelled(new FarmMagicEvent(getFarm(), this, sb, sb.getCurrentCrop()))){
					return;
				}
				Constant.takeOne(p);
				sb.boost();
				p.getWorld().playSound(p.getLocation(),
						Sound.ENTITY_PLAYER_LEVELUP, 2F, 0F);
				this.updateStage(fs,true);
			} else {
				p.sendMessage(ChatColor.RED.toString() + "There's nothing to apply magic to!");
			}

		} else {
			p.sendMessage(ChatColor.RED + "This Crop is already fully grown");
		}
	}

	@Override
	public void onInteractOwner(PlayerInteractEvent e) {
		if(e.getClickedBlock() == null){
			return;
		}
		int fs = farmSpot(e.getClickedBlock());
		ItemStack i = e.getPlayer().getInventory().getItemInMainHand();
		if (fs >= 0) {
			FarmSubplot sb = this.subPlots[fs];
			if (!sb.hasActualPlant() && i.hasItemMeta()) {
					Resource c = Resource.getByTitle(i.getItemMeta().getDisplayName());
					if (c != null && c != Resource.NOTHING) {
						CropType crop = CropType.getBySeed(c);
						if (CropType.verifyCrop(crop)) {
							plant(crop, fs, e.getPlayer());
						}
					}
			} else {
				if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
					harvest(sb, e.getPlayer(), fs);
				} else if (i.getType() == Material.GLOWSTONE_DUST) {
					if (i.hasItemMeta() && i.getItemMeta().getDisplayName().equals(Resource.MAGIC_DUST.getName())) {
						magicDust(sb, i, e.getPlayer(), fs);
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

	/**
	 * Calculates which of the 4 sub plots block b is in.
	 * @param b the block to check
	 */
	private int farmSpot(Block b){
		//relative x/z coordinate in chunk
		int relx = b.getX() - (getChunk().getX()*16);
		int relz = b.getZ() - (getChunk().getZ()*16);
		//Preconditions: b is within the chunk
		assert(relx < 16);
		assert(relz < 16);
		if(relx < 7){
			if(relz < 7){
				return 0;
			}else if(relz > 8){
				return 1;
			}
		}else if(relx > 8){
			if(relz < 7){
				return 2;
			}else if(relz > 8){
				return 3;
			}
		}
		return -1;
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
