package me.hapyl.fight.game.talents.shaman.resonance;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.shaman.Totem;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class CycloneResonance extends TotemResonance {

    @DisplayField private final double cycloneRadius = 8.0d;
    @DisplayField private final double pullMagnitude = 0.75d;

    protected CycloneResonance() {
        super(
                Material.PHANTOM_MEMBRANE,
                "Cyclone Aura",
                "Periodically pulls all &nenemies&7 towards the &atotem&7."
        );

        setType(TalentType.IMPAIR);
        setDisplayData(
                "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-0.7500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{east:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.7500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{north:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.7500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{west:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.2500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{south:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.2500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{east:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.7500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{north:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.7500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{west:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.2500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{south:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.2500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.1600f,0.0000f,0.8125f,0.0000f,0.6875f,0.0000f,0.0000f,0.8125f,-0.1600f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.6589f,0.0000f,0.8125f,0.0000f,0.5625f,0.0000f,0.0000f,0.8125f,-0.1600f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.6589f,0.0000f,0.8125f,0.0000f,0.8125f,0.0000f,0.0000f,0.8125f,-0.6568f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.1540f,0.0000f,0.8125f,0.0000f,0.4375f,0.0000f,0.0000f,0.8125f,-0.6568f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:white_terracotta\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.2500f,0.0000f,1.6250f,0.0000f,-0.9375f,0.0000f,0.0000f,0.5000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:dark_oak_trapdoor\",Properties:{facing:\"east\",half:\"bottom\",open:\"false\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,1.0000f,0.0000f,-1.0000f,0.0000f,0.0000f,1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
        );
        setInterval(20);
    }

    @Override
    public void resonate(@Nonnull Totem totem) {
        final Location location = totem.getLocation();
        final GamePlayer player = totem.getPlayer();

        Collect.nearbyEntities(location, cycloneRadius).forEach(entity -> {
            if (player.isSelfOrTeammateOrHasEffectResistance(entity)) {
                return;
            }

            final Vector vector = location.toVector().subtract(entity.getLocation().toVector()).normalize().multiply(pullMagnitude);
            entity.setVelocity(vector);
        });

        // Fx
        new TickingGameTask() {
            private double radius = cycloneRadius;

            @Override
            public void run(int tick) {
                for (int i = 0; i < 2; i++) {
                    if (run0()) {
                        cancel();
                        return;
                    }
                }
            }

            private boolean run0() {
                for (double d = 0; d < Math.PI * 2; d += Math.PI / (radius * 2)) {
                    final double x = Math.sin(d) * radius;
                    final double y = 0.25d / (Math.PI * 2) * d;
                    final double z = Math.cos(d) * radius;

                    location.add(x, y, z);

                    player.spawnWorldParticle(location, Particle.SPELL_INSTANT, 1);
                    player.spawnWorldParticle(location, Particle.SPELL_MOB, 0, 1.0d, 1.0d, 1.0d, 1.0f);

                    location.subtract(x, y, z);
                }

                return (radius -= 0.5d) <= 1;
            }
        }.runTaskTimer(0, 1);

        // Fx
        player.playWorldSound(location, Sound.ENTITY_PHANTOM_BITE, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_PHANTOM_FLAP, 0.75f);
    }
}
