package me.hapyl.fight.game.heroes.storage.orc;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.storage.orc.OrcAxe;
import me.hapyl.fight.game.talents.storage.orc.OrcGrowl;
import org.bukkit.entity.Player;

public class Orc extends Hero {

    /**
     * WEAPON:
     * - Axe
     * - RIGHT CLICK:
     * -- Throw: (10s max)
     * --- When enemy hit:
     * ---- Freeze & Slow
     * --- When hit block:
     * ----- Stuck in block, flies back longer.
     * --- Flies back parabola
     * <p>
     * ABILITY 1:
     * - Grown
     * -- 8 blocks radius zone:
     * --- Slowness, Weakness (6s)
     * <p>
     * ABILITY 2: INPUT
     * - LEFT CLICK: Eula
     * - RIGHT CLICK: Dash damage
     * <p>
     * PASSIVE: (3s, cd 30s)
     * If keep taking damage by player mini ultimate.
     * Negative effects deal less damage
     * <p>
     * ULTIMATE: (20s)
     * - Berserk Mode
     * -- -70 defense
     * -- Crit chance increase
     * -- Speed
     * -- Attack
     */

    public Orc() {
        super("Pakarat Rakab");

        final HeroAttributes attributes = getAttributes();
        attributes.setValue(AttributeType.HEALTH, 150);
        attributes.setValue(AttributeType.DEFENSE, 0.75d);
        attributes.setValue(AttributeType.SPEED, 0.22d);
        attributes.setValue(AttributeType.CRIT_CHANCE, 0.15d);

        setWeapon(new OrcWeapon());
    }

    @Override
    public void useUltimate(Player player) {
    }

    @Override
    public OrcGrowl getFirstTalent() {
        return (OrcGrowl) Talents.ORC_GROWN.getTalent();
    }

    @Override
    public OrcAxe getSecondTalent() {
        return (OrcAxe) Talents.ORC_AXE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }
}
