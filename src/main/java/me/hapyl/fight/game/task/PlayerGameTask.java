package me.hapyl.fight.game.task;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * GameTask that is automatically canceled if a player has died.
 */
public abstract class PlayerGameTask extends GameTask {

    private final GamePlayer player;

    public PlayerGameTask(@Nonnull GamePlayer player) {
        this.player = player;
        this.player.addTask(this);
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }
}
