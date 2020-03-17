package main.java.com.speuce.farmtopia.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.java.com.speuce.farmtopia.resources.Resource;
import org.bukkit.ChatColor;


public enum JobLore {
	PIE(new String[]{ChatColor.GOLD.toString() + "Hey, I'm just looking for some ITEM1",
			ChatColor.GOLD.toString() + "to make some ITEM1 Pie."}, 1),
	CAT(new String[]{ChatColor.GOLD.toString() + "Could I possibly get some ITEM1?",
			ChatColor.GOLD.toString() + "I need it for my cat."},1),
	ROACH(new String[]{ChatColor.GOLD.toString() + "I've been dealing with a roach infestation",
			ChatColor.GOLD.toString() + "and I've heard that ITEM1" +ChatColor.GOLD.toString()+" could help."},1),
	GRAB(new String[]{ChatColor.GOLD.toString() + "Mind getting me some ITEM1 "+ChatColor.GOLD.toString()+"and ITEM2?",
			ChatColor.GOLD.toString() + "I'll pay you nicely for your work."}, 2);
	private String[] lore;
	private int items;
	private JobLore(String[] lore, int items){
		this.lore = lore;
		this.items = items;
	}
	public String[] getLore(){
		return this.lore;
	}
	public int getItems(){
		return this.items;
	}
	public List<String> getLore(Resource[] items){
		if(items.length != this.items){
			throw new IllegalArgumentException("Item Input size is not of JobLore size: " + this.items);
		}
		for(Resource s: items){
			if(s == null){
				throw new NullPointerException("Input array has a null value");
			}
		}
		List<String> ret = new ArrayList<String>(Arrays.asList(lore));
		for(int x = 1; x <= this.items; x++){
			for(int y = 0; y < ret.size(); y++){
				String s = ret.get(y);
				ret.set(y, s.replaceAll("ITEM"+x, items[x-1].getName()));
			}
		}
		return ret;
	}
	public static List<JobLore> getBasedOnItems(int items){
		List<JobLore> ret = new ArrayList<JobLore>();
		for(JobLore j: JobLore.values()){
			if(j.getItems() == items){
				ret.add(j);
			}
		}
		return ret;
	}
}
