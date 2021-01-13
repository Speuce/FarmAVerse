package com.speuce.farmtopia.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.farm.Tutorial;
import com.speuce.farmtopia.main.FarmTopia;
import com.speuce.farmtopia.resources.Resource;
import com.speuce.farmtopia.util.Economy;

public class JobManager implements CommandExecutor, Listener{
	
	private FarmTopia main;
	private Map<Player, List<Job>> jobs;
	private Map<Player, Inventory> open;
	private Map<Player, Byte> unopened;
	private int count = 0;
	private final int maxJobs = 10;
	private BukkitRunnable update;
	private Random rr = new Random();
	public JobManager(FarmTopia main){
		this.main = main;
		this.open = new HashMap<Player, Inventory>();
		this.jobs = new HashMap<Player, List<Job>>();
		this.unopened = new HashMap<Player, Byte>();
		this.update = this.getUpdater();
		this.update.runTaskTimerAsynchronously(main, 100L, 100L);
		main.getServer().getPluginManager().registerEvents(this, main);
		main.getCommand("job").setExecutor(this);
		main.getCommand("givejob").setExecutor(this);
	}
	public void disable(){
		this.update.cancel();
	}
	public static byte[] newPlayer(){
		byte[] ret = new byte[2];
		ret[0] = Constant.currentProtocol;
		ret[1] = 10;
		return ret;
		
	}
	public void loadData(Player pl, byte[] dat){
		if(dat.length < 2){
			this.jobs.put(pl, new ArrayList<Job>());
		}else{
			int protocol = (int)dat[0];
			byte unopened = dat[1];
			this.unopened.put(pl, unopened);
			int bytes = JobManager.getBytes(protocol);
			List<Job> jobs = new ArrayList<Job>();
			if(dat.length > 2){
				for(int i = 2; i < dat.length; i+=bytes){
					jobs.add(deserialize(Arrays.copyOfRange(dat, i, i+bytes), protocol));
				}
			}

			this.jobs.put(pl, jobs);
		}
	}
	public byte[] saveData(Player pl){
		List<Job> jo = this.jobs.get(pl);
		byte[] ret = new byte[2 + (getBytes(Constant.currentProtocol) * jo.size())];
		ret[0] = (byte) Constant.currentProtocol;
		if(this.unopened.containsKey(pl)){
			ret[1] = this.unopened.get(pl);
			this.unopened.remove(pl);
		}else{
			ret[1] = 0;
		}
		int pointer = 2;
		for(Job j: jo){
			byte[] ins = JobManager.serialize(j);
			for(int x = 0; x < ins.length; x++){
				ret[pointer+x] = ins[x];
			}
			pointer+=ins.length;
		}
		this.jobs.remove(pl);
		return ret;
	}
	private BukkitRunnable getUpdater(){
		return new BukkitRunnable(){

			@Override
			public void run() {
				for(Map.Entry<Player, Inventory> val: open.entrySet()){
					checkJobs(val.getKey());
					updateInv(val.getKey(), val.getValue());
					val.getKey().updateInventory();
				}
				count++;
				if(count > 20){
					count = 0;
					for(Player p: Bukkit.getOnlinePlayers()){
						Byte b = unopened.get(p);
						if(rr.nextInt(2)==0 && b < maxJobs){
							p.sendMessage(ChatColor.GREEN.toString() + "You got a new Job!");
							unopened.put(p, (byte) (b+1));
							b++;
						}
						if(b > 0){
							if(b == maxJobs){
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have &e" + unopened.get(p) +"&7 (MAX) "+ "&cUnopened Jobs!"));
							}else{
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have &e" + unopened.get(p) + " &cUnopened Job(s)!"));
							}
							
						}
					}
				}
			}
			
		};
	}
	private void updateInv(Player pl, Inventory i){
		int x = 0;
		for(Job j: jobs.get(pl)){
			i.setItem(x, j.getDisplay());
			x++;
		}
		while(x < 20){
			i.setItem(x, null);
			x++;
		}
	}
	private void checkJobs(Player pl){
		if(this.jobs.containsKey(pl)){
			List<Job> j = this.jobs.get(pl);
			for(Job l: j){
				if(l.getExpiry() < System.currentTimeMillis()){
					j.remove(l);
				}
			}
		}
	}
	private void openInv(Player p){
		Inventory i = Bukkit.createInventory(null, 27, Constant.jobInvName);
		this.open.put(p, i);
		i.setItem(26, this.getNewJobItem(this.unopened.get(p)));
		p.openInventory(i);
		this.updateInv(p, i);
	}
	public void giveJob(Player pl, byte level){
		if(this.jobs.containsKey(pl)){
			this.jobs.get(pl).add(this.getNewJob(level, 24L));
		}
	}
	private Job getNewJob(byte level, Long hours){
		Random r = new Random();
		return generateJob(System.currentTimeMillis() + (hours*3600*1000), level, (short) r.nextInt(Short.MAX_VALUE + 1));
	}
	private ItemStack getNewJobItem(byte amt){
		ItemStack ret = new ItemStack(Material.BOOK, 1);
		ItemMeta retm = ret.getItemMeta();
		List<String> lore = new ArrayList<String>();
		if(amt > 0){
			if(amt >= maxJobs){
				retm.setDisplayName(ChatColor.GREEN.toString() + amt+ ChatColor.GRAY.toString() + " (MAX)"+ChatColor.AQUA.toString() + " Unopened Jobs");
			}else{
				retm.setDisplayName(ChatColor.GREEN.toString() + amt+ ChatColor.AQUA.toString() + " Unopened Jobs");
			}

			lore.add(" ");
			lore.add(ChatColor.RED.toString() + "Click to open!");
			retm.setLore(lore);
		}else{
			retm.setDisplayName(ChatColor.RED.toString() +"No More new jobs!");
			lore.add(" ");
			lore.add(ChatColor.GOLD.toString() + "To find new jobs, ");
			lore.add(ChatColor.GOLD.toString() + "simply play on the server,");
			lore.add(ChatColor.GOLD.toString() + "they'll slowly come...");
			retm.setLore(lore);
		}
		ret.setItemMeta(retm);
		return ret;
	}
	@EventHandler
	public void onClick(InventoryClickEvent e){
		if(e.getClickedInventory() != null && e.getView().getTitle().equals(Constant.jobInvName)){
			e.setCancelled(true);
			if( e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR){
				return;
			}
			Player pl = (Player)e.getWhoClicked();
			if(e.getSlot() == 26){

				if(this.unopened.get(pl) > 0){
					if(this.jobs.get(pl).size() > 10){
						pl.sendMessage(ChatColor.RED.toString() + "You have already opened the max number of jobs!");
						pl.sendMessage(ChatColor.RED.toString() + "Please complete some, or wait for them to expire.");
						return;
					}
					byte amt = (byte) (this.unopened.get(pl)-1);
					this.unopened.put(pl, amt);
					e.getClickedInventory().setItem(26, this.getNewJobItem(amt));
					this.jobs.get(pl).add(this.getNewJob(this.main.getFarmManager().getLevel(pl), 24L));
					//e.setCurrentItem(null);
					pl.playSound(pl.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2F, 2F);
					this.updateInv(pl, e.getClickedInventory());
					return;
				}
			}else{
				Job j = this.jobs.get(pl).get(e.getSlot());
				//Attempt job complete
				//int[] locs = new int[j.getAmounts().length];
				boolean pass = true;
				for(int x = 0; x < j.getAmounts().length; x++){
					int amt = 0;
					Resource check = j.getRes()[x];
					for(ItemStack s: pl.getInventory().getContents()){
						if(Resource.getByItem(s) == check){
							amt += s.getAmount();
							if(amt >= j.getAmounts()[x]){
								break;
							}
						}
					}
					if(amt < j.getAmounts()[x]){
						pass = false;
						pl.sendMessage(ChatColor.RED.toString() + "You are Missing: " +ChatColor.GOLD.toString() + (j.getAmounts()[x]-amt) + " " + j.getRes()[x].getName());
					}
				}
				if(pass){
					for(int x = 0; x < j.getAmounts().length; x++){
						int amt = j.getAmounts()[x] * 1;
						Resource check = j.getRes()[x];
						//Bukkit.broadcastMessage("Check: " + check.toString());
						for(int y = 0; y < pl.getInventory().getContents().length; y++){
							ItemStack s = pl.getInventory().getContents()[y];
							if(s != null){
								if(Resource.getByItem(s) == check){
									if(s.getAmount() > amt){
										s.setAmount(s.getAmount()-amt);
										amt = 0;
									}else{
									//	Bukkit.broadcastMessage("FInd: " + Resource.getByItem(s).toString());
										amt -= s.getAmount();
									//	Bukkit.broadcastMessage("x: " + y);
										pl.getInventory().setItem(y, null);
									}
									if(amt == 0){
										break;
									}
								}
							}

						}
						//COMPLETE
						Economy.addBal(pl.getUniqueId(), (double) j.getReward());
						Tutorial.onJobComplete(pl);
						this.jobs.get(pl).remove(j);
						
						pl.playSound(pl.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.2F, 1.2F);


						this.updateInv(pl,e.getClickedInventory());			}
				}
			}
		}
	}
	@EventHandler
	public void onClose(InventoryCloseEvent e){
		if(this.open.containsKey((Player)e.getPlayer())){
			this.open.remove((Player)e.getPlayer());
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(this.open.containsKey(e.getPlayer())){
			this.open.remove(e.getPlayer());
		}
	}
	public static Job generateJob(Long expiry, byte level, short seed){
		Random ran = new Random(seed);
		JobLore l;
		Resource[] res;
		int[] amt;
		int rwd = 0;
		double scalar = 1;
		if(level > 15 && ran.nextBoolean()){

			List<Resource> lis = null;
			int check = 0;
			while(lis == null || lis.size() < 2){
				lis = Resource.getUnder((int)level+check);
				check+=3;
			}
			res = new Resource[2];
			res[0] = lis.get(ran.nextInt(lis.size()));
			lis.remove(res[0]);
			res[1] = lis.get(ran.nextInt(lis.size()));
			
			amt = new int[2];
			amt[0] = res[0].getJobAmount().getRandom();
			amt[1] = res[1].getJobAmount().getRandom();
			
			rwd += res[0].getValue() * scalar * (ran.nextDouble()+0.3) * amt[0];
			rwd += res[1].getValue() * scalar * (ran.nextDouble()+0.3) * amt[1];
			
			List<JobLore> ll = JobLore.getBasedOnItems(2);
			l = ll.get(ran.nextInt(ll.size()));
		}else{

			List<Resource> lis = null;
			int check = 0;
			while(lis == null || lis.isEmpty()){
				lis = Resource.getUnder((int)level+check);
				check+=2;
			}
//			for(Resource r: lis){
//				Bukkit.broadcastMessage(r.getName());
//			}
			res = new Resource[1];
			res[0] = lis.get(ran.nextInt(lis.size()));
			lis.remove(res[0]);
			amt = new int[1];
			amt[0] = res[0].getJobAmount().getRandom();
			
			rwd += res[0].getValue() * scalar * (ran.nextDouble()+0.3) * amt[0];
			List<JobLore> ll = JobLore.getBasedOnItems(1);
			l = ll.get(ran.nextInt(ll.size()));
		}
		String name = Constant.names[ran.nextInt(Constant.names.length)];
		return new Job(expiry, l, res, amt, rwd, level, seed, name);
	}
	public static int getBytes(int protocol){
		if(protocol == 2 || protocol == Constant.PROTOCOL_V3){
			return 11;
		}else{
			throw new NullPointerException("Couldn't Find Protocol: " + protocol);
		}
	}
	public static byte[] serialize(Job j){
		byte[] ret = new byte[11];
		Constant.insertIn(ret, Longs.toByteArray(j.getExpiry()), 0);
		ret[8] = j.getLevel();
		Constant.insertIn(ret, Shorts.toByteArray(j.getSeed()), 9);
		return ret;
	}
	public static Job deserialize(byte[] in, int protocol){
		if(protocol == 2 || protocol == Constant.PROTOCOL_V3){
			if(in.length == 11){
				Long exp = Longs.fromByteArray(Arrays.copyOfRange(in, 0, 8));
				byte lvl = in[8];
				short seed = Shorts.fromByteArray(Arrays.copyOfRange(in, 9, 11));
				return JobManager.generateJob(exp, lvl, seed);
			}else{
				throw new IllegalArgumentException("Input array must be of size 11!");
			}
		}else{
			throw new IllegalArgumentException("Protocol Version: " + protocol + " is not supported by JobManager!");
		}
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] arg3) {
		if(cmd.getName().equalsIgnoreCase("job")){
			if(sender instanceof Player){
				Player pl = (Player) sender;
				this.openInv(pl);
				return true;
			}
		}else if(cmd.getName().equalsIgnoreCase("givejob")){
			if(sender.isOp()){
				if(arg3.length == 1){
					Player target = Constant.getPlayer(arg3[0]);
					if(target != null){
						if(this.unopened.containsKey(target)){
							this.unopened.put(target, (byte) (this.unopened.get(target) + 1));
							sender.sendMessage(ChatColor.GREEN.toString() + "Success!");
							return true;
						}
					}else{
						sender.sendMessage(ChatColor.RED.toString() + "Target not found.");
						return true;
					}
				}
			}else{
				sender.sendMessage(ChatColor.RED.toString() + "You ain't Staff!");
				return true;
			}
		}
		return false;
	}

}
