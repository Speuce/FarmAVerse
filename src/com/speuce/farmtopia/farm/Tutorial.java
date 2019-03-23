package com.speuce.farmtopia.farm;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.resources.Resource;

public class Tutorial {
	private static Map<Player, Integer> progress = new HashMap<Player, Integer>();
//	p.sendMessage(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
//			"T");
	private static Map<Player, Queue<String[]>> list = new HashMap<Player, Queue<String[]>>();
	private static Plugin plug = null;
	private static BukkitRunnable task;
	private static TutorialTextBuilder start = new TutorialTextBuilder().add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
			"To get started, right click your wheat seeds on one of your farming spaces.");
	//private static TutorialTextBuilder nothing = new TutorialTextBuilder();
	private static TutorialTextBuilder lvl1 = new TutorialTextBuilder().add(ChatColor.AQUA.toString() + "� " + ChatColor.GOLD.toString() + 
					"As you level up, you'll begin to see jobs that require other items.", ChatColor.AQUA.toString() + "� " + ChatColor.GOLD.toString() + 
					"One of which is a " + ChatColor.YELLOW.toString() + "Bundle of Wheat.",ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
					"The only way to obtain it, is by crafting it.").add(ChatColor.AQUA.toString() + "� " + ChatColor.AQUA.toString() + 
							"Try going into your crafting table, at your town hall, and crafting 4 Wheat in a 2x2 pattern.").add(ChatColor.AQUA.toString() + "� " + ChatColor.GOLD.toString() + 
									"In jobs, Bundles of Wheat are worth slightly more than 4 wheat on their own.").add(ChatColor.AQUA.toString() + "� " + ChatColor.GOLD.toString() + 
					"If you wish, they can also be crafted back in to wheat by simply placing it in a crafting table.");
