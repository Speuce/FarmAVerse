package com.speuce.farmtopia.crop;

import java.util.Arrays;

import com.google.common.primitives.Longs;
import com.speuce.farmtopia.Constant;

public class FarmSubplot {
	private CropType currentCrop;
	private long planted;
	private byte fertility;
	public FarmSubplot(CropType crop, long planted, byte fertility){
		this.currentCrop = crop;
		this.fertility = fertility;
		this.planted = planted;
	}
	public byte getFertility(){
		return this.fertility;
	}
	public void setFertility(byte f){
		this.fertility = f;
	}
	public CropType getCurrentCrop() {
		return currentCrop;
	}
	public void setCurrentCrop(CropType currentCrop) {
		this.currentCrop = currentCrop;
	}
	public long getPlanted() {
		return planted;
	}
	public void setPlanted(long planted) {
		this.planted = planted;
	}
	public void plantNewCrop(CropType crop){
		this.currentCrop = crop;
		this.planted = System.currentTimeMillis();
	}
	public int getStage(){
		long diff = System.currentTimeMillis() - planted;
		if(diff > this.currentCrop.getTotalGrowTime()){
			return this.currentCrop.getMaxStage();
		}else{
			return Math.max(0, (int) ((int)diff/this.currentCrop.getOneGrowthTime()));
		}
	}
	public boolean max(){
		return this.getStage() == this.currentCrop.getMaxStage();
	}
	public String getSchem(int stage){
		return this.currentCrop.getSchems()[stage];
	}
	public String currentSchem(){
		if(this.currentCrop == null || this.currentCrop == CropType.NULLIO){
			return "barren1";
		}
		return this.currentCrop.getSchems()[this.getStage()];
	}
	public boolean hasActualPlant(){
	//	Constant.debug(this.currentCrop.toString());
		boolean b = this.currentCrop.getAmounts() != null && this.currentCrop != CropType.BARREN && this.currentCrop != CropType.NULLIO;
	//	Constant.debug(b + "");
		return b;
	}
	public void boost(){
		this.planted -= 900 * 1000L;
	}
	public void dev(){
		this.planted = 1000L;
	}
	public void subtractSeconds(int seconds){
		this.planted-= seconds * 1000L;
	}
	public static int getBytes(int version){
		//V1 and V2
		if(version == 1 || version == 2){
			return 9;
		}else if(version == Constant.PROTOCOL_V3){
			return 10;
		}else{
			throw new IllegalArgumentException("Protocol Version: " + version + " is not Supported!");
		}
	}
	public byte[] serialize() {
		byte[] ret = new byte[10];
		ret[0] = this.getCurrentCrop().getId();
		ret[1] = this.fertility;
		byte[] lon = Longs.toByteArray(this.planted);
		for(int x = 2; x < ret.length; x++){
			ret[x] = lon[x-2];
		}
		return ret;
	}
	public static FarmSubplot deserialize(int protocol, byte[] dat){
		if(protocol == 2){
			CropType c = CropType.getById(dat[0]);
			long time = Longs.fromByteArray(Arrays.copyOfRange(dat, 1, dat.length));
			return new FarmSubplot(c, time, (byte)0);
		}else if(protocol == Constant.PROTOCOL_V3){
			CropType c = CropType.getById(dat[0]);
			byte fertility = dat[1];
			long time = Longs.fromByteArray(Arrays.copyOfRange(dat, 2, dat.length));
			return new FarmSubplot(c, time, fertility);
		}else{
			throw new IllegalArgumentException("Protocol Version: " + protocol + " is not Supported!");
		}
	}
}
