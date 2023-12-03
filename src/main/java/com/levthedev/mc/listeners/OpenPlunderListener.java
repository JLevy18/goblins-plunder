package com.levthedev.mc.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.levthedev.mc.GoblinsPlunder;
import com.levthedev.mc.dao.Plunder;
import com.levthedev.mc.managers.ConfigManager;
import com.levthedev.mc.managers.DatabaseManager;
import com.levthedev.mc.managers.PlunderManager;
import com.levthedev.mc.utility.Serializer;

public class OpenPlunderListener implements Listener {

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlunderOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) return;
        if (player.isSneaking()) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {

            if (event.getClickedBlock().getState() instanceof Container){
                Container container = (Container) event.getClickedBlock().getState();

                PersistentDataContainer con = container.getPersistentDataContainer();

                if (con.getKeys().toString().contains("goblinsplunder")) {
                    
                    // DO NOT LET PLAYER OPEN THE CHEST
                    event.setCancelled(true);
                    String blockId = con.get(new NamespacedKey(GoblinsPlunder.getInstance(), "blockid"), PersistentDataType.STRING);

                    
                    Plunder plunder = new Plunder(blockId, null, null, null, container.getWorld().getName(), null);


                    PlunderManager.getInstance().addOpenPlunder(player.getUniqueId(), plunder);


                    DatabaseManager.getInstance().getPlunderStateByIdAsync(player.getUniqueId(), blockId, stateResponse -> {
                        if (stateResponse == null || stateResponse.getPlayerUuid() == null) {
                            // No existing interaction, fill with loot table items

                            if (container instanceof Chest){
                                fillInventoryWithLoot(Sound.BLOCK_CHEST_OPEN,container.getLocation(), blockId, player);
                            } else if (container instanceof Barrel){
                                fillInventoryWithLoot(Sound.BLOCK_BARREL_OPEN,container.getLocation(), blockId, player);
                            }

                        } else {
                            // Existing interaction found, fill with saved state
                            if (container instanceof Chest){
                                fillInventoryWithSavedState(Sound.BLOCK_CHEST_OPEN,container.getLocation(), stateResponse.getStateData(), player);
                            } else if (container instanceof Barrel){
                                fillInventoryWithSavedState(Sound.BLOCK_BARREL_OPEN,container.getLocation(), stateResponse.getStateData(), player);
                            }
                        }
                    });           
                }
            }
        }
    }
    

    @EventHandler
    public void onPlunderOpen(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) return;

        if (event.getRightClicked() instanceof StorageMinecart) {
            StorageMinecart cart = (StorageMinecart) event.getRightClicked();

            PersistentDataContainer con = cart.getPersistentDataContainer();

            if (con.getKeys().toString().contains("goblinsplunder")) {

                // DO NOT LET PLAYER INTERACT WITH THE CART
                event.setCancelled(true);
                String blockId = con.get(new NamespacedKey(GoblinsPlunder.getInstance(), "blockid"), PersistentDataType.STRING);
                Plunder plunder = new Plunder(blockId, null, null, null, cart.getWorld().getName(), null);
                PlunderManager.getInstance().addOpenPlunder(player.getUniqueId(), plunder);

                DatabaseManager.getInstance().getPlunderStateByIdAsync(player.getUniqueId(), blockId, stateResponse -> {
                    if (stateResponse == null || stateResponse.getPlayerUuid() == null) {
                        // No existing interaction, fill with loot table items

                        fillInventoryWithLoot(null, null, blockId, player);

                    } else {
                        // Existing interaction found, fill with saved state
                        fillInventoryWithSavedState(null, null, stateResponse.getStateData(), player);
                    }
                });     

            }

        }

    }




    // NEED TO REVIST THIS FOR EDGE CASES
    private void fillInventoryWithLoot(Sound sound, Location location, String blockId, Player player){
        DatabaseManager.getInstance().getPlunderDataByIdAsync(blockId, response -> {

            Bukkit.getScheduler().runTask(GoblinsPlunder.getInstance(), () -> {
                if (response.getLootTableKey() != null && !response.getLootTableKey().equalsIgnoreCase("")) {
                    //Play sound
                    if (sound != null && location != null){
                        location.getWorld().playSound(location, sound, 1.0f, 1.0f);
                    }

                    //Open fake chest
                    Inventory playerChest = Bukkit.createInventory(player, InventoryType.CHEST, ConfigManager.getInstance().getPlunderTitle());
                    LootTable lootTable = Bukkit.getLootTable(NamespacedKey.minecraft(response.getLootTableKey()));
                    LootContext.Builder builder = new LootContext.Builder(player.getLocation());

                    lootTable.fillInventory(playerChest, new Random(), builder.build());
                    
                    player.openInventory(playerChest);
                    
                } else if (response.getContents() != null){ // Manually filled and added chests

                    //Play sound
                    if (sound != null && location != null){
                        location.getWorld().playSound(location, sound, 1.0f, 1.0f);
                    }

                    //Open fake chest
                    Inventory playerChest = Bukkit.createInventory(player, InventoryType.CHEST, ConfigManager.getInstance().getPlunderTitle());
                    
                    ItemStack[] contents = null;

                    try {
                        contents = Serializer.fromBase64(response.getContents());
                    } catch (Exception e) {
                        System.err.println("[GP ERROR] " + e.getCause() +  " - " + e.getMessage());
                    }

                    playerChest.setContents(contents);
                    player.openInventory(playerChest);

                    
                } else if ( response != null && response.getId() == null){
                    player.sendMessage(response.getResponseMessage());
                } else {
                    System.err.println("[GP Error] Database Error");
                }
            });
        });
    }

    private void fillInventoryWithSavedState(Sound sound, Location location, byte[] data, Player player){

        Bukkit.getScheduler().runTask(GoblinsPlunder.getInstance(), () -> {

            //Play sound
            if (sound != null && location != null){
                location.getWorld().playSound(location, sound, 1.0f, 1.0f);
            }

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
