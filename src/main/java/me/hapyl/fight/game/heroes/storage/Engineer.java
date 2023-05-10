package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.entity.Player;

public class Engineer extends Hero implements DisabledHero {
    public Engineer() {
        super("Engineer");

        setRole(Role.STRATEGIST);
        setItem("55f0bfea3071a0eb37bcc2ca6126a8bdd79b79947734d86e26e4d4f4c7aa9");
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
