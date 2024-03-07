package me.hapyl.fight.game.talents.archive.shaman.resonance;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.shaman.Totem;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class DamageResonance extends TotemResonance {

    @DisplayField private final double damageRadius = 3.5d;
    @DisplayField private final double damage = 5.0d;

    protected DamageResonance() {
        super(
                Material.RED_DYE,
                "Damage Aura",
                """
                        Periodically deals &cdamage&7 to nearby &nenemies&7.
                        """
        );

        setType(Talent.Type.DAMAGE);
        setInterval(16);
        setDisplayData(
                "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-0.7500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{east:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.7500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{north:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.7500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{west:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.2500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{south:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.2500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{east:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.7500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{north:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.7500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{west:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.2500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{south:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.2500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.1600f,0.0000f,0.8125f,0.0000f,0.6875f,0.0000f,0.0000f,0.8125f,-0.1600f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.6589f,0.0000f,0.8125f,0.0000f,0.5625f,0.0000f,0.0000f,0.8125f,-0.1600f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.6589f,0.0000f,0.8125f,0.0000f,0.8125f,0.0000f,0.0000f,0.8125f,-0.6568f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.1540f,0.0000f,0.8125f,0.0000f,0.4375f,0.0000f,0.0000f,0.8125f,-0.6568f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:red_terracotta\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.2500f,0.0000f,1.6250f,0.0000f,-0.9375f,0.0000f,0.0000f,0.5000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:dark_oak_trapdoor\",Properties:{facing:\"east\",half:\"bottom\",open:\"false\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,1.0000f,0.0000f,-1.0000f,0.0000f,0.0000f,1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
        );
    }

    @Override
    public void resonate(@Nonnull Totem totem) {
        final Location location = totem.getLocation();
        final GamePlayer player = totem.getPlayer();

        Collect.nearbyEntities(location, damageRadius).forEach(entity -> {
            if (player.isSelfOrTeammate(entity)) {
                return;
            }

            entity.setLastDamager(player);
            entity.damageTick(damage, EnumDamageCause.TOTEM, 0);
        });

        // Fx
        new TickingGameTask() {
            private double radius = damageRadius;

            @Override
            public void run(int tick) {
                for (double d = 0; d < Math.PI * 2; d += Math.PI / (radius * 2)) {
                    final double x = Math.sin(d) * radius + player.random.nextDouble();
                    final double z = Math.cos(d) * radius + player.random.nextDouble();

                    location.add(x, -1, z);

                    player.spawnWorldParticle(location, Particle.LAVA, 1, 0.1, 0.1, 0.1, 0.5f);

                    location.subtract(x, -1, z);
                }

                if ((radius -= 0.5d) <= 1) {
                    cancel();
                }
            }
        }.runTaskTimer(0, 1);

        player.playWorldSound(location, Sound.BLOCK_LAVA_POP, 0.0f);
    }
}
