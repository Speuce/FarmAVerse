package com.speuce.farmtopia.shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Shop {
	private List<ShopOffer> offers;
	private int id, lv;
	private Player owner;
	private Inventory inv, invo;
	//private long lastOpened;
	public Shop(int id, int lv, Player owner) {
		offers = new ArrayList<ShopOffer>();
		this.owner = owner;
		this.lv = lv;
		this.id = id;
	}
	public Shop(int id, int lv, Player owner, ArrayList<ShopOffer> offers) {
		this.offers = offers;
		this.owner = owner;
		this.lv = lv;
		this.id = id;
	}
	public void addOffer(ShopOffer i) {
		offers.add(i);
	}
	public void setShopId(int s) {
		if(id >= 0) {
			throw new IllegalArgumentException("Tried to set id to: " + s + " when it was already: " + id);
		}else {
			this.id = s;
		}
	}
	public Player getOwner() {
		return this.owner;
	}
	private Inventory getNew(boolean owner) {
		return Shop.getInv(lv, offers, owner);
	}
	public static Inventory getInv(int lvl, List<ShopOffer> offers, boolean owner) {
		int lv = lvl;
		if(owner) {
			lv++;
		}
		Inventory i = Bukkit.createInventory(null, (lv+1)*9, ChatColor.GREEN.toString() + owner + "'s Shop");
		//Bukkit.createInventory(null, size, title)
		if(offers != null && !offers.isEmpty()) {
			offers.stream().forEach(o -> i.addItem(ShopManager.getItemFrom(o, owner)));
		}
		if(owner) {
			int mx = (lv+1)*9;
			//i.setItem(mx-3, ShopManager.getAdvertiseItem());
			i.setItem(mx-5, ShopManager.getNewOfferItem());
		}
		return i;
	}
	public boolean isFull() {
		if(offers == null) {
			return false;
		}
		int sz = offers.size();
		return (lv+1)*9 <= sz;
	}
	public int getId() {
		return this.id;
	}
	public boolean needsId() {
		return id < 0;
	}
	
	public ShopOffer getOffer(int i) {
		return offers.get(i);
	}
	
	public ShopOffer removeOffer(int i) {
		ShopOffer s = offers.remove(i);
		if(invo != null) {
			invo.setItem(i, null);
			//inv.setItem(i, null);
			invo = getNew(false);
		}
		if(inv != null) {
			inv.setItem(i, null);
			inv = getNew(false);
		}
		if(offers.isEmpty()) {
			this.id = -1;
		}
		return s;
	}
	public ShopOffer sold(int i) {
		ShopOffer s = offers.get(i);
		s.setTaken(true);
		if(inv != null) {
			inv.setItem(i, ShopManager.getItemFrom(s, false));
		}
		if(invo != null) {
			invo.setItem(i, ShopManager.getItemFrom(s, true));
		}
		return s;
	}
	public void check() {
		if(invo.getViewers().isEmpty()) {
			invo = null;
		}
		if(inv.getViewers().isEmpty()) {
			inv = null;
		}
	}
	public Inventory getInv() {
		if(inv == null) {
			inv = getNew(false);
		}
		return inv;
	}
	public Inventory getInvo() {
		if(invo == null) {
			invo = getNew(true);
		}
		return inv;
	}
	public void open(Player pl) {
		if(pl.equals(owner)) {
			pl.openInventory(getInvo());
		}else {
			pl.openInventory(getInv());
		}
	}
	public void levelUp() {
		lv++;
		if(inv != null) {
			inv = getNew(false);
		}
		if(invo != null) {
			invo = getNew(false);
		}
		
	}
}
