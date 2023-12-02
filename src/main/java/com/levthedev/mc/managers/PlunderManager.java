package com.levthedev.mc.managers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;

import com.levthedev.mc.dao.Plunder;

public class PlunderManager {
    private static PlunderManager instance = null;
    private final Map<UUID, Plunder> openPlunderMap = new ConcurrentHashMap<>();

    private PlunderManager() {}

    public static synchronized PlunderManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PlunderManager not initialized");
        }
        return instance;
    }

    public static synchronized void initialize() {
        instance = new PlunderManager();
    }

    public void addOpenPlunder(UUID playerUuid, Plunder plunder) {
        openPlunderMap.put(playerUuid, plunder);
    }

    public String getPlunderId(UUID playerUuid){
        return openPlunderMap.get(playerUuid).getId();
    }

    public Plunder getPlunder(UUID playerUuid){
        return openPlunderMap.get(playerUuid);
    }

    public void removeOpenPlunder(UUID playerUuid) {
        openPlunderMap.remove(playerUuid);
    }

    public Map<UUID,Plunder> getOpenPlunderMap(){
        return openPlunderMap;
    }

    public boolean isChestEmpty(Container container) {
        for (ItemStack item : container.getInventory().getContents()) {
            if (item != null && item.getAmount() > 0) {
                return false; // Found an item, chest is not empty
            }
        }
        return true; // No items found, chest is empty
    }

}
