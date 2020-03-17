package main.java.com.speuce.farmtopia.main;

import main.java.com.speuce.farmtopia.resources.Resource;
import main.java.com.speuce.farmtopia.util.Debug;
import main.java.com.speuce.farmtopia.util.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.NumberFormat;
import java.util.stream.Stream;

/**
 * Handles all of the command-related functions of the main
 * plugin class.
 */
public abstract class MainCommandListener extends DebugMessager {

    /**
     * Safely registers a command
     * @param name the name of the command
     * @param e the associated {@link CommandExecutor}
     */
    public void safeCommandRegister(String name, CommandExecutor e){
        PluginCommand c = this.getCommand(name);
        //Precondition: The given command is a valid command (i.e c isnt null)
        assert(c != null);
        c.setExecutor(e);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String sk, String[] args){
        if(cmd.getName().equalsIgnoreCase("balance")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(Economy.hasInfo(p.getUniqueId())){
                    NumberFormat.getNumberInstance();
                    p.sendMessage(ChatColor.GOLD.toString() + "Balance: " + ChatColor.DARK_GREEN.toString()
                            + NumberFormat.getCurrencyInstance().format(Economy.getBalance(p.getUniqueId())));
                    return true;
                }else{
                    p.sendMessage(ChatColor.RED.toString() + "Whoa, slow down there partner! The server hasn't even loaded your dollar bills yet!");
                    return true;
                }
            }
        }else if(cmd.getName().equalsIgnoreCase("debug")){
            if(sender.isOp()) {
                if(args.length == 0) {
                    if(Debug.getInstance().toggleType(Debug.Type.GENERAL)) {
                        sender.sendMessage(ChatColor.GREEN + "Toggled General Debugging ON");
                        return true;
                    }else {
                        sender.sendMessage(ChatColor.RED + "Toggled General Debugging OFF");
                        return true;
                    }
                }else {
                    if(args[0].equalsIgnoreCase("log")) {
                        if(Debug.getInstance().isLogging()) {
                            Debug.getInstance().setLog(false);
                            sender.sendMessage(ChatColor.RED + "Toggled Debug Logging OFF");
                            return true;
                        }else {
                            Debug.getInstance().setLog(true);
                            sender.sendMessage(ChatColor.GREEN + "Toggled Debug Logging ON");
                            return true;
                        }
                    }else {
                        Debug.Type t = Debug.Type.match(args[0]);
                        if(t == null) {
                            sender.sendMessage(ChatColor.RED + "Debug Type: " + t + " couldn't be found!");
                            StringBuilder s = new StringBuilder(" ");
                            Stream.of(Debug.Type.values()).forEach(i -> s.append(i.toString()));
                            sender.sendMessage(ChatColor.RED + "Valid Types: " + s.toString());
                            return true;
                        }else {
                            if(Debug.getInstance().toggleType(t)) {
                                sender.sendMessage(ChatColor.GREEN + "Toggled Debug for " + t.toString() + " ON");
                                return true;
                            }else {
                                sender.sendMessage(ChatColor.RED + "Toggled Debug for " + t.toString() + " OFF");
                                return true;
                            }
                        }
                    }
                }
            }
        }else if(cmd.getName().equalsIgnoreCase("gm")){
            if(sender.isOp() && sender instanceof Player) {
                Player pl = (Player) sender;
                if(args.length == 2) {
                    String name = args[1];
                    Player p2 = Bukkit.getPlayer(name);
                    if(p2 == null) {
                        sender.sendMessage(ChatColor.RED + "Player: " + name + " couldn't be found!");
                        return true;
                    }
                    pl = p2;
                }
                if(args[0].equalsIgnoreCase("c")) {
                    pl.setGameMode(GameMode.CREATIVE);
                    return true;
                }else if(args[0].equalsIgnoreCase("s")) {
                    pl.setGameMode(GameMode.SURVIVAL);
                    return true;
                }else if(args[0].equalsIgnoreCase("a")) {
                    pl.setGameMode(GameMode.ADVENTURE);
                    return true;
                }else if(args[0].equalsIgnoreCase("sp")) {
                    pl.setGameMode(GameMode.SPECTATOR);
                    return true;
                }
            }
        }else if(cmd.getName().equalsIgnoreCase("eco")){
            if(sender.isOp() && args.length ==3){
                Player p = null;
                for(Player plf: Bukkit.getOnlinePlayers()){
                    if(plf.getName().equalsIgnoreCase(args[1])){
                        p = plf;
                        break;
                    }
                }
                if(p == null){
                    sender.sendMessage(ChatColor.RED.toString() + "Couldn't find player: " + args[1]);
                    return true;
                }
                Double d = 0D;
                try{
                    d = Double.parseDouble(args[2]);
                }catch(NumberFormatException e){
                    sender.sendMessage(ChatColor.RED.toString() + "Couldn't parse amount: " + args[2]);
                    return true;
                }

                if(args[0].equalsIgnoreCase("add")){
                    Economy.addBal(p.getUniqueId(), d);
                    sender.sendMessage(ChatColor.GREEN.toString() + "Success!");
                    return true;
                }else if(args[0].equalsIgnoreCase("remove")){
                    Economy.subtractBal(p.getUniqueId(), d);
                    sender.sendMessage(ChatColor.GREEN.toString() + "Success!");
                    return true;
                }else if(args[0].equalsIgnoreCase("set")){
                    Economy.setBalance(p.getUniqueId(), d);
                    sender.sendMessage(ChatColor.GREEN.toString() + "Success!");
                    return true;
                }
            }
        }else if(cmd.getName().equalsIgnoreCase("fdebug")){
            if(sender.isOp()){
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("spam")){
                        setListeningfor(DebugLevel.SPAM);
                        sender.sendMessage(ChatColor.GREEN + "Success!");
                        return true;
                    }else if(args[0].equalsIgnoreCase("semi")){
                        setListeningfor(DebugLevel.SEMI);
                        sender.sendMessage(ChatColor.GREEN + "Success!");
                        return true;
                    }else if(args[0].equalsIgnoreCase("maj")){
                        setListeningfor(DebugLevel.MAJOR);
                        sender.sendMessage(ChatColor.GREEN + "Success!");
                        return true;
                    }else{
                        sender.sendMessage(ChatColor.RED + "/fdebug <spam,semi,maj>");
                        return true;
                    }


                }else{
                    sender.sendMessage(ChatColor.RED + "/fdebug <spam,semi,maj>");
                    return true;
                }
            }else{
                sender.sendMessage(ChatColor.RED + "No Perms!");
                return true;
            }
        }else if(cmd.getName().equalsIgnoreCase("md")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(!p.isOp()){
                    sender.sendMessage(ChatColor.RED + "No Perms!");
                    return true;
                }
            }
            if(args.length != 1){
                sender.sendMessage(ChatColor.RED + "/md <player|all>");
                return true;
            }
            if(args[0].equalsIgnoreCase("all")){
                for(Player p: Bukkit.getOnlinePlayers()){
                    p.getInventory().addItem(Resource.MAGIC_DUST.toItemStack(1));
                }
                sender.sendMessage(ChatColor.GREEN + "Done!");
                return true;
            }else{
                for(Player p: Bukkit.getOnlinePlayers()){
                    if(p.getName().equalsIgnoreCase(args[0])){
                        p.getInventory().addItem(Resource.MAGIC_DUST.toItemStack(1));
                        sender.sendMessage(ChatColor.GREEN + "Done!");
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.RED + "Player: " + args[0] + "not found!");
                return true;
            }
        }
        return false;
    }
}
