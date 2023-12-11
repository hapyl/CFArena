package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.Vectors;
import me.hapyl.spigotutils.module.player.EffectType;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class MovementContainment extends GameEffect {

    public MovementContainment() {
        super("Containment");
        setDescription("""
                Prevents players from moving and jumping.
                """);

        setPositive(false);
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity) {
        final EntityAttributes attributes = entity.getAttributes();

        attributes.subtractSilent(AttributeType.SPEED, 100);

        if (entity instanceof GamePlayer) {
            entity.addPotionEffect(EffectType.JUMP_BOOST, 10000, 128);
        }
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity) {
        final EntityAttributes attributes = entity.getAttributes();

        attributes.addSilent(AttributeType.SPEED, 100);
        if (entity instanceof GamePlayer) {
            entity.removePotionEffect(PotionEffectType.JUMP);
        }
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        if (entity instanceof GamePlayer) {
            return;
        }

        entity.setVelocity(Vectors.DOWN);
    }
}
