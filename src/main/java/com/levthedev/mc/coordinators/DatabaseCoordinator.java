package com.levthedev.mc.coordinators;

import org.bukkit.entity.Player;

public interface DatabaseCoordinator {
    
    public void setup();

    public void createPlunderData(String blockId, String location, String blockType, Player player);

}
