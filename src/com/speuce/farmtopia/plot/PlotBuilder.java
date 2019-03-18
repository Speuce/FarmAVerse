package com.speuce.farmtopia.plot;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.scheduler.BukkitRunnable;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.crop.FarmSubplot;
import com.speuce.schemetic.Schematic;
import com.speuce.schemetic.SchemeticManager;

public class PlotBuilder {
	
	private SchemeticManager man;
	private Plot p;
	private Random r;
	public PlotBuilder(Plot p, SchemeticManager man, Chunk ch){
		this.man = man;
		this.p = p;
		this.r = new Random();
		if(p == null){
			throw new IllegalArgumentException("Plot cannot be null!");
		}
		p.setChunk(ch);
		
	}
	public void build(boolean queue){
		build(queue, null);
	}
	//@SuppressWarnings("deprecation")
	public void build(boolean queue, Runnable r){
		Schematic sc = man.getSchemetic(p.getSchematic());
		if(sc == null){
			e(p.getSchematic());
			return;
		}
		if(queue){
			BuildQueue.queue(sc.def(p.getChunk().getBlock(0, Constant.baseY, 0), man.getPlugin()));
		}else{
			sc.buildOptimized(p.getChunk().getBlock(0, Constant.baseY, 0), man.getPlugin(),0, new BukkitRunnable(){

				@Override
				public void run() {
					p.setup();
					if(r != null){
						r.run();
					}
				}
				
			});
			//Bukkit.broadcastMessage("builld");
		}

		if(p instanceof FarmPlot){
			BukkitRunnable br = new BukkitRunnable(){

				@Override
				public void run() {
					FarmPlot fm = (FarmPlot)p;
					for(int spot = 0; spot < 4; spot++){
						FarmSubplot c = fm.getSubplot(spot);
						if(c != null){
							Schematic cro = man.getSchemetic(c.currentSchem());
							if(cro == null){
								e(c.currentSchem());
								return;
							}
							if(queue){
								BuildQueue.queue(cro.def(fm.getFarmSpot(spot), man.getPlugin()));
							}else{
								cro.buildOptimized(fm.getFarmSpot(spot), man.getPlugin());
							}

						}else{
							String s = getRanEmptySchem();
							Schematic emp = man.getSchemetic(s);
							if(emp == null){
								e(s);
								return;
							}
							if(queue){
								BuildQueue.queue(emp.def(fm.getFarmSpot(spot), man.getPlugin()));
							}else{
								emp.buildOptimized(fm.getFarmSpot(spot), man.getPlugin());
							}
							//Bukkit.broadcastMessage("SPot");
						}
					}
					
				}
				
			};
			br.runTaskLater(man.getPlugin(), 20L);

		}
	}
	private void e(String schem){
		Bukkit.broadcastMessage(ChatColor.RED + "FATAL ERROR: Couldn't find schemetic: " 
				+ schem + " for plot: " + p.getName());
	}
	private String getRanEmptySchem(){
		int x = r.nextInt(3)+1;
		return "og"+x;
	}
}
