package main.java.com.speuce.farmtopia.util;

import java.util.Random;

public class RandomChance {
	private int base;
	private int variant;
	public RandomChance(int base, int variant) {
		super();
		this.base = base;
		this.variant = variant;
	}
	public int getBase() {
		return base;
	}
	public int getVariant() {
		return variant;
	}
	public int getRandom(){
		if(variant > 0){
			Random r = new Random();
			return r.nextInt(variant+1)+base;
		}else{
			return base;
		}

	}
}
