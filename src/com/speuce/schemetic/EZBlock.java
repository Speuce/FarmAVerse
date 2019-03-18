package com.speuce.schemetic;

import java.util.EnumSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;

@Deprecated
public class EZBlock {
	private int type;
	private byte damage;
	public EZBlock(int type, byte damage) {
		this.type = type;
		this.damage = damage;
	}

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public byte getDamage() {
		return damage;
	}
	public void setDamage(byte damage) {
		this.damage = damage;
	}
	public EZBlock clone(){
		return new EZBlock(type, damage);
	}
	public EBlock convert(){
		
		Material newM = convertMaterial(type, damage);
		BlockData b = Bukkit.getUnsafe().fromLegacy(newM, damage);
		EBlock ret = new EBlock(newM, b);
		System.out.println("Converted: " + toString() + " to: " + ret.toString());
		return ret;
	}  
	private static Material convertMaterial(int ID, byte Data) {
	    for(Material i : EnumSet.allOf(Material.class))
	    	if(i.getId() == ID) 
	    		return Bukkit.getUnsafe().fromLegacy(new MaterialData(i, Data));
	    return null;
	}
	protected static BlockFace getFacing(byte dir) {
        switch (dir) {
        	case 0: return BlockFace.SOUTH;
        	case 1: return BlockFace.WEST;
        	case 2: return BlockFace.NORTH;
        	case 3: return BlockFace.EAST;
        }
        return BlockFace.UP;
	}
	@Override
	public String toString(){
		return this.type + ":" + this.getDamage();
	}
}
