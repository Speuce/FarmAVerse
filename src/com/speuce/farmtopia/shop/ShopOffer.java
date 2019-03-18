package com.speuce.farmtopia.shop;

import com.speuce.farmtopia.resources.Resource;

public class ShopOffer {
	private Resource item;
	private short amount;
	private double price;
	private Long created;
	private int shopID;
	private boolean taken;
	
	public ShopOffer(Resource item, short amount, double price, Long created, int shopID, boolean taken) {
		this.item = item;
		this.amount = amount;
		this.price = price;
		this.created = created;
		this.shopID = shopID;
		this.taken = taken;
	}
	public Resource getItem() {
		return item;
	}
	public short getAmount() {
		return amount;
	}
	public Long getCreated() {
		return created;
	}
	public int getShopID() {
		return shopID;
	}
	public boolean isTaken() {
		return taken;
	}
	public void setTaken(boolean taken) {
		this.taken = taken;
	}
	public double getPrice() {
		return price;
	}
}
