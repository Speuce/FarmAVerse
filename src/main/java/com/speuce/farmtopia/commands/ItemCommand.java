package main.java.com.speuce.farmtopia.commands;

import main.java.com.speuce.farmtopia.resources.Resource;
import main.java.com.speuce.farmtopia.util.Constant;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ItemCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(cmd.getName().equalsIgnoreCase("item") && sender.isOp()){
			if(args.length == 3){
				Player p = null;
				for(Player l: Bukkit.getOnlinePlayers()){
					if(l.getName().equalsIgnoreCase(args[0])){
						p = l;
					}
				}
				int amt = 0;
				try{
					amt = Integer.parseInt(args[2]);
				}catch(NumberFormatException e){
					sender.sendMessage(ChatColor.RED.toString() + "Invalid number!");
					return true;
				}
				Resource s = Resource.getByName(args[1].replace("_", " "));
				
				if(p != null && s != Resource.NOTHING){
					Constant.forceGive(p, s.toItemStack(amt));
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}

}
