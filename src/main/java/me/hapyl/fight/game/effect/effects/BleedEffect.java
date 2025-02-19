package me.hapyl.fight.game.effect.effects;

import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.*;

import javax.annotation.Nonnull;

public class BleedEffect extends Effect {

    private final Particle.DustTransition dustTransition = new Particle.DustTransition(
            Color.fromRGB(125, 1, 20),
            Color.fromRGB(194, 14, 41),
            2
    );

    private final int damagePeriod = 20;
    private final double damage = 2.0d;

    public BleedEffect() {
        super("&4∲ Bleeding", EffectType.NEGATIVE);
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        entity.sendMessage("&4∲ &cYou are bleeding!");
        entity.playSound(Sound.ENTITY_ZOMBIE_INFECT, 1.0f);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.sendMessage("&4∲ &aThe bleeding has stopped!");
        entity.playSound(Sound.ENTITY_HORSE_SADDLE, 1.25f);
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        if (tick % damagePeriod != 0) {
            return;
        }

        entity.damage(damage, DamageCause.BLEED);
        spawnParticle(entity.getLocation());
    }

    public void spawnParticle(Location location) {
        final World world = location.getWorld();

        if (world == null) {
            return;
        }

        world.spawnParticle(Particle.DUST_COLOR_TRANSITION, location, 1, 0.2d, 0.2d, 0.2d, 0, dustTransition);
    }
}
