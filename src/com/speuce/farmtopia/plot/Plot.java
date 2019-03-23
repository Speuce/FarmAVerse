package com.speuce.farmtopia.plot;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.util.Serializable;

public abstract class Plot implements Serializable{
	private Chunk c;
	private String name;
	private String schematic;
	private Farm f;
	public Plot(String name, String shematic, Farm f) {
		super();
		this.c = null;
		this.name = name;
		this.schematic = shematic;
		this.f = f;
	}
	public Chunk getChunk() {
		return c;
	}
	public void setChunk(Chunk c){
		this.c = c;
	}
	public Block getBaseLocation(){
		return this.c.getBlock(0, Constant.baseY, 0);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public abstract void onInteractOwner(PlayerInteractEvent e);
	
	public void onInteractAny(PlayerInteractEvent e) {};
	
	public void onEntityInteract(PlayerInteractEntityEvent e) {};
	
	public String getSchematic(){
		return this.schematic;
	}
	public Farm getFarm(){
		return this.f;
	}
	public void update(){};
	public void setup(){};
	public void cleanup(){};
	
}
