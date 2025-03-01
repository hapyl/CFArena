package me.hapyl.fight.game.heroes.mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Particle;

import javax.annotation.Nonnull;

public class DragonSkinSpell extends MageSpell {

    @DisplayField private final double attackIncrease = 1.2d;
    @DisplayField private final double defenseIncrease = 0.7d;

    private final TemperInstance temperInstance = Temper.WYVERN_HEART.newInstance()
            .increase(AttributeType.ATTACK, attackIncrease)
            .increase(AttributeType.DEFENSE, defenseIncrease)
            .decrease(AttributeType.JUMP_STRENGTH, 1.0);

    public DragonSkinSpell() {
        super(
                Key.ofString("mage_dragons_skin"),
                "Dragon's Skin",
                "Consume to gain &cincredible strength&7, but suffer %s and %s reduction.".formatted(
                        AttributeType.SPEED,
                        AttributeType.JUMP_STRENGTH
                ),
                Material.PHANTOM_MEMBRANE
        );

        setDurationSec(8);
    }

    @Override
    protected void useSpell(@Nonnull GamePlayer player) {
        final int duration = getDuration();

        player.addEffect(EffectType.SLOW, 2, duration);

        temperInstance.temper(player, duration);

        // Fx
        player.spawnWorldParticle(player.getLocation().add(0.0d, 1.0d, 0.0d), Particle.ENCHANTED_HIT, 40, 0.1, 0.1, 0.1, 1);
    }

}
