package com.levthedev.mc.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.levthedev.mc.GoblinsPlunder;
import com.levthedev.mc.managers.ConfigManager;
import com.levthedev.mc.managers.DatabaseManager;

import net.md_5.bungee.api.ChatColor;

public class AddPlunderListener implements Listener {

    // This is a set of the players that are concurrently running the command
    private List<Player> activePlayers = new ArrayList<>();

    private Map<Player,Map<String,String>> playerFlags = new ConcurrentHashMap<Player,Map<String,String>>();

    public void setActivePlayer(Player player, boolean isActive) {
        if (isActive) {
            activePlayers.add(player);
        } else {
            activePlayers.remove(player);
            playerFlags.remove(player);
        }
    }

    public void setPlayerFlags(Player player, Map<String,String> flags){
        playerFlags.put(player, flags);
    }

    @EventHandler
    public void onCreateDoubleChest(BlockPlaceEvent event) {

        Block placedBlock = event.getBlockPlaced();
        
        // Check if the placed block is a chest
        if (placedBlock.getType() == Material.CHEST) {
            // Get all blocks adjacent to the placed chest
            Block[] adjacentBlocks = new Block[]{
                placedBlock.getRelative(BlockFace.NORTH),
                placedBlock.getRelative(BlockFace.SOUTH),
                placedBlock.getRelative(BlockFace.EAST),
                placedBlock.getRelative(BlockFace.WEST)
            };

            // Check each adjacent block
            for (Block adjacentBlock : adjacentBlocks) {
                if (adjacentBlock.getType() == Material.CHEST) {

                    Chest chest = (Chest) adjacentBlock.getState();
                    if (!(chest.getPersistentDataContainer().getKeys().toString().contains("goblinsplunder"))) return;

                    event.setCancelled(true);    
                    event.getPlayer().sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "you can't create a double chest using this loot.");
                    return;
                }
            }
        }
    }  

    // Add Plunder using command
    @EventHandler
    public void onAddPlunder(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (!activePlayers.contains(player)) return;
        event.setCancelled(true);
        if (!((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK ) && event.getHand() == EquipmentSlot.HAND)) return;

        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;
            
        if (!(clickedBlock.getState() instanceof Container)){
            event.getPlayer().sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.DARK_RED + "Invalid block type: " + ChatColor.RESET + "" + ChatColor.RED + "block must be a container.");
            setActivePlayer(player, false);
            return;
        }
            
        Container container = (Container) clickedBlock.getState();
        String loot_table_key = setLootTable(container);

        // Check container loottable , if null check inventory, if empty error

        boolean ignore_restock = false;

        if (playerFlags.get(player) != null){
            ignore_restock = playerFlags.get(player).containsKey("ignorerestock");
        }

        if (loot_table_key != null) {
            DatabaseManager.getInstance().getDatabaseCoordinator().createPlunderDataByBlock(clickedBlock, player, ignore_restock, loot_table_key);
        } else {
            if (container.getInventory().isEmpty()){
                player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "Container can't be empty.");
                setActivePlayer(player, false);
                return;
            }
            DatabaseManager.getInstance().getDatabaseCoordinator().createPlunderDataByBlock(clickedBlock, player, ignore_restock, loot_table_key);
        }

        setActivePlayer(player, false);

    }


    // Generated Structures Listener

    @EventHandler
    public void onAddPlunder(ChunkLoadEvent event){

        if (!ConfigManager.getInstance().isGSEnabled()) return;
        if (!ConfigManager.getInstance().getGSWorldWhitelist().contains(event.getWorld().getName())) return;
        if (!event.isNewChunk()) return;

        BlockState[] tileEntities = event.getChunk().getTileEntities();
        Entity[] entities = event.getChunk().getEntities();

        Bukkit.getScheduler().runTaskAsynchronously(GoblinsPlunder.getInstance(), () -> {

            for (BlockState blockState : tileEntities){
                if (blockState instanceof Chest){

                    Bukkit.getScheduler().runTask(GoblinsPlunder.getInstance(), () -> {            
                        Chest chest = (Chest) blockState;
                        if (chest.getLootTable() != null){
                            DatabaseManager.getInstance().getDatabaseCoordinator().createPlunderDataByBlock(chest.getBlock(), null, false, chest.getLootTable().toString());

                        }
                        
                    });

                }
            }

            for (Entity entity : entities) {
                if (entity instanceof StorageMinecart) {

                    Bukkit.getScheduler().runTask(GoblinsPlunder.getInstance(), () -> {
                        StorageMinecart cart = (StorageMinecart) entity;
                        if (cart.getLootTable() != null){
                            DatabaseManager.getInstance().getDatabaseCoordinator().createPlunderDataByEntity(entity, null, false, cart.getLootTable().toString());
                        }

                    });
                }
            }

        });

    }


    // Check if container has a LootTable

    public String setLootTable(Container container){

        if (container instanceof Chest chest){
            
            // Check if loottable on chest
            if (chest.getLootTable() != null){
                return chest.getLootTable().getKey().toString();
            }

        } else if (container instanceof Barrel barrel){

            if (barrel.getLootTable() != null){
                return barrel.getLootTable().getKey().toString();
            }
        } 


        return null;
    }

}
