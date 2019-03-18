package com.speuce.farmtopia.farm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.crop.CropType;
import com.speuce.farmtopia.plot.BuildQueue;
import com.speuce.farmtopia.plot.FarmPlot;
import com.speuce.farmtopia.plot.Plot;
import com.speuce.farmtopia.plot.PlotBuilder;
import com.speuce.farmtopia.plot.Plots;
import com.speuce.farmtopia.plot.upgradeable.Pavillion;
import com.speuce.farmtopia.plot.upgradeable.ResearchCentre;
import com.speuce.farmtopia.plot.upgradeable.TownHall;
import com.speuce.farmtopia.plot.upgradeable.Upgradeable;
import com.speuce.farmtopia.resources.Resource;
import com.speuce.schemetic.Schematic;

public class Farm {
	private List<Plot> plots;
	private Location baseLocation;
	private Player owner;
	private FarmManager fm;
	private int iter = 0, count = 0; 
	private Chunk curr;
	private int lvl;
	private byte progress;
	private TownHall hall;
	private Chunk currentSelected = null;
	public Farm(Location base, Player p, FarmManager fm, int lvl, byte progress){
		this.baseLocation = base;
		this.fm = fm;
		this.plots = new ArrayList<Plot>();
		this.owner = p;
		this.lvl = lvl;
		this.progress = progress;
		base.getWorld().getPopulators().clear();
		curr = base.getChunk();
	}
	public int getLevel(){
		return this.lvl;
	}
	public int getProgress(){
		return this.progress&255;
	}
	public void buildAllWalls(){
		System.out.println("building walls..");
		for(Plot pl: plots){
			buildWalls(pl, false);
		}
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
	public Plot[] getNearbyPlots(Plot pl){
		Chunk c = pl.getChunk();
		Plot[] ret = new Plot[8];
		ret[0]= getPlot(c.getWorld().getChunkAt(c.getX(), c.getZ()-1));
		ret[1]= getPlot( c.getWorld().getChunkAt(c.getX()+1, c.getZ()-1));
		ret[2]= getPlot( c.getWorld().getChunkAt(c.getX()+1, c.getZ()));
		ret[3]= getPlot(c.getWorld().getChunkAt(c.getX()+1, c.getZ()+1));
		ret[4]= getPlot(c.getWorld().getChunkAt(c.getX(), c.getZ()+1));
		ret[5]= getPlot(c.getWorld().getChunkAt(c.getX()-1, c.getZ()+1));
		ret[6]= getPlot(c.getWorld().getChunkAt(c.getX()-1, c.getZ()));
		ret[7]= getPlot(c.getWorld().getChunkAt(c.getX()-1, c.getZ()-1));
		return ret;

	}
	public void addExp(int xp){
		int add = (int) Math.round((xp)/((1 + lvl)*0.2));
		int val =  add+ (progress&255);
		if(val > 0){
			owner.sendMessage(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD.toString()  + "+ " + add + " exp");
		}
		while(val >= 255){
			val -= 255;

			lvl++;
			Constant.forceGive(owner, Resource.MAGIC_DUST.toItemStack(5));
			Tutorial.onLevelUp(owner, lvl);
			owner.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "LEVEL UP: " + ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + lvl);
			owner.playSound(owner.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 1.6F);
		}
		progress = (byte)val;
		//Bukkit.broadcastMessage("prog: " + progress);
		owner.setLevel(lvl);
		owner.setExp(this.getProgress()/255F);
	}
	public void subtractExp(int xp){
		int val =  (progress&255) - xp;
		//Bukkit.broadcastMessage("subtract: "+ xp);
		owner.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString()  + "- " + xp + " exp");

