package com.speuce.farmtopia.commands;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.farm.FarmManager;
import javafx.scene.web.HTMLEditorSkin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpCommand implements CommandExecutor {

    private FarmManager manager;


    public ExpCommand(FarmManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] arg3) {
        if (cmd.getName().equalsIgnoreCase("exp")) {
            if (sender.isOp()) {
                if (arg3.length == 2) {
                    Player target = Constant.getPlayer(arg3[0]);
                    int amt = Constant.getInt(arg3[1]);
                    if (amt == 0 || target == null) {
                        sender.sendMessage(ChatColor.RED.toString() + "Bad number or bad player!");
                        return true;
                    } else {
                        if(!manager.hasFarm(target)){
                            sender.sendMessage(ChatColor.RED.toString() + "Player's farm not loaded.");
                            return true;
                        }
                        if (amt > 0) {
                           manager.getFarm(target).addExp(amt);
                            return true;
                        } else {
                            manager.getFarm(target).subtractExp(Math.abs(amt));
                            // sender.sendMessage(ChatColor.RED.toString() +
                            // "Took " + amt );
                            return true;
                        }

                    }

                } else {
                    return false;
                }
            } else {
                sender.sendMessage(ChatColor.RED.toString() + "Nice try..");
                return true;
            }
        }
        return false;
    }
}
