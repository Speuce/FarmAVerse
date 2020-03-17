package main.java.com.speuce.farmtopia.main;

import main.java.com.speuce.farmtopia.commands.Item;
import main.java.com.speuce.farmtopia.craft.CraftingManager;
import main.java.com.speuce.farmtopia.farm.FarmManager;
import main.java.com.speuce.farmtopia.farm.Tutorial;
import main.java.com.speuce.farmtopia.jobs.JobManager;
import main.java.com.speuce.farmtopia.plot.BuildQueue;
import main.java.com.speuce.farmtopia.shop.ShopManager;
import main.java.com.speuce.farmtopia.util.Economy;
import main.java.com.speuce.schemetic.SchemeticManager;
import main.java.com.speuce.sql.SQLManager;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.scheduler.BukkitRunnable;


public class FarmTopia extends MainCommandListener implements Listener{
	private static FarmTopia instance = null;
	private SQLManager sql;
	private SchemeticManager schem;
	private FarmManager fm;
	private BukkitRunnable timer1;
	private CraftingManager cm;
	private JobManager jm;
	private ChunkGenerator chunk;
	private ShopManager shop;
	@Override
	public void onEnable(){
//		this.sql = new SQLManager(this);
//		this.stats = new StatsManager(this.sql, this);
//		this.tm = new TransactionManager(this, this.stats);
//		this.ct = new CrateUI(this, this.stats, this.tm);
//		this.chestMan = new ChestsManagers(this.sql, this.stats, this, this.ct, this.tm);
//		this.cosman = new CosmeticManager(this.sql, this.stats, this, this.chestMan);
//		this.getCommand("cosmetic").setExecutor(this.cosman);
//		this.getCommand("fdebug").setExecutor(this);
//		this.listeningfor = DebugLevel.MAJOR;
		//TODO register commands in yml
		instance = this;
		Tutorial.setPlugin(this);
		this.sql = new SQLManager(this);
		Economy.setPlugin(this);
		this.schem = new SchemeticManager(this, 100);
		this.jm = new JobManager(this);
		this.fm = new FarmManager(this, sql, this.jm);
		chunk = new ChunkGenerator(){
			
		};


		this.getServer().getPluginManager().registerEvents(this, this);
		this.timer1 = BuildQueue.start();
		this.timer1.runTaskTimerAsynchronously(this, 50L, 5L);
		this.cm = new CraftingManager(this);
		this.shop = new ShopManager(sql, this);
		
	}

	/**
	 * Safely registers the command assosciated with the main class.
	 */
	private void registerCommands(){
		safeCommandRegister("item", new Item());
		safeCommandRegister("balance", this);
		safeCommandRegister("eco", this);
		safeCommandRegister("debug", this);
		safeCommandRegister("gm", this);
	}

	@Override
	public void onDisable(){
		this.timer1.cancel();
		this.jm.disable();
		//this.cosman.disable();
		//this.chestMan.disable();
	}
	public SchemeticManager getSchem(){
		return this.schem;
	}

	public static FarmTopia getFarmTopia(){
		return instance;
	}

	public ShopManager getShop() {
		return this.shop;
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return chunk;
	}

	public CraftingManager getCraftingManager(){
		return this.cm;
	}

	public FarmManager getFarmManager(){
		return this.fm;
	}
}
