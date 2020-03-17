package main.java.com.speuce.farmtopia.plot;


import main.java.com.speuce.farmtopia.farm.Farm;
import main.java.com.speuce.farmtopia.plot.upgradeable.*;

public class Plots {
	
	
	
	public static int getBytes(byte id, int protocol){
		switch(id){
			case 0:
				return 1;
			case 1:
				return FarmPlot.getBytes(protocol);
			case 2:
				return TownHall.getBytes(protocol);
			case 3:
				return ResearchCentre.getBytes(protocol);
			case 4:
				return Pavillion.getBytes(protocol, 0);
			case 5:
				return Pavillion.getBytes(protocol, 1);
			case 6:
				return Pavillion.getBytes(protocol, 2);
			case 7:
				return Pavillion.getBytes(protocol, 3);
				//RESERVE to 16
			case 17: return Shop.getBytes(protocol);
			default:
				return 0;
		}
	}
	public static int getMaxPlots(Class<? extends Plot> clazz, int lv, int playerlvl){
		if(clazz.equals(FarmPlot.class)){
			if(lv < 0 || playerlvl <= 1){
				return 1;
			}else if(lv < 1){
				return 3;
			}else if(lv < 2){
				return 5;
			}else if(lv < 3){
				return 8;
			}else{
				return 12;
			}
		}else if(clazz.equals(TownHall.class)){
			return 1;
		}else if(clazz.equals(ResearchCentre.class)){
			if(lv >= 0){
				return 1;	
			}else{
				return 0;
			}
		}else if(clazz.equals(Pavillion.class)){
			if(lv <= 0){
				return 0;
			}else{
				return 3;
			}
		}else if(clazz.equals(Shop.class)){
			if(lv >= 2) {
				return 1;
			}else {
				return 0;
			}
		}else{
			return 100;
		}
		
	}
	public static Class<? extends Plot> getFromName(String nom){
		if(nom.equalsIgnoreCase("Town Hall")){
			return TownHall.class;
		}else if(nom.equalsIgnoreCase("Farm Plot")){
			return FarmPlot.class;
		}else if(nom.equalsIgnoreCase("Seed Research Centre")){
			return ResearchCentre.class;
		}else if(nom.equalsIgnoreCase("Pavillion")){
			return Pavillion.class;
		}else if(nom.equalsIgnoreCase("Shop")){
			return Shop.class;
		}else{
			return null;
		}
	}
	public static int getBytes(Plot p, int protocol){
		if(p instanceof EmptyPlot){
			return 1;
		}else if(p instanceof FarmPlot){
			return FarmPlot.getBytes(protocol);
		}else if(p instanceof TownHall){
			return TownHall.getBytes(protocol);
		}else if(p instanceof ResearchCentre){
			return ResearchCentre.getBytes(protocol);
		}else if(p instanceof Shop){
			return Shop.getBytes(protocol);
		}else if(p instanceof Pavillion){
			return Pavillion.getBytes(protocol,((Pavillion) p).getLv());
		}else{
			return 0;
		}
	}
	public static Plot deserialize(byte id, byte[] data, int protocol, Farm f){
		//TODO protocol 1
		switch(id){
			case 0:
				return new EmptyPlot(f);
			case 1:
				return FarmPlot.deserialize(data, protocol, f);
			case 2:
				return TownHall.deserialize(data, protocol, f);
			case 3:
				return ResearchCentre.deserialize(data, protocol, f);
			case 4:
				return Pavillion.deserialize(data,protocol, f, 0);
			case 5:
				return Pavillion.deserialize(data,protocol, f, 1);
			case 6:
				return Pavillion.deserialize(data,protocol, f, 2);
			case 7:
				return Pavillion.deserialize(data,protocol, f, 3);
			case 17:
				return Shop.deserialize(f, data);
			default:
				return null;
		}
	}
	public static byte getId(Plot p){
		if(p instanceof EmptyPlot){
			return (byte)0;
		}else
		if(p instanceof FarmPlot){
			return (byte)1;
		}else if(p instanceof TownHall){
			return (byte)2;
		}else if(p instanceof ResearchCentre){
			return (byte)3;
		}else if(p instanceof Pavillion){
			int lv = ((Pavillion) p).getLv();
			switch(lv){
			case 0:
				return 4;
			case 1:
				return 5;
			case 2:
				return 6;
			case 3:
				return 7;
			default:
				return 4;
			}
		}else if(p instanceof Shop){
			return (byte)17;
		}else{
			return 0;
		}
	}

}
