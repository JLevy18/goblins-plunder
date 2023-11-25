package com.levthedev.mc.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

public class GetListener implements Listener {
    private boolean isActive = false;
    private LootTable loot;
    private NamespacedKey key;

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public void setLoot(LootTable loot){
        this.loot = loot;
    }

    public void setKey(NamespacedKey key){
        this.key = key;
    }


    @EventHandler
    public void onGetCommand(PlayerInteractEvent event) {
        
        if(!isActive) return;

        event.setCancelled(true);
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock.getState() instanceof Chest){

            Chest chest = (Chest) clickedBlock.getState();
            setLoot(chest.getLootTable());
            setKey(chest.getLootTable().getKey());
            System.out.println(loot);
            System.out.println(chest.getLootTable().getKey());
        }

        if (clickedBlock.getState() instanceof Barrel){

            System.out.println("Barrel");
            Barrel barrel = (Barrel) clickedBlock.getState();

            LootContext.Builder builder = new LootContext.Builder(clickedBlock.getLocation());

            LootContext lootContext = builder.build();
            
            LootTable lootTable = Bukkit.getLootTable(this.key);

            lootTable.fillInventory(barrel.getInventory(), new Random(), lootContext);
            //loot.fillInventory(barrel.getInventory(), new Random(), lootContext);
        }




        setActive(false);
    }
}
