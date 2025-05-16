package me.hapyl.fight.game.talents.shark;


import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.*;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class SharkPassive extends PassiveTalent {
    
    @DisplayField public final short maxStacks = 6;
    @DisplayField public final short minStacksForHeartbeat = 3;
    @DisplayField public final int stackDuration = 40;
    @DisplayField(scale = 100) public final double attackIncreasePerStack = 0.075d;
    @DisplayField public final double critChanceIncreasePerStack = 5;
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("blood_thirst"));
    
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
        
        setMaterial(Material.REDSTONE);
    }
    
    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
    
    public void temper(GamePlayer player, int stacks) {
        player.getAttributes().addModifier(
                modifierSource, Constants.INFINITE_DURATION, modifier -> modifier
                        .of(AttributeType.ATTACK, ModifierType.MULTIPLICATIVE, attackIncreasePerStack * stacks)
                        .of(AttributeType.CRIT_CHANCE, ModifierType.FLAT, critChanceIncreasePerStack * stacks)
        );
    }
}
