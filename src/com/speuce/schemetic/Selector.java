package com.speuce.schemetic;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.speuce.farmtopia.resources.Resource;

public class Selector implements Listener{
	Map<Player, Selection> selections;
	public Selector(JavaPlugin p){
		p.getServer().getPluginManager().registerEvents(this, p);
		this.selections = new HashMap<Player, Selection>();
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent e){
		if(e.getPlayer().hasPermission("speuce.select") && 
				(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.WOODEN_SHOVEL
				||Resource.getByItem(e.getItem()).equals(Resource.DEV_WAND))){
			e.setCancelled(true);
			if(this.selections.containsKey(e.getPlayer())){
				Selection s = this.selections.get(e.getPlayer());
				if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
					s.setLoc2(e.getClickedBlock());
					e.getPlayer().sendMessage(ChatColor.GREEN + "Set location 2!");
				}else if(e.getAction() == Action.LEFT_CLICK_BLOCK){
					s.setLoc1(e.getClickedBlock());
					e.getPlayer().sendMessage(ChatColor.GREEN + "Set location 1!");
				}
			}else{
				Selection s = new Selection(e.getClickedBlock(), e.getClickedBlock());
				e.getPlayer().sendMessage(ChatColor.GREEN + "New Selection Made.");
				if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
					e.getPlayer().sendMessage(ChatColor.GREEN + "Set location 2!");
				}else if(e.getAction() == Action.LEFT_CLICK_BLOCK){
					e.getPlayer().sendMessage(ChatColor.GREEN + "Set location 1!");
				}
				this.selections.put(e.getPlayer(), s);
			}
				
		}
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(this.selections.containsKey(e.getPlayer())){
			this.selections.remove(e.getPlayer());
		}
	}
	public Selection getSelection(Player p){
		return this.selections.get(p);
	}
}
