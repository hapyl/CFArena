package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.entity.Player;

public class Zealot extends Hero implements DisabledHero {
    public Zealot() {
        super("Zealot");

        setItem("131530db74bac84ad9e322280c56c4e0199fbe879883b76c9cf3fd8ff19cf025");
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