//	private static TutorialTextBuilder lvl2 = new TutorialTextBuilder().add(ChatColor.AQUA.toString() + "� " + ChatColor.RED.toString() + 
//					"You are now able to upgrade your Town Hall.",ChatColor.AQUA.toString() + "� " + ChatColor.GOLD.toString() + 
//					"It is Strongly Recommended that you do so.",ChatColor.AQUA.toString() + "� " + ChatColor.GOLD.toString() + 
//					"It allows you access to a bigger Crafting table, more Farm plots, and the ability to build the"
//							+ChatColor.BOLD.toString() + ChatColor.AQUA.toString()+" Seed Research Centre!");
	private static TutorialTextBuilder dustuse = new TutorialTextBuilder().add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
					"Good Job!").add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
							"Now, the Wheat is ready to harvest.",ChatColor.AQUA.toString() + "� " + ChatColor.AQUA.toString() + 
							"Left Click on the crop to harvest it.");
	private static TutorialTextBuilder seedplant = new TutorialTextBuilder().add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
					"You have Planted a Crop of Wheat, which will fully grow in a bit.",ChatColor.AQUA.toString() + "� " + ChatColor.AQUA.toString() + 
					"You can speed up the growth of this crop by using some " + ChatColor.YELLOW.toString() + "Magic Dust.").add(ChatColor.AQUA.toString() + "� " + ChatColor.AQUA.toString() + 
							"Try it! Right click on the crop with your Magic Dust in hand.");
	private static TutorialTextBuilder wh1 = new TutorialTextBuilder().add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
						"You can also slightly speed up crop growth by shifting repetitively on top of that crop,"
						+ " although this method is much slower.").add(ChatColor.AQUA.toString() + "� " + ChatColor.AQUA.toString() + 
								"Harvest 4 more wheat crops.");
	private static TutorialTextBuilder wh2 = new TutorialTextBuilder().add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
						"Now it's time to make some money from your product!").add(ChatColor.AQUA.toString() + "� " + ChatColor.AQUA.toString() + 
								"Open up the job menu by typing " + ChatColor.RED.toString() + "/j",ChatColor.AQUA.toString() + "� " + ChatColor.AQUA.toString() + 
								"Then, click the book at the bottom right corner to open up a new job!",ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
								"Once the job is opened, it will appear in the top row as an item.",ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
								" Hover over the job for details on it, and left click the item to accept the job (Once you have all the required items).");
	private static TutorialTextBuilder jo1 = new TutorialTextBuilder().add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
						"Complete one more job.");
	
	private static TutorialTextBuilder exp = new TutorialTextBuilder().add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
							"Now it's time to expand your farm!").add(ChatColor.AQUA.toString() + "� " + ChatColor.AQUA.toString() + 
							"First, open the farm menu by typing " + ChatColor.RED.toString() + "/f ", ChatColor.AQUA.toString() + "� " + ChatColor.AQUA.toString() + 
							"From there, click the anvil to build a new plot!");
	private static TutorialTextBuilder npb = new TutorialTextBuilder().add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
					"Your new plot is being built, but it might look a bit empty.").add(ChatColor.AQUA.toString() + "� " + ChatColor.AQUA.toString() + 
					"When the new plot is built, right click on the empty plot to open up the plot selector.", ChatColor.AQUA.toString() + "� " + ChatColor.AQUA.toString() + 
					"From there, click on the enchanting table to build a Town Hall in that plot.");
	
	private static TutorialTextBuilder th1 = new TutorialTextBuilder().add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
					"The Town Hall serves as a base of operations for your farm.").add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
					"As you upgrade your Town Hall, you will be able to build more buildings and set more plots as farm plots!", ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
					"To upgrade it, right click on the enchanting table inside your Town Hall.")
					.add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
							"The Town Hall also has a crafting table for you to craft items.", ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
							"Keep in mind, all recipes are custom, so no vanilla Minecraft recipes will work.")
					.add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
							"Now, save up to buy another plot.");
	private static TutorialTextBuilder researchSuggest = new TutorialTextBuilder().add(Constant.trans("&b� &aNow, set the plot as an &b&lSeed Research Centre."));
	private static TutorialTextBuilder researchBuild = new TutorialTextBuilder().add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
			"You have just built a Research Center!").add(Constant.trans("&b� &aNow,"
					+ " you are able to research new crop types."), Constant.trans("&b� &aTo start,"
							+ "try &cRight Clicking &athe &6Extractor &awith some &cWheat Seeds &ain hand."));
	private static TutorialTextBuilder extractStart = new TutorialTextBuilder().add(Constant.trans("&b� &aNow,"
			+ " you must wait for your seeds to be extracted."), Constant.trans
			("&b� &aThe remaining time is shown above the Extractor.")).add(Constant.trans
					("&b� &6When the extracting is done, you can right click the extractor again to receive a &a&lGrass Essence.")).
			add(Constant.trans("&b� &6You can then extract the &a&lGrass Essence "
					+ "&6to receive a new seed within the &a&lGrass &6family."));
	private static TutorialTextBuilder extractComplete1 = new TutorialTextBuilder().add(Constant.trans
			("&b� &aGreat, now, extract the &lGrass Essence &athat you received.")).add(Constant.trans
					("&b� &aWhen you extract an essence, you will always receive a seed."));
	private static TutorialTextBuilder extractComplete2 = new TutorialTextBuilder().add(Constant.trans
			("&b� &aCongratulations on your new seed!"), Constant.trans("&b� &aTry planting it and see what grows!"),
			Constant.trans("&b� &6Remember, some plants will take more time, and therefore need more than 1 magic dust to fully grow!"));	
