package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Color;

import javax.annotation.Nonnull;

public class CauldronPotionStatTemper extends CauldronPotion {
    
    private static final ModifierSource modifierSource = new ModifierSource(Key.ofString("alchemist_cauldron"), true);
    
    private final AttributeType attributeType;
    private final double amount;
    private final int duration;
    
    protected CauldronPotionStatTemper(@Nonnull AttributeType attributeType, double amount, int duration, @Nonnull Color from, @Nonnull Color to) {
        super(from, to);
        
        this.attributeType = attributeType;
        this.amount = amount;
        this.duration = duration;
    }
    
    @Override
    public void onHit(@Nonnull LivingGameEntity entity, @Nonnull GamePlayer alchemist) {
        entity.getAttributes().addModifier(modifierSource, duration, alchemist, modifier -> modifier.of(attributeType, ModifierType.ADDITIVE, amount));
    }
    
}
