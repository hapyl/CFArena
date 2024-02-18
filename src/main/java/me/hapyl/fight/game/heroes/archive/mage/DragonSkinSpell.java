package me.hapyl.fight.game.heroes.archive.mage;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Particle;

import javax.annotation.Nonnull;

public class DragonSkinSpell extends MageSpell {

    @DisplayField private final double attackIncrease = 1.2;

    public DragonSkinSpell() {
        super(
                "Dragon's Skin",
                "Consume to gain &cincredible strength&7, but suffer %s reduction.".formatted(AttributeType.SPEED),
                Material.PHANTOM_MEMBRANE
        );

        setDurationSec(10);
    }

    @Override
    protected void useSpell(@Nonnull GamePlayer player) {
        final EntityAttributes attributes = player.getAttributes();
        final int duration = getDuration();

        player.addEffect(Effects.SLOW, 2, duration);
        player.addEffect(Effects.JUMP_BOOST, 250, duration);

        attributes.increaseTemporary(Temper.WYVERN_HEART, AttributeType.ATTACK, attackIncrease, duration);

        // Fx
        player.spawnWorldParticle(player.getLocation().add(0.0d, 1.0d, 0.0d), Particle.CRIT_MAGIC, 40, 0.1, 0.1, 0.1, 1);
    }
}
