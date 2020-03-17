package main.java.com.speuce.farmtopia.farm;

import main.java.com.speuce.farmtopia.main.FarmTopia;
import main.java.com.speuce.farmtopia.plot.PlotBuilder;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;


public class FarmBuilder {
	
	private FarmTopia pl;
	public FarmBuilder(FarmTopia pl){
		this.pl = pl;
	}
	public void build(final Farm farm, FarmReady fr){

		BukkitRunnable br = new BukkitRunnable(){
			int plot = 0;

			@Override
			public void run() {
				Chunk curr = farm.getCurrentChunk();
				for(Entity e: curr.getEntities()){
					if(e.getType() == EntityType.ARMOR_STAND){
						e.remove();
					}
				}
				if(plot < farm.getPlots()){
					PlotBuilder pb = new PlotBuilder(farm.getPlot(plot), pl.getSchem(), curr);
					//Plot plot = farm.getPlot(plot);
					pb.build(false);
					plot++;
					farm.nextChunk();
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
