package com.speuce.farmtopia.main;

import java.text.NumberFormat;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.speuce.farmtopia.commands.Item;
import com.speuce.farmtopia.craft.CraftingManager;
import com.speuce.farmtopia.farm.FarmManager;
import com.speuce.farmtopia.farm.Tutorial;
import com.speuce.farmtopia.jobs.JobManager;
import com.speuce.farmtopia.plot.BuildQueue;
import com.speuce.farmtopia.resources.Resource;
import com.speuce.farmtopia.shop.ShopManager;
import com.speuce.farmtopia.util.Debug;
import com.speuce.farmtopia.util.Economy;
import com.speuce.schemetic.SchemeticManager;
import com.speuce.sql.SQLManager;

public class FarmTopia extends JavaPlugin implements Listener{
	private static FarmTopia instance = null;
	private SQLManager sql;
	private DebugLevel listeningfor;
	private SchemeticManager schem;
	private FarmManager fm;
	private BukkitRunnable timer1;
	private CraftingManager cm;
	private JobManager jm;
	private ChunkGenerator chunk;
	private ShopManager shop;
	@Override
	public void onEnable(){
//		this.sql = new SQLManager(this);
//		this.stats = new StatsManager(this.sql, this);
//		this.tm = new TransactionManager(this, this.stats);
//		this.ct = new CrateUI(this, this.stats, this.tm);
//		this.chestMan = new ChestsManagers(this.sql, this.stats, this, this.ct, this.tm);
//		this.cosman = new CosmeticManager(this.sql, this.stats, this, this.chestMan);
//		this.getCommand("cosmetic").setExecutor(this.cosman);
//		this.getCommand("fdebug").setExecutor(this);
//		this.listeningfor = DebugLevel.MAJOR;
		//TODO register commands in yml
		instance = this;
		Tutorial.setPlugin(this);
		this.sql = new SQLManager(this);
		Economy.setPlugin(this);
		this.schem = new SchemeticManager(this, 100);
		this.jm = new JobManager(this);
		this.fm = new FarmManager(this, sql, this.jm);
		chunk = new ChunkGenerator(){
			
		};
		this.getCommand("item").setExecutor(new Item());
		this.getCommand("balance").setExecutor(this);
		this.getCommand("eco").setExecutor(this);
		this.getCommand("debug").setExecutor(this);
		this.getCommand("gm").setExecutor(this);
		this.getServer().getPluginManager().registerEvents(this, this);
		this.timer1 = BuildQueue.start();
		this.timer1.runTaskTimerAsynchronously(this, 50L, 5L);
		this.cm = new CraftingManager(this);
		this.shop = new ShopManager(sql, this);
		
	}
	public static FarmTopia getFarmTopia(){
		return instance;
	}
	public ShopManager getShop() {
		return this.shop;
	}
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return chunk;
    }
	public CraftingManager getCraftingManager(){
		return this.cm;
	}
	public FarmManager getFarmManager(){
		return this.fm;
	}
	@Override
	public void onDisable(){
		this.timer1.cancel();
		this.jm.disable();
		//this.cosman.disable();
		//this.chestMan.disable();
	}
	public void debug(DebugLevel d, String s){
		if(this.listeningfor.getValue() <= d.getValue()){
			Bukkit.broadcastMessage(s);
		}
	}
	public void spamDebug(String s){
		this.debug(DebugLevel.SPAM, s);
	}
	public void semiDebug(String s){
		this.debug(DebugLevel.SEMI, s);
	}
	public SchemeticManager getSchem(){
		return this.schem;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String sk, String[] args){
	if(cmd.getName().equalsIgnoreCase("balance")){
		if(sender instanceof Player){
			Player p = (Player) sender;
			if(Economy.hasInfo(p.getUniqueId())){
				NumberFormat.getNumberInstance();
				p.sendMessage(ChatColor.GOLD.toString() + "Balance: " + ChatColor.DARK_GREEN.toString()
				+ NumberFormat.getCurrencyInstance().format(Economy.getBalance(p.getUniqueId())));
				return true;
			}else{
				p.sendMessage(ChatColor.RED.toString() + "Whoa, slow down there partner! The server hasn't even loaded your dollar bills yet!");
				return true;
			}
		}
	}else if(cmd.getName().equalsIgnoreCase("debug")){
		if(sender.isOp()) {
			if(args.length == 0) {
				if(Debug.getInstance().toggleType(Debug.Type.GENERAL)) {
					sender.sendMessage(ChatColor.GREEN + "Toggled General Debugging ON");
					return true;
				}else {
					sender.sendMessage(ChatColor.RED + "Toggled General Debugging OFF");
					return true;
				}
			}else {
				if(args[0].equalsIgnoreCase("log")) {
					if(Debug.getInstance().isLogging()) {
						Debug.getInstance().setLog(false);
						sender.sendMessage(ChatColor.RED + "Toggled Debug Logging OFF");
						return true;
					}else {
						Debug.getInstance().setLog(true);
						sender.sendMessage(ChatColor.GREEN + "Toggled Debug Logging ON");
						return true;
					}
				}else {
					Debug.Type t = Debug.Type.match(args[0]);
					if(t == null) {
						sender.sendMessage(ChatColor.RED + "Debug Type: " + t + " couldn't be found!");
						StringBuilder s = new StringBuilder(" ");
						Stream.of(Debug.Type.values()).forEach(i -> s.append(i.toString()));
						sender.sendMessage(ChatColor.RED + "Valid Types: " + s.toString());
						return true;
					}else {
						if(Debug.getInstance().toggleType(t)) {
							sender.sendMessage(ChatColor.GREEN + "Toggled Debug for " + t.toString() + " ON");
							return true;
						}else {
							sender.sendMessage(ChatColor.RED + "Toggled Debug for " + t.toString() + " OFF");
							return true;
						}
					}
				}
			}
		}
	}else if(cmd.getName().equalsIgnoreCase("gm")){
		if(sender.isOp() && sender instanceof Player) {
			Player pl = (Player) sender;
			if(args.length == 2) {
				String name = args[1];
				Player p2 = Bukkit.getPlayer(name);
				if(p2 == null) {
					sender.sendMessage(ChatColor.RED + "Player: " + name + " couldn't be found!");
					return true;
				}
				pl = p2;
			}
			if(args[0].equalsIgnoreCase("c")) {
				pl.setGameMode(GameMode.CREATIVE);
				return true;
			}else if(args[0].equalsIgnoreCase("s")) {
				pl.setGameMode(GameMode.SURVIVAL);
				return true;
			}else if(args[0].equalsIgnoreCase("a")) {
				pl.setGameMode(GameMode.ADVENTURE);
				return true;
			}else if(args[0].equalsIgnoreCase("sp")) {
				pl.setGameMode(GameMode.SPECTATOR);
				return true;
			}
		}
	}else if(cmd.getName().equalsIgnoreCase("eco")){
		if(sender.isOp() && args.length ==3){
			Player p = null;
			for(Player plf: Bukkit.getOnlinePlayers()){
				if(plf.getName().equalsIgnoreCase(args[1])){
					p = plf;
					break;
				}
			}
			if(p == null){
				sender.sendMessage(ChatColor.RED.toString() + "Couldn't find player: " + args[1]);
				return true;
			}
			Double d = 0D;
			try{
				d = Double.parseDouble(args[2]);
			}catch(NumberFormatException e){
				sender.sendMessage(ChatColor.RED.toString() + "Couldn't parse amount: " + args[2]);
				return true;
			}
			
			if(args[0].equalsIgnoreCase("add")){
				Economy.addBal(p.getUniqueId(), d);
				sender.sendMessage(ChatColor.GREEN.toString() + "Success!");
				return true;
			}else if(args[0].equalsIgnoreCase("remove")){
				Economy.subtractBal(p.getUniqueId(), d);
				sender.sendMessage(ChatColor.GREEN.toString() + "Success!");
				return true;
			}else if(args[0].equalsIgnoreCase("set")){
				Economy.setBalance(p.getUniqueId(), d);
				sender.sendMessage(ChatColor.GREEN.toString() + "Success!");
				return true;
			}
		}
	}else if(cmd.getName().equalsIgnoreCase("fdebug")){
			if(sender.isOp()){
				if(args.length == 1){
					if(args[0].equalsIgnoreCase("spam")){
						this.listeningfor = DebugLevel.SPAM;
						sender.sendMessage(ChatColor.GREEN + "Success!");
						return true;
					}else if(args[0].equalsIgnoreCase("semi")){
						this.listeningfor = DebugLevel.SEMI;
						sender.sendMessage(ChatColor.GREEN + "Success!");
						return true;
					}else if(args[0].equalsIgnoreCase("maj")){
						this.listeningfor = DebugLevel.MAJOR;
						sender.sendMessage(ChatColor.GREEN + "Success!");
						return true;
					}else{
						sender.sendMessage(ChatColor.RED + "/fdebug <spam,semi,maj>");
						return true;
					}
					
					
				}else{
					sender.sendMessage(ChatColor.RED + "/fdebug <spam,semi,maj>");
					return true;
				}
			}else{
				sender.sendMessage(ChatColor.RED + "No Perms!");
				return true;
			}
		}else if(cmd.getName().equalsIgnoreCase("md")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				if(!p.isOp()){
					sender.sendMessage(ChatColor.RED + "No Perms!");
					return true;
				}
			}
			if(args.length != 1){
				sender.sendMessage(ChatColor.RED + "/md <player|all>");
				return true;
			}
			if(args[0].equalsIgnoreCase("all")){
				for(Player p: Bukkit.getOnlinePlayers()){
					p.getInventory().addItem(Resource.MAGIC_DUST.toItemStack(1));
				}
				sender.sendMessage(ChatColor.GREEN + "Done!");
				return true;
			}else{
				for(Player p: Bukkit.getOnlinePlayers()){
					if(p.getName().equalsIgnoreCase(args[0])){
						p.getInventory().addItem(Resource.MAGIC_DUST.toItemStack(1));
						sender.sendMessage(ChatColor.GREEN + "Done!");
						return true;
					}
				}
				sender.sendMessage(ChatColor.RED + "Player: " + args[0] + "not found!");
				return true;
			}
		}
		return false;
	}
}
