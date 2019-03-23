package com.speuce.farmtopia.plot.upgradeable;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.main.FarmTopia;

public class Shop extends Upgradeable{
	int id;
	public Shop(Farm f, int lvl, int id) {
		super("Store", new String[] {"sh1", "sh2", "sh3"}, f, lvl);
		this.id = id;
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] serialize() {
		 byte [] bytes = ByteBuffer.allocate(4).putInt(id).array();
		 byte[] ret = new byte[5];
		 ret[0] = (byte) this.getLv();
		 ret[1] = bytes[0];
		 ret[2] = bytes[1];
		 ret[3] = bytes[2];
		 ret[4] = bytes[03];
		 return ret;
	}
	public static Shop deserialize(Farm f, byte[] b) {
		//return ByteBuffer.wrap(System)
		int lv = b[0];
		int id = ByteBuffer.wrap(Arrays.copyOfRange(b, 1, 5)).getInt();
		return new Shop(f, lv, id);
	}
	public void assignId(int id) {
		this.id = id;
	}
	public boolean idAssigned() {
		return this.id > -1;
	}
	@Override
	public int getLevelReqToUpgrade(int next) {
		return 0;
	}

	@Override
	public int getThLvToUpgrade(int next) {
		if(next == 1) {
			return 1;
		}else if(next == 2) {
			return 2;
		}else {
			return 999;
		}
		
	}
	public static int getBytes(int protocol){
		if(protocol == Constant.PROTOCOL_V3){
			return 5;
		}else {
			return 5;
		}
	}
	@Override
	public int getCost(int currentlv) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onInteractOwner(PlayerInteractEvent e) {
		 if (e.getClickedBlock().getType() == Material.ENCHANTING_TABLE) {
				e.setCancelled(true);
				this.openUpgradeInventory(e.getPlayer());
		} 
	}
	@Override
	public void onEntityInteract(PlayerInteractEntityEvent e) {
		if(e.getRightClicked().getType() == EntityType.VILLAGER) {
			if(this.getFarm().getOwner().equals(e.getPlayer())) {
				FarmTopia.getFarmTopia().getShop().openShop(p, id);
			}else {
				
			}
		}
	}

}
