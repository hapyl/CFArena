package me.hapyl.fight.game.talents;

import me.hapyl.fight.annotate.SelfCallable;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public interface Creation extends Removable {

    @SelfCallable(false)
    void create(@Nonnull GamePlayer player);

}
