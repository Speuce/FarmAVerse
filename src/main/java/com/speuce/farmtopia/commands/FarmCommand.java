package main.java.com.speuce.farmtopia.commands;

import main.java.com.speuce.farmtopia.farm.FarmManager;
import main.java.com.speuce.farmtopia.util.PrebuiltInventories;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the "/farm" or "/f" command
 * essentially just opens up the farm menu.
 */
public class FarmCommand implements CommandExecutor {

    private FarmManager fm;

    public FarmCommand(FarmManager fm) {
        this.fm = fm;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("farm")) {
            if (sender instanceof Player) {
                Player pl = (Player) sender;
                if (fm.getLoadedFarms().containsKey(pl)) {
                    pl.openInventory(PrebuiltInventories.getFarmHomeMenu(fm.getLoadedFarms().get(pl)));
                } else {
                    pl.sendMessage(ChatColor.RED
                            + "Sorry, your farm isn't loaded yet! (try relogging if you keep seeing this)");
                }
                return true;
            }
        }
        return false;
    }
}
