package me.hapyl.fight.game;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface PlayerExecutable {

    void execute(@Nonnull Player player);

    default void execute(@Nonnull GamePlayer player) {
        execute(player.getPlayer());
    }

}
