package com.levthedev.mc.dao;

import java.util.UUID;

import org.bukkit.Sound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Plunder {

    // public Plunder() {
    //     this.id = UUID.randomUUID().toString();
    //     this.location = null;
    //     this.blockType = null;
    //     this.lootTableKey = null;
    //     this.worldName = null;
    //     this.ignoreRestock = null;
    //     this.contents = null;
    //     this.sound = null;
    // }

    @NonNull
    private String id;
    @NonNull
    private String location;
    @NonNull
    private String blockType;
    private String lootTableKey;
    @NonNull
    private String worldName;
    private Boolean ignoreRestock;
    private byte[] contents;

    private Sound sound;
    @NonNull
    private Boolean locked;
}
