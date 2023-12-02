package com.levthedev.mc.dao;

import java.sql.Blob;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Plunder {

    private String id;
    private String location;
    private String blockType;
    private String lootTableKey;
    private String worldName;
    private byte[] contents;

}
