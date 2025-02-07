package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.alchemist.ActivePotion;
import me.hapyl.fight.game.heroes.alchemist.AlchemistData;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Color;

import javax.annotation.Nonnull;

public class AlchemistAttributePotion extends AlchemistPotion {

    protected final AttributeType attributeType;
    protected final double attributeIncrease;

    public AlchemistAttributePotion(String name, int intoxication, Color potionColor, AttributeType attributeType, double attributeIncrease) {
        super(name, intoxication, potionColor);

        this.attributeType = attributeType;
        this.attributeIncrease = attributeIncrease;

        setDescription("""
                        Increases your %s by %s%.0f&7 for &b%s&7.
                        """.formatted(
                        attributeType.toString(),
                        attributeType.getColor(),
                        attributeType.scaleUp(attributeIncrease),
                        CFUtils.formatTick(getDuration())
                )
        );
    }

    @Nonnull
    @Override
    public ActivePotion use(@Nonnull AlchemistData data, @Nonnull GamePlayer player) {
        final EntityAttributes attributes = player.getAttributes();
        attributes.increaseTemporary(Temper.ALCHEMIST, attributeType, attributeIncrease, getDuration());

        return new ActivePotion(data, player, this);
    }
}
