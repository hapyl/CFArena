package me.hapyl.fight.game.entity;

import me.hapyl.eterna.module.util.IOptional;
import me.hapyl.fight.CF;
import me.hapyl.fight.ux.Notifier;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class OptionalGamePlayer extends IOptional<GamePlayer> {

    private final Player bukkitPlayer;

    public OptionalGamePlayer(@Nullable Player player) {
        super(CF.getPlayer(player));

        this.bukkitPlayer = player;
    }

    @Override
    public IOptionalCallback ifPresent(@Nonnull Consumer<GamePlayer> action) {
        if (!isPresent() && bukkitPlayer != null) {
            Notifier.error(bukkitPlayer, "You must be in a game to use this!");
        }

        return super.ifPresent(action);
    }
}
