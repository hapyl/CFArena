package me.hapyl.fight.game.heroes.echo;


import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.collection.player.PlayerMap;

import javax.annotation.Nonnull;

public class Echo extends Hero implements Disabled {

    private final PlayerMap<PlayerEcho> playerEchoes = PlayerMap.newMap();

    public Echo(@Nonnull Key key) {
        super(key, "Echo");
    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.ECHO;
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
