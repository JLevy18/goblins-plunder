package com.levthedev.mc.listeners;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import com.levthedev.mc.managers.DatabaseManager;
import com.levthedev.mc.managers.PlunderManager;
import com.levthedev.mc.utility.Serializer;

public class ClosePlunderListener implements Listener {

    private PlunderManager plunderManager = PlunderManager.getInstance();


    @EventHandler
    public void onPlunderClose(InventoryCloseEvent event) {

        Inventory inv = event.getInventory();


        if (plunderManager.getOpenPlunderMap().containsKey(event.getPlayer().getUniqueId())) {
            // Retrieve necessary information


            Player player = (Player) event.getPlayer();
            String playerUuid = player.getUniqueId().toString();
            String pbId = plunderManager.getPlunderId(event.getPlayer().getUniqueId());
            
            // Serialize the state of the inventory
            byte[] state = null;
            try {
                state = Serializer.toBase64(inv.getContents());
            } catch (IOException e) {
                System.err.println("[GP ERROR] " + e.getCause() +  " - " + e.getMessage());
            }

            // Save the interaction asynchronously to the database

            DatabaseManager.getInstance().createPlunderStateAsync(playerUuid, pbId, plunderManager.getPlunder(player.getUniqueId()).getWorldName(), state);
            plunderManager.removeOpenPlunder(player.getUniqueId());
        }
    }

}