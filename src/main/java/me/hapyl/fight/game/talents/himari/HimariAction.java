package me.hapyl.fight.game.talents.himari;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public interface HimariAction {

    boolean execute(@Nonnull GamePlayer player);

}
