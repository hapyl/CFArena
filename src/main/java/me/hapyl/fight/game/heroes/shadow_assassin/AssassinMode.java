package me.hapyl.fight.game.heroes.shadow_assassin;

import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.Outline;
import org.bukkit.Color;
import org.bukkit.Particle;

import javax.annotation.Nonnull;

public enum AssassinMode {

    STEALTH(new Particle.DustTransition(Color.fromRGB(9, 65, 133), Color.fromRGB(75, 130, 196), 2)) {
        @Override
        protected void switchTo(@Nonnull GamePlayer player, @Nonnull ShadowAssassin assassin) {
            assassin.getEquipment().equip(player);
            player.setOutline(Outline.CLEAR);

            // Reset temper
            player.getAttributes().resetTemper(Temper.SHADOW_ASSASSIN_FURY_MODE);
        }
    },

    FURY(new Particle.DustTransition(Color.fromRGB(99, 6, 6), Color.fromRGB(230, 48, 48), 2)) {
        @Override
        protected void switchTo(@Nonnull GamePlayer player, @Nonnull ShadowAssassin assassin) {
            assassin.furyEquipment.equip(player);
            player.setOutline(Outline.RED);

            // Temper with attributes
            final EntityAttributes attributes = player.getAttributes();
            final double baseAttack = attributes.getWithoutTempers(AttributeType.ATTACK);
            final double speedDecrease = assassin.getAttributes().get(AttributeType.SPEED) - AttributeType.SPEED.getDefaultValue();

            attributes.increaseTemporary(Temper.SHADOW_ASSASSIN_FURY_MODE, AttributeType.ATTACK, baseAttack * assassin.attackIncrease, Constants.INFINITE_DURATION);
            attributes.decreaseTemporary(Temper.SHADOW_ASSASSIN_FURY_MODE, AttributeType.SPEED, speedDecrease, Constants.INFINITE_DURATION);
        }
    };

    private final Particle.DustTransition transition;

    AssassinMode(Particle.DustTransition transition) {
        this.transition = transition;
    }

    @Nonnull
    public Particle.DustTransition getTransition() {
        return transition;
    }

    protected void switchTo(@Nonnull GamePlayer player, @Nonnull ShadowAssassin assassin) {
    }

}
