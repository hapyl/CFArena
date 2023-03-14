package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import org.bukkit.entity.Player;

public class Healer extends Hero {
    public Healer() {
        super("Healer");


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
        return Talents.REVIVE.getTalent();
    }
}
