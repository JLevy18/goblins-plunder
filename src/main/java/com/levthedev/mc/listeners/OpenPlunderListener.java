package com.levthedev.mc.listeners;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.levthedev.mc.events.SpamPlunderEvent;
import com.levthedev.mc.managers.ConfigManager;
import com.levthedev.mc.managers.DatabaseManager;
import com.levthedev.mc.managers.PlunderManager;
import com.levthedev.mc.utility.Serializer;

import net.md_5.bungee.api.ChatColor;

public class OpenPlunderListener implements Listener {


    private Logger logger = GoblinsPlunder.getInstance().getLogger();

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlunderOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {

            if (event.getClickedBlock().getState() instanceof Container){
                Container container = (Container) event.getClickedBlock().getState();

                PersistentDataContainer con = container.getPersistentDataContainer();

                if (con.getKeys().toString().contains("goblinsplunder")) {
                    
                    // DO NOT LET PLAYER OPEN THE CHEST
                    event.setCancelled(true);
                    String blockId = con.get(new NamespacedKey(GoblinsPlunder.getInstance(), "blockid"), PersistentDataType.STRING);


                    // Make sure plunder is not already open
                    if (PlunderManager.getInstance().getOpenPlunder(player.getUniqueId()) != null){

                        player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "You can't loot this plunder right now.");
                        logger.log(Level.WARNING, this.getClass().getSimpleName() + ": A player is trying to loot a plunder that is already open. \nContinued spam will decrease database performance.\n {\r\n   Player: " + player.getName() + "(" + player.getUniqueId() + ")" + ", \r\n   blockId: " + blockId + ", \r\n   worldName: " + container.getWorld().getName() + "\r\n }");

                        SpamPlunderEvent spamEvent = new SpamPlunderEvent("A player is trying to loot a plunder that is already open. \nContinued spam will decrease database performance.\n {\r\n   Player: " + player.getName() + "(" + player.getUniqueId() + ")" + ", \r\n   blockId: " + blockId + ", \r\n   worldName: " + container.getWorld().getName() + "\r\n }");
                        Bukkit.getServer().getPluginManager().callEvent(spamEvent);
                        // We do not want to open the chest so we return
                        return;
                    }


                    // Add to open plunder list
                    PlunderManager.getInstance().addOpenPlunder(player.getUniqueId(), new Plunder(blockId, "", "", null, container.getWorld().getName(),null, null, null, false));


                    DatabaseManager.getInstance().getPlunderStateByIdAsync(player.getUniqueId(), blockId, stateResponse -> {
                        if (stateResponse == null || stateResponse.getPlayerUuid() == null) {
                            // No existing interaction, fill with loot table items

                            if (container instanceof Chest){
                                PlunderManager.getInstance().getOpenPlunder(player.getUniqueId()).setSound(Sound.BLOCK_CHEST_CLOSE);
                                fillInventoryWithLoot(Sound.BLOCK_CHEST_OPEN,container.getLocation(), blockId, player);
                            } else if (container instanceof Barrel){
                                PlunderManager.getInstance().getOpenPlunder(player.getUniqueId()).setSound(Sound.BLOCK_BARREL_CLOSE);
                                fillInventoryWithLoot(Sound.BLOCK_BARREL_OPEN,container.getLocation(), blockId, player);
                            }

                        } else {
                            // Existing interaction found, fill with saved state

                            if (container instanceof Chest){
                                
                                if (stateResponse.getIgnoreRestock() != null) {
                                    PlunderManager.getInstance().getOpenPlunder(player.getUniqueId()).setIgnoreRestock(stateResponse.getIgnoreRestock());
                                }

                                PlunderManager.getInstance().getOpenPlunder(player.getUniqueId()).setSound(Sound.BLOCK_CHEST_CLOSE);
                                fillInventoryWithSavedState(Sound.BLOCK_CHEST_OPEN,container.getLocation(), stateResponse.getStateData(), player);
                            } else if (container instanceof Barrel){

                                if (stateResponse.getIgnoreRestock() != null) {
                                    PlunderManager.getInstance().getOpenPlunder(player.getUniqueId()).setIgnoreRestock(stateResponse.getIgnoreRestock());
                                }

                                PlunderManager.getInstance().getOpenPlunder(player.getUniqueId()).setSound(Sound.BLOCK_BARREL_CLOSE);
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


                // DO NOT LET PLAYER OPEN THE CHEST
                event.setCancelled(true);
                String blockId = con.get(new NamespacedKey(GoblinsPlunder.getInstance(), "blockid"), PersistentDataType.STRING);


                // Make sure plunder is not already open
                // Right now, if a player gets locked. They are locked from all plunders until an admin fixes them.
                if (PlunderManager.getInstance().getOpenPlunder(player.getUniqueId()) != null){

                    player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "You can't loot this plunder right now.");
                    logger.log(Level.WARNING, this.getClass().getSimpleName() + ": A player is trying to loot a plunder that is already open. \nThis usually indicates an attempt to dupe items. Continued spam will decrease database performance.\n {\r\n   Player: " + player.getName() + "(" + player.getUniqueId() + ")" + ", \r\n   blockId: " + blockId + ", \r\n   worldName: " + cart.getWorld().getName() + "\r\n }");

                    return;
                }

                    // Add to open plunder list
                    PlunderManager.getInstance().addOpenPlunder(player.getUniqueId(), new Plunder(blockId, "", "", null, cart.getWorld().getName(),null, null, null, false));


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


    private void fillInventoryWithLoot(Sound sound, Location location, String blockId, Player player){

        DatabaseManager.getInstance().getPlunderDataByIdAsync(blockId, response -> {

            // Check response data - notify player
            if (response == null){
                player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "Unrecognized Plunder. Please report to an admin immediately.");

                logger.log(Level.SEVERE, this.getClass().getSimpleName() + ": " + player.getName() + "tried to open a plunder that no longer exists in the database.\n" +
                                                                           "Plunder: " + blockId + "\n" +
                                                                           "Location: (X: " + location.getX() + ", Y: " + location.getY() + ", Z: " + location.getZ() + ")");

                
                PlunderManager.getInstance().removeOpenPlunder(player.getUniqueId());

                return;
            }

            Bukkit.getScheduler().runTask(GoblinsPlunder.getInstance(), () -> {

                if (response.getLootTableKey() != null && !response.getLootTableKey().equalsIgnoreCase("")) {

                    // Set Flags to be transfered to the state
                    PlunderManager.getInstance().getOpenPlunder(player.getUniqueId()).setIgnoreRestock(response.getIgnoreRestock());

                    //Play sound
                    if (sound != null && location != null){
                        location.getWorld().playSound(location, sound, 1.0f, 1.0f);
                    }
                    
                    //Open fake chest
                    Inventory playerChest = Bukkit.createInventory(player, InventoryType.CHEST, ConfigManager.getInstance().getPlunderTitle());
                    LootTable lootTable = null;
                    
                    try {
                        String[] lootKeySplit = response.getLootTableKey().split(":");
                        lootTable = Bukkit.getLootTable(NamespacedKey.minecraft(lootKeySplit[1]));
                    } catch (Exception e) {

                        player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "Error generating loot - LootTable not found.");

                        logger.log(Level.SEVERE, this.getClass().getSimpleName() + player.getName() + " tried to generate loot using an undefined LootTable (" + 
                        lootTable.getKey().getKey().toString() != null ? lootTable.getKey().getKey().toString() : "missing minecraft namespace" + ")" , e);
                        PlunderManager.getInstance().removeOpenPlunder(player.getUniqueId());;
                        return;
                    }
                    LootContext.Builder builder = new LootContext.Builder(player.getLocation());

                    if (lootTable != null){
                        lootTable.fillInventory(playerChest, new Random(), builder.build());
                    }
                    
                    player.openInventory(playerChest);
                    
                    
                } else if (response.getContents() != null){ // Filled from contents

                    PlunderManager.getInstance().getOpenPlunder(player.getUniqueId()).setIgnoreRestock(response.getIgnoreRestock());

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
                        logger.log(Level.SEVERE, this.getClass().getSimpleName() + ": Failed to deserialize inventory contents", e);
                        return;
                    }

                    playerChest.setContents(contents);
                    player.openInventory(playerChest);

                    
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
                logger.log(Level.SEVERE, this.getClass().getSimpleName() + ": Failed to deserialize inventory contents from previous state", e);
                return;
            }

            playerChest.setStorageContents(contents);
            player.openInventory(playerChest);

        });
    }


}
