package main.java.com.speuce.farmtopia.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InfiniteInv {
	private List<ItemStack> items;
	
	
	public InfiniteInv(){
		this.items = new ArrayList<ItemStack>();
	}
	public void setItem(int index, ItemStack i){
		this.items.set(index, i);
	}
	public void addItem(ItemStack i){
		this.items.add(i);
	}
	public ItemStack getItem(int x){
		return this.items.get(x);
	}
	public void removeItem(ItemStack i){
		this.items.remove(i);
	}
	public List<ItemStack> getItems(){
		return this.items;
	}
	public int getMaxPage(){
		return (int)(items.size()/45) +1;
	}
	public Inventory getInventory(int page, String name){
		if(page < this.getMaxPage()){
			int start = page * 45;
			Inventory ret = Bukkit.createInventory(null, 54, name);
			for(int i = start; i < start + 45; i++){
				ItemStack it = items.get(i);
				if(it == null){
					break;
				}else{
					ret.addItem(it);
				}
			}
			return ret;
		}else{
			return null;
		}
	}
}
