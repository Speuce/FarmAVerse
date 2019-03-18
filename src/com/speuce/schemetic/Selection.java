package com.speuce.schemetic;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;


public class Selection {
	private Block loc1;
	private Block loc2;
	public Block getLoc1() {
		return loc1;
	}
	public void setLoc1(Block loc1) {
		this.loc1 = loc1;
	}
	public Block getLoc2() {
		return loc2;
	}
	public void setLoc2(Block loc2) {
		this.loc2 = loc2;
	}
	public Selection(Block loc1, Block loc2) {
		this.loc1 = loc1;
		this.loc2 = loc2;
	}
	public Schematic getSchem(){
		int lowX = (loc1.getX() < loc2.getX()) ? loc1.getX() : loc2.getX();
		int lowY= (loc1.getY() < loc2.getY()) ? loc1.getY() : loc2.getY();
		int lowZ = (loc1.getZ() < loc2.getZ()) ? loc1.getZ() : loc2.getZ();
		int highX = (loc1.getX() > loc2.getX()) ? loc1.getX() : loc2.getX();
		int highY= (loc1.getY() > loc2.getY()) ? loc1.getY() : loc2.getY();
		int highZ = (loc1.getZ() > loc2.getZ()) ? loc1.getZ() : loc2.getZ();
		int ZDif = highZ - lowZ;
		int YDif = highY - lowY;
		int XDif = highX - lowX;
		Location orgin = new Location(loc1.getWorld(), lowX, lowY, lowZ);
		EBlock[][][] fin = new EBlock[XDif+1][YDif+1][ZDif+1];
		for(int x = 0; x <= XDif; x++){
			EBlock[][] column = new EBlock[YDif+1][ZDif+1];
			for(int y = 0; y <= YDif; y++){
				EBlock[] row = new EBlock[ZDif+1];
				for(int z= 0; z <= ZDif; z++){
					Block b = orgin.clone().add(x, y, z).getBlock();
					if(b.getType() == Material.AIR || b == null){
						row[z] = null;
					}else{
						row[z] = new EBlock(b.getType(), b.getBlockData());
					}
				}
				column[y] = row;
			}
			fin[x] = column;
		}
		return new Schematic(fin, "unnamed");
	}
	
}
