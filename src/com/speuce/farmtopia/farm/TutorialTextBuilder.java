package com.speuce.farmtopia.farm;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.bukkit.ChatColor;

public class TutorialTextBuilder {
	private List<String[]> items;
	
	public TutorialTextBuilder(){
		this.items = new LinkedList<String[]>();
	}
	public TutorialTextBuilder add(String... add){
		this.items.add(convert(add));
		return this;
	}
	private String[] convert(String[] in){
		String[] ret = new String[in.length];
		for(int x = 0; x < in.length; x++){
			ret[x] = ChatColor.translateAlternateColorCodes('&', in[x]);
		}
		return ret;
	}
	public Queue<String[]> build(){
		return new LinkedList<String[]>(items);
	}
}
