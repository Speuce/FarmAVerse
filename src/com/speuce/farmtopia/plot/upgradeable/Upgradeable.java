package com.speuce.farmtopia.plot.upgradeable;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.plot.Plot;

public abstract class Upgradeable extends Plot{
	private String[] schems;
	private int lvl;
	
	
	public Upgradeable(String name, String[] schematics, Farm f, int lvl) {
		super(name, schematics[0], f);
		this.schems = schematics;
		this.lvl = lvl;
	}
	public int getMaxLv(){
		return schems.length;
	}
	public int getLv(){
		return this.lvl;
	}
	public abstract int getLevelReqToUpgrade(int next);
	public abstract int getThLvToUpgrade(int next);
	public void onUpgrade(){};
	//public abstract boolean canBuy(int thLv, int xplv, int nextTier);
	@Override
	public String getSchematic(){
		return this.getSchem(lvl);
	}
	public String getSchem(int lvl){
		return this.schems[lvl];
	}
	public void upgrade(){
		if((this.lvl + 1) < this.getMaxLv()){
			this.lvl++;
			//Bukkit.broadcastMessage("upgraded");
			this.getChunk().getWorld().playSound(this.getChunk().getBlock(8, Constant.baseY, 8).getLocation(), Sound.BLOCK_ANVIL_USE, 3F, 0F);
			this.getFarm().getFm().buildSchem(this.getSchem(lvl), this.getChunk().getBlock(0, Constant.baseY, 0));
			this.onUpgrade();
		}
	}
	public abstract int getCost(int currentlv);
	
	public boolean canPurchase(){
		return this.canBuy(this.getFarm().getTownHallLevel(), this.getFarm().getLevel(), this.lvl+1);
	}
	
	private boolean canBuy(int th, int xp, int next){
		//Bukkit.broadcastMessage("debug1: " + this.getLevelReqToUpgrade(next) + ":" + xp);
		//Bukkit.broadcastMessage("debug2: " + this.getThLvToUpgrade(next) + ":" + th);
		return this.getLevelReqToUpgrade(next) <= xp && this.getThLvToUpgrade(next) <= th;
	}
	
	public void openUpgradeInventory(Player p){
		Inventory open = Bukkit.createInventory(null, 9, "Upgrade-" + this.getName());
		ItemStack info = new ItemStack(Material.ANVIL, 1);
		ItemMeta infomet = info.getItemMeta();
		infomet.setDisplayName(ChatColor.GREEN.toString() + this.getName());
		List<String> lore = new ArrayList<String>();
		if((this.lvl +1) < this.getMaxLv()){
			ItemStack accept = new ItemStack(Material.GREEN_WOOL);
			ItemMeta met = accept.getItemMeta();
			met.setDisplayName(ChatColor.GREEN.toString() + "Buy");
			if(!this.canPurchase()){
				List<String> l2 = new ArrayList<String>();
				//l2.add("CANNOT PURCHASE: ");
				if(this.getFarm().getTownHallLevel() < this.getThLvToUpgrade(this.lvl+1)){
					l2.add(ChatColor.RED.toString() + "Requires Town Hall Level: " + (this.getThLvToUpgrade(this.lvl+1)+1));
		
				}
				if(this.getFarm().getLevel() < this.getLevelReqToUpgrade(lvl+1)){
					l2.add(ChatColor.RED.toString() + "Requires Farming Level: " + this.getLevelReqToUpgrade(this.lvl+1));
				}
				met.setLore(l2);
			}
			accept.setItemMeta(met);
			ItemStack deny = new ItemStack(Material.RED_WOOL);
			ItemMeta met2 = deny.getItemMeta();
			met2.setDisplayName(ChatColor.RED.toString() + "Cancel");
			deny.setItemMeta(met2);
			open.setItem(3, accept);
			open.setItem(5, deny);
			lore.add(ChatColor.LIGHT_PURPLE.toString() + "Current Level: " + ChatColor.AQUA.toString() +(this.getLv()+1));
			lore.add(" ");
			lore.add(ChatColor.GOLD.toString() + "Cost to Upgrade: " + ChatColor.GREEN.toString() + "$" + this.getCost(this.getLv()));
		}else{
			lore.add(ChatColor.LIGHT_PURPLE.toString() + "Current Level: " + ChatColor.AQUA.toString() +(this.getLv()+1) +ChatColor.GRAY.toString() + " (MAX)");
		}
		infomet.setLore(lore);
		info.setItemMeta(infomet);
		open.setItem(5, info);
		p.openInventory(open);
	}
}
