package com.speuce.farmtopia.shop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.main.FarmTopia;
import com.speuce.farmtopia.resources.Resource;
import com.speuce.farmtopia.util.Debug;
import com.speuce.farmtopia.util.Economy;
import com.speuce.sql.SQLManager;

public class ShopManager implements CommandExecutor, Listener{
	private SQLManager sql;
	private FarmTopia main;
	private Map<Integer, Shop> loadedShops;
	private Map<Player, Shop> opened;
	private static String putItem = ChatColor.BLUE.toString() + "Insert the item you wish to sell.";
	private static ItemStack advertise, newOffer;
	public ShopManager(SQLManager sql, FarmTopia main){
		this.sql = sql;
		this.main = main;
		advertise = new ItemStack(Material.GOLD_BLOCK, 1);
		ItemMeta met = advertise.getItemMeta();
		met.setDisplayName(ChatColor.GOLD.toString() + "Advertise an Offer");
		advertise.setItemMeta(met);
		newOffer = new ItemStack(Material.EMERALD, 1);
		ItemMeta met2 = newOffer.getItemMeta();
		met2.setDisplayName(ChatColor.GREEN.toString() + "Sell Item");
		newOffer.setItemMeta(met2);		
		
		main.getCommand("shop").setExecutor(this);
		this.makeTable();
		loadedShops = new HashMap<Integer, Shop>();
		this.opened = new HashMap<Player, Shop>();
		FarmTopia.getFarmTopia().getServer().getPluginManager().registerEvents(this, FarmTopia.getFarmTopia());
		
		//this.search = new ItemStack(Material.BOW, 1, ());
	}
	public static ItemStack getAdvertiseItem() {
		return advertise;
	}
	public static ItemStack getNewOfferItem() {
		return newOffer;
	}
	public SQLManager getSql(){
		return this.sql;
	}
	public void openShop(Player p, int id, int lvl) {
		db("opening shop: " + id);
		if(id < 0) {
			db("loading blank shop");
			return;
		}
		if(loadedShops.containsKey(id)) {
			Shop s = loadedShops.get(id);
			s.open(p);
			opened.put(p, s);
		}else {
			db("shop not in cache. Attempting to find?");
			
		}
		
	}
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getInventory().getName().equals(putItem)) {
			checkOff((Player)e.getPlayer(), e.getInventory());
		}
		if(this.opened.containsKey(e.getPlayer())) {
			this.opened.remove(e.getPlayer());
		}
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if(this.opened.containsKey(e.getPlayer())) {
			this.opened.remove(e.getPlayer());
		}
	}
	private void checkOff(Player p, Inventory i) {
		short amt = 0;
		Resource r = null;
		boolean success = true;
		for(int x = 0; x < i.getSize(); x++) {
			ItemStack s = i.getItem(x);
			if(s != null && !s.equals(Constant.getOk())) {
				Resource find = Resource.getByItem(s);
				if(find==null || find == Resource.NOTHING) {
					p.sendMessage("found non-resource item???");
					success = false;
					break;
				}
				if(r == null) {
					r = find;
				}else if(r != find){
					p.sendMessage(ChatColor.RED.toString() + "You may only sell items of the same type in one offer!");
					success = false;
					break;
				}else {
					amt += s.getAmount();
				}
			}
		}
		if(!success || r==null|| amt == 0) {
			Arrays.stream(i.getContents()).forEach(o -> Constant.forceGive(p, o));
			p.sendMessage(ChatColor.RED.toString() + "New offer making cancelled.");
			return;
		}else {
			final Shop s = loadedShops.get(p);
			final Resource res = r;
			final short a = amt;
			if(s.needsId()) {
				this.getNewShopId(new Consumer<Integer>() {
					@Override
					public void accept(Integer t) {
						s.setShopId(t);
						newOffer(res,a, 50.0, t, getNewItemConsumer(s));
						p.sendMessage(ChatColor.GREEN.toString() + "New offer added successfully!");
					}
				});
			}else {
				newOffer(res,a, 50.0, s.getId(), getNewItemConsumer(s));
				p.sendMessage(ChatColor.GREEN.toString() + "New offer added successfully!");
			}
		}
	}
	private Consumer<ShopOffer> getNewItemConsumer(final Shop s){
		return new Consumer<ShopOffer>() {

			@Override
			public void accept(ShopOffer t) {
				s.addOffer(t);
			}
			
		};
	}
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getClickedInventory() != null && e.getClickedInventory().equals(e.getView().getTopInventory())
				&& e.getClickedInventory().getName().equals(putItem)) {
			if(e.getCurrentItem().equals(Constant.getOk())) {
				checkOff((Player)e.getWhoClicked(), e.getClickedInventory());
				e.setCancelled(true);
				return;
			}else {
				e.setCancelled(false);
				return;
			}
		}else
		if(e.getClickedInventory() != null && e.getClickedInventory().equals(e.getView().getTopInventory())
				&& opened.containsKey((Player)e.getWhoClicked())) {
			db("clicked in an open inv");
			if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
				db("current is null. Cancelled.");
				e.setCancelled(true);
				return;
			}
			e.setCancelled(true);
			Player pl = (Player) e.getWhoClicked();
			Shop s = opened.get(pl);
			int sl = e.getSlot();
			ItemStack stack = e.getClickedInventory().getItem(sl);
			if(stack != null) {
				if(stack.getType() == Material.BARRIER) {
					if(pl.equals(s.getOwner())) {
						db("Barrier: owner.");
						ShopOffer o = s.getOffer(sl);
						if(o.isTaken()) {
							Economy.addBal(pl.getUniqueId(), o.getPrice());
							removeOffer(o.getId());
							s.removeOffer(sl);
							return;
						}else {
							db("WEIRD ERRORRRRRRR");
							return;
						}
					}else {
						Constant.wrong(pl);
						db("Barrier: not owner.");
						return;
					}
				}else if(!pl.equals(s.getOwner())){
					ShopOffer o = s.getOffer(sl);
					if(o != null) {
						if(Economy.hasEnough(pl.getUniqueId(), o.getPrice())) {
							if(!Constant.isFull(pl)) {
								Economy.subtractBal(pl.getUniqueId(), o.getPrice());
								Constant.forceGive(pl, o.getItem().toItemStack(o.getAmount()));
								o.setTaken(true);
								updateOffer(o.getId());
								pl.sendMessage(ChatColor.GREEN.toString() + "Item bought!");
								return;
							}else {
								pl.sendMessage(ChatColor.RED.toString() + "You have no room in your inventory for this!");
								Constant.wrong(pl);
								return;
							}
						}else {
							pl.sendMessage(ChatColor.RED.toString() + "You need " + Economy.remainder(pl.getUniqueId(), o.getPrice()) + " more to buy this!");
							Constant.wrong(pl);
							return;
						}
					}else {
						db("WRONG OFFER: " + sl);
					}
				}else {
					if(stack.equals(newOffer)) {
						if(!s.isFull()) {
							Inventory i = Bukkit.createInventory(null, 27, putItem);
							i.setItem(26, Constant.getOk());
							opened.remove(e.getWhoClicked());
							e.getWhoClicked().openInventory(i);
							e.setCancelled(true);
							return;
						}else {
							pl.sendMessage(ChatColor.RED.toString() + "Your shop is full! Upgrade this building to unlock more offer slots!");
							Constant.wrong(pl);
							return;
						}
					}else if(stack.equals(advertise)) {
						//TODO advertisements
					}else {
						ShopOffer o = s.getOffer(sl);
						if(o != null) {
							if(!Constant.isFull(pl)) {
								short amt = o.getAmount();
								while(amt > 0) {
									if(amt > 64) {
										Constant.forceGive(pl, o.getItem().toItemStack(64));
										amt -= 64;
									}else {
										Constant.forceGive(pl, o.getItem().toItemStack(amt));
										amt = 0;
									}
								}
								s.removeOffer(sl);
								removeOffer(o.getId());
								//Constant.forceGive(pl, );
							}else {
								pl.sendMessage(ChatColor.RED.toString() + "You have no room in your inventory for this!");
								Constant.wrong(pl);
								return;
							}
						}else {
							db("WRONG OFFER: " + sl);
						}
					}

				}
			}else {
				db("stack is null");
			}
		}
	}
	@EventHandler
	public void onClose(InventoryClickEvent e) {
		if(opened.containsKey((Player)e.getInventory())) {
			opened.remove((Player)e.getInventory());
		}
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
							+ " `item` SMALLINT NOT NULL , "
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
	public void removeOffer(int id) {
		BukkitRunnable br = new BukkitRunnable(){
			@Override
			public void run() {
				Connection c = null;
				ResultSet rs = null;
				PreparedStatement ps = null, p2=null;
				try {
					c = sql.getConnection();
					ps = c.prepareStatement("DELETE FROM shop WHERE id=?;");
					ps.setInt(1, id);
					db("attempting to deleted offer " + id);
					if(ps.executeUpdate() >0) {
						db("deleted offer " + id);
					}else {
						db("FAILED AT deleting offer " + id);
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
	public void removeAllOffers(int Shopid) {
		BukkitRunnable br = new BukkitRunnable(){
			@Override
			public void run() {
				Connection c = null;
				ResultSet rs = null;
				PreparedStatement ps = null, p2=null;
				try {
					c = sql.getConnection();
					ps = c.prepareStatement("DELETE FROM shop WHERE shopid=?;");
					ps.setInt(1, Shopid);
					db("attempting to deleted offer " + Shopid);
					int x = ps.executeUpdate();
					if(x >0) {
						Debug.getInstance().log(Debug.Type.GENERAL, "Removed all offers from shop: " + Shopid + ". Removed " + x + " offers.");
					}else {
						db("FAILED AT deleting offer " + Shopid);
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
	public void updateOffer(int id) {
		BukkitRunnable br = new BukkitRunnable(){
			@Override
			public void run() {
				Connection c = null;
				ResultSet rs = null;
				PreparedStatement ps = null, p2=null;
				try {
					c = sql.getConnection();
					ps = c.prepareStatement("UPDATE shop SET taken=? WHERE id=?;");
					ps.setBoolean(1, true);
					ps.setInt(2, id);
					db("attempting to set offer " + id + " to taken.");
					if(ps.executeUpdate() >0) {
						db("set offer " + id + " to taken.");
					}else {
						db("FAILED AT settng offer " + id + " to taken.");
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
	public void getNewShopId(Consumer<Integer> newshop) {
		BukkitRunnable br = new BukkitRunnable(){
			@Override
			public void run() {
				Connection c = null;
				ResultSet rs = null;
				PreparedStatement ps = null, p2=null;
				Integer ret = 0;
				try {
					c = sql.getConnection();
					ps = c.prepareStatement("SELECT MAX(shopid) FROM shop;");
					db("attempting to find new shop id");
					rs = ps.executeQuery();
					if(rs.next()) {
						ret = rs.getInt(1);
					}else {
						db("RETURNED NULL WHILST LOOKING FOR NEW ID. HELPPPPPPP");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					sql.close(c);
					sql.close(ps);
					sql.close(rs);
					sql.close(p2);
					newshop.accept(ret);
				}
			}
		};
		br.runTaskAsynchronously(main);
	}
	public void getShop(int shopId, Consumer<List<ShopOffer>> create){
		BukkitRunnable br = new BukkitRunnable(){
			@Override
			public void run() {
				Connection c = null;
				ResultSet rs = null;
				PreparedStatement ps = null, p2=null;
				List<ShopOffer> ret = new ArrayList<ShopOffer>();
				try {
					c = sql.getConnection();
					ps = c.prepareStatement("SELECT * FROM shop WHERE shopid=?;");
					rs = ps.executeQuery();
					while(rs.next()) {
						int id = rs.getInt("id");
						int item = rs.getInt("item");
						short amount = rs.getShort("amount");
						double price = rs.getDouble("price");
						Timestamp created = rs.getTimestamp("created");
						boolean taken = rs.getBoolean("taken");
						ret.add(new ShopOffer(id, Resource.getById((byte) item), amount, price, created, shopId, taken));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					sql.close(c);
					sql.close(ps);
					sql.close(rs);
					sql.close(p2);
					create.accept(ret);
				}
			}
		};
		br.runTaskAsynchronously(main);
	}
	public void newOffer(Resource item, short amount, double price, int shopId, Consumer<ShopOffer> create){
		BukkitRunnable br = new BukkitRunnable(){
			@Override
			public void run() {
				Connection c = null;
				ResultSet rs = null;
				PreparedStatement ps = null, p2=null;
				ShopOffer so = null;
				try {
					c = sql.getConnection();
					ps = c.prepareStatement("INSERT INTO shop (item, amount, price, created, taken, shopid) OUTPUT Inserted.id"
							+ " VALUES (?,?,?,?,?,?);");
					ps.setInt(1, item.getId());
					ps.setInt(2, amount);
					ps.setDouble(3, price);
					Timestamp s = Timestamp.valueOf(LocalDateTime.now());
					ps.setTimestamp(4, s);
					ps.setBoolean(5, false);
					ps.setInt(6, shopId);
					db(ps.toString());
					rs = ps.executeQuery();
					db(rs.toString());
					if(rs.next()){
						int id = rs.getInt("id");
						db("got new id: " + id);
						so = new ShopOffer(id, item, amount, price, s,shopId, false);
						
					}else {
						throw new SQLException("rs not given????");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					sql.close(c);
					sql.close(ps);
					sql.close(rs);
					sql.close(p2);
					create.accept(so);
				}
			}
		};
		br.runTaskAsynchronously(main);
	}
	public static ItemStack getItemFrom(ShopOffer s, boolean owner) {
		return getItemFrom(s.getItem(), s.getAmount(), s.getPrice(), s.isTaken(), owner);
	}
	public static ItemStack getItemFrom(Resource item, short amount, double price, boolean taken, boolean owner){
		ItemStack ret;
		if(taken) {
			ret = new ItemStack(Material.BARRIER, (amount <= 64) ? amount : 64);
		}else {
			ret = item.toItemStack((amount <= 64) ? amount : 64);
		}
		ItemMeta retm = ret.getItemMeta();
		List<String> lore = new ArrayList<String>();
		if(taken) {
			lore.add(ChatColor.RED.toString() + "SOLD");
			if(owner) {
				lore.add(" ");
				lore.add(ChatColor.GREEN.toString() + "Item: " + item.getName());
				lore.add(ChatColor.GREEN.toString() + "Amount: " + ChatColor.GOLD.toString() + amount);
				lore.add(ChatColor.GREEN.toString() + "Price: " + ChatColor.DARK_GREEN.toString() + NumberFormat.getCurrencyInstance().format(price));
				lore.add(" ");
				lore.add(ChatColor.GOLD.toString() + "Click to claim money.");
			}
		}else {
			lore.add(ChatColor.GREEN.toString() + "Amount: " + ChatColor.GOLD.toString() + amount);
			lore.add(ChatColor.GREEN.toString() + "Price: " + ChatColor.DARK_GREEN.toString() + NumberFormat.getCurrencyInstance().format(price));
		}
		retm.setLore(lore);
		ret.setItemMeta(retm);
		return ret;
	}
//	public void search(String searchString, int page, Consumer<ItemStack[]> ret){
//		BukkitRunnable br = new BukkitRunnable(){
//
//			@Override
//			public void run() {
//				Connection c = null;
//				PreparedStatement ps = null;
//				ResultSet rs = null;
//				try{
//					c = sql.getConnection();
//					ps = c.prepareStatement("SELECT item, amount, price FROM shop"
//							+ " WHERE item LIKE ? AND taken='0' "
//							+ "ORDER BY "
//							+ "CASE WHEN item = ? THEN 0 "
//							+ "WHEN item LIKE ? THEN 1 "
//							+ "WHEN item LIKE ? THEN 2 "
//							+ "WHEN item LIKE ? THEN 3 "
//							+ "ELSE 4 END, LENGTH(item), created LIMIT " + page*45 +", "+ (page+1)*45);
//					ps.setString(1, "%" + searchString + "%");
//					ps.setString(2, searchString);
//					ps.setString(3, searchString + "%");
//					ps.setString(4, "%" + searchString + "%");
//					ps.setString(5, "%" + searchString);
//					rs = ps.executeQuery();
//					List<ItemStack> r = new ArrayList<ItemStack>();
//					while(rs.next()){
//						r.add(getItemFrom(rs.getString("item"), rs.getShort("amount"), rs.getDouble("price")));
//					}
//					if(r.isEmpty()){
//						ret.accept(null);
//					}else{
//						ret.accept(r.toArray(new ItemStack[0]));
//					}
//				}catch(SQLException e){
//					ret.accept(null);
//				}finally{
//					sql.close(rs);
//					sql.close(ps);
//					sql.close(c);
//				}
//				
//			}
//			
//		};
//		br.runTaskAsynchronously(this.main);
//		
//	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(cmd.getName().equalsIgnoreCase("shop")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				
			}
		}
		return false;
	}
	private static void db(String s) {
		Debug.getInstance().log(Debug.Type.SHOP, s);
	}
}
