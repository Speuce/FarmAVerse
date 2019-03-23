package com.speuce.farmtopia.plot;

import org.bukkit.event.player.PlayerInteractEvent;

import com.speuce.farmtopia.farm.Farm;

public class EmptyPlot extends Plot{

	public EmptyPlot(Farm f) {
		super("Empty", "plainplot", f);
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] serialize() {
		return new byte[1];
	}

	@Override
	public void onInteractOwner(PlayerInteractEvent e) {
		this.getFarm().blankSelect(e.getClickedBlock().getChunk());
	}


}
