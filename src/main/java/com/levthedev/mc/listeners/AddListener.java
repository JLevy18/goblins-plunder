package com.levthedev.mc.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.levthedev.mc.managers.DatabaseManager;

import net.md_5.bungee.api.ChatColor;

public class AddListener implements Listener {


    private boolean isActive = false;

    public void setActive(boolean active) {
        this.isActive = active;
    }


    @EventHandler
    public void onAddCommand(PlayerInteractEvent event) {
        if (!isActive) return;

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK ) && event.getHand() == EquipmentSlot.HAND) {

            event.setCancelled(true);

            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {
                
                // Block must be a container
                if (clickedBlock.getState() instanceof Container){

                    DatabaseManager.getInstance().getDatabaseCoordinator().createPlunderData(clickedBlock, event.getPlayer());
                    
                } else {
                    event.getPlayer().sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Invalid block type: " + ChatColor.RESET + "" + ChatColor.RED + "block must be a container.");
                }

                System.out.println(clickedBlock.getBlockData().getMaterial());
            }

        }
        setActive(false);
    }
    
}
