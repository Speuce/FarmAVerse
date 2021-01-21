package com.speuce.farmtopia.farm.handlers;

import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.farm.manager.FarmManager;
import com.speuce.farmtopia.main.FarmTopia;
import com.speuce.farmtopia.plot.BuildQueue;
import com.speuce.farmtopia.plot.Plot;
import com.speuce.farmtopia.util.SC;
import com.speuce.schemetic.RunnablePredefinedSchem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Handles players joining/leaving the server.
 */
public class JoinQuitHandler implements Listener {

    private FarmManager manager;


    public JoinQuitHandler(FarmManager manager) {
        this.manager = manager;
        FarmTopia.registerListener(this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (manager.hasFarm(e.getPlayer())) {
            manager.teleportTo(e.getPlayer(), manager.getFarm(e.getPlayer()));
        } else {
            e.getPlayer().teleport(manager.getLocationManager().getMainWorld().getSpawnLocation());
            e.getPlayer().sendMessage(ChatColor.GREEN + "Loading your farm..");
            final Player pl = e.getPlayer();
            manager.getIomanager().loadFarm(e.getPlayer(), manager.getLocationManager().getMainWorld(), f -> {
                manager.teleportTo(pl, f);
                e.getPlayer().setExp(f.getProgress() / 255F);
                e.getPlayer().setLevel(f.getLevel());
                // Bukkit.broadcastMessage("leve " + f.getLevel());
                pl.sendMessage(ChatColor.GREEN + "Welcome to your Farm!");
            });
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        // TODO optimize so it doesnt clear farm until minutes after
        if (manager.hasFarm(e.getPlayer())) {
            final Farm fl = manager.getFarm(e.getPlayer());
            cleanEntities(fl);
            manager.getLocationManager().cleanFarm(fl);
            BukkitRunnable br = new BukkitRunnable() {

                @Override
                public void run() {
                    manager.getIomanager().saveFarm(fl, e.getPlayer());

                    manager.removeCachedFarm(fl);
                    if (Bukkit.getServer().getOnlinePlayers().isEmpty()) {
                        BuildQueue.queue(new RunnablePredefinedSchem(new Runnable() {

                            @Override
                            public void run() {
                                System.out.println("Server is ready for shutdown.");
                            }

                        }));
                    }
                }

            };
            br.runTaskLaterAsynchronously(FarmTopia.getFarmTopia(), 5L);
        }
        SC.logoff(e.getPlayer());

    }

    /**
     * Helper method to clear all entities off of a farm.
     */
    private void cleanEntities(Farm f) {
        for (Plot p : f.getAllPlots()) {
            p.cleanup();
        }
    }
}
