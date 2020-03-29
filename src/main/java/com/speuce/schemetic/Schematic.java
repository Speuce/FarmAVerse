package main.java.com.speuce.schemetic;

import main.java.com.speuce.farmtopia.main.FarmTopia;
import main.java.com.speuce.schemetic.EBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;


public class Schematic {
	private EBlock[][][] blocks;
	private boolean hasSecondLayer = false;
	private EBlock[][][] secondLayer = null;
	private String nom;
	public Schematic(EBlock[][][] blocks, String nom) {
		super();
		this.nom = nom;
		this.blocks = blocks;
		for(int x = 0; x < blocks.length; x++){
			for(int y = 0; y < blocks[x].length; y++){
				for(int z = 0; z < blocks[x][y].length; z++){
					EBlock br = blocks[x][y][z];
					if(br != null){
						if(br.getType().toString().contains("TORCH")){
							if(!this.hasSecondLayer){
								this.hasSecondLayer = true;
								this.secondLayer = new EBlock[blocks.length][blocks[0].length][blocks[0][0].length];
							}
							this.secondLayer[x][y][z] = br;
						}
					}

				}
			}
		}
	}
	public String getNom(){
		return this.nom;
	}
	public EBlock[][][] getBlocks() {
		return blocks;
	}
	
	public void setBlocks(EBlock[][][] blocks) {
		this.blocks = blocks;
	}
	public EBlock[][][] rotate(EBlock[][][] in, int rotation){
		if(rotation > 3 || rotation < 0){
			throw new IllegalArgumentException("Rotation cannot be greater than 3!");
		}
		if(rotation == 0){
			return in;
		}
		if(rotation == 2){
			int l1 = in.length;
			int l2 = in[0][0].length;
			EBlock[][][] ret = new EBlock[in.length][in[0].length][in[0][0].length];
			for(int x = 0; x < l1; x++){
				for(int z = 0; z < l2; z++){
					for(int y = 0; y < in[0].length; y++){
						EBlock l = in[x][y][z];
						if(l != null){
							if(l.getDamage() instanceof Directional){
								l = l.clone();
								Directional d=(Directional) l.getDamage();
								//System.out.println("Before: " + d.getFacing().toString());
								l.setDamage(rotate(d, rotation));
								//System.out.println("After: " + ((Directional)l.getDamage()).getFacing().toString());
							}
								
						}
						ret[(l1-x)-1][y][(l2-z)-1] = l;
					}
				}
			}
			return ret;
		}
		if(rotation == 1){
			EBlock[][][] ret = new EBlock[in[0][0].length][in[0].length][in.length];
			for(int x = 0; x < ret.length; x++){
				for(int z = 0; z < in.length; z++){
					for(int y = 0; y < ret[0].length; y++){
						EBlock l = in[z][y][x];
						if(l != null){
							if(l.getDamage() instanceof Directional){
								l = l.clone();
								Directional d=(Directional) l.getDamage();
								//System.out.println("Before: " + d.getFacing().toString());
								l.setDamage(rotate(d, rotation));
								//System.out.println("After: " + ((Directional)l.getDamage()).getFacing().toString());
							}
						}
						ret[x][y][z] = l;
					}
				}
			}
			return ret;
		}
		if(rotation == 3){
			int l1 = in[0][0].length;
			int l2 = in.length;
			EBlock[][][] ret = new EBlock[in[0][0].length][in[0].length][in.length];
			for(int x = 0; x < l1; x++){
				for(int z = 0; z < l2; z++){
					for(int y = 0; y < ret[0].length; y++){
						EBlock l = in[z][y][x];
						if(l != null){
//							if(Material.values()[l.getType()].toString().contains("STAIRS")){
//								l = rotateStair(l, rotation);
//							}else if(l.getType() == 50){
//								l = rotateTorch(l, rotation);
//							}
							if(l.getDamage() instanceof Directional){
								l = l.clone();
								Directional d=(Directional) l.getDamage();
								//System.out.println("Before: " + d.getFacing().toString());
								l.setDamage(rotate(d, rotation));
								//System.out.println("After: " + ((Directional)l.getDamage()).getFacing().toString());
							}
						}
						ret[(l1-x)-1][y][(l2-z)-1] = l;
					}
				}
			}
			return ret;
		}
		Bukkit.broadcastMessage("errorrrr");
		return in;
	}
	private Directional rotate(Directional in, int rotate){
		if(rotate == 0){
			return in;
		}
		System.out.println("did a rotation for: "+ in.getMaterial().toString());
		BlockFace newFace = getFacing(in.getFacing(),rotate);
		in.setFacing(newFace);
		//System.out.println("newFace: " + newFace.toString() + " fac: " + fac + " fac2: " + fac2);
		return in;
	}
//	private static int getFNum(BlockFace b){
//		switch(b){
//		case NORTH:
//			return 0;
//		case WEST:
//			return 1;
//		case SOUTH:
//			return 2;
//		case EAST:
//			return 3;
//		default:
//			return -999;
//		}
//	}
	protected static BlockFace getFacing(BlockFace b, int rot) {
		if(rot == 0){
			return b;
		}
		if(rot == 2){
			return b.getOppositeFace();
		}
		if(rot == 1){
			switch(b){
			case NORTH:
				return BlockFace.WEST;
			case EAST:
				return BlockFace.SOUTH;
			case SOUTH:
				return BlockFace.EAST;
			case WEST:
				return BlockFace.NORTH;
			}
		}
		if(rot == 3){
			switch(b){
			case NORTH:
				return BlockFace.EAST;
			case EAST:
				return BlockFace.NORTH;
			case SOUTH:
				return BlockFace.WEST;
			case WEST:
				return BlockFace.SOUTH;
			}
		}
        return BlockFace.UP;
	}
//	private EBlock rotateStair(EBlock in, int rotate){
//		in = in.clone();
//		in.setDamage((byte) (in.getDamage()%8));
//		if(rotate == 0){
//			return in;
//		}else if(rotate==1){
//			switch(in.getDamage()){
//			case 0:
//			case 1:
//			case 4:
//			case 5:
//				in.setDamage((byte) (in.getDamage() + 2));
//				break;
//			default:
//				in.setDamage((byte) (in.getDamage() - 2));
//				break;
//			
//			}
//			return in;
//		}else if(rotate == 2){
//			if(in.getDamage() %2 == 0){
//				in.setDamage((byte) (in.getDamage()+1));
//			}else{
//				in.setDamage((byte)(in.getDamage()-1));
//			}
//			return in;
//		}else if(rotate == 3){
//			switch(in.getDamage()){
//			case 0:
//			case 1:
//			case 4:
//			case 5:
//				in.setDamage((byte) (in.getDamage() + 2));
//				break;
//			default:
//				in.setDamage((byte) (in.getDamage() - 2));
//				break;
//			
//			}
//			if(in.getDamage() %2 == 0){
//				in.setDamage((byte) (in.getDamage()+1));
//			}else{
//				in.setDamage((byte)(in.getDamage()-1));
//			}
//			return in;
//		}
//		Bukkit.broadcastMessage("Error rotating stair.");
//		return in;
//	}
//	private EBlock rotateTorch(EBlock in, int rotation){
//		if(rotation == 0 || in.getDamage() == 0){
//			return in;
//		}
//		in = in.clone();
//		
////		byte amt = (byte) (in.getDamage()%5);
////		if(rotation == 0 || rotation == 8){
////			return in;
////		}else
////		if(rotation == 2){
////			if(amt %2 == 0){
////				in.setDamage((byte) (amt-1));
////			}else{
////				in.setDamage((byte) (amt+1));
////			}
////			return in;
////		}else if(rotation == 1){
////			switch(amt){
////			case 1:
////				in.setDamage((byte) 4);
////				break;
////			case 2:
////				in.setDamage((byte) 3);
////				break;
////			case 3:
////				in.setDamage((byte) 1);
////				break;
////			default:
////				in.setDamage((byte) 2);
////				break;
////			}
////			return in;
////		}else{
////			if(amt %2 == 0){
////				in.setDamage((byte) (amt-1));
////			}else{
////				in.setDamage((byte) (amt+1));
////			}
////			amt = in.getDamage();
////			switch(amt){
////			case 1:
////				in.setDamage((byte) 4);
////				break;
////			case 2:
////				in.setDamage((byte) 3);
////				break;
////			case 3:
////				in.setDamage((byte) 1);
////				break;
////			default:
////				in.setDamage((byte) 2);
////				break;
////			}
////			return in;
////		}
////	}
	public PredefinedSchem def(Block orgin){
		return new PredefinedSchem(this, orgin);
	}
	public PredefinedSchem def(Block orgin,int rot){
		return new PredefinedSchem(this, orgin, rot);
	}
	public void buildOptimized(Block orgin){
		this.buildOptimized(orgin, 0);
	}
	public void buildOptimized(final Block orgin, int rotation){
		buildOptimized(orgin, rotation, null);
	}
	public void buildOptimized(final Block orgin,int rotation, BukkitRunnable r){
		BukkitRunnable br = new BukkitRunnable(){

			@Override
			public void run() {
				EBlock[][][] bls = rotate(blocks, rotation);
				EBlock[][][] ne = new EBlock[bls.length][bls[0].length][bls[0][0].length];
				for(int x = 0; x < bls.length; x++){
					for(int y = 0; y < bls[x].length; y++){
						for(int z = 0; z < bls[x][y].length; z++){
							Block curr = orgin.getRelative(x, y, z);
							EBlock br = bls[x][y][z];
							Material type;
							BlockData data;
							if(br == null){
								//curr.getType().getId
								if(curr.getType() != Material.AIR){
									ne[x][y][z] = new EBlock(Material.AIR, Material.AIR.createBlockData());
								}
							}else{
								type = br.getType();
								data = br.getDamage();
								
								if(curr.getType() != type || !curr.getBlockData().equals(data)){
									ne[x][y][z] = br;
								}
							}

						}
					}
				}
				//Bukkit.broadcastMessage("Optimized from: " + count(bls) + " to: " + count(ne));
				build(ne, orgin, rotation, r);

			}
			
		};
		br.runTask(FarmTopia.getFarmTopia());

	}
	@SuppressWarnings("unused")
	private int count(EBlock[][][] arr){
		int count = 0;
		for(EBlock[][] arr2: arr){
			for(EBlock[] arr3: arr2){
				for(EBlock e: arr3){
					if(e != null){
						count++;
					}
				}
			}
		}
		return count;
	}
	private void build(EBlock[][][] blocks, Block orgin, int rotation, BukkitRunnable done){
		//Bukkit.broadcastMessage("Schem Being Built: " + blocks.length + "x" + blocks[0].length + "x" + blocks[0][0].length);
		BukkitRunnable br = new BukkitRunnable(){

			@Override
			public void run() {
				//System.out.println("---Building---");
				//Bukkit.broadcastMessage(blocks.length + "");
				for(int x = 0; x < blocks.length; x++){
					for(int y = 0; y < blocks[x].length; y++){
						for(int z = 0; z < blocks[x][y].length; z++){
							Block curr = orgin.getRelative(x, y, z);
							if(curr.getBiome() != Biome.THE_VOID){
								curr.setBiome(Biome.THE_VOID);
							}
							EBlock br = blocks[x][y][z];
							if(br != null && !br.getType().toString().contains("TORCH")){
								curr.setType(br.getType(), false);
								curr.setBlockData(br.getDamage());
							}

						}
					}
				}
				secondLayer(orgin, rotation);
				//System.out.println("---Done4---");
				if(done != null){
					done.runTaskLater(FarmTopia.getFarmTopia(), 5L);
				}
			}
			
		};
		br.runTask(FarmTopia.getFarmTopia());
	}
	private void secondLayer(Block orgin, int rotate){
		if(this.hasSecondLayer && secondLayer != null){
			EBlock[][][] lay = rotate(secondLayer, rotate);
			for(int x = 0; x < lay.length; x++){
				for(int y = 0; y < lay[x].length; y++){
					for(int z = 0; z < lay[x][y].length; z++){
						Block curr = orgin.getRelative(x, y, z);
						EBlock br = lay[x][y][z];
						if(br != null){
							//curr.setTypeIdAndData(br.getType(), br.getDamage(), false);
							curr.setType(br.getType(), false);
							curr.setBlockData(br.getDamage());
						}

					}
				}
			}
		}
	}
	@Deprecated
	public void build(final Block orgin, Plugin p){
		BukkitRunnable br = new BukkitRunnable(){

			@Override
			public void run() {
				//Bukkit.broadcastMessage(blocks.length + "");
				for(int x = 0; x < blocks.length; x++){
					for(int y = 0; y < blocks[x].length; y++){
						for(int z = 0; z < blocks[x][y].length; z++){
							Block curr = orgin.getRelative(x, y, z);
							EBlock br = blocks[x][y][z];
							if(br == null){
								curr.setType(Material.AIR);
							}else if(br.getType() != Material.TORCH){
								//curr.setTypeIdAndData(br.getType(), br.getDamage(), false);
								curr.setType(br.getType(), false);
								curr.setBlockData(br.getDamage());
							}

						}
					}
				}
				secondLayer(orgin, 0);
			}
			
		};
		br.runTask(p);
	}
}
