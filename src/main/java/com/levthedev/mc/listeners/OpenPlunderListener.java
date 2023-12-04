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

import net.md_5.bungee.api.ChatColor;

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

                    
                    Plunder plunder = new Plunder(blockId, "", "", null, container.getWorld().getName(), null, null, null);


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
                                
                                if (stateResponse.getIgnoreRestock() != null) {
                                    PlunderManager.getInstance().getOpenPlunderMap().get(player.getUniqueId()).setIgnoreRestock(stateResponse.getIgnoreRestock());
                                }

                                fillInventoryWithSavedState(Sound.BLOCK_CHEST_OPEN,container.getLocation(), stateResponse.getStateData(), player);
                            } else if (container instanceof Barrel){

                                if (stateResponse.getIgnoreRestock() != null) {
                                    PlunderManager.getInstance().getOpenPlunderMap().get(player.getUniqueId()).setIgnoreRestock(stateResponse.getIgnoreRestock());
                                }

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
                Plunder plunder = new Plunder(blockId, null, null, null, cart.getWorld().getName(), null, null, null);
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



    private void fillInventoryWithLoot(Sound sound, Location location, String blockId, Player player){
        DatabaseManager.getInstance().getPlunderDataByIdAsync(blockId, response -> {

            Bukkit.getScheduler().runTask(GoblinsPlunder.getInstance(), () -> {


                if (response == null){
                    player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "Loot database error. Please report this to an admin immediately.");
                    System.err.println(ChatColor.DARK_RED + "[GP]" + ChatColor.RED + "");
                    return;
                }

                if (response.getLootTableKey() != null && !response.getLootTableKey().equalsIgnoreCase("")) {

                    PlunderManager.getInstance().getOpenPlunderMap().get(player.getUniqueId()).setIgnoreRestock(response.getIgnoreRestock());

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
                        player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "Error generating loot: " + response.getLootTableKey() + " not found.");
                        PlunderManager.getInstance().getOpenPlunderMap().remove(player.getUniqueId());
                        return;
                    }
                    LootContext.Builder builder = new LootContext.Builder(player.getLocation());

                    if (lootTable != null){
                        lootTable.fillInventory(playerChest, new Random(), builder.build());
                    }
                    
                    player.openInventory(playerChest);
                    
                    
                } else if (response.getContents() != null){ // Filled from contents

                    System.out.println(response.getIgnoreRestock());

                    PlunderManager.getInstance().getOpenPlunderMap().get(player.getUniqueId()).setIgnoreRestock(response.getIgnoreRestock());

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
