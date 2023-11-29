package com.levthedev.mc.utility;

public enum LootTablesOverworld {
    ABANDONED_MINESHAFT("chests/abandoned_mineshaft"),
    VILLAGE_ARMORER("chests/village/village_armorer"),
    VILLAGE_BUTCHER("chests/village/village_butcher"),
    VILLAGE_CARTOGRAPHER("chests/village/village_cartographer"),
    VILLAGE_DESERT_HOUSE("chests/village/village_desert_house"),
    VILLAGE_FLETCHER("chests/village/village_fletcher"),
    VILLAGE_MASON("chests/village/village_mason"),
    VILLAGE_PLAINS_HOUSE("chests/village/village_plains_house"),
    VILLAGE_SAVANNA_HOUSE("chests/village/village_savanna_house"),
    VILLAGE_SHEPHERD("chests/village/village_shepherd"),
    VILLAGE_SNOWY_HOUSE("chests/village/village_snowy_house"),
    VILLAGE_TAIGA_HOUSE("chests/village/village_taiga_house"),
    VILLAGE_TANNERY("chests/village/village_tannery"),
    VILLAGE_TEMPLE("chests/village/village_temple"),
    VILLAGE_TOOLSMITH("chests/village/village_toolsmith"),
    DESERT_PYRAMID("chests/desert_pyramid"),
    JUNGLE_TEMPLE("chests/jungle_temple"),
    SIMPLE_DUNGEON("chests/simple_dungeon"),
    STRONGHOLD_CORRIDOR("chests/stronghold_corridor"),
    STRONGHOLD_CROSSING("chests/stronghold_crossing"),
    STRONGHOLD_LIBRARY("chests/stronghold_library"),
    PILLAGER_OUTPOST("chests/pillager_outpost"),
    BURIED_TREASURE("chests/buried_treasure"),
    SHIPWRECK_MAP("chests/shipwreck_map"),
    SHIPWRECK_SUPPLY("chests/shipwreck_supply"),
    SHIPWRECK_TREASURE("chests/shipwreck_treasure"),
    IGLOO_CHEST("chests/igloo_chest"),
    RUINED_PORTAL("chests/ruined_portal"),
    WOODLAND_MANSION("chests/woodland_mansion");

    private final String key;

    LootTablesOverworld(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
