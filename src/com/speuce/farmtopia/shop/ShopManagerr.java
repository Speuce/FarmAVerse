package com.speuce.farmtopia.shop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.speuce.farmtopia.main.FarmTopia;
import com.speuce.farmtopia.resources.Resource;
import com.speuce.sql.SQLManager;

public class ShopManagerr implements CommandExecutor, Listener{
	private SQLManager sql;
	private FarmTopia main;
	private ItemStack search;
	public ShopManagerr(SQLManager sql, FarmTopia main){
		this.sql = sql;
		this.main = main;
		main.getCommand("shop").setExecutor(this);
		this.makeTable();
		
		//this.search = new ItemStack(Material.BOW, 1, ());
	}
	public SQLManager getSql(){
		return this.sql;
	}
	private void makeTable(){
		BukkitRunnable br = new BukkitRunnable(){
			@Override
			public void run() {
				Connection c = null;
				PreparedStatement ps = null;
				try {
					c = sql.getConnection();
					ps = c.prepareStatement("CREATE TABLE IF NOT EXISTS`Speuce`.`shop` "
							+ "( `id` INT NOT NULL AUTO_INCREMENT ,"
							+ " `item` VARCHAR(24) NOT NULL , "
							+ "`amount` SMALLINT NOT NULL , "
							+ "`price` DOUBLE NOT NULL , "
							+ "`created` TIMESTAMP NOT NULL ,"
							+ " `taken` BOOLEAN NOT NULL DEFAULT FALSE , "
							+ "`shopid` INT NOT NULL ,"
							+ " PRIMARY KEY (`id`)) ENGINE = InnoDB;");
					ps.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					sql.close(c);
					sql.close(ps);
				}
			}
		};
		br.runTaskAsynchronously(main);
	}
	public void getShopItem(Resource item, short amount, double price, Function<ShopOffer, Boolean> purchase){
		BukkitRunnable br = new BukkitRunnable(){
			@Override
			public void run() {
				Connection c = null;
				ResultSet rs = null;
				PreparedStatement ps = null, p2=null;
				try {
					c = sql.getConnection();
					ps = c.prepareStatement("SELECT * FROM shop WHERE item=? AND amount=? AND price=? LIMIT 0, 1;");
					rs = ps.executeQuery();
					if(!rs.next()){
						purchase.apply(null);
					}else{
						ShopOffer s = new ShopOffer(Resource.valueOf(rs.getString("item")),
								rs.getShort("amount"), rs.getDouble("price"),
								rs.getTimestamp("created").getTime(),
								rs.getInt("shopid"), rs.getBoolean("taken"));
						boolean take = purchase.apply(s);
						if(take){
							p2 = c.prepareStatement("UPDATE shop SET taken=? WHERE item=?"
									+ " AND amount=? AND price=? AND created=?");
							p2.setBoolean(1, true);
							p2.setString(2, rs.getString("item"));
							p2.setShort(3, rs.getShort("amount"));
							p2.setBoolean(4, rs.getBoolean("taken"));
							p2.setTimestamp(5, rs.getTimestamp("created"));
							p2.executeUpdate();
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					sql.close(c);
					sql.close(ps);
					sql.close(rs);
					sql.close(p2);
				}
			}
		};
		br.runTaskAsynchronously(main);
	}
	private ItemStack getItemFrom(String itemm, short amount, double price){
		Resource item = Resource.valueOf(itemm);
		if(item == null || item == Resource.NOTHING){
			return null;
		}
		ItemStack ret = item.toItemStack((amount <= 64) ? amount : 64);
		ItemMeta retm = ret.getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GREEN.toString() + "Amount: " + ChatColor.GOLD.toString() + amount);
		lore.add(ChatColor.GREEN.toString() + "Price: " + ChatColor.DARK_GREEN.toString() + NumberFormat.getCurrencyInstance().format(price));
		retm.setLore(lore);
		ret.setItemMeta(retm);
		return ret;
	}
	public void search(String searchString, int page, Consumer<ItemStack[]> ret){
		BukkitRunnable br = new BukkitRunnable(){

			@Override
			public void run() {
				Connection c = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try{
					c = sql.getConnection();
					ps = c.prepareStatement("SELECT item, amount, price FROM shop"
							+ " WHERE item LIKE ? AND taken='0' "
							+ "ORDER BY "
							+ "CASE WHEN item = ? THEN 0 "
							+ "WHEN item LIKE ? THEN 1 "
							+ "WHEN item LIKE ? THEN 2 "
							+ "WHEN item LIKE ? THEN 3 "
							+ "ELSE 4 END, LENGTH(item), created LIMIT " + page*45 +", "+ (page+1)*45);
					ps.setString(1, "%" + searchString + "%");
					ps.setString(2, searchString);
					ps.setString(3, searchString + "%");
					ps.setString(4, "%" + searchString + "%");
					ps.setString(5, "%" + searchString);
					rs = ps.executeQuery();
					List<ItemStack> r = new ArrayList<ItemStack>();
					while(rs.next()){
						r.add(getItemFrom(rs.getString("item"), rs.getShort("amount"), rs.getDouble("price")));
					}
					if(r.isEmpty()){
						ret.accept(null);
					}else{
						ret.accept(r.toArray(new ItemStack[0]));
					}
				}catch(SQLException e){
					ret.accept(null);
				}finally{
					sql.close(rs);
					sql.close(ps);
					sql.close(c);
				}
				
			}
			
		};
		br.runTaskAsynchronously(this.main);
		
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(cmd.getName().equalsIgnoreCase("shop")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				
			}
		}
		return false;
	}
}
