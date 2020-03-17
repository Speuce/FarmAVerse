package main.java.com.speuce.farmtopia.shop;

import main.java.com.speuce.farmtopia.resources.Resource;

import java.sql.Timestamp;


public class ShopOffer {
	private Resource item;
	private short amount;
	private double price;
	private Timestamp created;
	private int shopID;
	private int ID;
	private boolean taken;
	
	public ShopOffer(int id, Resource item, short amount, double price, Timestamp created, int shopID, boolean taken) {
		this.ID = id;
		this.item = item;
		this.amount = amount;
		this.price = price;
		this.created = created;
		this.shopID = shopID;
		this.taken = taken;
	}
	public int getId() {
		return ID;
	}
	public Resource getItem() {
		return item;
	}
	public short getAmount() {
		return amount;
	}
	public Timestamp getCreated() {
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
