package me.hapyl.fight.invite;

import me.hapyl.fight.game.Response;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface Acceptable {
    
    @Nonnull
    Response onAccept(@Nonnull Player player);
    
    void onDecline(@Nonnull Player player);
    
}
