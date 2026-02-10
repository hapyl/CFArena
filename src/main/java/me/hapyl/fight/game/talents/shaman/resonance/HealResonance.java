package me.hapyl.fight.game.talents.shaman.resonance;

import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.shaman.Totem;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;

import javax.annotation.Nonnull;

public class HealResonance extends TotemResonance {
    
    @DisplayField private final double healing = 0.03;
    @DisplayField private final double healingRadius = 4.5;
    
    private final Color leavesColor = Color.fromARGB(200, 16, 222, 56);
    
    protected HealResonance() {
        super(
                Material.GREEN_DYE,
                "Healing Aura",
                "Periodically &aheals&7 all nearby &aallies&7."
        );
        
        setType(TalentType.SUPPORT);
        setDisplayData(
                "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[1.0000f,0.0000f,0.0000f,-0.7500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{east:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.7500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{north:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.7500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{west:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.2500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{south:\"true\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.2500f,0.0000f,1.0000f,0.0000f,-0.8750f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{east:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.7500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{north:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.7500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{west:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.2500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{south:\"true\"}},transformation:[-1.0000f,-0.0000f,0.0000f,0.2500f,0.0000f,-1.0000f,0.0000f,1.1250f,0.0000f,0.0000f,1.0000f,-0.7500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.1600f,0.0000f,0.8125f,0.0000f,0.6875f,0.0000f,0.0000f,0.8125f,-0.1600f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.6589f,0.0000f,0.8125f,0.0000f,0.5625f,0.0000f,0.0000f,0.8125f,-0.1600f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.6589f,0.0000f,0.8125f,0.0000f,0.8125f,0.0000f,0.0000f,0.8125f,-0.6568f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.8125f,0.0000f,0.0000f,-0.1540f,0.0000f,0.8125f,0.0000f,0.4375f,0.0000f,0.0000f,0.8125f,-0.6568f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:lime_terracotta\",Properties:{}},transformation:[0.5000f,0.0000f,0.0000f,-0.2500f,0.0000f,1.6250f,0.0000f,-0.9375f,0.0000f,0.0000f,0.5000f,-0.2500f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:dark_oak_trapdoor\",Properties:{facing:\"east\",half:\"bottom\",open:\"false\"}},transformation:[1.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,1.0000f,0.0000f,-1.0000f,0.0000f,0.0000f,1.0000f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
        );
    }
    
    @Override
    public void resonate(@Nonnull Totem totem) {
        final Location location = totem.getLocation();
        final GamePlayer player = totem.getPlayer();
        
        Collect.nearbyEntities(location, healingRadius, player::isSelfOrTeammate)
               .forEach(entity -> {
                   entity.healRelativeToMaxHealth(healing, player);
               });
        
        // Fx
        player.playWorldSound(location, Sound.BLOCK_GRASS_STEP, 0.0f);
    }
    
    @Override
    public void tick(@Nonnull Totem totem, int tick) {
        final Location location = totem.getLocation();
        final GamePlayer player = totem.getPlayer();
        
        final double rad = Math.toRadians(tick) * 6;
        final double x = Math.sin(rad) * healingRadius;
        final double y = Math.sin(Math.toRadians(tick) * 10) * 0.2;
        final double z = Math.cos(rad) * healingRadius;
        
        LocationHelper.offset(location, x, y, z, () -> {
            player.spawnWorldParticle(location, Particle.TINTED_LEAVES, 1, 0,0,0, 0, leavesColor);
        });
        
        LocationHelper.offset(location, -x, y, -z, () -> {
            player.spawnWorldParticle(location, Particle.TINTED_LEAVES, 1, 0,0,0, 0, leavesColor);
        });
    }
}
