package me.hapyl.fight.activity;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

public interface Activity {
    
    void onStart(@Nonnull Player player);
    
    void onStop(@Nonnull Player player);
    
    void onTick(@Nonnull Player player, int tick);
    
    void onKick(@Nonnull Player player);
    
    @Nonnull
    List<Player> players();
    
    default void forEachPlayers(@Nonnull Consumer<Player> consumer) {
        players().forEach(consumer);
    }
}
