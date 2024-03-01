package me.hapyl.fight.util;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public interface PlayerAction {

    void accept(@Nonnull GamePlayer player);

}
