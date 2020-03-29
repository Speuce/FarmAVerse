package main.java.com.speuce.farmtopia.commands;

import main.java.com.speuce.farmtopia.farm.FarmManager;
import main.java.com.speuce.farmtopia.util.Constant;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Manages the "/exp" command
 * format: "/exp player amt
 * @author matt
 */
public class ExpCommand implements CommandExecutor {

    private FarmManager fm;

    public ExpCommand(FarmManager fm) {
        this.fm = fm;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] arg3) {
        if (cmd.getName().equalsIgnoreCase("exp")) {
            if (sender.isOp()) {
                if (arg3.length == 2) {
                    Player target = Constant.getPlayer(arg3[0]);
                    int amt = Constant.getInt(arg3[1]);
                    if (amt == 0 || target == null) {
                        sender.sendMessage(ChatColor.RED.toString() + "Bad number or bad player!");
                    } else {
                        if (amt > 0) {
                            fm.getLoadedFarms().get(target).addExp(amt);
                        } else {
                            fm.getLoadedFarms().get(target).subtractExp(Math.abs(amt));
                        }
                    }
                    return true;

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
