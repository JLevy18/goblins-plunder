package com.levthedev.mc.utility;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;

public class Serializer {
    public static String serializeContainerContents(ItemStack[] items) {
        Map<String,Object>[] serializeItems = new Map[items.length];
        
        for (int i = 0; i < items.length; i++) {
            if( items[i] != null){
                serializeItems[i] = items[i].serialize();
            } else {
                serializeItems[i] = new HashMap<>();
            }
        }

        return new Gson().toJson(serializeItems);
    }

    //public static String deserializeContainerContents();

    public static byte[] toByteArray(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }
}
