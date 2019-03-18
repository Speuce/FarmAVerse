package com.speuce.schemetic;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class EBlock {
	private Material type;
	private BlockData damage;
	public EBlock(Material type, BlockData data) {
		this.type = type;
		this.damage = data;
	}

	public Material getType() {
		return type;
	}
	public void setType(Material type) {
		this.type = type;
	}
	public BlockData getDamage() {
		return damage;
	}
	public void setDamage(BlockData damage) {
		this.damage = damage;
	}
	public EBlock clone(){
		return new EBlock(type, damage.clone());
	}
	@Override
	public String toString(){
		return type.toString() + "|" + damage.getAsString();
	}
	public static EBlock fromString(String s){
		//System.out.println("Loading EBlock: " + s);
		String[] arg = s.split("\\|");
		return new EBlock(Material.getMaterial(arg[0]), Bukkit.getServer().createBlockData(arg[1]));
	}
}