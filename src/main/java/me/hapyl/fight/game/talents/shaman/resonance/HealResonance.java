package me.hapyl.fight.game.talents.shaman.resonance;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.shaman.Totem;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.geometry.Quality;
import me.hapyl.eterna.module.math.geometry.WorldParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class HealResonance extends TotemResonance {

    @DisplayField private final double healingAmount = 3.0d;
    @DisplayField private final double healingRadius = 3.0d;

    protected HealResonance() {
        super(
                Material.GREEN_DYE,
                "Healing Aura",
                "Periodically &aheals&7 all nearby &nallies&7."
        );

        setType(TalentType.SUPPORT);
        setDisplayData(
                "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-0.7500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{east:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.7500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{north:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.7500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{west:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.2500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{south:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.2500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{east:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.7500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{north:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.7500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{west:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.2500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{south:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.2500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.1600f,0.0000f,0.8125f,0.0000f,0.6875f,0.0000f,0.0000f,0.8125f,-0.1600f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.6589f,0.0000f,0.8125f,0.0000f,0.5625f,0.0000f,0.0000f,0.8125f,-0.1600f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.6589f,0.0000f,0.8125f,0.0000f,0.8125f,0.0000f,0.0000f,0.8125f,-0.6568f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.1540f,0.0000f,0.8125f,0.0000f,0.4375f,0.0000f,0.0000f,0.8125f,-0.6568f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:lime_terracotta\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.2500f,0.0000f,1.6250f,0.0000f,-0.9375f,0.0000f,0.0000f,0.5000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:dark_oak_trapdoor\",Properties:{facing:\"east\",half:\"bottom\",open:\"false\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,1.0000f,0.0000f,-1.0000f,0.0000f,0.0000f,1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
        );
        setInterval(10);
    }

    @Override
    public void resonate(@Nonnull Totem totem) {
        final Location location = totem.getLocation();
        final GamePlayer player = totem.getPlayer();

        Collect.nearbyEntities(location, healingRadius).forEach(entity -> {
            if (!player.isSelfOrTeammate(entity)) {
                return;
            }

            entity.heal(healingAmount, player);
        });

        // Fx
        Geometry.drawCircleAnchored(location, healingRadius, Quality.VERY_HIGH, new WorldParticle(Particle.HAPPY_VILLAGER));
        player.playWorldSound(location, Sound.BLOCK_GRASS_STEP, 0.0f);
    }
}
