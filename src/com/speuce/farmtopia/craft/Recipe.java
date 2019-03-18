package com.speuce.farmtopia.craft;

import com.speuce.farmtopia.resources.Resource;

public enum Recipe {
	WHEAT_BUNDLE(new Resource[][]{new Resource[]{Resource.WHEAT, Resource.WHEAT},
			new Resource[]{Resource.WHEAT, Resource.WHEAT}}, 2, Resource.WHEAT_BUNDLE, 1),
	WHEAT(new Resource[][]{new Resource[]{Resource.WHEAT_BUNDLE}}, 0, Resource.WHEAT, 4),
	MAGIC_SEEDS(new Resource[][]{
		new Resource[]{Resource.MAGIC_DUST, Resource.MAGIC_DUST, Resource.MAGIC_DUST},
		new Resource[]{Resource.MAGIC_DUST, Resource.THISTLE_SEEDS, Resource.MAGIC_DUST},
		new Resource[]{Resource.MAGIC_DUST, Resource.MAGIC_DUST, Resource.MAGIC_DUST}}, 15,
			Resource.MAGIC_SEEDS, 1);
	
	
	private Resource[][] shape;
	private Resource result;
	private int amount;
	private int minLv;
	private int x, y;
	private Recipe(Resource[][] shape, int minlv, Resource result, int amount){
		this.shape = shape;
		this.minLv = minlv;
		this.amount = amount;
		this.result = result;
		x = shape[0].length;
		y = shape.length;
	}
	public static Recipe getRecipe(Resource[][] current, int lv){
		int cx = current[0].length;
		int cy = current.length;
		for(Recipe e: Recipe.values()){
		
			if(lv >= e.getMinLv()){
				if(e.getX() == cx && e.getY() == cy){
					//Bukkit.broadcastMessage("recipe: " + CraftingManager.getString(e.getShape()));
					//Bukkit.broadcastMessage("actual: " + CraftingManager.getString(current));
					if(Recipe.compareShape(current, e)){
						return e;
					}
				}
			}
		}
		return null;
	}
	public static boolean compareShape(Resource[][] one, Recipe compare){
		Resource[][] comp = compare.getShape();
		for(int y = 0; y < one.length; y++){
			for(int x = 0; x < one[0].length; x++){
				if(comp[y][x] != one[y][x]){
					return false;
				}
			}
		}
		return true;
	}
	public Resource[][] getShape() {
		return shape;
	}
	public Resource getResult() {
		return result;
	}
	public int getAmount() {
		return amount;
	}
	public int getMinLv() {
		return minLv;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
}
