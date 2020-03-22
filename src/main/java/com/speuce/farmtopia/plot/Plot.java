package main.java.com.speuce.farmtopia.plot;

import main.java.com.speuce.farmtopia.event.Events;
import main.java.com.speuce.farmtopia.event.farm.plot.FarmPlotInteractEvent;
import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.util.Constant;
import main.java.com.speuce.farmtopia.util.Serializable;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public abstract class Plot implements Serializable {
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
	public void onInteractOther(PlayerInteractEvent e){};

	/**
	 * Called when this plot is interacted on
	 */
	public void onInteract(PlayerInteractEvent e) {
		Events.call(new FarmPlotInteractEvent(f, this, e));
		if(e.getPlayer().equals(f.getOwner())){
			onInteractOwner(e);
		}else{
			onInteractOther(e);
		}
	};
	
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
