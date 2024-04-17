package me.hapyl.fight.game.talents.tamer;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.tamer.Tamer;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.Timed;

import javax.annotation.Nonnull;

public interface TamerTimed extends Timed {

    @Deprecated
    @Override
    int getDuration();

    default int getDuration(@Nonnull GamePlayer player) {
        final int duration = getDuration0();

        return isUsingUltimate(player) ? (int) (duration * getHero().ultimateMultiplier) : duration;
    }

    default double scaleUltimateEffectiveness(@Nonnull GamePlayer player, double value) {
        return isUsingUltimate(player) ? value * getHero().ultimateMultiplier : value;
    }

    default boolean isUsingUltimate(@Nonnull GamePlayer player) {
        return player.isUsingUltimate();
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
