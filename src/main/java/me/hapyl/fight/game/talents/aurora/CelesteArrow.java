package me.hapyl.fight.game.talents.aurora;

import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.particle.ParticleDrawer;
import me.hapyl.fight.util.particle.Particles;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Arrow;

import javax.annotation.Nonnull;

public class CelesteArrow extends AuroraArrowTalent {

    @DisplayField private final double healing = 7;

    private final BlockData blockData = Material.GREEN_GLAZED_TERRACOTTA.createBlockData();
    private final ParticleDrawer fxParticle = Particles.mobSpell(36, 227, 71);

    public CelesteArrow(@Nonnull Key key) {
        super(key, "Celeste Arrows", ChatColor.GREEN, 4, 5.0d, 0.85d);

        setDescription("""
                Equip {name} that &aheal&7 hit &bentities.
                &8;;If an enemy is hit, instead of healing, the damage is reduced.
                
                &7&o;;Celeste arrows home towards nearby teammates.
                """
        );

        setType(TalentType.SUPPORT);
        setItem(Material.SMALL_DRIPLEAF);

        setCooldownSec(12);
    }

    @Override
    public void onMove(@Nonnull GamePlayer player, @Nonnull Location location) {
        player.spawnWorldParticle(location, Particle.FALLING_DUST, 1, 0.25, 0.25, 0.25, 0.05f, blockData);
    }

    @Override
    public void onShoot(@Nonnull GamePlayer player, @Nonnull Arrow arrow) {
        arrow.setColor(Color.GREEN);
        arrow.setCritical(false);
    }

    @Override
    public void onHit(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, @Nonnull DamageInstance instance) {
        instance.setCause(EnumDamageCause.CELESTE_ARROW);

        // If not teammate, just reduce the damage
        if (!player.isTeammate(entity)) {
            instance.setDamage(instance.getDamage() - healing);
            return;
        }

        entity.heal(healing, player);

        // Fx
        new TickingGameTask() {
            private double d;

            private boolean next(int tick) {
                if (d >= Math.PI * 4) {
                    cancel();
                    return true;
                }

                final Location location = entity.getLocation();

                final double x = Math.sin(d) * 0.9d;
                final double y = tick / 20d;
                final double z = Math.cos(d) * 0.9d;

                LocationHelper.offset(location, x, y, z, loc -> {
                    fxParticle.draw(loc);
                });

                LocationHelper.offset(location, z, y, x, loc -> {
                    fxParticle.draw(loc);
                });

                d += Math.PI / 16;
                return false;
            }

            @Override
            public void run(int tick) {
                for (int i = 0; i < 5; i++) {
                    if (next(tick)) {
                        cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(0, 1);

        entity.playWorldSound(Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.25f);
    }

}
