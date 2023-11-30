package com.levthedev.mc.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.levthedev.mc.GoblinsPlunder;
import com.levthedev.mc.managers.ConfigManager;
import com.levthedev.mc.managers.DatabaseManager;
import com.levthedev.mc.managers.PlunderManager;
import com.levthedev.mc.utility.Serializer;

import net.md_5.bungee.api.ChatColor;

public class OpenPlunderListener implements Listener {

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlunderInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {

            if (event.getClickedBlock().getState() instanceof Chest){
                Chest chest = (Chest) event.getClickedBlock().getState();

                PersistentDataContainer con = chest.getPersistentDataContainer();

                if (con.getKeys().toString().contains("goblinsplunder")) {
                    
                    // DO NOT LET PLAYER OPEN THE CHEST
                    event.setCancelled(true);
                    String blockId = con.get(new NamespacedKey(GoblinsPlunder.getInstance(), "blockid"), PersistentDataType.STRING);

                    PlunderManager.getInstance().addOpenPlunder(player.getUniqueId(), blockId);

                    DatabaseManager.getInstance().getPlunderStateByIdAsync(player.getUniqueId(), blockId, stateResponse -> {
                        if (stateResponse == null || stateResponse.getPlayerUuid() == null) {
                            // No existing interaction, fill with loot table items
                            fillChestWithLoot(chest, blockId, player);
                        } else {
                            // Existing interaction found, fill with saved state
                            fillChestWithSavedState(chest, stateResponse.getStateData(), player);
                        }
                    });           
                }
            }
        }
    }



    // NEED TO REVIST THIS FOR EDGE CASES
    private void fillChestWithLoot(Chest chest, String blockId, Player player){
        DatabaseManager.getInstance().getPlunderDataByIdAsync(blockId, response -> {

            Bukkit.getScheduler().runTask(GoblinsPlunder.getInstance(), () -> {
                if (response != null && response.getId() != null && response.getLootTableKey() != null) {

                    //Play sound
                    chest.getLocation().getWorld().playSound(chest.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);

                    //Open fake chest
                    Inventory playerChest = Bukkit.createInventory(player, InventoryType.CHEST, ConfigManager.getInstance().getPlunderTitle());
                    
                    if (!response.getLootTableKey().equalsIgnoreCase("")){
                        LootTable lootTable = Bukkit.getLootTable(NamespacedKey.minecraft(response.getLootTableKey()));
                        LootContext.Builder builder = new LootContext.Builder(player.getLocation());

                        lootTable.fillInventory(playerChest, new Random(), builder.build());
                        
                        player.openInventory(playerChest);
                    }
                    System.out.println(response.getBlockType() + " " + response.getId() + " " + response.getLootTableKey()); 
                } else if ( response != null && response.getId() == null){
                    player.sendMessage(response.getResponseMessage());
                } else {
                    System.err.println("[GP Error] Database Error");
                }
            });
        });
    }

    private void fillChestWithSavedState(Chest chest, byte[] data, Player player){

        Bukkit.getScheduler().runTask(GoblinsPlunder.getInstance(), () -> {

            chest.getLocation().getWorld().playSound(chest.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);

            ItemStack[] contents = null;
            Inventory playerChest = Bukkit.createInventory(player, InventoryType.CHEST,  ConfigManager.getInstance().getPlunderTitle());

            try {
              contents = Serializer.fromBase64(data);  
            } catch (Exception e) {
                System.err.println("[GP Error] Serializer failed to deserialize contents\n" + e.getMessage());
            }

            playerChest.setStorageContents(contents);
            player.openInventory(playerChest);

        });
    }


}
