package main.java.com.speuce.farmtopia.plot;

import main.java.com.speuce.farmtopia.farm.Farm;
import org.bukkit.event.player.PlayerInteractEvent;


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
