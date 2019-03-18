package com.speuce.farmtopia.crop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import com.speuce.farmtopia.resources.Resource;
import com.speuce.farmtopia.util.RandomChance;

public enum CropType {
	NULLIO(Resource.NOTHING, null, 1, null, null, null, 1F, null,0, Family.NONE, 0),
	WHEAT(Resource.WHEAT_SEEDS, new String[]{"wheat1", "wheat2", "wheat3"}, 75L,
			new Resource[]{Resource.WHEAT_SEEDS, Resource.WHEAT}, 
			new RandomChance[]{new RandomChance(1,1), new RandomChance(1,2)}, Sound.BLOCK_GRASS_PLACE, 0F,
			new RandomChance(4,3),1, Family.GRASS, 0),
	BARREN(Resource.NOTHING, new String[]{"barren1", "og1"}, 1200, null, null, null,
			1F, null, 0, Family.NONE, 0),
	APPLE(Resource.APPLE_SEEDS, new String[]{"apple1", "apple2", "apple3", "apple4", "apple5", "apple6", "apple7"},
			1800L, new Resource[]{Resource.APPLE}, new RandomChance[]{new RandomChance(7,6)}, null, 1F,
			new RandomChance(60,100),8, Family.ROSE, 50),
	WHEATGRASS(Resource.WHEATGRASS_SEEDS, new String[]{"wheatgrass1", "wheatgrass2","wheatgrass3","wheatgrass4","wheatgrass5","wheatgrass6"},
			200L, new Resource[]{Resource.WHEATGRASS_SEEDS}, new RandomChance[]{new RandomChance(10,3)},
			null, 1F, new RandomChance(5,3),4, Family.GRASS, 20),
	ROSE(Resource.ROSE_SEEDS, new String[]{"rose1","rose2","rose3","rose4","rose5"}, 1200L,
			new Resource[]{Resource.ROSE_SEEDS, Resource.ROSE},
			new RandomChance[]{new RandomChance(2,3), new RandomChance(2,5)}, null, 1F,
			new RandomChance(15,10), 8, Family.ROSE, 20),
	THISTLE(Resource.THISTLE_SEEDS, new String[]{"thistle1","thistle2","thistle3","thistle4"},
			600L, new Resource[]{Resource.THISTLE_SEEDS}, new RandomChance[]{new RandomChance(2,2)},
			null, 1F, new RandomChance(3,1),2, Family.GRASS, 30),
	MAGIC(Resource.MAGIC_SEEDS, new String[]{"magic1","magic2","magic3","magic4","magic5"}, 2700L,
			new Resource[]{Resource.THISTLE_SEEDS}, new RandomChance[]{new RandomChance(1,0)},
			null, 1F, new RandomChance(1,0), 0, Family.NONE, 0),
	CORN(Resource.KERNALS, new String[]{"corn1", "corn2","corn3","corn4","corn5","corn6","corn7"}, 600L,
			new Resource[]{Resource.CORN, Resource.KERNALS}, new RandomChance[]{new RandomChance(15, 10), new RandomChance(0, 2)}, Sound.BLOCK_GRASS_BREAK,
			0F, new RandomChance(2, 8), 4, Family.GRASS, 15),
	SUGAR_CANE(Resource.SUGAR_CANE_SEEDS, new String[]{"sugarcane1", "sugarcane2", "sugarcane3", "sugarcane4",
			"sugarcane5"}, 1440L, new Resource[]{Resource.SUGAR_CANES, Resource.SUGAR_CANE_SEEDS}, new
			RandomChance[]{new RandomChance(18, 12), new RandomChance(0, 1)}, Sound.BLOCK_GRASS_PLACE, 1.6F, 
					new RandomChance(5, 9),3, Family.GRASS, 25),
	SPRUCE(Resource.PINECONE, new String[]{"spruce1","spruce2","spruce3","spruce4","spruce5","spruce6",
			"spruce7","spruce8","spruce9"}, 1800L, new Resource[]{Resource.SPRUCE_LOG, Resource.PINECONE},
			new RandomChance[]{new RandomChance(5,3), new RandomChance(2,3)}, Sound.BLOCK_WOOD_BREAK, 0F,
			new RandomChance(15, 5), 5, Family.PINE, 35);
	
	private Resource seed;
	private String[] schems;
	private long growthTime;
	private Resource[] reward;
	private RandomChance[] amounts;
	private Sound harvestSound;
	private float soundPitch;
	private RandomChance xp;
	private byte fertilityNeeded;
	private Family family;
	private int weight;
	private CropType(Resource seed, String[] schems, long growthTime,
			Resource[] reward, RandomChance[] amounts,Sound harvestSound,
			float pitch, RandomChance xp, int fertilityNeeded, Family fam, int weight){
		if(reward != null){
			if(reward.length != amounts.length){
				throw new IllegalArgumentException("Rewards length MUST be equal to amounts length");
			}
		}
		this.fertilityNeeded = (byte)fertilityNeeded;
		this.seed = seed;
		this.xp = xp;
		this.harvestSound = harvestSound;
		this.schems = schems;
		this.soundPitch = pitch;
		this.growthTime = growthTime*1000L;
		this.reward = reward;
		this.amounts = amounts;
		this.family = fam;
		this.weight = weight;
	}
	public boolean hasFamily(){
		return this.family != null && this.family != Family.NONE;
	}
	public int getMinLvl(){
		return this.seed.getLevel();
	}
	public Family getFamily(){
		return this.family;
	}
	public int getWeight(){
		return this.weight;
	}
	public static List<CropType> getAllInFamily(Family f){
		List<CropType> ret = new ArrayList<CropType>();
		for(CropType t: CropType.values()){
			if(t.getFamily() == f && t.getWeight() != 0){
				ret.add(t);
			}
		}
		return ret;
	}

	public byte getFertilityNeeded(){
		return this.fertilityNeeded;
	}
	public ItemStack[] getItems(int mult){
		ItemStack[] ret = new ItemStack[this.reward.length];
		for(int x = 0; x < ret.length; x++){
			ret[x] = new ItemStack(reward[x].toItemStack(amounts[x].getRandom() * mult));
		}
		return ret;
	}
	public RandomChance getExp(){
		return this.xp;
	}
	public Resource getSeed() {
		return seed;
	}
	public String[] getSchems() {
		return schems;
	}
	public long getTotalGrowTime(){
		if(this != NULLIO){
			return this.growthTime * (schems.length);
		}else{
			return 0L;
		}

	}
	public int getMaxStage(){
		if(this != NULLIO){
			return this.schems.length-1;
		}else{
			return 0;
		}

	}
	public long getOneGrowthTime() {
		return growthTime;
	}
	public Resource[] getReward() {
		return reward;
	}
	public RandomChance[] getAmounts() {
		return amounts;
	}
	public Sound getHarvestSound() {
		return harvestSound;
	}
	public float getSoundPitch() {
		return soundPitch;
	}
	public byte getId(){
		return (byte)this.ordinal();
	}
	public static CropType getById(byte id){
		return CropType.values()[id];
	}
	public static CropType getBySeed(Resource seed){
		if(seed != Resource.NOTHING && seed != null){
			for(CropType t: CropType.values()){
				if(t.getSeed() == seed){
					return t;
				}
			}
		}
		return NULLIO;
	}
	
	
}
