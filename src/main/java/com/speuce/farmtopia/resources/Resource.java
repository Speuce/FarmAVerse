package main.java.com.speuce.farmtopia.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.java.com.speuce.farmtopia.util.Constant;
import main.java.com.speuce.farmtopia.util.RandomChance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public enum Resource {
	NOTHING("", Material.AIR, new String[]{}),
	WHEAT(ChatColor.YELLOW + "Wheat", Material.WHEAT,
			new String[]{ChatColor.GOLD + "The main staple in a well-balanced diet!"}, 2, 1),
	MAGIC_DUST(ChatColor.DARK_PURPLE + "Magic Dust", Material.GLOWSTONE_DUST, 
			new String[]{ChatColor.AQUA + "From the ashes of the Pixie Tree"},0, 0),
	WHEAT_SEEDS(ChatColor.GREEN.toString() + "Wheat Seeds", Material.WHEAT_SEEDS, 
			new String[]{ChatColor.YELLOW.toString() + "The starting point of all things great.."}, 0, 0),
	WHEAT_BUNDLE(ChatColor.YELLOW.toString() + "Bundle of Wheat", Material.HAY_BLOCK,
			new String[]{ChatColor.GREEN.toString() + "A Space-Saver!"}, 9, 4, new RandomChance(2, 3)),
	XP_BOTTLE_SMALL(ChatColor.GREEN.toString() + "Small Bottle Of EXP", Material.EXPERIENCE_BOTTLE,
			new String[]{ChatColor.LIGHT_PURPLE.toString() + "Instant."}),
	APPLE_SEEDS(ChatColor.GREEN.toString() + "Apple Seeds", Material.BEETROOT_SEEDS,
			new String[]{ChatColor.RED.toString() + "Plant a Seed,", ChatColor.RED.toString() + "Grow a Life."}
	, 0D, 12),
	APPLE(ChatColor.RED.toString() + "Apple", Material.APPLE,
			new String[]{ChatColor.RED.toString() + "Red Delicious."}, 10.75, 10),
	WHEATGRASS_SEEDS(ChatColor.GREEN.toString() + "Wheatgrass Seeds", Material.WHEAT_SEEDS,
			new String[]{ChatColor.YELLOW.toString() + "An Ancient Grain."}, 2, 5),
	GRAPES(ChatColor.DARK_PURPLE.toString() + "Grapes", Material.BOW,
			new String[]{ChatColor.DARK_GREEN.toString() + "Dionysus liked them."}, 0, 15, (byte)3),
	PINECONE(ChatColor.GOLD + "Pine Cone", Material.BOW,
			new String[]{ChatColor.GREEN.toString() + "Good for throwing."}, 0, 20, (byte)4),
	CORN(ChatColor.YELLOW.toString() + "Corn", Material.BOW,
			new String[]{ChatColor.GOLD.toString() + "960 Million tonnes were produced in 2016"},
			1.5, 3, (byte)5, new RandomChance(4, 8)),
	CHERRY(ChatColor.LIGHT_PURPLE.toString() + "Cherry", Material.BOW,
			new String[]{ChatColor.DARK_PURPLE.toString() + 
					"Once upon a time, in Kansas,", ChatColor.DARK_PURPLE.toString() + 
					"serving ice cream on cherry pie was banned."}, 
			0, 25, (byte)6),
	THISTLE_SEEDS(ChatColor.GREEN.toString() + "Thistle Seeds", Material.PUMPKIN_SEEDS,
			new String[]{ChatColor.DARK_GREEN.toString() + "A Weed is a Weed is a Weed."}, 0, 15),
	MAGIC_SEEDS(ChatColor.LIGHT_PURPLE.toString() + "Magic Seeds", Material.PUMPKIN_SEEDS,
			new String[]{ChatColor.DARK_PURPLE.toString() + "Disclaimer: Does not grow a bean stalk to the skies."},
			0, 15,(byte)0, true),
	SPRUCE_LOG(ChatColor.DARK_RED.toString() + "Spruce Log", Material.SPRUCE_LOG, 
			new String[]{ChatColor.YELLOW.toString() + "Speuce's Favourite"}, 20, 25, (byte)1),
	ROSE(ChatColor.RED.toString() + "Rose", Material.POPPY,
			new String[]{ChatColor.DARK_RED.toString() + "A symbol of " + ChatColor.ITALIC.toString() + "love."},
			7, 10),
	ROSE_SEEDS(ChatColor.GREEN.toString() + "Rose Seeds", Material.MELON_SEEDS,
			new String[]{ChatColor.DARK_GREEN.toString() + "Not as pretty as what they grow."}, 0, 10),
	GRASS_ESSENCE(ChatColor.GREEN.toString() + "Grass Essence", Material.LIME_DYE,
			new String[]{ChatColor.DARK_GREEN.toString() + "Smells grassy."}, 0,0,(byte)10),
	ROSE_ESSENCE(ChatColor.RED.toString() + "Rose Essence", Material.ROSE_RED,
			new String[]{ChatColor.DARK_RED.toString() + "Very Merry Red."}, 0,4,(byte)1),
	SPECIAL_COOKIE(ChatColor.LIGHT_PURPLE.toString() + "Special Cookie", Material.COOKIE,
	new String[]{ChatColor.GREEN.toString() + "Given By Speuce to those worthy.", 
			ChatColor.GREEN.toString() + "Very rare.", ChatColor.GREEN.toString() + "Very useless."},0,0),
	KERNALS(ChatColor.YELLOW.toString() + "Kernals", Material.BEETROOT_SEEDS,
			new String[]{ChatColor.GREEN.toString() + "Its POPPIN"}, 0, 0),
	SUGAR_CANES(ChatColor.GREEN.toString() + "Sugar Canes", Material.SUGAR_CANE, new String[]{
	ChatColor.DARK_GREEN.toString() + "Sweet. Just like you."}, 1.5,5, new RandomChance(7, 8)),
	SUGAR_CANE_SEEDS(ChatColor.GREEN.toString() + "Sugar Cane Seeds", Material.WHEAT_SEEDS, 
			new String[]{ChatColor.YELLOW.toString() + "Bittersweet. Hard."}, 0, 3),
	PINE_ESSENCE(ChatColor.DARK_GREEN.toString() + "Pine Essence", Material.CACTUS_GREEN, 
			new String[]{ChatColor.GREEN.toString() + "Don't poke yourself with it."}, 0,10,(byte)2),
	DEV_WAND(ChatColor.LIGHT_PURPLE.toString() + "Dev Wand", Material.BLAZE_ROD,
			new String[]{ChatColor.DARK_RED.toString() + "You Probably Shouldn't Have this."},0,0);
	
	private String name;
	private Material mat;
	private List<String> lore;
	private double value;
	private int lvl;
	private short damage = 0;
	private RandomChance jobAmount = new RandomChance(2,5);
	private boolean enchanted = false;
	private Resource(String name, Material mat, String[] lore, double value, int lvl) {
		this.name = name;
		this.mat = mat;
		this.value = value;
		this.lvl = lvl;
		this.lore = Arrays.asList(lore);
	}
	private Resource(String name, Material mat, String[] lore, double value, int lvl, short damage, RandomChance job) {
		this(name, mat, lore, value, lvl, damage);
		this.jobAmount = job;
	}
	private Resource(String name, Material mat, String[] lore, double value, int lvl, RandomChance job) {
		this(name, mat, lore, value, lvl);
		this.jobAmount = job;
	}
	private Resource(String name, Material mat, String[] lore, double value, int lvl, short damage) {
		this.name = name;
		this.damage = damage;
		this.mat = mat;
		this.value = value;
		this.lvl = lvl;
		this.lore = Arrays.asList(lore);
	}
	private Resource(String name, Material mat, String[] lore, double value, int lvl, short damage, boolean enchanted) {
		this.name = name;
		this.damage = damage;
		this.mat = mat;
		this.value = value;
		this.lvl = lvl;
		this.enchanted = enchanted;
		this.lore = Arrays.asList(lore);
	}
	private Resource(String name, Material mat, String[] lore) {
		this.name = name;
		this.mat = mat;
		this.value = 0;
		this.lvl = 1000;
		this.lore = Arrays.asList(lore);
	}
	public RandomChance getJobAmount(){
		return this.jobAmount;
	}

	/**
	 * Get the id of this resource, used for serializing
	 */
	public byte getId() {
		return (byte)this.ordinal();
	}
	public double getValue(){
		return this.value;
	}
	public short getDamage(){
		return this.damage;
	}
	public int getLevel(){
		return this.lvl;
	}
	public String getName() {
		return name;
	}
	public Material getMat() {
		return mat;
	}
	public List<String> getLore(){
		return this.lore;
	}
	public static Resource getById(byte id){
		if(id < Resource.values().length){
			return Resource.values()[id];
		}else{
			return Resource.NOTHING;
		}

	}
	public static Resource getByItem(ItemStack i){
		if(i != null && i.hasItemMeta()){
			return getByTitle(i.getItemMeta().getDisplayName());
		}
		return NOTHING;
	}
	public static Resource getByTitle(String title){
		for(Resource s: Resource.values()){
			if(s.getName().equals(title)){
				return s;
			}
		}
		return NOTHING;
	}
	public static Resource getByName(String nom){
		for(Resource s: Resource.values()){
			if(ChatColor.stripColor(s.getName()).equalsIgnoreCase(nom)){
				return s;
			}
		}
		return NOTHING;
	}
	@SuppressWarnings("deprecation")
	public ItemStack toItemStack(int amt){
		if(this != NOTHING){
			ItemStack ret;
			ItemMeta met;
			if(this.damage == 0){
				ret = new ItemStack(getMat(), amt);
				met = ret.getItemMeta();
				met.setDisplayName(name);
				met.setLore(lore);
			}else{
				ret = new ItemStack(getMat(), amt, damage);
				met = ret.getItemMeta();
				met.setDisplayName(name);
				met.setLore(lore);
				met.spigot().setUnbreakable(true);
				met.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			}
			if(enchanted){
				met.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				met.addEnchant(Enchantment.OXYGEN, 1, true);
			}
			ret.setItemMeta(met);
			return ret;

		}else{
			return new ItemStack(Material.AIR);

		}

	}
	public static int getBytes(int protocol){
		//TODO other protocols
		return 2; 
	}
	public static ItemStack deserialize(byte[] data, int protocol){
		if(data.length != Resource.getBytes(protocol)){
			Bukkit.broadcastMessage(ChatColor.RED.toString() + "ERROR: Data input not valid with current bytes!");
			return null;
		}
		//TODO other protocols
		Resource r = Resource.getById(data[0]);
		if(r == null){
			return null;
		}
		return r.toItemStack((int)data[1]);
	}
	public static byte[] serialize(ItemStack i){
		int protocol = Constant.currentProtocol;
		if(i == null || !i.hasItemMeta()){
			return new byte[2];
		}
		if(protocol == 2 || protocol == Constant.PROTOCOL_V3){
			Resource r = Resource.getByTitle(i.getItemMeta().getDisplayName());
			if(r == null){
				return new byte[2];
			}
			byte[] ret = {r.getId(), (byte)i.getAmount()};
			return ret;
		}
		return null;
	}
	public static List<Resource> getUnder(int level){
		List<Resource> ret = new ArrayList<Resource>();
		for(Resource r: Resource.values()){
			if(r.getValue() > 0&&r.getLevel() <= level){
				ret.add(r);
			}
		}
		return ret;
	}
}
