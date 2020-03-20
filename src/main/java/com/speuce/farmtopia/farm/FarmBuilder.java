package main.java.com.speuce.farmtopia.farm;

import main.java.com.speuce.farmtopia.main.FarmTopia;
import main.java.com.speuce.farmtopia.plot.PlotBuilder;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;


public class FarmBuilder {
	
	private FarmTopia pl;
	public FarmBuilder(FarmTopia pl){
		this.pl = pl;
	}
	public void build(final Farm farm, FarmReady fr){

		BukkitRunnable br = new BukkitRunnable(){
			int plot = 0;
			Iterator<Chunk> chunkIterator = farm.iterator();
			@Override
			public void run() {
				if(chunkIterator.hasNext()){
					Chunk curr = chunkIterator.next();
						for(Entity e: curr.getEntities()){
							if(e.getType() == EntityType.ARMOR_STAND){
								e.remove();
							}
						}
						PlotBuilder pb = new PlotBuilder(farm.getPlot(plot), pl.getSchem(), curr);
						//Plot plot = farm.getPlot(plot);
						pb.build(false);
						plot++;
					}else{
						fr.onFinish(farm);
						farm.buildAllWalls();
						this.cancel();
					}
			}
		};
		br.runTaskTimer(this.pl, 20L, 20L);
	}

}
