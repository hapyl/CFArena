package me.hapyl.fight.game.talents.shark;


import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class SharkPassive extends PassiveTalent {

    @DisplayField public final short maxStacks = 6;
    @DisplayField public final short minStacksForHeartbeat = 3;
    @DisplayField public final int stackDuration = 40;
    @DisplayField(attribute = AttributeType.ATTACK) public final double attackIncreasePerStack = 0.075d;
    @DisplayField(attribute = AttributeType.CRIT_CHANCE) public final double critChanceIncreasePerStack = 0.05d;

    public SharkPassive(@Nonnull Key key) {
        super(key, "Blood Thirst");

        setDescription("""
                Dealing &ncontinuous&7 damage grants a stack of %s up to &b%s&7 stacks.
                &8;;Lose all stacks after not gaining a stack for {stackDuration}.
                
                Each stack increases your %s and %s by {attackIncreasePerStack} and {critChanceIncreasePerStack} respectively.
                
                Having at least %s stacks allows you to hear hurt enemies heartbeat.
                """.formatted(
                Named.BLOOD_THIRST,
                maxStacks,
                AttributeType.ATTACK,
                AttributeType.CRIT_CHANCE,
                minStacksForHeartbeat
        ));

        setItem(Material.REDSTONE);
    }

    @Override
    public boolean isDisplayAttributes() {
        return true;
    }

    public void temper(GamePlayer player, int stacks) {
        final EntityAttributes attributes = player.getAttributes();

        final double attackIncrease = attackIncreasePerStack * stacks;
        final double critIncrease = critChanceIncreasePerStack * stacks;

        attributes.increaseTemporary(Temper.SHARK, AttributeType.ATTACK, attackIncrease, -1);
        attributes.increaseTemporary(Temper.SHARK, AttributeType.CRIT_CHANCE, critIncrease, -1);
    }
}
