package com.levthedev.mc.listeners;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.levthedev.mc.managers.DatabaseManager;

public class AddPlunderListener implements Listener {


    private boolean isActive = false;

    public void setActive(boolean active) {
        this.isActive = active;
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isActive) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {

            event.setCancelled(true);

            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {

                String blockId = UUID.randomUUID().toString();
                String location = "(X: " + clickedBlock.getX() + ", Y: " + clickedBlock.getY() + ", Z: " + clickedBlock.getZ() + ")";
                String blockType = clickedBlock.getState().getBlock().getBlockData().getAsString();


                if (blockType.contains("chest")){
                    
                    Chest chest = (Chest) clickedBlock.getState();

                    DatabaseManager.getInstance().getDatabaseCoordinator().createPlunderData(blockId, location, blockType, null);

                    
                }
                // Can throw errors 
                DatabaseManager.getInstance().getDatabaseCoordinator().createPlunderData(blockId, location, blockType, event.getPlayer());
                
                
                event.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Plunder successfully created");
                setActive(false);
            }

        }
    }
    
}
