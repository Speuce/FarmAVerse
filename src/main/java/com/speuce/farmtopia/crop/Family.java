package main.java.com.speuce.farmtopia.crop;


import main.java.com.speuce.farmtopia.resources.Resource;
import main.java.com.speuce.farmtopia.util.RandomCollection;

public enum Family {
	NONE(null, new Resource[1], new int[1], 0L),
	GRASS(Resource.GRASS_ESSENCE, new Resource[]{Resource.ROSE_ESSENCE}, new int[]{100}, 90L),
	ROSE(Resource.ROSE_ESSENCE, new Resource[]{Resource.GRASS_ESSENCE, Resource.PINE_ESSENCE}, new int[]{75,25}, 360L),
	PINE(Resource.PINE_ESSENCE, new Resource[]{Resource.GRASS_ESSENCE, Resource.ROSE_ESSENCE},
			new int[]{20, 80}, 1200L);
	
	private Resource display;
	private Resource[] mutate;
	private int[] weights;
	private Long time;
	private RandomCollection<Resource> ran;
	private Family(Resource dis, Resource[] mutate, int[] weight, Long t){
		if(mutate.length != weight.length){
			throw new IllegalArgumentException("Mutate must have same length as weight");
		}
		this.time = t*1000;
		this.display = dis;
		this.mutate = mutate;
		this.weights = weight;
		this.ran = new RandomCollection<Resource>();
		for(int x = 0; x < mutate.length; x++){
			ran.add(weight[x], mutate[x]);
		}
	}
	public Long getTime(){
		return this.time;
	}
	public Resource getDisplay() {
		return display;
	}
	public Resource[] getMutate() {
		return mutate;
	}
	public int[] getWeights() {
		return weights;
	}
	public Resource mutate(){
		return ran.next();
	}
	public static Family getByResource(Resource r){
		for(Family f: Family.values()){
			if(f.getDisplay() == r){
				return f;
			}
		}
		return NONE;
	}
}
