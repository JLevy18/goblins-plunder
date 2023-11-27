package com.levthedev.mc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.loot.LootTable;

import com.levthedev.mc.GoblinsPlunder;
import com.levthedev.mc.managers.DatabaseManager;
import com.levthedev.mc.utility.LootTablesOverworld;

import net.md_5.bungee.api.ChatColor;

public class AddListener implements Listener {


    private boolean isActive = false;
    private LootTablesOverworld loot;

    public void setLoot(LootTablesOverworld loot) {
        this.loot = loot;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }


    @EventHandler
    public void onAddCommand(PlayerInteractEvent event) {
        if (!isActive) return;

        event.setCancelled(true);

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK ) && event.getHand() == EquipmentSlot.HAND) {

            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {
                
                // Block must be a container
                if (clickedBlock.getState() instanceof Container){
                    
                    Container container = (Container) clickedBlock.getState();

                    String[] keySplit = loot.getKey().split(":");

                    if (loot != null){
                        NamespacedKey lootKey = new NamespacedKey(keySplit[0], keySplit[1]);
                        LootTable lootTable = Bukkit.getLootTable(lootKey);
                        System.out.println(lootTable);

                        if (container instanceof Chest){
                            Chest chest = (Chest) container;
            
                            chest.setLootTable(lootTable);
                            chest.update();
                            
                            
                            //System.out.println("GP-DEBUG: " + chest.getLootTable().getKey());

                        }
            
                        if (container instanceof Barrel){
                            Barrel barrel = (Barrel) container;
            
                            barrel.setLootTable(lootTable);
                            barrel.update();
                        }
                        

                        DatabaseManager.getInstance().getDatabaseCoordinator().createPlunderData(clickedBlock, event.getPlayer(), loot);
            
                    }

                    
                } else {
                    event.getPlayer().sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Invalid block type: " + ChatColor.RESET + "" + ChatColor.RED + "block must be a container.");
                }

            }

        }


        setActive(false);
    }
    
}
