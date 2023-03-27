package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.entity.Player;

public class Taker extends Hero implements DisabledHero {

    public Taker() {
        super("");
    }

    @Override
    public void useUltimate(Player player) {

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