//	
//	private static TutorialTextBuilder end = new TutorialTextBuilder().add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
//					"You have completed the tutorial.").add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
//					"You may occasionally see similar text in the future with helpful advice/tips").add(ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
//					"As for future steps � Upgrading your town hall is suggested.",ChatColor.AQUA.toString() + "� " + ChatColor.GREEN.toString() + 
//					"It will allow you to unlock the"+ChatColor.BOLD.toString() + ChatColor.AQUA.toString()+
//					" Seed Research Centre,"+ChatColor.RESET.toString() + ChatColor.GREEN.toString()+
//					" which will allow you to grow more exotic/profitable crops.").add(ChatColor.AQUA.toString() + "� " + 
//					ChatColor.GOLD.toString() + 
//							"Have fun farming and expanding your empire! Don't hesitate to ask anyone if you're not sure what to do next.");
	public static void newPlayer(Player pl, int b){
		if(b == 0){
			progress.put(pl, b);
			add(pl, start.build());
		}else if(b >= 25565){
			return;
		}else{
			progress.put(pl, b);
		}

	}
	public static void setPlugin(Plugin pl){
		plug = pl;
	}
	public static void onLevelUp(Player p, int lv){
		if(lv == 2){
			add(p, lvl1.build());
		}
//		else if(lv == 4){
//			add(p, lvl2.build());
//		}
	}
	public static int getDelProgress(Player p){
		if(progress.containsKey(p)){
			int ret = progress.get(p);
			progress.remove(p);
			return ret;
		}else{
			return 25565;
		}

	}
	public static void onSeedPlant(Player p){
		if(progress.containsKey(p) && progress.get(p) == 0){
			progress.put(p, 1);
			add(p, seedplant.build());
		}
	}
	public static void onMagicDustUse(Player p){
		if(progress.containsKey(p) && progress.get(p) == 1){
			progress.put(p, 2);
			add(p,dustuse.build());
		}
	}
	public static void onWheatHarvest(Player p){
		//Constant.debug("up3");
		if(progress.containsKey(p)){
			int pr = progress.get(p);
			if(pr <= 2){
				//Constant.debug("up1");
				progress.put(p, 3);
				add(p, wh1.build());
			}else if(pr < 6){
				progress.put(p, (pr+1));
				//Constant.debug("up");
			}else if(pr == 6){
				//Constant.debug("up2");
				progress.put(p, (7));
				add(p, wh2.build());
			}

		}
		//Constant.debug("nup");
	}
	public static void onJobComplete(Player p){
		if(progress.containsKey(p)){
			int prog = progress.get(p);
			if(prog == 7){
				progress.put(p, 8);
				add(p, jo1.build());
			}else if(prog == 8){
				progress.put(p, 9);
				add(p, exp.build());
			}

		}
	}
	public static void onNewPlotBuild(Player p){
		if(progress.containsKey(p) && progress.get(p) == 9){
			progress.put(p, 10);
			//Economy.addBal(p.getUniqueId(), 10D);
			add(p, npb.build());
		}else if(progress.containsKey(p) && progress.get(p) == 11){
			progress.put(p, 12);
			add(p, researchSuggest.build());
		}
	}
	public static void onResearchCentreBuild(Player p){
		if(check(p, 12)){
			progress.put(p, 13);
			add(p, researchBuild.build());
		}
	}
	private static boolean check(Player p, int x){
		return progress.containsKey(p) && progress.get(p) == x;
	}
	public static void onExtractorStart(Player p, Resource s){
		if(check(p, 13)){
			progress.put(p, 14);
			add(p, extractStart.build());
		}
	}
	public static void onShopBuild(Player p) {
		//TODO
	}
	public static void onExtractorFinish(Player p, Resource s){
		if(check(p, 14) && s == Resource.WHEAT_SEEDS){
			progress.put(p, 15);
			add(p, extractComplete1.build());
		}else if(check(p, 15) && s == Resource.GRASS_ESSENCE){
			progress.put(p, 16);
			add(p, extractComplete2.build());
		}
	}	
	public static void onTownHallBuild(Player p){
		if(progress.containsKey(p) && progress.get(p) == 10){
			progress.put(p, 11);
			//Economy.addBal(p.getUniqueId(), 10D);
			add(p, th1.build());
		}
	}
	public static void onFarmPlotSet(Player p){
//		if(progress.containsKey(p) && progress.get(p) == 11){
//			progress.put(p, 12);
//			add(p, end.build());
//		}
	}
	public static void add(Player pl, Queue<String[]> stuff){
		if(task == null && plug != null){
			task = new BukkitRunnable(){

				@Override
				public void run() {
					for(Player p: list.keySet()){
						String[] nex = list.get(p).poll();
						if(nex != null && p.isOnline()){
							display(p, nex);
							p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3F, 1F);
						}else{
							list.remove(p);
						}
					}
				}
				
			};
			task.runTaskTimerAsynchronously(plug, 0L, 100L);
		}
		list.put(pl, stuff);
	}
	public static void disable(){
		if(task != null){
			task.cancel();
		}

	}
	private static void display(Player pl, String[] text){
		for(int x = 0; x < 2; x++){
			pl.sendMessage(" ");
		}
		pl.sendMessage(ChatColor.STRIKETHROUGH.toString() + ChatColor.RED.toString() + "-----------------------------------");

		if(text.length < 4){
			pl.sendMessage(" ");
		}
		if(text.length == 1){
			pl.sendMessage(" ");
		}
		for(String s: text){
			pl.sendMessage(s);
		}
		if(text.length < 3){
			pl.sendMessage(" ");
		}
		if(text.length < 5){
			pl.sendMessage(" ");
		}
		pl.sendMessage(ChatColor.STRIKETHROUGH.toString() + ChatColor.RED.toString() + "-----------------------------------");
	
	}
}
