package me.hapyl.fight.game.effect.effects;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import javax.annotation.Nonnull;

public class BleedEffect extends Effect {

    private final Particle.DustTransition dustTransition = new Particle.DustTransition(
            Color.fromRGB(125, 1, 20),
            Color.fromRGB(194, 14, 41),
            2
    );
    private final double damage = 2.0d;

    public BleedEffect() {
        super("Bleed", EffectType.NEGATIVE);
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
        entity.sendMessage("&c&l∲ &7You are bleeding!");
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {
        entity.sendMessage("&c&l∲ &aThe bleeding has stopped!");
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        if (tick % 20 == 0) {
            entity.damage(damage, EnumDamageCause.BLEED);
            spawnParticle(entity.getLocation());
        }
    }

    public void spawnParticle(Location location) {
        final World world = location.getWorld();

        if (world == null) {
            return;
        }

        world.spawnParticle(Particle.DUST_COLOR_TRANSITION, location, 1, 0.2d, 0.2d, 0.2d, 0, dustTransition);
    }
}
