package main.java.com.speuce.farmtopia.plot.upgradeable.seedResearch;

import main.java.com.speuce.farmtopia.util.Constant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * An object which holds information for text, through armour stands
 * @author matt
 */
public class ArmourTextHolder {

    /**
     * The list of armour stand uuids that this is watching over
     */
    private UUID[] uuids;

    /**
     * The lines of text associated with this
     */
    private String[] lines;

    /**
     * Flag which indicates whether or not this armour stand text is built.
     */
    private boolean built = false;

    public ArmourTextHolder(int lines){
        uuids = new UUID[lines];
        this.lines = new String[lines];
    }

    /**
     * Sets the given lines of text in the armour stand name
     * @param line the line to change
     * @param l the text to set it to.
     */
    public void setText(int line, String l){
        lines[line] = l;
    }

    /**
     * Call this to set the location of this text holder
     * and place the corresponding armour stands.
     * @param b the Location to set this to.
     */
    public void setLocation(Location b){
        if(built){
            destroyStands();
        }
        built = true;
        for(int i = 1; i <= uuids.length; i++){
            ArmorStand s = (ArmorStand) b.getWorld().spawnEntity(b.add(0.5, -0.25*i, 0.5),
                    EntityType.ARMOR_STAND);
            Constant.setupStand(s);
            s.setCustomName(lines[i-1]);
            uuids[i-1] = s.getUniqueId();
        }
    }

    /**
     * Forcibly updates the display text of the armour stands
     */
    public void updateText(){
        for (int i = 0; i < uuids.length; i++) {
            UUID d = uuids[i];
            if (d != null) {
                ArmorStand as = (ArmorStand) Bukkit.getEntity(d);
                if (as != null) {
                    as.setCustomName(lines[i]);
                }
            }
        }
    }

    /**
     * Destroys the armour stands, if they are built.
     */
    public void destroyStands(){
        if(!built){
            return;
        }
        built = false;
        UUID d;
        for(int i = 0; i < uuids.length;i++){
            d = uuids[i];
            if(d != null){
                ArmorStand as = (ArmorStand) Bukkit.getEntity(d);
                if(as != null)
                    as.remove();
                uuids[i] = null;
            }
        }
    }
}
