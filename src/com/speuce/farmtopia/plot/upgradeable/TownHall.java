package com.speuce.farmtopia.plot.upgradeable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.plot.Plot;

public class TownHall extends Upgradeable{

	public TownHall(Farm f, int lvl) {
		super("Town Hall", new String[]{"th1", "th2", "th3"}, f, lvl);
	}

	@Override
	public byte[] serialize() {
		return new byte[]{(byte)this.getLv()};
	}

	@Override
	public int getCost(int currentlv) {
		if(currentlv == 0){
			return 50;
		}else if(currentlv == 1){
			return 550;
		}else if(currentlv == 2){
			return 1850;
		}else if(currentlv == 3){
			return 4333;
		}else{
			return 99999999;
		}
	}

	@Override
	public void onInteractOwner(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getType() == Material.ENCHANTING_TABLE){
				this.openUpgradeInventory(e.getPlayer());
				e.setCancelled(true);
			}else if(e.getClickedBlock().getType() == Material.CRAFTING_TABLE){
				if(this.getLv() == 0){
					this.getFarm().getFm().getPlugin().getCraftingManager().openCrafter(0, e.getPlayer());
				}else if(this.getLv() == 1){
					this.getFarm().getFm().getPlugin().getCraftingManager().openCrafter(1, e.getPlayer());
				}else if(this.getLv() == 2){
					this.getFarm().getFm().getPlugin().getCraftingManager().openCrafter(2, e.getPlayer());
				}

				e.setCancelled(true);
			}else if(e.getClickedBlock().getType() == Material.BREWING_STAND){
				Plot pl = this.getFarm().getFirstPlot(ResearchCentre.class);
				if(pl != null){
					e.setCancelled(true);
					e.getPlayer().teleport(pl.getChunk().getBlock(8, Constant.baseY+1, 5).getLocation().add(0, 0, 0.5));
				}else{
					e.getPlayer().sendMessage(ChatColor.RED.toString() + "You can use this once you have build a Seed Research Centre!");
					e.setCancelled(true);
				}
			}else if(e.getClickedBlock().getType() == Material.ENDER_CHEST){
				e.setCancelled(true);
			}
		}
		
	}
	public static TownHall deserialize(byte[] data, int protocol, Farm f){
		if(protocol == 2 || protocol == Constant.PROTOCOL_V3){
			return new TownHall(f,(int)data[0]);
		}else{
			return null;
		}
	}
	public static int getBytes(int protocol){
		if(protocol == 2 || protocol == Constant.PROTOCOL_V3){
			return 1;
		}else{
			return 1;
		}
	}
//	public static byte getLevelToUpgrade(int current){
//		if(current == 0){
//			return 0;
//		}else if(current == 1){
//			return 4;
//		}else if(current == 2){
//			return 11;
//		}else if(current == 3){
//			return 23;
//		}else{
//			return (byte) 254;
//		}
//	}

	@Override
	public int getLevelReqToUpgrade(int next) {
		if(next == 0){
			return 0;
		}else if(next == 1){
			return 3;
		}else if(next == 2){
			return 13;
		}else{
			return 1000;
		}
	}

	@Override
	public int getThLvToUpgrade(int next) {
		return -1;
	}



}
