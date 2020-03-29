package main.java.com.speuce.farmtopia.util;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class SC {
	private static Map<UUID, ScoreboardWrapper> scoreboards = new HashMap<UUID, ScoreboardWrapper>();
	
	public static void newScoreboard(Player p){
		
		ScoreboardWrapper ne = new ScoreboardWrapper(main.java.com.speuce.farmtopia.util.Constant.SCOREBOARD_NAME);
		ne.addBlankSpace();
		ne.addBlankSpace();
		ne.addBlankSpace();
		ne.addBlankSpace();
		p.setScoreboard(ne.getScoreboard());
		scoreboards.put(p.getUniqueId(), ne);
	}
	public static void logoff(Player p){
		if(scoreboards.containsKey(p.getUniqueId())){
			scoreboards.remove(p.getUniqueId());
		}
	}
	public static void setLine(Player p, int line, String val){
		if(!scoreboards.containsKey(p.getUniqueId())){
			SC.newScoreboard(p);
		}
		scoreboards.get(p.getUniqueId()).setLine(line, val);
	}
	public static void updateMoney(Player p, double balance){
		SC.setLine(p, 1, ChatColor.DARK_GREEN.toString() 
				+ NumberFormat.getCurrencyInstance().format(balance));
	}
}
