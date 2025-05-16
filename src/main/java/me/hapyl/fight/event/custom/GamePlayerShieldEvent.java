package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GamePlayerShieldEvent extends GamePlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final LivingGameEntity damager;
    private boolean cancel;
    
    public GamePlayerShieldEvent(@Nonnull GamePlayer gamePlayer, @Nullable LivingGameEntity damager) {
        super(gamePlayer);
        
        this.damager = damager;
    }
    
    @Nullable
    public LivingGameEntity damager() {
        return damager;
    }
    
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
