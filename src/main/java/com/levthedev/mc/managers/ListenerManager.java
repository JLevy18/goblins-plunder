package com.levthedev.mc.managers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import com.levthedev.mc.GoblinsPlunder;

public class ListenerManager {
    private static final GoblinsPlunder plugin = GoblinsPlunder.getInstance();
    private static ListenerManager instance;


    private final Map<String, Listener> listeners;


    private ListenerManager(Map<String, Listener> listeners){
        this.listeners = new ConcurrentHashMap<>(listeners);
    }

    public static synchronized ListenerManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ListenerManager not initialized");
        }

        return instance;
    }

    public static synchronized void initialize(Map<String, Listener> listeners) {
        instance = new ListenerManager(listeners);
        instance.registerListeners();
    }

    private void registerListeners() {
        for (Listener listener : listeners.values()) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }

    }

    public Map<String, Listener> getListeners(){
        return this.listeners;
    }





}
