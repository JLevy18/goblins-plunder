package com.levthedev.mc.dao;

import java.sql.Blob;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlunderDAO {

    @NonNull
    private String id;
    @NonNull
    private String location;
    @NonNull
    private String blockType;
    private String lootTableKey;
    private byte[] contents;

    private String responseMessage;
}
