package com.levthedev.mc.coordinators;

import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import com.levthedev.mc.GoblinsPlunder;
import com.levthedev.mc.managers.DatabaseManager;
import com.levthedev.mc.utility.Serializer;
import com.mysql.cj.jdbc.Blob;


public class DatabaseCoordinator {
    private final DatabaseManager databaseManager;
    private final GoblinsPlunder plugin;

    public DatabaseCoordinator(DatabaseManager databaseManager, GoblinsPlunder plugin) {
        this.databaseManager = databaseManager;
        this.plugin = plugin;
    }

    public void createPlunderData(Block block, Player player) {

        
        String blockId = UUID.randomUUID().toString();
        String location = "(X: " + block.getX() + ", Y: " + block.getY() + ", Z: " + block.getZ() + ")";
        String blockType = block.getBlockData().getMaterial().toString();
        Blob contents = null;

        
        Container container = (Container) block.getState();

        String serializedContents = Serializer.serializeContainerContents(container.getInventory().getContents());
        contents = new Blob(Serializer.toByteArray(serializedContents), null);
        
        NamespacedKey key = new NamespacedKey(plugin, "blockId");
        container.getPersistentDataContainer().set(key, PersistentDataType.STRING, blockId);
        container.update();

        databaseManager.createPlunderData(blockId, location, blockType, contents, player);


        
    }



}
