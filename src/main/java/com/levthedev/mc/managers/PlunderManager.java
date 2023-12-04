package com.levthedev.mc.managers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


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


}
