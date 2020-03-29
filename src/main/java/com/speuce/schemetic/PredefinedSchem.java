package main.java.com.speuce.schemetic;

import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class PredefinedSchem {
	private Schematic s;
	private Block place;
	private int rotation = 0;
	public PredefinedSchem(Schematic s,Block place, int rot) {
		super();
		this.s = s;
		this.place = place;
		this.rotation = rot;
	}
	public PredefinedSchem(Schematic s,Block place) {
		this(s,place, 0);
	}
	public void build(){
		s.buildOptimized(place, this.rotation);
		//System.out.println("build");
	}
	public Schematic getS() {
		return s;
	}
	public Block getPlace() {
		return place;
	}
}
