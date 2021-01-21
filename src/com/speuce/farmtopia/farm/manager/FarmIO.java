package com.speuce.farmtopia.farm.manager;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.farm.Farm;
import com.speuce.farmtopia.farm.FarmBuilder;
import com.speuce.farmtopia.farm.FarmReady;
import com.speuce.farmtopia.farm.Tutorial;
import com.speuce.farmtopia.farm.manager.FarmManager;
import com.speuce.farmtopia.jobs.JobManager;
import com.speuce.farmtopia.main.FarmTopia;
import com.speuce.farmtopia.plot.FarmPlot;
import com.speuce.farmtopia.resources.Resource;
import com.speuce.farmtopia.util.Economy;
import com.speuce.farmtopia.util.SC;
import com.speuce.sql.DataType;
import com.speuce.sql.SQLManager;
import com.speuce.sql.TableCheck;
import com.speuce.sql.booleanQuery;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface between {@link FarmManager} and the
 * respective {@link SQLManager}.
 */
public class FarmIO {

    private FarmManager manager;

    private SQLManager sql;


    public FarmIO(FarmManager manager, SQLManager sql) {
        this.manager = manager;
        this.sql = sql;
        //query to see if the table infact exists.
        sql.Query(new TableCheck("farms", new booleanQuery() {

            @Override
            public void onReturn(boolean b) {
                if (!b) {
                    makeTable();
                }

            }
        }));
    }

    /**
     * Creates the table if not found.
     */
    private void makeTable() {
        Map<String, DataType> columns = new HashMap<String, DataType>();
        columns.put("uuid", DataType.UUID);
        columns.put("farm", DataType.BLOB);
        columns.put("inv", DataType.BLOB);
        columns.put("bal", DataType.DOUBLE);
        columns.put("jobs", DataType.BLOB);
        columns.put("tut", DataType.INT);
        sql.CreateTable("farms", columns, "uuid");
    }

    /**
     * Helper function to serialize an inventory into an array
     * of bytes.
     */
    private byte[] SerializeInventory(@NotNull  Inventory i) {
        byte[] ret = new byte[i.getSize() * 2];
        for (int x = 0; x < i.getSize(); x++) {
            if (i.getItem(x) != null) {
                byte[] t = Resource.serialize(i.getItem(x));
                ret[x * 2] = t[0];
                ret[(x * 2) + 1] = t[1];

            }
        }
        return ret;
    }

    /**
     * Creates a new player's farm, as well as builds it.
     * @throws InterruptedException
     */
    public void newPlayer(Player p, World w, FarmReady fr) throws InterruptedException {
        Resource r = Resource.WHEAT_SEEDS;
        ItemStack i = r.toItemStack(5);
        p.getInventory().addItem(i);
        p.getInventory().addItem(Resource.MAGIC_DUST.toItemStack(15));
        // Bukkit.broadcastMessage("NEW PLAYERRR");
        p.sendMessage(ChatColor.GREEN.toString() + "Welcome!");

        manager.getLocationManager().requestLocation(p, location -> {
            Farm f = new Farm(location, p, manager, 0, (byte) 0);
            f.addPlot(new FarmPlot(f));
            FarmBuilder fb = new FarmBuilder(manager.getPlugin());
            fb.build(f, fr);
            manager.addCachedFarm(p, f);
            manager.getLocationManager().addLocationLookup(location, f);
            SC.newScoreboard(p);
            manager.updateBalance(p, 0D);
            Tutorial.newPlayer(p, 0);
            byte[] job = JobManager.newPlayer();
            FarmTopia.getFarmTopia().getJobManager().loadData(p, job);
            newFarm(f, job);
        });
    }

