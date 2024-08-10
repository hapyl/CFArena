package me.hapyl.fight.game.heroes.echo;

import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.util.collection.player.PlayerMap;

import javax.annotation.Nonnull;

public class Echo extends Hero implements Disabled {

    private final PlayerMap<PlayerEcho> playerEchoes = PlayerMap.newMap();

    public Echo(@Nonnull Heroes handle) {
        super(handle, "Echo");
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.ECHO.getTalent();
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
