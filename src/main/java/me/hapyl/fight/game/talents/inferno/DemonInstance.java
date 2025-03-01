package me.hapyl.fight.game.talents.inferno;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.inferno.InfernoData;
import me.hapyl.fight.game.talents.Removable;

import javax.annotation.Nonnull;

public interface DemonInstance extends Removable {

    void onForm(@Nonnull GamePlayer player, @Nonnull InfernoData data);

    void onReform(@Nonnull GamePlayer player, @Nonnull InfernoData data);

    void onTick(@Nonnull GamePlayer player, @Nonnull InfernoData data, int tick);

}