    /**
     * Saves a new farm into the database
     * @param f the Farm to save.
     * @param jobs the jobs associated with the player to save
     */
    private void newFarm(Farm f, byte[] jobs){
        new BukkitRunnable() {
            @Override
            public void run() {
                Connection c = null;
                PreparedStatement ps = null;
                // Economy.setBalance(p.getUniqueId(),0D);
                try {
                    c = sql.getConnection();
                    ps = c.prepareStatement("INSERT INTO farms (uuid, farm, inv, bal, jobs, tut) VALUES (?,?,?,?,?,?)");
                    ps.setString(1, f.getOwner().getUniqueId().toString());
                    ps.setBytes(2, f.FINALserialize(Constant.currentProtocol));
                    ps.setBytes(3, new byte[2]);
                    ps.setDouble(4, 0);
                    ps.setBytes(5, jobs);
                    ps.setInt(6, 0);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    sql.close(ps);
                    sql.close(c);
                }
            }
        }.runTaskAsynchronously(FarmTopia.getFarmTopia());
    }

    /**
     * Saves an existing player's farm.
     */
    public void saveFarm(Farm f, Player p) {
        Connection c = null;
        PreparedStatement ps = null;
        final byte[] inv = this.SerializeInventory(p.getInventory());
        try {
            c = sql.getConnection();
            ps = c.prepareStatement("UPDATE farms SET farm=?, inv=?, bal=?, jobs=?, tut=? WHERE uuid=?");
            ps.setString(6, p.getUniqueId().toString());
            byte[] ftr = f.FINALserialize(Constant.currentProtocol);
            // Bukkit.broadcastMessage("saving::: " + Hex.encodeHexString(ftr));
            ps.setBytes(1, ftr);
            ps.setBytes(2, inv);
            ps.setDouble(3, Economy.getRemoveBalance(p.getUniqueId()));
            ps.setBytes(4, FarmTopia.getFarmTopia().getJobManager().saveData(p));
            ps.setInt(5, Tutorial.getDelProgress(p));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sql.close(ps);
            sql.close(c);
        }
    }

    /**
     * Loads a specific players farm, if possible
     * @param p
     * @param w
     * @param fr
     */
    public void loadFarm(Player p, World w, FarmReady fr) {
        p.getInventory().clear();
        BukkitRunnable br = new BukkitRunnable() {

            @Override
            public void run() {
                PreparedStatement ps = null;
                Connection c = null;
                ResultSet rs = null;
                try {
                    c = sql.getConnection();
                    ps = c.prepareStatement("SELECT * FROM farms WHERE uuid=?");
                    ps.setString(1, p.getUniqueId().toString());
                    rs = ps.executeQuery();
                    if (!rs.next()) {
                        newPlayer(p, w, fr);
                    } else {
                        FarmTopia.getFarmTopia().getJobManager().loadData(p, rs.getBytes("jobs"));

                        byte[] data = rs.getBytes("farm");
                        ResultSet finalRs = rs;
                        manager.getLocationManager().requestLocation(p, l -> {
                            Farm f = Farm.deserialize(data, l, p, manager);
                            FarmBuilder fb = new FarmBuilder(FarmTopia.getFarmTopia());
                            fb.build(f, fr);
                            manager.addCachedFarm(p, f);
                            manager.getLocationManager().addLocationLookup(l, f);
                            try {
                                manager.updateBalance(p, finalRs.getDouble("bal"));
                                byte[] inv = finalRs.getBytes("inv");
                                Tutorial.newPlayer(p, finalRs.getInt("tut"));
                                for (int x = 0; x < inv.length; x += 2) {
                                    ItemStack r = Resource.deserialize(Arrays.copyOfRange(inv, x, x + 2),
                                            Constant.currentProtocol);
                                    if (r != null && r.getType() != Material.AIR) {
                                        p.getInventory().setItem(x, r);
                                    }

                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }finally{
                                sql.close(finalRs);
                            }
                        });
                    }
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    sql.close(ps);
                    sql.close(rs);
                    sql.close(c);

                }

            }

        };
        br.runTaskAsynchronously(manager.getPlugin());
    }

    /**
     * Wipes a user's data from the db.
     */
    public void deleteData(Player p) {
        Farm f = manager.getFarm(p);
        manager.removeCachedFarm(f);
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = sql.getConnection();
            ps = c.prepareStatement("DELETE FROM farms WHERE uuid=?");
            ps.setString(1, p.getUniqueId().toString());
            // Bukkit.broadcastMessage("query: " + ps.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sql.close(ps);
            sql.close(c);
        }
    }
}
