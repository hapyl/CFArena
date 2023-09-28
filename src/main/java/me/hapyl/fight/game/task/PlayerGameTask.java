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

    public PlayerGameTask(Player player) {
        this(GamePlayer.getExistingPlayer(player));
    }

    public PlayerGameTask(GamePlayer player) {
        Validate.isTrue(player != null, "must be an existing player");
        this.player = player;
        this.player.addTask(this);
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }
}
