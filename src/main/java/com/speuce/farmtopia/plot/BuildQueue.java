package main.java.com.speuce.farmtopia.plot;

import java.util.LinkedList;
import java.util.Queue;

import main.java.com.speuce.farmtopia.util.Constant;
import main.java.com.speuce.schemetic.PredefinedSchem;
import main.java.com.speuce.schemetic.TestPredefinedSchem;
import org.bukkit.scheduler.BukkitRunnable;

public class BuildQueue {
	private static Queue<PredefinedSchem> queue = new LinkedList<PredefinedSchem>();
	public static BukkitRunnable start(){
		return new BukkitRunnable(){

			@Override
			public void run() {
				if(!queue.isEmpty()){
					PredefinedSchem pl = queue.poll();
					pl.build();
				}
			}
			
		};
	}
	public static void queue(PredefinedSchem task){
		queue.offer(task);
	}
	public static void dump(Long time){
		System.out.println("DUMP: Build Queue took " + Constant.milliSecondsToDisplay(time) + " to build");
		System.out.println("Total In Queue: " + queue.size());
		System.out.println("Elements: ");
		LinkedList<PredefinedSchem> ss = new LinkedList<PredefinedSchem>(queue);
		for(PredefinedSchem f: ss.toArray(new PredefinedSchem[0])){
			if(f.getS() != null){
				System.out.println(f.getS().getNom() + " AT: " + f.getPlace().getX() + ", " + f.getPlace().getZ());
			}
			
		}
	}
	public static void test(){
		queue.add(new TestPredefinedSchem());
	}
}
