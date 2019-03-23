package com.speuce.farmtopia.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import com.speuce.farmtopia.main.FarmTopia;

public class Debug{
	
	public enum Type{
		GENERAL,
		SQL,
		FARM,
		SHOP;
		
		public static Type match(String s) {
			for(Type t: Type.values()) {
				if(t.toString().equalsIgnoreCase(s)) {
					return t;
				}
			}
			return null;
		}
	}
	private Set<Type> types; 
	private boolean log = false;
	File f, folder;
	FileWriter fw = null; PrintWriter pw = null;
	public Debug() {
		this.types = new HashSet<Type>();
		this.folder = new File(FarmTopia.getFarmTopia().getDataFolder().getAbsolutePath() + File.separator + "logs");
	}
	public boolean isLogging() {
		return this.log;
	}
	public void setLog(boolean b) {
		this.log = b;
		if(b) {
			if(f == null) {
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/HH/mm/ss");
				Date date = new Date();
				f = new File(this.folder, "Log " + dateFormat.format(date) + ".txt");
			}
		}
	}
	public boolean hasLog(Type t) {
		return types.contains(t);
	}
	public boolean toggleType(Type t) {
		if(types.contains(t)) {
			types.remove(t);
			return false;
		}else {
			types.add(t);
			return true;
		}
		
	}
	public void log(Type t, String msg) {
		if(types.contains(t)) {
			FarmTopia.getFarmTopia().getLogger().log(Level.INFO, msg);
			if(log) {
				
				try {
					fw = new FileWriter(f.getAbsolutePath());
					pw = new PrintWriter(fw);
					pw.println(java.time.LocalTime.now() + " "+msg);
					pw.close();
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public void log(String msg) {
		log(Type.GENERAL, msg);
	}
	
	
	private static Debug instance = null;
	
	public static Debug getInstance() {
		if(instance == null) {
			instance = new Debug();
		}
		return instance;
	}
	
}
