package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.*;

import javax.annotation.Nonnull;

public class BleedEffect extends GameEffect {

    private final Particle.DustTransition dustTransition = new Particle.DustTransition(
            Color.fromRGB(125, 1, 20),
            Color.fromRGB(194, 14, 41),
            2
    );
    private final double damage = 2.0d;

    public BleedEffect() {
        super("Bleed");
        setPositive(false);
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity) {
        entity.sendMessage("&c&l∲ &7You are bleeding!");
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity) {
        entity.sendMessage("&c&l∲ &aThe bleeding has stopped!");
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        if (tick == 0) {
            entity.damage(damage, EnumDamageCause.BLEED);
            entity.getWorld()
                    .spawnParticle(
                            Particle.BLOCK_CRACK,
                            entity.getLocation(),
                            10,
                            0.5d,
                            0.5d,
                            0.5d,
                            0.0d,
                            Material.REDSTONE_BLOCK.createBlockData()
                    );
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
