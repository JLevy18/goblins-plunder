package com.levthedev.mc.dao;

import org.bukkit.Sound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Plunder {

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
    private String responseMessage;
}
