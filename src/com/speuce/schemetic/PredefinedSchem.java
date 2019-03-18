package com.speuce.schemetic;

import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class PredefinedSchem {
	private Schematic s;
	private Plugin p;
	private Block place;
	private int rotation = 0;
	public PredefinedSchem(Schematic s, Plugin p, Block place, int rot) {
		super();
		this.s = s;
		this.p = p;
		this.place = place;
		this.rotation = rot;
	}
	public PredefinedSchem(Schematic s, Plugin p, Block place) {
		this(s, p, place, 0);
	}
	public void build(){
		s.buildOptimized(place, p, this.rotation);
		//System.out.println("build");
	}
	public Schematic getS() {
		return s;
	}
	public Plugin getP() {
		return p;
	}
	public Block getPlace() {
		return place;
	}
}
