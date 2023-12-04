package com.levthedev.mc.coordinators;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.persistence.PersistentDataType;

import com.levthedev.mc.GoblinsPlunder;
import com.levthedev.mc.managers.DatabaseManager;
import com.levthedev.mc.utility.Serializer;


public class DatabaseCoordinator {

    private final GoblinsPlunder plugin = GoblinsPlunder.getInstance();
    private final DatabaseManager databaseManager = DatabaseManager.getInstance();

    public void createPlunderDataByBlock(Block block, Player player, String loot_table_key) {
        String blockId = UUID.randomUUID().toString();
        String location = "(" + block.getWorld().getName() + ", X: " + block.getX() + ", Y: " + block.getY() + ", Z: " + block.getZ() + ")";
        String blockType = block.getBlockData().getMaterial().toString();
        
        // We expect the block to have been validated as a Container by this point
        Container container = (Container) block.getState();

        byte[] contents = null;

        if (!container.getInventory().isEmpty()){
            try {
                contents = Serializer.toBase64(container.getInventory().getContents());
            } catch (IOException e) {
                System.err.println("[GP ERROR] " + e.getCause() +  " - " + e.getMessage());
            }
        }

        //Add our custom key to the block data
        NamespacedKey key = new NamespacedKey(plugin, "blockId");
        container.getPersistentDataContainer().set(key, PersistentDataType.STRING, blockId);
        container.update();

        databaseManager.createPlunderDataAsync(blockId, location, blockType, loot_table_key, contents, player);
        
    }

    public void createPlunderDataByEntity(Entity entity, Player player, String loot_table_key) {
        String blockId = UUID.randomUUID().toString();
        String location = "(" + entity.getWorld().getName() + ", X: " + entity.getLocation().getX() + ", Y: " + entity.getLocation().getY() + ", Z: " + entity.getLocation().getZ() + ")";
        String blockType = entity.getType().toString();
        
        StorageMinecart cart = (StorageMinecart) entity;

        byte[] contents = null;

        try {
            contents = Serializer.toBase64(cart.getInventory().getContents());
        } catch (IOException e) {
            System.err.println("[GP ERROR] " + e.getCause() +  " - " + e.getMessage());
        }
        
        //Add our custom key to the block data
        NamespacedKey key = new NamespacedKey(plugin, "blockId");
        cart.getPersistentDataContainer().set(key, PersistentDataType.STRING, blockId);

        databaseManager.createPlunderDataAsync(blockId, location, blockType, loot_table_key, contents, player);
        
    }



}
