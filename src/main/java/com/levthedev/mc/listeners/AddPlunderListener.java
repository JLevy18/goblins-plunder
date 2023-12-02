package com.levthedev.mc.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.loot.LootTable;

import com.levthedev.mc.managers.ConfigManager;
import com.levthedev.mc.managers.DatabaseManager;
import com.levthedev.mc.managers.PlunderManager;
import com.levthedev.mc.utility.LootTablesOverworld;

import net.md_5.bungee.api.ChatColor;

public class AddPlunderListener implements Listener {

    // This is a set of the players that are concurrently running the command
    private Set<Player> activePlayers = new HashSet<>();
    private Map<Player, LootTablesOverworld> activeLootTables = new HashMap<>();    


    public void setLoot(Player player, LootTablesOverworld loot) {
        activeLootTables.put(player, loot);
    }

    public void setActive(Player player, boolean active) {
        if (active) {
            activePlayers.add(player);
        } else {
            activePlayers.remove(player);
            activeLootTables.remove(player);
        }
    }



    // Add Plunder using command

    @EventHandler
    public void onAddPlunder(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!activePlayers.contains(player)) return;

        event.setCancelled(true);

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK ) && event.getHand() == EquipmentSlot.HAND) {

            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {
                
                // Block must be a container
                if (clickedBlock.getState() instanceof TileState){
                    
                    Container container = (Container) clickedBlock.getState();

                    if (activeLootTables.get(player) != null){

                        LootTable lootTable = Bukkit.getLootTable(NamespacedKey.minecraft(activeLootTables.get(player).getKey()));

                        if (container instanceof Chest){
                            Chest chest = (Chest) container;
            
                            chest.setLootTable(lootTable);
                            chest.update();
                        }

                        DatabaseManager.getInstance().getDatabaseCoordinator().createPlunderData(clickedBlock, event.getPlayer(), activeLootTables.get(player));
            
                    } else {

                        if (PlunderManager.getInstance().isChestEmpty(container)){
                            player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "The container can't be empty");
                            setActive(player, false);
                            return;
                        }

                        DatabaseManager.getInstance().getDatabaseCoordinator().createPlunderData(clickedBlock, event.getPlayer(), activeLootTables.get(player));
                    }

                    
                } else {
                    event.getPlayer().sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.DARK_RED + "Invalid block type: " + ChatColor.RESET + "" + ChatColor.RED + "block must be a container.");
                }

            }

        }

        setActive(player, false);
    }
    
    // Generated Structures Listener

    @EventHandler
    public void onAddPlunder(ChunkLoadEvent event){

        if (!ConfigManager.getInstance().isGSEnabled()) return;
        if (!ConfigManager.getInstance().getGSWorldWhitelist().contains(event.getWorld().getName())) return;
        if (!event.isNewChunk()) return;

        Chunk chunk = event.getChunk();

        for (BlockState blockState : chunk.getTileEntities()){
            if (!(blockState instanceof Chest)) continue;
            Chest chest = (Chest) blockState;
            if (chest.getLootTable() == null) continue;

            DatabaseManager.getInstance().getDatabaseCoordinator().createPlunderData(chest.getBlock(), null, LootTablesOverworld.fromKey(chest.getLootTable().getKey().getKey()) != null ? LootTablesOverworld.fromKey(chest.getLootTable().getKey().getKey()) : null);

        }
   


    }
}
