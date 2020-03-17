package main.java.com.speuce.farmtopia.util;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Economy {
	private static Map<UUID, Double> balances = new HashMap<UUID, Double>();
	private static Plugin pl;
	public static void setPlugin(Plugin p){
		pl = p;
	}
	public static Double getBalance(UUID d){
		return balances.get(d);
	}
	public static Double getRemoveBalance(UUID d){
		Double va = balances.get(d);
		balances.remove(d);
		return va;
	}
	public static void update(UUID d){
		if(balances.containsKey(d)){
			new BukkitRunnable(){

				@Override
				public void run() {
					SC.updateMoney(Bukkit.getPlayer(d), balances.get(d));	
				}
				
			}.runTask(pl);

		}
	}
	public static void setBalance(UUID d, Double val){
		balances.put(d, val);
		SC.updateMoney(Bukkit.getPlayer(d), val);
	}
	public static void subtractBal(UUID d, Double sub){
		Double ne = balances.get(d) -sub;
		balances.put(d,ne);
		SC.updateMoney(Bukkit.getPlayer(d), ne);
		Player pl = Bukkit.getPlayer(d);
		if(pl != null && pl.isOnline()){
			pl.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString() + "+ " + NumberFormat.getCurrencyInstance().format(sub));
		}
		
	}
	public static void addBal(UUID d, Double add){
		Double ne = balances.get(d) + add;
		Player pl = Bukkit.getPlayer(d);
		if(pl != null && pl.isOnline()){
			pl.sendMessage(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD.toString() + "+ " + NumberFormat.getCurrencyInstance().format(add));
		}
		balances.put(d, ne);
		SC.updateMoney(Bukkit.getPlayer(d), ne);
	}
	public static boolean hasInfo(UUID d){
		return balances.containsKey(d);
	}
	public static boolean hasEnough(UUID d, Double amt){
		return Economy.getBalance(d) >= amt;
	}
	public static String remainder(UUID d, Double amt){
		return NumberFormat.getCurrencyInstance().format(Economy.getBalance(d) - amt);
	}
}
