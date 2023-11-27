package com.levthedev.mc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.levthedev.mc.GoblinsPlunder;
import com.levthedev.mc.managers.DatabaseManager;

import net.md_5.bungee.api.ChatColor;

public class PlunderListener implements Listener {
    

    @EventHandler
    public void onPlunderInteract(PlayerInteractEvent event) {



        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {

            if (event.getClickedBlock().getState() instanceof Chest){
                Chest chest = (Chest) event.getClickedBlock().getState();

                PersistentDataContainer con = chest.getPersistentDataContainer();

                if (con.getKeys().toString().contains("goblinsplunder")) {

                    event.setCancelled(true);

                    String blockId = con.get(new NamespacedKey(GoblinsPlunder.getInstance(), "blockid"), PersistentDataType.STRING);

                    DatabaseManager.getInstance().getPlunderDataByIdAsync(blockId, response -> {
                        if (response != null && response.getId() != null) {

                            Inventory test = Bukkit.createInventory(null, 9, "Test");
                            event.getPlayer().openInventory(test);

                            chest.getLocation().getWorld().playSound(chest.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);

                            event.getPlayer().sendMessage(ChatColor.GREEN + response.getBlockType() + " " + response.getId() + " " + response.getLootTableKey());
                        } else if ( response != null && response.getId() == null){
                            event.getPlayer().sendMessage(response.getResponseMessage());
                        } else {
                            System.err.println("Database Error");
                        }
                    });

                   

                }


            }

        }
    }

}
