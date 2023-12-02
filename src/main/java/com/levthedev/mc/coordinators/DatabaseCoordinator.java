package com.levthedev.mc.coordinators;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import com.levthedev.mc.GoblinsPlunder;
import com.levthedev.mc.managers.DatabaseManager;
import com.levthedev.mc.utility.LootTablesOverworld;
import com.levthedev.mc.utility.Serializer;


public class DatabaseCoordinator {

    private final GoblinsPlunder plugin = GoblinsPlunder.getInstance();
    private final DatabaseManager databaseManager = DatabaseManager.getInstance();

    public void createPlunderData(Block block, Player player, LootTablesOverworld loot) {
        String loot_table_key = ""; // default loot_table_key as an empty string
        String blockId = UUID.randomUUID().toString();
        String location = "(X: " + block.getX() + ", Y: " + block.getY() + ", Z: " + block.getZ() + ")";
        String blockType = block.getBlockData().getMaterial().toString();
        
        if (loot != null){
            loot_table_key = loot.getKey();
        }
        
        Container container = (Container) block.getState();

        byte[] contents = null;

        try {
            contents = Serializer.toBase64(container.getInventory().getContents());
        } catch (IOException e) {
            System.err.println("[GP ERROR] " + e.getCause() +  " - " + e.getMessage());
        }
        
        //Add our custom key to the block data
        NamespacedKey key = new NamespacedKey(plugin, "blockId");
        container.getPersistentDataContainer().set(key, PersistentDataType.STRING, blockId);
        container.update();

        databaseManager.createPlunderDataAsync(blockId, location, blockType, loot_table_key, contents, player);
        
    }



}
