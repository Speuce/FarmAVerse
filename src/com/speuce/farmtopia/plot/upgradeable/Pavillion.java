package com.speuce.farmtopia.plot.upgradeable;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.resources.Resource;

public class Pavillion extends Upgradeable implements Listener{
	private List<ItemStack[]> chests;
	private List<Block> locs;
	private int open = -1;
	public Pavillion(Farm f, int lvl){
		this(f, lvl, new ArrayList<ItemStack[]>());
	}
	public Pavillion(Farm f, int lvl, List<ItemStack[]> items) {
		super("Pavillion", new String[]{"pavillion1", "pavillion2", "pavillion3", "pavillion4"}, f, lvl);
		// TODO Auto-generated constructor stub
		//this.chests = new ArrayList<ItemStack[]>();
		this.chests = items;
		this.locs = new ArrayList<Block>();
		Bukkit.getServer().getPluginManager().registerEvents(this, f.getFm().getPlugin());
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onClose(InventoryCloseEvent e){
		if(open >= 0 && e.getPlayer().getUniqueId().equals(this.getFarm().getOwner().getUniqueId())){
			chests.set(open, e.getInventory().getContents());
			open = -1;
			Constant.playsound((Player) e.getPlayer(), Sound.BLOCK_CHEST_CLOSE, 0F);
		}
	}

	@Override
	public void onInteract(PlayerInteractEvent e) {
		e.setCancelled(true);
		if(e.getClickedBlock().getType() == Material.CHEST && !locs.isEmpty()){
			int chestnum = locs.indexOf(e.getClickedBlock());

			Inventory inv = Bukkit.createInventory(e.getPlayer(), 9, "Storage");
			//i.addItem(chests.get(chestnum));
			int x = 0;
			for(ItemStack i: chests.get(chestnum)){
				if(i != null){
					inv.setItem(x, i);
				}
				x++;
			}
			open = chestnum;
			Constant.playsound(e.getPlayer(), Sound.BLOCK_CHEST_OPEN, 0F);
			e.getPlayer().openInventory(inv);
		}else if(e.getClickedBlock().getType() == Material.ENCHANTING_TABLE){
			this.openUpgradeInventory(e.getPlayer());
		}
		
	}
	@Override
	public void onUpgrade(){
		locs.clear();
		chests.add(new ItemStack[9]);
		BukkitRunnable br = new BukkitRunnable(){

			@Override
			public void run() {
				setup();
				
			}
			
			
		};
		br.runTaskLater(this.getFarm().getFm().getPlugin(), 20L);
	}
	@Override
	public void setup(){
		Bukkit.broadcastMessage("Setup");
		for (int x = 0; x < 15; x++) {
			for (int z = 0; z < 15; z++) {
				for (int y = Constant.baseY+1; y <= Constant.baseY + 2; y++) {
					Block b = this.getChunk().getBlock(x, y, z);
					if (this.getChunk().getBlock(x, y, z).getType() == Material.CHEST) {
						if(chests.isEmpty()){
							chests.add(new ItemStack[9]);
						}
						Bukkit.broadcastMessage("found");
						this.locs.add(b);
						if(locs.size() > this.getLv()){
							break;
						}
					}

				}
			}
		}
	}
	@Override
	public byte[] serialize() {
		byte[] ret = new byte[Pavillion.getBytes(Constant.currentProtocol, this.getLv())];
		int count = 0;
		for(int x = 0; x <= this.getLv(); x++){
			ItemStack[] chest = this.chests.get(x);
			for(int y = 0; y <9; y++){
				ret[count] = Resource.getByItem(chest[y]).getId();
				ret[count+1] = (chest[y] == null || ret[count] == Resource.NOTHING.getId()) ? 0: (byte)chest[y].getAmount();
				count+=2;
			}
		}
		//Constant.printOut(ret);
		return ret;
	}
	public static Pavillion deserialize(byte[] in,int protocol, Farm f, int lvl){
		int count = 0;
		List<ItemStack[]> items = new ArrayList<ItemStack[]>();
		//Bukkit.broadcastMessage("thing");
		//Constant.printOut(in);
		for(int x = 0; x <= lvl; x++){
			ItemStack[] add = new ItemStack[9];
			for(int y = 0; y<9; y++){
				byte res = in[count];
				byte amt = in[count+1];
				add[y] = Resource.getById(res).toItemStack(amt);
				count+=2;
			}
			items.add(add);

		}
		return new Pavillion(f, lvl, items);
	}
	@Override
	public int getLevelReqToUpgrade(int next) {
		// TODO Auto-generated method stub
		//return 0;
		if(next == 1){
			return 7;
		}else if(next == 2){
			return 21;
		}else if(next == 3){
			return 33;
		}else{
			return 99;
		}
	}

	@Override
	public int getThLvToUpgrade(int next) {
		if(next == 1){
			return 0;
		}else if(next == 2){
			return 1;
		}else{
			return 2;
		}
	}

	@Override
	public int getCost(int currentlv) {
		if(currentlv == 0){
			return 100;
		}else if(currentlv == 1){
			return 175;
		}else if(currentlv == 2){
			return 250;
		}else{
			return 999;
		}
	}

	public static int getBytes(int protocol, int lvl){
		if(protocol >= 3){
			return (lvl+1)*18;
		}else{
			throw new IllegalArgumentException("Protocol v. " + protocol + " not recongnized.");
		}
	}

}
