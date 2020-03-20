package main.java.com.speuce.farmtopia.farm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;

import main.java.com.speuce.farmtopia.crop.CropType;
import main.java.com.speuce.farmtopia.farm.plotcollections.ModifiablePlotCollection;
import main.java.com.speuce.farmtopia.plot.*;
import main.java.com.speuce.farmtopia.plot.upgradeable.*;
import main.java.com.speuce.farmtopia.util.Animations;
import main.java.com.speuce.farmtopia.util.Constant;
import main.java.com.speuce.schemetic.Schematic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class Farm extends ModifiablePlotCollection {

	private FarmManager fm;
	private TownHall hall;
	private Chunk currentSelected = null;
	public Farm(Location base, Player p, FarmManager fm, int lvl, byte progress){
		super(base, 1, p, lvl, progress);
		this.fm = fm;
		base.getWorld().getPopulators().clear();
	}
	public void buildAllWalls(){
		System.out.println("building walls..");
		for(Plot pl: getPlots()){
			buildWalls(pl, false);
		}
	}

	public TownHall getTownHall(){
		return this.hall;
	}
	public int getTownHallLevel(){
		if(this.getTownHall() != null){
			return this.getTownHall().getLv();
		}else{
			return -1;
		}
	}

	@Override
	public void addPlot(Plot p){
		if(p instanceof TownHall){
			this.hall = (TownHall) p;
		}
		super.addPlot(p);
	}

	@Override
	public void setPlot(int x, Plot f){
		if(f instanceof TownHall){
			this.hall = (TownHall) f;
		}
		super.setPlot(x, f);
	}


	@Override
	public void setBuildPlot(Chunk c,Plot f){
		if(f instanceof TownHall){
			this.hall = (TownHall) f;
			Tutorial.onTownHallBuild(this.getOwner());
		}
		super.setBuildPlot(c, f);
	}
	public FarmManager getFm(){
		return this.fm;
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
	public void buildWalls(Plot pl, boolean all){
		Plot[] nearby = this.getNearbyPlots(pl);
		Schematic sc = this.getFm().getPlugin().getSchem().getSchemetic("cliff1");
		Schematic edge = this.getFm().getPlugin().getSchem().getSchemetic("cliffedge1");
		Schematic edge2 = this.getFm().getPlugin().getSchem().getSchemetic("cliffedge3");
		Schematic wrap1 = this.getFm().getPlugin().getSchem().getSchemetic("cliffwrap1");
		Schematic wrap2 = this.getFm().getPlugin().getSchem().getSchemetic("cliffwrap2");
		//pl.getBaseLocation().getWorld().setBiome(arg0, arg1, arg2);
		if(nearby[6] == null){
			if(nearby[0] == null){
				//assume noone in NW
				BuildQueue.queue(edge.def(pl.getBaseLocation().getRelative(-7, 0, -7),
						this.getFm().getPlugin(), 0));
				if(nearby[5] == null){
					BuildQueue.queue(sc.def(pl.getBaseLocation().getRelative(-7, 0, 0), this.getFm().getPlugin(), 0));
				}
			}else{
				if(nearby[7] == null){
					BuildQueue.queue(sc.def(pl.getBaseLocation().getRelative(-7, 0, 0), this.getFm().getPlugin(), 0));
				}else{
					BuildQueue.queue(wrap1.def(pl.getBaseLocation().getRelative(-16, 0, 0), 
							this.getFm().getPlugin(), 0));
					//WRAPPED EDGE
				}
			}
		}else{
			if(nearby[0] == null && nearby[7] != null){
				BuildQueue.queue(wrap1.def(pl.getBaseLocation().getRelative(0, 0, -16), 
						this.getFm().getPlugin(), 2));
				//WRAPPED EDGE 2
			}
		}

		if(nearby[0] == null){
			//System.out.println("wall2");
			if(nearby[2] == null){
				BuildQueue.queue(edge2.def(pl.getBaseLocation().getRelative(16, 0, -7),
						this.getFm().getPlugin(), 2));
				if(nearby[7] == null){
					BuildQueue.queue(sc.def(pl.getBaseLocation().getRelative(0, 0, -7), this.getFm().getPlugin(), 1));
				}
			}else{
				if(nearby[1] == null){
					BuildQueue.queue(sc.def(pl.getBaseLocation().getRelative(0, 0, -7), this.getFm().getPlugin(), 1));
				}else{
					BuildQueue.queue(wrap2.def(pl.getBaseLocation().getRelative(0, 0, -16), this.getFm().getPlugin(), 0));
					//WRAPPED EDGE
				}
			}
		}else{
			if(nearby[2] == null && nearby[1] != null){
				//WRAPPED EDGE 2
				BuildQueue.queue(wrap2.def(pl.getBaseLocation().getRelative(16, 0, 0), this.getFm().getPlugin(), 2));
			}
		}

		if(nearby[2] == null){
			//System.out.println("wall3: " + pl.getName());
			//BuildQueue.queue(sc.def(pl.getBaseLocation().getRelative(16, 0, 0), this.getFm().getPlugin(), 2));
			if(nearby[4] == null){
				//System.out.println("nearby 4 is null");
				BuildQueue.queue(edge.def(pl.getBaseLocation().getRelative(16, 0, 16),
						this.getFm().getPlugin(), 2));
				if(nearby[1] == null){
				//	System.out.println("nearby 1 is null");
					BuildQueue.queue(sc.def(pl.getBaseLocation().getRelative(16, 0, 0), this.getFm().getPlugin(), 2));
				}
			}else{
				//System.out.println("nearby 4 isn't null");
				if(nearby[3] == null){
					//System.out.println("nearby 3 is null");
//					BuildQueue.queue(edge.def(pl.getBaseLocation().getRelative(16, 0, 16),
//							this.getFm().getPlugin(), 2));
					BuildQueue.queue(sc.def(pl.getBaseLocation().getRelative(16, 0, 0), this.getFm().getPlugin(), 2));
				}else if(all){
					BuildQueue.queue(wrap1.def(pl.getBaseLocation().getRelative(16, 0, 0), 
							this.getFm().getPlugin(), 2));
				}
			}
//			if(nearby[3] == null && nearby[4] == null){
//				//System.out.println("wall1");
//				BuildQueue.queue(edge.def(pl.getBaseLocation().getRelative(16, 0, 16),
//						this.getFm().getPlugin(), 2));
//			}
		}else if(all){
			if(nearby[4]==null && nearby[3] != null){
				BuildQueue.queue(wrap1.def(pl.getBaseLocation().getRelative(0, 0, 16), 
						this.getFm().getPlugin(), 0));
			}
		}

		if(nearby[4] == null){
			//System.out.println("wall4");
			if(nearby[6] == null){
				BuildQueue.queue(edge2.def(pl.getBaseLocation().getRelative(-7, 0, 16),
						this.getFm().getPlugin(), 0));
				if(nearby[3]==null){
					BuildQueue.queue(sc.def(pl.getBaseLocation().getRelative(0, 0, 16), this.getFm().getPlugin(), 3));
				}
			
			}else{
				if(nearby[5] == null){
					BuildQueue.queue(sc.def(pl.getBaseLocation().getRelative(0, 0, 16), this.getFm().getPlugin(), 3));
				}else if(all){
					BuildQueue.queue(wrap1.def(pl.getBaseLocation().getRelative(0, 0, 1), 
							this.getFm().getPlugin(), 0));
					//WRAPPED EDGE DONOT
				}
			}
//			if(nearby[5] == null && nearby[6] == null){
//				//System.out.println("wall1");
//				BuildQueue.queue(edge2.def(pl.getBaseLocation().getRelative(-7, 0, 16),
//						this.getFm().getPlugin(), 0));
//			}
		}else if(all){
			if(nearby[6] == null && nearby[5] != null){
				BuildQueue.queue(wrap2.def(pl.getBaseLocation().getRelative(-16, 0, 0), 
						this.getFm().getPlugin(), 0));
			}
		}

	}
	public static Farm deserialize(byte[] data, Location base, Player p, FarmManager fm){
		int protocol = data[0];
		if(protocol == 1){
			int index = 1;
			Farm ret = new Farm(base, p, fm, 0, (byte)0);
			while(index < data.length){
				byte id = data[index];
				index++;
				int neededBytes = Plots.getBytes(id, protocol);
				Plot pl = Plots.deserialize(id, Arrays.copyOfRange(data, index, index+neededBytes), protocol, ret);
				index += neededBytes;
				ret.addPlot(pl);
			}
			return ret;
		}else if(protocol == 2 || protocol == Constant.PROTOCOL_V3){
			int index = 3;
			Farm ret = new Farm(base, p, fm, data[1], data[2]);
			while(index < data.length){
				byte id = data[index];
				index++;
				int neededBytes = Plots.getBytes(id, protocol);
				Plot pl = Plots.deserialize(id, Arrays.copyOfRange(data, index, index+neededBytes), protocol, ret);
				index += neededBytes;
				ret.addPlot(pl);
			}
			return ret;
		}else{
			throw new IllegalArgumentException("Protocol: " + protocol + " doesn't exsist!");
		}

	}
	public byte[] FINALserialize(int protocol){
		int size = 3;
		for(Plot p: getPlots()){
			size += Plots.getBytes(p, protocol) + 1;
		}
		byte[] fin = new byte[size];
		fin[0] = (byte)protocol;
		fin[1] = (byte)this.getLevel();
		fin[2] = (byte)this.getProgress();
		//Bukkit.broadcastMessage("save: " + progress);
		int index = 3;
		for(Plot p: getPlots()){

			int neededBytes = Plots.getBytes(p, protocol);

			fin[index] = Plots.getId(p);
			index++;
			byte[] dat = p.serialize();
			//Bukkit.broadcastMessage(p.getName() + ":" + neededBytes + "-" + Hex.encodeHexString(dat));
			for(int i = 0; i < neededBytes; i++){
				fin[index] = dat[i];
				index++;
			}
		}
		return fin;
	}

	/**
	 * Shoots up the player and gives them the appropriate amount of xp
	 * after harvesting a crop.
	 * @param c the {@link CropType} harvested
	 * @param p the {@link Player} that harvested it
	 * @param mult the integer multiplier for harvested items.
	 */
	public void harvest(CropType c, Player p, int mult){
		ItemStack[] items = c.getItems(mult);
		int amt = Constant.countItems(items);
		if(amt < 10){
			Animations.playHarvestAni(p, items, 9L);
		}else if(amt < 20){
			Animations.playHarvestAni(p, items, 7L);
		}else{
			Animations.playHarvestAni(p, items, 3L);
		}

		this.addExp(c.getExp().getRandom());
		for(ItemStack s: items){
			Constant.forceGive(p, s);
		}
		p.setVelocity(new Vector(0, 1.2, 0));
	}
	public void blankSelect(Chunk pl){
		int loc = this.getPlotSlot(pl);
		if(loc > -1){
			this.openSet();
			this.currentSelected = pl;
		}
	}
	public void plotChange(Class<? extends Plot> set){
		if(set != null && this.currentSelected != null){
			if(Upgradeable.class.isAssignableFrom(set)){
				try {
					@SuppressWarnings("unchecked")
					Constructor<? extends Upgradeable> con = (Constructor<? extends Upgradeable>) set.getDeclaredConstructor(Farm.class, int.class);
					this.setBuildPlot(this.currentSelected, con.newInstance(this, 0));
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}else{
				try {
					Constructor<? extends Plot> con = set.getDeclaredConstructor(Farm.class);
					this.setBuildPlot(this.currentSelected, con.newInstance(this));
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void openSet(){
		Inventory ret = Bukkit.createInventory(null, 27, Constant.setPlotName);
		if(this.getPlots("Farm Plot") < Plots.getMaxPlots(FarmPlot.class, this.getTownHallLevel(), this.getLevel())){
			ItemStack add = new ItemStack(Material.HAY_BLOCK, 1);
			ItemMeta am = add.getItemMeta();
			assert am != null;
			am.setDisplayName(ChatColor.GREEN.toString()+ "Farm Plot");
			am.setLore(Collections.singletonList(ChatColor.GREEN.toString() + "Cost: " +
					ChatColor.DARK_GREEN.toString() + NumberFormat.getCurrencyInstance().format((this.getLevel() + 1) * 10)));
			add.setItemMeta(am);
			
			ret.addItem(add);
		}
		if(this.getPlots("Town Hall") < Plots.getMaxPlots(TownHall.class, this.getTownHallLevel(), this.getLevel())){
			ItemStack add = new ItemStack(Material.ENCHANTING_TABLE, 1);
			ItemMeta am = add.getItemMeta();
			assert am != null;
			am.setDisplayName(ChatColor.LIGHT_PURPLE.toString()+ "Town Hall");
			am.setLore(Collections.singletonList(ChatColor.GREEN.toString() + "Cost: " + ChatColor.DARK_GREEN.toString() + Constant.format(0D)));
			add.setItemMeta(am);
			
			ret.addItem(add);
		}
		if(this.getPlots("Seed Research Centre") < Plots.getMaxPlots(ResearchCentre.class, this.getTownHallLevel(), this.getLevel())){
			ItemStack add = new ItemStack(Material.BREWING_STAND, 1);
			ItemMeta am = add.getItemMeta();
			assert am != null;
			am.setDisplayName(ChatColor.LIGHT_PURPLE.toString()+ "Seed Research Centre");
			am.setLore(Collections.singletonList(ChatColor.GREEN.toString() + "Cost: "
					+ ChatColor.DARK_GREEN.toString() + Constant.format(0D)));
			add.setItemMeta(am);
			
			ret.addItem(add);
		}
		if(this.getPlots("Pavillion") < Plots.getMaxPlots(Pavillion.class, this.getTownHallLevel(), this.getLevel())){
			ItemStack add = new ItemStack(Material.CHEST, 1);
			ItemMeta am = add.getItemMeta();
			assert am != null;
			am.setDisplayName(ChatColor.GOLD.toString()+ "Pavillion");
			am.setLore(Arrays.asList(ChatColor.GREEN.toString() + "Cost: " +
			ChatColor.DARK_GREEN.toString() + Constant.format(50D), ChatColor.DARK_PURPLE.toString() + "Used for storage"));
			add.setItemMeta(am);
			
			ret.addItem(add);
		}
		if(this.getPlots("Shop") < Plots.getMaxPlots(Shop.class, this.getTownHallLevel(), this.getLevel())){
			ItemStack add = new ItemStack(Material.GOLD_INGOT, 1);
			ItemMeta am = add.getItemMeta();
			assert am != null;
			am.setDisplayName(ChatColor.YELLOW.toString()+ "Shop");
			am.setLore(Arrays.asList(ChatColor.GREEN.toString() + "Cost: " +
			ChatColor.DARK_GREEN.toString() + Constant.format(500D), ChatColor.DARK_PURPLE.toString() + "Sell your items!"));
			add.setItemMeta(am);
			
			ret.addItem(add);
		}
		
		this.getOwner().openInventory(ret);
	}

}
