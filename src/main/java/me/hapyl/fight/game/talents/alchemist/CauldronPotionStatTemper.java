package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Color;

import javax.annotation.Nonnull;

public class CauldronPotionStatTemper extends CauldronPotion {

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
        entity.getAttributes().decreaseTemporary(Temper.ALCHEMIST, attributeType, amount, duration, alchemist);
    }

}
