package com.speuce.farmtopia;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.speuce.farmtopia.crop.CropType;
import com.speuce.farmtopia.crop.Family;
import com.speuce.farmtopia.resources.Resource;


public class Constant {
	public static final int baseY = 80;
	public static final int currentProtocol = 3;
	public static final String scoreboardName = ChatColor.DARK_PURPLE.toString() + "Farm";
	public static final String setPlotName = ChatColor.DARK_PURPLE.toString() + "Select Plot";
	public static final String seedExtractorName = "Seedex";
	public static final int stairVals = 8;
	public static final String jobInvName = "Jobs";
	public static final int PROTOCOL_V3 = 3;
	public static final String[] names = {"Joe", "Aaron","Erin", "Levi", "Pam",
			"Anita", "Sandra", "Alexander", "Penny", "Ben", "Stuart",
			"Bob", "Brian", "Bailey","Matt", "Jane", "Justin", "Abel", "Susan", "Hudson", "Kara"};
	private static Set<Player> debugs = new HashSet<Player>();
	private static ItemStack ok = null;
	public static ItemStack getOk(){
		if(ok == null) {
			ok = new ItemStack(Material.EMERALD_BLOCK);
			ItemMeta met = ok.getItemMeta();
			met.setDisplayName(ChatColor.GREEN + "Ok");
			ok.setItemMeta(met);
		}
		return ok;
	}
	public static void wrong(Player pl) {
		pl.playSound(pl.getLocation(), Sound.BLOCK_ANVIL_FALL, 0.8F, 1.2F);
	}
	public static void addDebug(Player pl){
		debugs.add(pl);
	}
	public static void removeDebug(Player pl){
		debugs.remove(pl);
	}
	public static boolean hasDebug(Player pl){
		return debugs.contains(pl);
	}
	public static void debug(String s){
		for(Player pl: debugs){
			if(pl != null && pl.isOnline()){
				pl.sendMessage(s);
			}
		}
	}
	public static String itemInfo(ItemStack s){
		if(s != null){
			return "{"+s.getType().toString() + "|" + s.getAmount() + "}";
		}else{
			return "null";
		}
	}
	public static void playsound(Player p, Sound s, float pitch){
		p.playSound(p.getLocation(), s, 1F, pitch);
	}
	public static void printOut(byte... s){
		String thing = "";
		for(byte j: s){
			thing += "|" + j;
		}
		Bukkit.broadcastMessage(thing);
	}
	public static void setupStand(ArmorStand s){
		s.setBasePlate(false);
		s.setVisible(false);
		s.setGravity(false);
		s.setCanPickupItems(false);
		s.setInvulnerable(true);
		s.setCustomNameVisible(true);
		s.setRemoveWhenFarAway(false);
	}
	public static String trans(String in){
		return ChatColor.translateAlternateColorCodes('&', in);
	}
//	public static void debug(String s){
//		Player p = Bukkit.getPlayer("Speuce");
//		if(p != null){
//			p.sendMessage(s);
//		}
//	}
	public static String format(Double d){
		if(d < 0.01){
			return "FREE";
		}else{
			return NumberFormat.getCurrencyInstance().format(d);
		}
	}
	public static ItemStack getItem(Material type, byte damage, String name){
		ItemStack ret = new ItemStack(type, 1, damage);
		ItemMeta retm = ret.getItemMeta();
		retm.setUnbreakable(true);
		retm.setDisplayName(name);
		retm.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		retm.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		ret.setItemMeta(retm);
		return ret;
	}
	public static boolean isFull(Player p) {
		return p.getInventory().firstEmpty() == -1;
	}
	public static boolean isEssence(Resource s){
		return s.toString().contains("ESSENCE");
	}
	public static boolean canExtract(Resource s){
		return (s != null && s != Resource.NOTHING) && 
				(s.toString().contains("ESSENCE") || CropType.getBySeed(s).hasFamily());
	}
	public static boolean canMutate(Resource s){
		return (s != null && s != Resource.NOTHING) && 
				(s.toString().contains("ESSENCE"));
	}
	public static Family getFamilyOf(Resource s){
		Family f1 = Family.getByResource(s);
		if(f1 == Family.NONE || f1 == null){
			CropType cr = CropType.getBySeed(s);
			if(cr.hasFamily()){
				return cr.getFamily();
			}else{
				return Family.NONE;
			}
		}else{
			return f1;
		}
	}
	public static String pluralize(String in){
		if(in.endsWith("s")){
			return in;
		}else if(in.endsWith("y")){
			return in.substring(0, in.length()-1) + "ies";
		}else if(in.endsWith("g") || in.endsWith("e")){
			return in + "s";
		}else{
			return in;
		}
	}

	public static void forceGive(Player p, ItemStack i){
		if(i == null || i.equals(Constant.getOk())) {
			return;
		}
		if(p.getInventory().firstEmpty() > -1){
			if(i.getType() != Material.BOW){
				p.getInventory().addItem(i);
			}else{
				int amt = i.getAmount();
				short dmg = i.getDurability();
				for(int x = 0; x < p.getInventory().getContents().length; x++){
					ItemStack s = p.getInventory().getItem(x);
					if(s != null && s.getType() == Material.BOW && s.getDurability() == dmg){
						if(s.getAmount() < 64){
							//how much stuff we can put in this stack
							int add = 64-s.getAmount();
							if(add >= amt){
								s.setAmount(s.getAmount()+amt);
								return;
							}else{
								s.setAmount(64);
								amt -= add;
							}
						}
					}
				}
				if(amt > 0){
					//p.getInventory().addItem(new ItemStack(Material.BOW, amt, dmg));
					ItemStack add = i.clone();
					add.setAmount(amt);
					p.getInventory().addItem(add);
				}
			}

		}else{
			p.getLocation().getWorld().dropItem(p.getLocation(), i);
		}
	}
	public static int countItems(ItemStack[] items){
		int sum = 0;
		for(ItemStack s: items){
			sum += s.getAmount();
		}
		return sum;
	}
	public static Player getPlayer(String name){
		for(Player pl: Bukkit.getOnlinePlayers()){
			if(pl.getName().equalsIgnoreCase(name)){
				return pl;
			}
		}
		return null;
	}
	public static int getInt(String s){
		try{
			return Integer.parseInt(s);
		}catch(NumberFormatException e){
			return 0;
		}
	}
	public static String milliSecondsToDisplay(Long ms){ 
		if(ms < 60000){
			return "<1m";
		}
		Long minutes = ms/1000/60;
		Long hours = minutes/60;
		Long days = hours/24;
		StringJoiner sb = new StringJoiner(", ", " ", "");
		if(days > 0){
			sb.add(days + "d");
		}
		if(hours > 0){
			sb.add(hours%24 + "h");
		}
		sb.add(minutes%60 + "m");
		return sb.toString();
	}
	public static void insertIn(byte[] put, byte[] insert, int location){
		if(insert.length > put.length){
			throw new IllegalArgumentException("Insert cannot be bigger in size than put!");
		}
		for(int x = 0; x < insert.length; x++){
			put[x + location] = insert[x];
		}
	}
	public static int relativeDirectionTo(Location from, Location to){
		double xdiff = to.getX() - from.getX();
		double zdif = to.getZ() - from.getZ();
		if(Math.abs(zdif) < Math.abs(xdiff)){
			if(xdiff > 0){
				return 3;
			}else{
				return 1;
			}
		}else{
			if(zdif > 0){
				return 2;
			}else{
				return 0;
			}
		}
	}
}
