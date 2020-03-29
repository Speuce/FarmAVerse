package main.java.com.speuce.farmtopia.farm;

import main.java.com.speuce.farmtopia.event.Events;
import main.java.com.speuce.farmtopia.event.farm.FarmLoadedEvent;
import main.java.com.speuce.farmtopia.main.FarmTopia;
import main.java.com.speuce.farmtopia.plot.PlotBuilder;
import main.java.com.speuce.schemetic.SchemeticManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;


public class FarmBuilder {

	private Farm farm;

	public FarmBuilder(Farm f){
		this.farm = f;
	}
	public void build(){

		BukkitRunnable br = new BukkitRunnable(){
			int plot = 0;
			Iterator<Chunk> chunkIterator = farm.iterator();
			final SchemeticManager schem = FarmTopia.getFarmTopia().getSchem();
			@Override
			public void run() {
				if(chunkIterator.hasNext()){
					Chunk curr = chunkIterator.next();
					for(Entity e: curr.getEntities()){
						if(e.getType() == EntityType.ARMOR_STAND){
							e.remove();
						}
					}
					PlotBuilder pb = new PlotBuilder(farm.getPlot(plot), schem, curr);
					//Plot plot = farm.getPlot(plot);
					pb.build(false);
					plot++;
				}else{
					Events.call(new FarmLoadedEvent(farm));
					farm.buildAllWalls();
					this.cancel();
				}
			}
		};
		br.runTaskTimer(FarmTopia.getFarmTopia(), 20L, 20L);
	}

}
