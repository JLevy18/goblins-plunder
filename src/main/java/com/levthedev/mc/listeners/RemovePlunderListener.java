package com.levthedev.mc.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.Hopper;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.persistence.PersistentDataType;

import com.levthedev.mc.GoblinsPlunder;
import com.levthedev.mc.managers.ConfigManager;
import com.levthedev.mc.managers.DatabaseManager;

public class RemovePlunderListener implements Listener {


    @EventHandler
    public void onRemovePlunder(BlockBreakEvent event){

        Block brokenBlock = event.getBlock();

        if (ConfigManager.getInstance().isPlunderInvincible()) {

            if (brokenBlock.getState() instanceof Chest) {

                Chest chest = (Chest) brokenBlock.getState();

                if(chest.getPersistentDataContainer().getKeys().toString().contains("goblinsplunder")){

                    event.setCancelled(true);
                }
            }
        } else {
            
            if (brokenBlock.getState() instanceof Chest) {

                Chest chest = (Chest) brokenBlock.getState();

                if(chest.getPersistentDataContainer().getKeys().toString().contains("goblinsplunder")){
                    chest.getInventory().clear();
                    DatabaseManager.getInstance().deletePlunderBlocksByIdsAsync(Arrays.asList(chest.getPersistentDataContainer().get(new NamespacedKey(GoblinsPlunder.getInstance(), "blockid"), PersistentDataType.STRING)));
                }
            }
        }
    }

    @EventHandler
    public void onRemovePlunder(EntityExplodeEvent event){

        if (ConfigManager.getInstance().isPlunderInvincible()) {

            List<Block> blocksToProtect = new ArrayList<>();

            for (Block block : event.blockList()){
                if (block.getState() instanceof Chest) {

                    Chest chest = (Chest) block.getState();
        
                    if(chest.getPersistentDataContainer().getKeys().toString().contains("goblinsplunder")){
                        chest.getInventory().clear();
                        blocksToProtect.add(block);
                    }
                }
            }

            event.blockList().removeAll(blocksToProtect);
        } else {

            List<String> blocksToRemove = new ArrayList<>();

            for (Block block : event.blockList()){
                if (block.getState() instanceof Chest) {

                    Chest chest = (Chest) block.getState();
                    if(chest.getPersistentDataContainer().getKeys().toString().contains("goblinsplunder")){
                        chest.getInventory().clear();
                        blocksToRemove.add(chest.getPersistentDataContainer().get(new NamespacedKey(GoblinsPlunder.getInstance(), "blockid"), PersistentDataType.STRING));
                    }
                }
            }

            DatabaseManager.getInstance().deletePlunderBlocksByIdsAsync(blocksToRemove);
            //Probably should remove from the state here as well. But we'll worry about that later
        }

    }


    

    @EventHandler
    public void onAttemptToStealPlunder(InventoryMoveItemEvent event){

        if (event.getSource().getHolder() instanceof Container){
           if (event.getDestination().getHolder() instanceof Hopper || event.getDestination().getHolder() instanceof HopperMinecart ){
                Container con = (Container) event.getSource().getHolder();

                if (con.getPersistentDataContainer().getKeys().toString().contains("goblinsplunder")){
                    event.setCancelled(true);
                }
           }
        }

    }
}
