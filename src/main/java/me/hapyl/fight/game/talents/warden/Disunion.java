package me.hapyl.fight.game.talents.warden;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class Disunion extends Talent {
    public Disunion(@NotNull Key key) {
        super(key, "Disunion");
    }

    @Override
    public @Nullable Response execute(@NotNull GamePlayer player) {
        return null;
    }
}
