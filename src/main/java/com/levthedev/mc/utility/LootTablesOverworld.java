package com.levthedev.mc.utility;

public enum LootTablesOverworld {
    ABANDONED_MINESHAFT("minecraft:chests/abandoned_mineshaft"),
    VILLAGE_ARMORER("minecraft:chests/village/village_armorer"),
    VILLAGE_BUTCHER("minecraft:chests/village/village_butcher"),
    VILLAGE_CARTOGRAPHER("minecraft:chests/village/village_cartographer"),
    VILLAGE_DESERT_HOUSE("minecraft:chests/village/village_desert_house"),
    VILLAGE_FLETCHER("minecraft:chests/village/village_fletcher"),
    VILLAGE_MASON("minecraft:chests/village/village_mason"),
    VILLAGE_PLAINS_HOUSE("minecraft:chests/village/village_plains_house"),
    VILLAGE_SAVANNA_HOUSE("minecraft:chests/village/village_savanna_house"),
    VILLAGE_SHEPHERD("minecraft:chests/village/village_shepherd"),
    VILLAGE_SNOWY_HOUSE("minecraft:chests/village/village_snowy_house"),
    VILLAGE_TAIGA_HOUSE("minecraft:chests/village/village_taiga_house"),
    VILLAGE_TANNERY("minecraft:chests/village/village_tannery"),
    VILLAGE_TEMPLE("minecraft:chests/village/village_temple"),
    VILLAGE_TOOLSMITH("minecraft:chests/village/village_toolsmith"),
    DESERT_PYRAMID("minecraft:chests/desert_pyramid"),
    JUNGLE_TEMPLE("minecraft:chests/jungle_temple"),
    SIMPLE_DUNGEON("minecraft:chests/simple_dungeon"),
    STRONGHOLD_CORRIDOR("minecraft:chests/stronghold_corridor"),
    STRONGHOLD_CROSSING("minecraft:chests/stronghold_crossing"),
    STRONGHOLD_LIBRARY("minecraft:chests/stronghold_library"),
    PILLAGER_OUTPOST("minecraft:chests/pillager_outpost"),
    BURIED_TREASURE("minecraft:chests/buried_treasure"),
    SHIPWRECK_MAP("minecraft:chests/shipwreck_map"),
    SHIPWRECK_SUPPLY("minecraft:chests/shipwreck_supply"),
    SHIPWRECK_TREASURE("minecraft:chests/shipwreck_treasure"),
    IGLOO_CHEST("minecraft:chests/igloo_chest"),
    RUINED_PORTAL("minecraft:chests/ruined_portal"),
    WOODLAND_MANSION("minecraft:chests/woodland_mansion");

    private final String key;

    LootTablesOverworld(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