		while(val < 0){
			if(lvl == 0){
				progress = (byte)0;
				owner.setLevel(lvl);
				owner.setExp(this.getProgress()/255F);
				return;
			}
			val += 255;
			lvl--;
			owner.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "LEVEL DOWN: " + ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + lvl);
			owner.playSound(owner.getLocation(), Sound.ENTITY_PLAYER_BURP, 2F, 0F);
		}
		progress = (byte)val;
		
		//Bukkit.broadcastMessage("prog: " + progress);
		owner.setLevel(lvl);
		owner.setExp(this.getProgress()/255F);
	}
	public Chunk getCurrentChunk(){
		return this.curr;
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
	public void nextChunk(){
		if(count <= 0){
			iter++;
			if(iter % 2 == 0){
				curr = getNearbyNS(curr, false);
			}else{
				curr = getNearbyWE(curr, false);
			}
			count = iter*2;
		}else{
			if(count > iter){
				if(iter % 2 == 0){
					curr = getNearbyWE(curr, false);
				}else{
					curr = getNearbyNS(curr, false);
				}
			}else{
				if(iter % 2 == 0){
					curr = getNearbyNS(curr, true);
				}else{
					curr = getNearbyWE(curr, true);
				}
			}
			count--;
		}
	}
	private Chunk getNearbyWE(Chunk c,boolean we){
		if(we){
			return c.getWorld().getChunkAt(c.getX()+1, c.getZ());
		}else{
			return c.getWorld().getChunkAt(c.getX()-1, c.getZ());
		}
	}
	private Chunk getNearbyNS(Chunk c,boolean ns){
		if(ns){
			return c.getWorld().getChunkAt(c.getX(), c.getZ()+1);
		}else{
			return c.getWorld().getChunkAt(c.getX(), c.getZ()-1);
		}
	}
	public int getPlots(){
		return plots.size();
	}
	public void addPlot(Plot p){
		if(p instanceof TownHall){
			this.hall = (TownHall) p;
		}
		plots.add(p);
	}
	public Plot getPlot(int x){
		return this.plots.get(x);
	}
	public Plot getPlot(Chunk c){
		for(Plot p: plots){
			if(p.getChunk().getX() == c.getX() && p.getChunk().getZ() == c.getZ()){
				return p;
			}
		}
		return null;
	}
	public int getPlotSlot(Chunk c){
		for(int x = 0; x < plots.size(); x++){
			Plot p = plots.get(x);
			if(p.getChunk().getX() == c.getX() && p.getChunk().getZ() == c.getZ()){
				return x; 
			}
		}
		return -1;
	}
	public boolean hasPlot(String plotname){
		for(Plot f: this.plots){
			if(f.getName().equalsIgnoreCase(plotname)){
				return true;
			}
		}
		return false;
	}
	public int getPlots(String type){
		int count = 0;
		for(Plot f: this.plots){
			if(f.getName().equalsIgnoreCase(type)){
				count++;
			}
		}
		return count;
	}
	public Plot getFirstPlot(Class<? extends Plot> type){
		for(Plot pl: this.plots){
			if(pl.getClass().equals(type)){
				return pl;
			}
		}
		return null;
	}
	public double getCostt(){
		if(this.getPlots() <= 1){
			return 0;
		}
		return Math.pow((this.getPlots() * 3), 1.4);
	}
	public void setPlot(int x, Plot f){
		if(f instanceof TownHall){
			this.hall = (TownHall) f;
		}
		this.plots.set(x, f);
	}
	public void setBuildPlot(Chunk c,Plot f){
		if(f instanceof TownHall){
			this.hall = (TownHall) f;
			Tutorial.onTownHallBuild(this.owner);
		}
		if(f instanceof FarmPlot){
			Tutorial.onFarmPlotSet(owner);
		}
		if(f instanceof ResearchCentre){
			Tutorial.onResearchCentreBuild(owner);
		}
		this.plots.set(this.getPlotSlot(c), f);
		//BuildQueue.queue(this.getFm().getClear().def(f.getChunk().getBlock(0, Constant.baseY - 1, 0), this.getFm().getPlugin()));
		PlotBuilder bl = new PlotBuilder(f, this.fm.getPlugin().getSchem(), c);
		bl.build(false);
//		bl.build(false, new Runnable(){
//
//			@Override
//			public void run() {
//				buildWalls(f);
//				
//			}
//			
//		});

	}
	public FarmManager getFm(){
		return this.fm;
	}
	public Player getOwner(){
		return this.owner;
	}
	public List<Plot> getAllPlots(){
		return this.plots;
	}
	public void removePlot(Plot p){
		this.plots.remove(p);
	}
	public void removePlot(int x){
		this.plots.remove(x);
	}
	public Location getBaseLocation(){
		return this.baseLocation;
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
//			if(nearby[1] == null && nearby[2] == null){
//				//System.out.println("wall1");
//
//			}
		}else{
			if(nearby[2] == null && nearby[1] != null){
				//WRAPPED EDGE 2
				BuildQueue.queue(wrap2.def(pl.getBaseLocation().getRelative(16, 0, 0), this.getFm().getPlugin(), 2));
			}
		}

		if(nearby[2] == null){
			System.out.println("wall3: " + pl.getName());
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
		int protocol = (int) data[0];
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
		//	Bukkit.broadcastMessage("protocol 2");
			Farm ret = new Farm(base, p, fm, (int)data[1], data[2]);
			//Bukkit.broadcastMessage("log: " + ret.getLevel());
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
		for(Plot p: plots){
			size += Plots.getBytes(p, protocol) + 1;
		}
		byte[] fin = new byte[size];
		fin[0] = (byte)protocol;
		fin[1] = (byte)lvl;
		fin[2] = progress;
		//Bukkit.broadcastMessage("save: " + progress);
		int index = 3;
		for(Plot p: plots){

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
	public void harvest(CropType c, Player p, int mult){
		ItemStack[] items = c.getItems(mult);
		int amt = Constant.countItems(items);
		if(amt < 10){
			this.playAni(p, items, 9L);
		}else if(amt < 20){
			this.playAni(p, items, 7L);
		}else{
			this.playAni(p, items, 3L);
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
	private void playAni(Player p, ItemStack[] items, long frequency){
		Inventory i = Bukkit.createInventory(null, 9, ChatColor.DARK_GREEN + "You Harvested: ");
		p.openInventory(i);
		BukkitRunnable br = new BukkitRunnable(){
			int ca = 1;
			int slot = 0;
			int amt = items[0].getAmount();
			@Override
			public void run() {
				if(p.getOpenInventory() == null || !p.getOpenInventory().getTitle().contains("Harvested")){
					p.openInventory(i);
				}
				if(slot < items.length){
					ItemStack it = items[slot];
					it.setAmount(ca);
					i.setItem(slot, it);
					p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2F, 2F);
					ca++;
					if(ca > amt){
						ca = 1;
						slot++;
						if(slot < items.length){
							amt = items[slot].getAmount();
						}
					}
				}else{
					i.setItem(8, Constant.getOk());
					this.cancel();
				}

			}
			
		};
		br.runTaskTimer(this.getFm().getPlugin(), 7L, frequency);
	}
	private void openSet(){
		Inventory ret = Bukkit.createInventory(null, 27, Constant.setPlotName);
		
		if(this.getPlots("Farm Plot") < Plots.getMaxPlots(FarmPlot.class, this.getTownHallLevel(), this.getLevel())){
			ItemStack add = new ItemStack(Material.HAY_BLOCK, 1);
			ItemMeta am = add.getItemMeta();
			am.setDisplayName(ChatColor.GREEN.toString()+ "Farm Plot");
			am.setLore(Arrays.asList(new String[]{ChatColor.GREEN.toString() + "Cost: " +
			ChatColor.DARK_GREEN.toString() + NumberFormat.getCurrencyInstance().format((this.getLevel()+1)*10)}));
			add.setItemMeta(am);
			
			ret.addItem(add);
		}
		if(this.getPlots("Town Hall") < Plots.getMaxPlots(TownHall.class, this.getTownHallLevel(), this.getLevel())){
			ItemStack add = new ItemStack(Material.ENCHANTING_TABLE, 1);
			ItemMeta am = add.getItemMeta();
			am.setDisplayName(ChatColor.LIGHT_PURPLE.toString()+ "Town Hall");
			am.setLore(Arrays.asList(new String[]{ChatColor.GREEN.toString() + "Cost: " + ChatColor.DARK_GREEN.toString() + Constant.format(0D)}));
			add.setItemMeta(am);
			
			ret.addItem(add);
		}
		if(this.getPlots("Seed Research Centre") < Plots.getMaxPlots(ResearchCentre.class, this.getTownHallLevel(), this.getLevel())){
			ItemStack add = new ItemStack(Material.BREWING_STAND, 1);
			ItemMeta am = add.getItemMeta();
			am.setDisplayName(ChatColor.LIGHT_PURPLE.toString()+ "Seed Research Centre");
			am.setLore(Arrays.asList(new String[]{ChatColor.GREEN.toString() + "Cost: "
			+ ChatColor.DARK_GREEN.toString() + Constant.format(0D)}));
			add.setItemMeta(am);
			
			ret.addItem(add);
		}
		if(this.getPlots("Pavillion") < Plots.getMaxPlots(Pavillion.class, this.getTownHallLevel(), this.getLevel())){
			ItemStack add = new ItemStack(Material.CHEST, 1);
			ItemMeta am = add.getItemMeta();
			am.setDisplayName(ChatColor.GOLD.toString()+ "Pavillion");
			am.setLore(Arrays.asList(new String[]{ChatColor.GREEN.toString() + "Cost: " + 
			ChatColor.DARK_GREEN.toString() + Constant.format(50D), ChatColor.DARK_PURPLE.toString() + "Used for storage"}));
			add.setItemMeta(am);
			
			ret.addItem(add);
		}

		
		this.owner.openInventory(ret);
	}
	public static Inventory getMenu(Farm f){
		Inventory ret = Bukkit.createInventory(null, 9, ChatColor.DARK_PURPLE.toString() + "Farm Menu");
		
		ItemStack add = new ItemStack(Material.ANVIL, 1);
		ItemMeta am = add.getItemMeta();
		am.setDisplayName(ChatColor.AQUA.toString()+ ChatColor.BOLD.toString() + "Buy another Plot");
		am.setLore(Arrays.asList(new String[]{ChatColor.GREEN.toString() + "Cost: " + ChatColor.DARK_GREEN.toString() + Constant.format(f.getCostt())}));
		add.setItemMeta(am);
		
		ItemStack a2dd = new ItemStack(Material.BOOK, 1);
		ItemMeta a2m = add.getItemMeta();
		a2m.setDisplayName(ChatColor.GREEN.toString() + "Jobs");
		a2m.setLore(Arrays.asList(new String[]{ChatColor.GOLD.toString() + "$$$"}));
		a2dd.setItemMeta(a2m);
		
		ret.setItem(0, add);
		ret.setItem(1, a2dd);
		
		return ret;
	}
}
