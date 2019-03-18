package com.speuce.farmtopia.jobs;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.resources.Resource;

public class Job {
	private Long expires;
	private JobLore lore;
	private Resource[] res;
	private int[] amounts;
	private double reward;
	private byte level;
	private short seed;
	private String name;
	public Job(Long expires, JobLore lore, Resource[] res, int[] amounts, double reward, byte level, short seed, String name) {
		super();
		if(res.length != amounts.length){
			throw new IllegalArgumentException("Resource size must be the same as amounts size!");
		}
		this.reward = reward;
		this.level = level;
		this.seed = seed;
		this.expires = expires;
		this.lore = lore;
		this.res = res;
		this.amounts = amounts;
		this.name = name;
	}
	public ItemStack getDisplay(){
		//ItemStack ret = new ItemStack(res[0].getMat(), 1, res[0].getDamage());
		ItemStack ret = res[0].toItemStack(1);
		ItemMeta retm = ret.getItemMeta();
		retm.setDisplayName(ChatColor.GREEN.toString() + name + "");
		List<String> lore = new ArrayList<String>();
		lore.addAll(this.lore.getLore(res));
		lore.add(" ");
		for(int x = 0; x < res.length; x++){
			lore.add(ChatColor.AQUA.toString() + "Bring: " + ChatColor.GREEN.toString() + amounts[x] + " " + res[x].getName() + "(s)");
		}
		lore.add(ChatColor.GOLD.toString() + "Reward: " + ChatColor.GREEN.toString() + NumberFormat.getCurrencyInstance().format(reward));
		lore.add(ChatColor.RED.toString() + "Expires: " +ChatColor.DARK_RED.toString() +  Constant.milliSecondsToDisplay(expires - System.currentTimeMillis()));
		retm.setLore(lore);
		ret.setItemMeta(retm);
		return ret;
	}
	public double getReward(){
		return this.reward;
	}
	public Long getExpiry() {
		return expires;
	}
	public JobLore getLore() {
		return lore;
	}
	public Resource[] getRes() {
		return res;
	}
	public int[] getAmounts() {
		return amounts;
	}
	public void setExpires(Long expires) {
		this.expires = expires;
	}
	public byte getLevel() {
		return level;
	}
	public short getSeed() {
		return seed;
	}
}
