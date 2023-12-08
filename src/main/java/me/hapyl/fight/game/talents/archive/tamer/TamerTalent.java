package me.hapyl.fight.game.talents.archive.tamer;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.tamer.Tamer;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.Timed;

import javax.annotation.Nonnull;

public interface TamerTalent extends Timed {

    @Deprecated
    @Override
    int getDuration();

    default int getDuration(@Nonnull GamePlayer player) {
        final int duration = getDuration0();

        return isUsingUltimate(player) ? duration * 2 : duration;
    }

    default boolean isUsingUltimate(@Nonnull GamePlayer player) {
        return getHero().isUsingUltimate(player);
    }

    private int getDuration0() {
        if (this instanceof InputTalent inputTalent) {
            return inputTalent.getLeftData().getDuration();
        }

        return getDuration();
    }

    private Tamer getHero() {
        return Heroes.TAMER.getHero(Tamer.class);
    }

}
