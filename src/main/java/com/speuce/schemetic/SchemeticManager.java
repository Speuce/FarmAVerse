package main.java.com.speuce.schemetic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;

import main.java.com.speuce.farmtopia.main.FarmTopia;
import main.java.com.speuce.schemetic.Selector;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


public class SchemeticManager implements CommandExecutor {
	private Map<String, Schematic> scache;
	private JavaPlugin p;
	private File folder;
	private Selector selector;
	private static float CURRENT_VERSION = 1.00F;
	@SuppressWarnings("serial")
	public SchemeticManager(JavaPlugin p, Integer cache){
		this.p = p;
		this.scache = new LinkedHashMap<String, Schematic>(cache + 1, 0.75F, true){
			public boolean removeEldestEntry(Map.Entry<String, Schematic> eldest) {
		        return size() > cache;
		    }
		};
		this.folder = new File(p.getDataFolder().getAbsolutePath() + File.separator + "schemetics");
		if(!this.folder.exists()){
			this.folder.mkdirs();
		}
		this.selector = new Selector(p);
		p.getCommand("save").setExecutor(this);
		p.getCommand("load").setExecutor(this);
	}
	public Plugin getPlugin(){
		return this.p;
	}

	private Schematic loadSchemetic(String name) throws FileNotFoundException{
		File f = new File(this.folder, name + ".scm");
		if(!f.exists()){
			System.out.println("ERROR: COULD NOT FIND SCHEM: " + name);
			return null;
		}
		Scanner sc = new Scanner(f);
		String l1 = sc.nextLine();
		if(l1.startsWith("V")){
			l1 = l1.substring(1);
			Float version = Float.parseFloat(l1);
			if(version == 1.00){
				List<EBlock[]> lis = new ArrayList<EBlock[]>();
				List<EBlock[][]> flis = new ArrayList<EBlock[][]>();
				int zsize = 0;
				while(sc.hasNext()){
					String st = sc.nextLine();
					//Bukkit.broadcastMessage(st);
					if(!st.startsWith(";")){
						String[] str = st.split(" ");
						if(zsize == 0){
							zsize = str.length; 
						}
						EBlock[] z = new EBlock[zsize];
						int index = 0;
						for(String s: str){
							if(!s.equalsIgnoreCase("n")){
								//String[] data = s.split(":");
								try{
									z[index] = EBlock.fromString(s);
								}catch(NumberFormatException e){
									System.out.println("Error loading in schem: " + name + " on line \"" + s + "\"");
									e.printStackTrace();
								}
							}else{
								z[index] = null;
							}
							index++;
						}
						lis.add(z);

					}else{
						flis.add(lis.toArray(new EBlock[0][0]));
						lis.clear();
					}

				}
				sc.close();
				EBlock[][][] fin = flis.toArray(new EBlock[0][0][0]); 
				//Bukkit.broadcastMessage("" + fin.length + ":" + fin[0].length + ":" + fin[0][0].length);
				Schematic ret = new Schematic(fin, name);
				this.scache.put(name, ret);	
				sc.close();
				return ret;
			}else{
				FarmTopia.getFarmTopia().getLogger().log(Level.SEVERE, "ERROR: TRIED TO PARSE A SCHEM VERSION: " + version +". THIS VERSION COULD NOT BE FOUND!!!!");
				sc.close();
				return null;
			}
		}else{
			sc.close();
			System.out.println(ChatColor.RED + "Legacy Schem: " + name + " found. Attempting Conversion");
			Schematic m = null;
			try{
				m = legacyLoad(f, name);
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
			System.out.println("Legacy Schem: " + name + " successfully converted. Attempting update.");
			try {
				this.saveSchemetic(m, name);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Legacy Schem: " + name + " successfully saved.");
			return m;
		}

	}
	@SuppressWarnings("deprecation")
	public Schematic legacyLoad(File f, String name) throws FileNotFoundException{
		Scanner sc = new Scanner(f);
		List<EBlock[]> lis = new ArrayList<EBlock[]>();
		List<EBlock[][]> flis = new ArrayList<EBlock[][]>();
		int zsize = 0;
		while(sc.hasNext()){
			String st = sc.nextLine();
			//Bukkit.broadcastMessage(st);
			if(!st.startsWith(";")){
				String[] str = st.split(" ");
				if(zsize == 0){
					zsize = str.length; 
				}
				EBlock[] z = new EBlock[zsize];
				int index = 0;
				for(String s: str){
					if(!s.equalsIgnoreCase("n")){
						String[] data = s.split(":");
						try{
							z[index] = new EZBlock(Integer.parseInt(data[0]), Byte.parseByte(data[1])).convert();
						}catch(NumberFormatException e){
							System.out.println("Error loading in schem: " + name + " on line \"" + s + "\"");
							e.printStackTrace();
						}
					}else{
						z[index] = null;
					}
					index++;
				}
				lis.add(z);

			}else{
				flis.add(lis.toArray(new EBlock[0][0]));
				lis.clear();
			}

		}
		sc.close();
		EBlock[][][] fin = flis.toArray(new EBlock[0][0][0]); 
		//Bukkit.broadcastMessage("" + fin.length + ":" + fin[0].length + ":" + fin[0][0].length);
		Schematic ret = new Schematic(fin, name);
		this.scache.put(name, ret);	
		return ret;
	}
	public void saveSchemetic(Schematic s, String name) throws IOException{
		File f = new File(this.folder, name + ".scm");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileWriter fw = new FileWriter(f.getAbsolutePath());
		PrintWriter pw = new PrintWriter(fw);
		pw.println("V" + CURRENT_VERSION);
		EBlock[][][] lis = s.getBlocks();
		for(EBlock[][] x: lis){
			for(EBlock[] y: x){
				String line = "";
				for(int z = 0; z < y.length; z++){
					if(y[z] == null){
						line += "n ";
					}else{
						line += y[z].toString() + " ";
					}
				}
				pw.println(line);
			}
			pw.println(";");
		}
		pw.close();
		fw.close();
	}
	public Schematic getSchemetic(String name){
		if(this.scache.containsKey(name)){
			return this.scache.get(name);
		}else{
			try {
				return this.loadSchemetic(name);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("save")){
				if(args.length != 1){
					p.sendMessage(ChatColor.RED + "/save <name>");
					return true;
				}
				Selection s = this.selector.getSelection(p);
				if(s != null){
					try {
						this.saveSchemetic(s.getSchem(), args[0]);
						p.sendMessage(ChatColor.GREEN + "Saved..");
						return true;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return true;
					}
				}else{
					p.sendMessage(ChatColor.RED + "No Selection Made!");
					return true;
				}
			}else if(cmd.getName().equalsIgnoreCase("load")){
				if(p.isOp()){
					if(args.length >= 1){
						Schematic sc = this.getSchemetic(args[0]);
						if(sc != null){
							if(args.length > 1){
								int rotate = 0;
								for(int val = 1; val < args.length; val++){
									String str = args[val];
									if(str.startsWith("rot")){
										String[] thing = str.split(":");
										if(thing.length == 2){
											try{
												rotate = Integer.parseInt(thing[1]);
											}catch(NumberFormatException e){
												p.sendMessage("Valid Number please!");
												continue;
											}
										}else{
											p.sendMessage(ChatColor.RED.toString() + "rotate:NUM");
											continue;
										}
									}else{
										p.sendMessage(ChatColor.RED.toString() + "Unknown build parameter: " + str);
										continue;
									}
								}
								//Bukkit.broadcastMessage("building with rot: " + rotate);
								sc.buildOptimized(p.getLocation().getBlock(), this.p, rotate);
								p.sendMessage(ChatColor.GREEN + "Buidling..");
								return true;
							}else{
								sc.buildOptimized(p.getLocation().getBlock(), this.p);
								p.sendMessage(ChatColor.GREEN + "Buidling..");
								return true;
							}
							
	
						}else{
							p.sendMessage(ChatColor.RED + "Couldn't find schemetic: " + args[0]);
							return true;
						}
					}else{
						p.sendMessage(ChatColor.RED + "/load <name>");
						return true;
					}
				}else{
					p.sendMessage(ChatColor.RED + "Nice one.");
					return true;
				}
			}
		}
		return false;
	}

	
	
	
}

