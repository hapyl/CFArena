package me.hapyl.fight.game.heroes.archive.jester;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Jester extends Hero implements DisabledHero {
    public Jester() {
        super("Jester");
    }

    @Override
    public void onDeathGlobal(@Nonnull GamePlayer player, @Nullable GameEntity killer, @Nullable EnumDamageCause cause) {
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        return UltimateCallback.OK;
    }

    @Override
    public Talent getFirstTalent() {
        return null;
    }

    @Override
    public Talent getSecondTalent() {
        return null;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }
}
