package me.hapyl.fight.event.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class PlayerNewbieEvent extends PlayerEvent {
    
    private static final HandlerList handlerList = new HandlerList();
    
    public PlayerNewbieEvent(@NotNull Player player) {
        super(player);
    }
    
    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    @Nonnull
    public static HandlerList getHandlerList() {
        return handlerList;
    }
    
}
