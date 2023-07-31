package me.hapyl.fight.game.heroes.archive.zealot;

import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.entity.Player;

public class Zealot extends Hero implements DisabledHero {

    /**
     * Sword adds ferocity like in hypixel.
     * Armor and sword changed color to indicate.
     */

    public Zealot() {
        super("Zealot");

        setArchetype(Archetype.STRATEGY);

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
