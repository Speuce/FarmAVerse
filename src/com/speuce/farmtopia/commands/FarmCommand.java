package com.speuce.farmtopia.commands;

import com.speuce.farmtopia.farm.manager.FarmManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FarmCommand implements CommandExecutor {

    private FarmManager manager;

    public FarmCommand(FarmManager manager) {
        this.manager = manager;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] arg3) {
        if (cmd.getName().equalsIgnoreCase("farm")) {
            if (sender instanceof Player) {
                Player pl = (Player) sender;
                if (manager.hasFarm(pl)) {
                    pl.openInventory(com.speuce.farmtopia.farm.Farm.getMenu(manager.getFarm(pl)));
                    return true;
                } else {
                    pl.sendMessage(ChatColor.RED
                            + "Sorry, your farm isn't loaded yet! (try relogging if you keep seeing this)");
                    return true;
                }
            }
        }
        return false;
    }
}
