package com.speuce.farmtopia.farm.handlers;

import com.speuce.farmtopia.Constant;
import com.speuce.farmtopia.farm.manager.FarmManager;
import com.speuce.farmtopia.main.FarmTopia;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This handler allows for stacking of non-stackable items disguised as broken bows.
 */
public class StackingHandler implements Listener {

    private FarmManager manager;

    public StackingHandler(FarmManager manager) {
        this.manager = manager;
        FarmTopia.registerListener(this);
    }


    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void itemStackManager(InventoryClickEvent e) {

        // Bukkit.broadcastMessage("click1");

        if (e.getCurrentItem() != null && e.getCursor() != null && e.getCurrentItem().getType() == Material.BOW
                && e.getCursor().getType() == Material.BOW && e.getAction() != InventoryAction.COLLECT_TO_CURSOR) {
            if (e.getCurrentItem().getDurability() == e.getCursor().getDurability()) {
                Constant.debug(e.getAction().toString());
//                Constant.debug("CurrentItem: " + Constant.itemInfo(e.getCurrentItem()));
//                Constant.debug("Cursor: " + Constant.itemInfo(e.getCursor()));
//                Constant.debug("Clicktype: " + e.getClick());
                // if (!(e.getAction().toString().contains("PICKUP") ||
                // (e.getAction().equals(InventoryAction.PICKUP_SOME) &&
                // e.getClick() == ClickType.LEFT))) {
                // if(e.getCurrentItem().getType() == Material.BOW &&
                // e.getCursor().getType() == Material.BOW){
                // if(e.getCurrentItem().getDurability() ==
                // e.getCursor().getDurability()){

                // Bukkit.broadcastMessage(e.getCurrentItem().toString()
                // + ":" + e.getCursor().toString());
                if (e.getClick() == ClickType.LEFT) {
                    if (e.getCurrentItem().getAmount() < 64) {

                        int spaceLeft = 64 - e.getCurrentItem().getAmount();
                        if (spaceLeft >= e.getCursor().getAmount()) {
                            e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() + e.getCursor().getAmount());
                            e.setCursor(null);
                            ((Player) e.getWhoClicked()).updateInventory();
                            // Bukkit.broadcastMessage("yes5");
                        } else {
                            // Bukkit.broadcastMessage("click3");
                            e.getCurrentItem().setAmount(64);
                            ItemStack s = e.getCursor();
                            s.setAmount(s.getAmount() - spaceLeft);
                            e.setCursor(s);
                            // e.getCursor().setAmount(e.getCursor().getAmount()
                            // - spaceLeft);
                            ((Player) e.getWhoClicked()).updateInventory();
                            // Bukkit.broadcastMessage("yes6");
                        }
                        e.setCancelled(true);

                    }else{
                        e.setCancelled(true);
                        ((Player) e.getWhoClicked()).updateInventory();
                        return;
                    }
                } else if (e.getClick() == ClickType.RIGHT) {
                    // Constant.debug("rightt");
                    if (e.getCurrentItem().getAmount() < 64) {
                        e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() + 1);
                        if (e.getCursor().getAmount() == 1) {
                            e.setCursor(null);
                            ((Player) e.getWhoClicked()).updateInventory();
                        } else {
                            e.getCursor().setAmount(e.getCursor().getAmount() - 1);
                        }
                        // int spaceLeft = 64 - e.getCurrentItem().getAmount();
                        e.setCancelled(true);

                    }
                }

            }

        } else if (e.getCursor() != null && e.getCursor().getType() == Material.BOW
                && e.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            //Constant.debug("work: " + e.getClickedInventory().getSize());

            int slotcheck = 0;
            while (e.getCursor().getAmount() < 64 && slotcheck < e.getClickedInventory().getSize()) {
                ItemStack check = e.getClickedInventory().getItem(slotcheck);
                //Constant.debug("Check: " + Constant.itemInfo(check));
                if (check != null && check.getType() == Material.BOW
                        && check.getDurability() == e.getCursor().getDurability()) {
                    int maxamt = 64 - e.getCursor().getAmount();
                    if (check.getAmount() > maxamt) {
                        e.getCursor().setAmount(64);
                        check.setAmount(check.getAmount() - maxamt);
                    } else {
                        e.getCursor().setAmount(e.getCursor().getAmount() + check.getAmount());
                        e.getClickedInventory().setItem(slotcheck, null);
                    }
                }
                slotcheck++;
            }
            ((Player) e.getWhoClicked()).updateInventory();
        }
    }
}
