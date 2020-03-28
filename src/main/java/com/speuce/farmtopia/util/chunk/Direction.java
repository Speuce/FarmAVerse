package main.java.com.speuce.farmtopia.util.chunk;

/**
 * Used for measuring relative directions
 * @author matt
 */
public enum Direction {

    //FORMAT
    // 0 = NORTH
    // 1 = NORTHEAST
    // 2 = EAST
    // 3 = SOUTHEAST
    // 4 = SOUTH
    // 5 = SOUTHWEST
    // 6 = WEST
    // 7 = NORTHWEST

    NORTH(0, -1),
    NORTH_EAST(1, -1),
    EAST(1,0),
    SOUTH_EAST(1,1),
    SOUTH(0,1),
    SOUTH_WEST(-1, 1),
    WEST(-1, 0),
    NORTH_WEST(-1, -1);

    /**
     * The xoffset and zoofset of this direction,
     * respectively. This tells you which way to go to be going
     * in this direction.
     */
    private int xoffset,zoffset;

    private Direction(int xoff,int zoff){
        this.xoffset = xoff;
        this.zoffset = zoff;
    }

    /**
     * Following the direction-integer format,
     * find the direction given an integer
     * @param i the given integer
     * @return the associated direction
     */
    public static Direction fromInt(int i){
        //precondition: 0<=i<=7;
        assert(i >= 0 && i <= 7);
        return Direction.values()[i];
    }

    /**
     * Using the specified format, convert this direction to an int
     * @return the associated int.
     */
    public int toInt(){
        return this.ordinal();
    }

    public int getXoffset() {
        return xoffset;
    }

    public int getZoffset() {
        return zoffset;
    }
}
