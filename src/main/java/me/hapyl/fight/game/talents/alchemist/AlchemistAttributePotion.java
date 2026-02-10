package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.alchemist.ActivePotion;
import me.hapyl.fight.game.heroes.alchemist.AlchemistData;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Color;

import javax.annotation.Nonnull;

public class AlchemistAttributePotion extends AlchemistPotion {
    
    private static final ModifierSource SOURCE = new ModifierSource(Key.ofString("alchemist_potion"), true);
    
    private final AttributeType attributeType;
    private final double attributeIncrease;
    
    public AlchemistAttributePotion(String name, int intoxication, Color potionColor, AttributeType attributeType, double attributeIncrease) {
        super(name, intoxication, potionColor);
        
        this.attributeType = attributeType;
        this.attributeIncrease = attributeIncrease;
        
        setDescription("""
                       Increases your %s by %s%.0f%%&7 for &b%s&7.
                       """.formatted(
                               attributeType.toString(),
                               attributeType.getColor(),
                               attributeIncrease * 100,
                               CFUtils.formatTick(getDuration())
                       )
        );
    }
    
    @Nonnull
    @Override
    public ActivePotion use(@Nonnull AlchemistData data, @Nonnull GamePlayer player) {
        player.getAttributes().addModifier(SOURCE, getDuration(), player, modifier -> modifier.of(attributeType, ModifierType.ADDITIVE, attributeIncrease));
        
        return new ActivePotion(data, player, this);
    }
}
