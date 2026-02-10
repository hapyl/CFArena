package me.hapyl.fight.game.talents.warden;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class Fracture extends Talent {
    public Fracture(@NotNull Key key) {
        super(key, "Fracture");
    }

    @Override
    public @Nullable Response execute(@NotNull GamePlayer player) {
        return null;
    }
}
