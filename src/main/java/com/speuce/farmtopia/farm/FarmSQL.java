package main.java.com.speuce.farmtopia.farm;

import main.java.com.speuce.farmtopia.jobs.JobManager;
import main.java.com.speuce.farmtopia.main.FarmTopia;
import main.java.com.speuce.farmtopia.resources.Resource;
import main.java.com.speuce.farmtopia.util.Constant;
import main.java.com.speuce.farmtopia.util.Economy;
import main.java.com.speuce.farmtopia.util.SC;
import main.java.com.speuce.sql.DataType;
import main.java.com.speuce.sql.SQLManager;
import main.java.com.speuce.sql.TableCheck;
import main.java.com.speuce.sql.booleanQuery;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for loading/saving/serializing farms
 * to/from SQL
 * @author matt
 */
public class FarmSQL {
    //for sending sql queries
    SQLManager sql;
    FarmManager fm;

    public FarmSQL(SQLManager sql, FarmManager fm) {
        this.sql = sql;
        this.fm = fm;
        //creat the table if it dont exist
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
     * Creates the Farm Table in the database
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
     * Saves the given farm for the given player into the database
     * ***SHOULD BE CALLED ASYNC****
     * @param f the farm to save
     * @param p the player to save the far under
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
     * Serialize an inventory into a byte stream
     * @param i the inventory to serialize
     * @return an array of bytes representing the given inventory
     */
    private byte[] SerializeInventory(Inventory i) {
        assert(i != null);
        assert(i.getSize() > 0);
        byte[] ret = new byte[i.getSize() * 2];
        for (int x = 0; x < i.getSize(); x++) {
            if (i.getItem(x) != null) {
                byte[] t = Resource.serialize(i.getItem(x));
                assert(t != null);
                ret[x * 2] = t[0];
                ret[(x * 2) + 1] = t[1];

            }
        }
        return ret;
    }

    /**
     * TO completely purge the data of the given player
     * @param p the Player whose data shall be purged
     */
    public void deleteData(Player p) {
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

    /**
     * Called when a new player has joined the server
     * @param p the Player that joined
     * @param w the world
     */
    public void newPlayer(Player p, World w) throws InterruptedException {
        Resource r = Resource.WHEAT_SEEDS;
        ItemStack i = r.toItemStack(5);
        p.getInventory().addItem(i);
        p.getInventory().addItem(Resource.MAGIC_DUST.toItemStack(15));
        p.sendMessage(ChatColor.GREEN.toString() + "Welcome!");
        Connection c = null;
        PreparedStatement ps = null;
        Farm f = new Farm(null, p, fm, 0, (byte) 0);
        fm.onFarmLoaded(p, f);
        // Economy.setBalance(p.getUniqueId(),0D);
        BukkitRunnable br = new BukkitRunnable() {

            @Override
            public void run() {
                SC.newScoreboard(p);

            }

        };
        br.runTask(FarmTopia.getFarmTopia());
        try {
            c = sql.getConnection();
            ps = c.prepareStatement("INSERT INTO farms (uuid, farm, inv, bal, jobs, tut) VALUES (?,?,?,?,?,?)");
            ps.setString(1, p.getUniqueId().toString());
            ps.setBytes(2, f.FINALserialize(Constant.currentProtocol));
            ps.setBytes(3, new byte[2]);
            ps.setDouble(4, 0);
            byte[] job = JobManager.newPlayer();
            ps.setBytes(5, job);
            ps.setInt(6, 0);
            FarmTopia.getFarmTopia().getJobManager().loadData(p, job);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sql.close(ps);
            sql.close(c);
        }
        Score(p, 0D);
        Tutorial.newPlayer(p, 0);
    }

    /**
     * Loads a farm for a given player in the world
     * @param p the Player whose farm should be found and loaded
     * @param w the world which the farm will be built in.
     */
    public void loadFarm(Player p, World w) {
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
                        newPlayer(p, w);
                    } else {
                        FarmTopia.getFarmTopia().getJobManager().loadData(p, rs.getBytes("jobs"));
                        byte[] data = rs.getBytes("farm");
                        Farm f = Farm.deserialize(data, null, p, FarmTopia.getFarmTopia().getFarmManager());
                        Score(p, rs.getDouble("bal"));
                        byte[] inv = rs.getBytes("inv");
                        fm.onFarmLoaded(p, f);
                        Tutorial.newPlayer(p, rs.getInt("tut"));
                        loadInventory(p, inv);
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
        br.runTaskAsynchronously(FarmTopia.getFarmTopia());
    }

    /**
     * Loads an players inventory from a byte array
     * @param p the Player whose inventory is loaded
     * @param inv the byte aray containing the inventory info
     */
    private void loadInventory(Player p, byte[] inv){
        for (int x = 0; x < inv.length; x += 2) {
            ItemStack r = Resource.deserialize(Arrays.copyOfRange(inv, x, x + 2),
                    Constant.currentProtocol);
            if (r != null && r.getType() != Material.AIR) {
                p.getInventory().setItem(x, r);
            }
        }
    }

    /**
     * Loads in the scoreboard (in a synchronous task)
     * @param p the Player to load the scoreboard for
     * @param bal the balance to show.
     */
    private void Score(Player p, Double bal) {
        BukkitRunnable br = new BukkitRunnable() {

            @Override
            public void run() {
                Economy.setBalance(p.getUniqueId(), bal);
                SC.setLine(p, 0, ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "Money");
            }

        };
        br.runTask(FarmTopia.getFarmTopia());
    }
}
