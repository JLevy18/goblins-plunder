package com.levthedev.mc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpamPlunderEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private String message;

    public SpamPlunderEvent(String message){
        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    @Override
    public HandlerList getHandlers() {
       return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
