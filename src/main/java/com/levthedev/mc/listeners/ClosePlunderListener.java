package com.levthedev.mc.listeners;

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


        if (plunderManager.getOpenPlunder(event.getPlayer().getUniqueId()) != null) {
            // Retrieve necessary information
            

            Player player = (Player) event.getPlayer();
            String playerUuid = player.getUniqueId().toString();
            String pbId = plunderManager.getOpenPlunderId(event.getPlayer().getUniqueId());
            
            
            player.getWorld().playSound(player.getLocation(), plunderManager.getOpenPlunder(player.getUniqueId()).getSound(), 1.0f, 1.0f);

            // Save the interaction asynchronously to the database
            DatabaseManager.getInstance().createPlunderStateAsync(playerUuid, pbId, plunderManager.getOpenPlunder(player.getUniqueId()).getWorldName(), plunderManager.getOpenPlunder(player.getUniqueId()).getIgnoreRestock() , inv.getContents());

        }
    }

}