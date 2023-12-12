package com.levthedev.mc.listeners;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import com.levthedev.mc.managers.DatabaseManager;
import com.levthedev.mc.managers.PlunderManager;

public class ClosePlunderListener implements Listener {

    private PlunderManager plunderManager = PlunderManager.getInstance();


    @EventHandler
    public void onPlunderClose(InventoryCloseEvent event) {

        Inventory inv = event.getInventory();


        if (plunderManager.getPlunder(event.getPlayer().getUniqueId()) != null && 
            !plunderManager.getPlunder(event.getPlayer().getUniqueId()).getLocked() ) {
            // Retrieve necessary information
            

            Player player = (Player) event.getPlayer();
            String playerUuid = player.getUniqueId().toString();
            String pbId = plunderManager.getPlunderId(event.getPlayer().getUniqueId());
            
            
            player.getWorld().playSound(player.getLocation(), plunderManager.getOpenPlunderMap().get(player.getUniqueId()).getSound(), 1.0f, 1.0f);

            // Save the interaction asynchronously to the database
            DatabaseManager.getInstance().createPlunderStateAsync(playerUuid, pbId, plunderManager.getPlunder(player.getUniqueId()).getWorldName(), plunderManager.getPlunder(player.getUniqueId()).getIgnoreRestock() , inv.getContents());

        }
    }

}