package me.hapyl.fight.game.heroes.archive.mage;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class WyvernHeartSpell extends MageSpell {

    @DisplayField(suffix = "❤") private final double healingAmount = 25.0d;
    @DisplayField(scaleFactor = 100) private final double attackDecrease = 0.99d;

    public WyvernHeartSpell() {
        super("Heart of Wyvern", "Consume to &cheal&7 yourself, gain &bspeed&7 boost but suffer %s reduction.".formatted(AttributeType.ATTACK), Material.FERMENTED_SPIDER_EYE);

        setDurationSec(25);
    }

    @Override
    protected void useSpell(@Nonnull GamePlayer player) {
        final int duration = getDuration();

        player.addEffect(Effects.SPEED, 2, duration);
        player.getAttributes().decreaseTemporary(Temper.WYVERN_HEART, AttributeType.ATTACK, attackDecrease, duration);
        player.heal(healingAmount);
    }
}
