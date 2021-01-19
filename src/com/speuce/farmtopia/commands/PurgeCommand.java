package com.speuce.farmtopia.commands;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.farm.FarmManager;
import com.speuce.farmtopia.farm.FarmReady;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PurgeCommand implements CommandExecutor {

    private FarmManager manager;

    public PurgeCommand(FarmManager manager) {
        this.manager = manager;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] arg3) {
        if (cmd.getName().equalsIgnoreCase("purge")) {
            if (sender.isOp() && arg3.length == 1) {
                Player target = Constant.getPlayer(arg3[0]);
                if (target != null) {
                    target.sendMessage(ChatColor.RED.toString() + "Your data has been purged..");
                    target.getInventory().clear();
                    com.speuce.farmtopia.farm.Farm f = manager.getFarm(target);
                    target.teleport(new Location(target.getWorld(), 0, Constant.baseY + 5, 0));
                    manager.cleanFarm(f);
                    manager.deleteData(target);
                    try {
                        manager.newPlayer(target, target.getWorld(), new FarmReady() {

                            @Override
                            public void onFinish(Farm f) {
                                manager.teleportTo(target, f);
                                target.setExp(f.getProgress() / 255F);
                                target.setLevel(f.getLevel());
                                target.sendMessage(ChatColor.GREEN + "Welcome to your Farm!");
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sender.sendMessage(ChatColor.GREEN.toString() + "Success!");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + "Target not found!");
                    return true;
                }
            }
        }
        return false;
    }
}
