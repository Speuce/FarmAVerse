package com.speuce.farmtopia.craft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.craft.crafters.Craft2x2;
import com.speuce.farmtopia.craft.crafters.Craft3x3;
import com.speuce.farmtopia.craft.crafters.Craft4x4;
import com.speuce.farmtopia.main.FarmTopia;
import com.speuce.farmtopia.resources.Resource;


public class CraftingManager implements Listener, CommandExecutor{
	private FarmTopia main;
	private List<Crafter> crafters;
	private Map<UUID, Recipe> currentRecipes;
	public CraftingManager(FarmTopia main){
		main.getServer().getPluginManager().registerEvents(this, main);
		this.main = main;
		this.crafters = new ArrayList<Crafter>();
		this.registerCrafters();
		main.getCommand("crft").setExecutor(this);
		this.currentRecipes = new HashMap<UUID, Recipe>();
	}
	private void registerCrafters(){
		this.crafters.add(new Craft2x2());
		this.crafters.add(new Craft3x3());
		this.crafters.add(new Craft4x4());

	}
	public void openCrafter(int crafter, Player pl){
		Crafter c = crafters.get(crafter);
		if(c != null){
			Inventory inv = Bukkit.createInventory(null, c.getInvSize(), c.getName());
			//Bukkit.broadcastMessage(c.getBase() + "");
			inv.setItem(c.getBase(), c.getItem());
			pl.openInventory(inv);
		}else{
			throw new NullPointerException("Couldn't find crafter: " + crafter);
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(InventoryClickEvent e){
		if(e.getView().getTitle().startsWith("crft")){
			if(e.isShiftClick()){
					if(!e.getView().getTitle().startsWith("crft")){
						e.setCancelled(true);
					}else{ 
						if(e.getSlot() % 9 == 0){
							e.setCancelled(true);
						}else{
							Crafter c = this.getCrafter(e.getView().getTitle());
							if(c != null){
								if(e.getSlot() == c.getOutput() && this.currentRecipes.containsKey(e.getWhoClicked().getUniqueId()) && this.currentRecipes.get(e.getWhoClicked().getUniqueId()) != null){
									Recipe r = this.currentRecipes.get(e.getWhoClicked().getUniqueId());
									int i = e.getWhoClicked().getInventory().firstEmpty();
									e.setCancelled(true);
									if(i >= 0){
										int amt = 0;
										while(this.doOn(e.getClickedInventory(), r, c) && amt <= 64){
											amt += r.getAmount();
										}
										e.getWhoClicked().getInventory().addItem(r.getResult().toItemStack(amt));
									}
								}
								this.checkRecipes(e.getView().getTopInventory(), c, e.getWhoClicked().getUniqueId());						
							}
						
						}
					}
			}else if(e.getClickedInventory() != null && e.getView().getTitle().startsWith("crft")){
				Crafter c = this.getCrafter(e.getView().getTitle());
				if(c != null){
					if(!c.getValidSlots().contains(e.getSlot())){
						if(e.getSlot() == c.getOutput()){
							if(e.getCurrentItem() != null && this.currentRecipes.containsKey(e.getWhoClicked().getUniqueId())){
								Recipe r = this.currentRecipes.get(e.getWhoClicked().getUniqueId());
								if(r != null){
									if(e.getCursor().getType() == Material.AIR|| (e.getCursor().getType() == r.getResult().getMat() && e.getCursor().getAmount() + r.getAmount() <= 64)){
										if(this.doOn(e.getClickedInventory(), r, c)){
											e.setCancelled(true);
											if(e.getCursor().getType() != Material.AIR){
												e.setCursor(r.getResult().toItemStack(r.getAmount() + e.getCursor().getAmount()));
											}else{
												e.setCursor(r.getResult().toItemStack(r.getAmount()));
											}

											e.setCurrentItem(null);
											//Bukkit.broadcastMessage("YAY");
											((Player)e.getWhoClicked()).updateInventory();
											this.checkRecipes(e.getClickedInventory(), c, e.getWhoClicked().getUniqueId());
											return;
										}
									}else{
										//Bukkit.broadcastMessage(e.getCursor() + "");
										e.setCancelled(true);
									}
			
								}else{
									e.setCancelled(true);
								}
								
							}else{
								e.setCancelled(true);
							}
						}else{
							e.setCancelled(true);
							e.setCursor(e.getCursor());
							e.setCurrentItem(e.getCurrentItem());
						}
					}else{
						//Bukkit.broadcastMessage("craft");
						this.checkRecipes(e.getClickedInventory(), c, e.getWhoClicked().getUniqueId());
					}
				}else{
					Bukkit.broadcastMessage(ChatColor.RED.toString() + "Crafter: " + e.getView().getTitle() + " is not reconized!");
					return;
				}

			}
		}

	}
	private boolean doOn(Inventory i, Recipe rec, Crafter c){
		int lowx = getFirstTaken(i, c, true, true);
		int lowy = getFirstTaken(i, c, false, true);
		if(lowx == -1 || lowy == -1){
			return false;
		}
		int low = (lowx < lowy) ? lowx: lowy;
		boolean good = true;
		for(int y = 0; y < rec.getShape().length; y++){
			for(int x = 0; x < rec.getShape()[0].length; x++){
				if(rec.getShape()[y][x].getMat() != Material.AIR){
					int slot = low + (y*9) + x;
					ItemStack r = i.getItem(slot);
					//Bukkit.broadcastMessage(r.getType() + ":" + slot);
					if(rec.getShape()[y][x].getMat() == r.getType()){
						if(r.getAmount() > 1){
							r.setAmount(r.getAmount()-1);
							i.setItem(slot, r);
						}else{
							i.setItem(slot, null);
						}
					}else{
						return false;
					}
				}
			}
		}
		return good;
	}
	private void checkRecipes(Inventory i, Crafter c, UUID uuid){
		BukkitRunnable br = new BukkitRunnable(){

			@Override
			public void run() {
				//Bukkit.broadcastMessage("craft2");
				int lowx = getFirstTaken(i, c, true, true);
				if(lowx < 0){
					i.setItem(c.getOutput(), null);
					return;
				}
				int lowy = getFirstTaken(i, c, false, true);
				int highx = getFirstTaken(i, c, true, false);
				int highy = getFirstTaken(i, c, false, false);
				//Bukkit.broadcastMessage(lowx + ":" + lowy + ":" + highx + ":" + highy);
				Resource[][] val = getBetween(i, lowx, lowy, highx, highy);
			//	Bukkit.broadcastMessage(getString(val));
				Recipe rec = Recipe.getRecipe(val, 100);
				Player pl = Bukkit.getPlayer(uuid);
				if(rec == null){
					i.setItem(c.getOutput(), null);
				}
				else if(main.getFarmManager().getFarm(pl).getLevel() >= rec.getMinLv()){
					currentRecipes.put(uuid, rec);
					i.setItem(c.getOutput(), rec.getResult().toItemStack(rec.getAmount()));
				}

				//Bukkit.broadcastMessage(i + "");
				
			}
			
		};
		br.runTask(this.main);
	}
	private Resource[][] getBetween(Inventory i, int lx, int ly, int hx, int hy){
		if((hx/9) > (hy/9)){
			int tx = hx;
			hx = hy;
			hy = tx;
		}
		if((ly/9) > (lx/9)){
			int val = ly;
			ly = lx;
			lx = val%9;
		}
		int diffx = ((hx%9)-(lx%9))+1;
		int diffy = ((hy/9)-(ly/9))+1;
		//Bukkit.broadcastMessage("differnces: " + diffx + ":" + diffy);
		Resource[][] ret = new Resource[diffy][diffx];
		for(int y = 0; y < diffy; y++){
			Resource[] add = new Resource[diffx];
			for(int x = 0; x < diffx; x++){
				//Bukkit.broadcastMessage("find item: " + (lx+(y*9)+x));
				add[x] = Resource.getByItem(i.getItem(lx+(y*9)+x));
			}
			ret[y] = add;
		}
		return ret;
	}
	public static String getString(Resource[][] res){
		StringBuilder ret = new StringBuilder();
		for(Resource[] r: res){
			ret.append("{");
			for(Resource rs: r){
				ret.append(rs.toString() + ";");
			}
			ret.append("}");
		}
		return ret.toString();
	}
	private int getFirstTaken(Inventory i, Crafter c, boolean x, boolean inc){
		int place = inc ? 0 : i.getSize()-1;
		int rows = i.getSize()/9;
		int iterations = 0;
		int row = 0;
		while(place >= 0 && place < i.getSize()){
			iterations++;
			if(iterations > i.getSize()){
				Bukkit.broadcastMessage("ended due to overflow!");
				break;
			}
			if(c.getValidSlots().contains(place)){
				if(i.getItem(place) != null){
					return place;
				}
			}
			if(x){
				if(inc){
					place++;
					continue;
				}else{
					place--;
					continue;
				}
			}else{
				if(row >= 8){
					break;
				}
				if(inc){
					if(place/9 < rows-1){
						place+=9;

						continue;
					}else{
						place++;
						place -= (rows-1)*9;

						row++;
						continue;
					}
				}else{
					if(place/9 > 0){
						place-=9;
					//	Bukkit.broadcastMessage("sub nine: " + place);
						continue;
					}else{
						place += (rows-1)*9;
						place--;
						//Bukkit.broadcastMessage("add + sub1: " + place);
						row++;
						continue;
					}
				}
			}
		}
		return -1;
	}
	@EventHandler
	public void onClose(InventoryCloseEvent e){
		if(this.currentRecipes.containsKey(e.getPlayer().getUniqueId())){
			this.currentRecipes.remove(e.getPlayer().getUniqueId());
		}
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(this.currentRecipes.containsKey(e.getPlayer().getUniqueId())){
			this.currentRecipes.remove(e.getPlayer().getUniqueId());
		}
	}
	private Crafter getCrafter(String name){
		for(Crafter c: this.crafters){
			if(c.getName().equals(name)){
				return c;
			}
		}
		return null;
	}
	@EventHandler
	public void onDrag(InventoryDragEvent e){
		if(e.getView().getTitle().contains("crft")){
			Crafter c = this.getCrafter(e.getView().getTitle());
			if(c != null){
				for(int slot: e.getInventorySlots()){
					if(!c.getValidSlots().contains(slot)){
							e.setCancelled(true);
							((Player)e.getWhoClicked()).updateInventory();

					}else{
						this.checkRecipes(e.getInventory(), c, e.getWhoClicked().getUniqueId());
					}
				}
			}else{
				Bukkit.broadcastMessage(ChatColor.RED.toString() + "Crafter: " + e.getView().getTitle() + " is not reconized!");
				return;
			}

		}else if(e.getView().getTitle().equals(Constant.seedExtractorName)){
			e.setCancelled(true);
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(sender instanceof Player){
			Player pl = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("crft") && pl.isOp()){
				if(args.length == 1){
					try{
						Integer i = Integer.parseInt(args[0]);
						if(i >= crafters.size()){
							pl.sendMessage(ChatColor.RED.toString() + "Could not find crafter: " + i);
							return true;
						}
						Crafter c = crafters.get(i);
						if(c != null){
							Inventory inv = Bukkit.createInventory(null, c.getInvSize(), c.getName());
							inv.setItem(c.getBase(), c.getItem());
							pl.openInventory(inv);
							return true;
						}else{
							pl.sendMessage(ChatColor.RED.toString() + "Could not find crafter: " + i);
							return true;
						}
					}catch(NumberFormatException e){
						pl.sendMessage(ChatColor.RED.toString() + "not a number!");
						return true;
					}
				}
				
			}
		}
		return false;
	}
}
