package com.speuce.farmtopia.craft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Crafter {
	private int invSize;
	private List<Integer> validSlots;
	private ItemStack r;
	private String name;
	private int output, base;
	public Crafter(int invSize, Integer[] validSlots,byte damage, String name, int output, int base) {
		this.invSize = invSize;
		this.validSlots = Arrays.asList(validSlots);
		this.output = output;
		this.base = base;
		this.r = new ItemStack(Material.BOW, 1, damage);
		ItemMeta met = r.getItemMeta();
		met.setLore(new ArrayList<String>());
		met.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		met.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		met.setDisplayName(ChatColor.RESET.toString());
		met.setUnbreakable(true);
		r.setItemMeta(met);
		this.name = name;
	}
	public int getInvSize() {
		return invSize;
	}
	public int getBase(){
		return this.base;
	}
	public List<Integer> getValidSlots() {
		return validSlots;
	}
	public ItemStack getItem(){
		return this.r;
	}
	public String getName(){
		return this.name;
		
	}
	public int getOutput(){
		return output;
	}

}
