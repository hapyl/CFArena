package me.hapyl.fight.game.talents.juju;


import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.dot.DotType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PoisonZone extends Talent {
    
    @DisplayField private final double radius = 5.0d;
    @DisplayField private final double damagePerTick = 1.0d;
    @DisplayField(scale = 100) private final double defenseReduction = -0.7d;
    
    @DisplayField private final int damagePeriod = 5;
    @DisplayField private final int defenseReductionDuration = Tick.fromSeconds(5);
    @DisplayField private final short poisonStacks = 5;
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("poison_ivy"));
    private final Color leavesColor = Color.fromARGB(200, 32, 140, 7);
    
    public PoisonZone(@Nonnull Key key) {
        super(key, "Poison Zone");
        
        setDurationSec(6);
    }
    
    @Override
    public final @Nullable Response execute(@Nonnull GamePlayer player) {
        execute(player, player.getLocation());
        return Response.OK;
    }
    
    public void execute(@Nonnull GamePlayer player, @Nonnull Location location) {
        new TimedGameTask(this) {
            private double theta;
            
            @Override
            public void run(int tick) {
                Collect.nearbyEntities(location, radius, player::isNotSelfOrTeammateOrHasEffectResistance).forEach(living -> {
                    // Can't use modulo() because we do need to damage at first tick
                    if (tick % damagePeriod == 0) {
                        living.damageNoKnockback(damagePerTick, player, DamageCause.POISON_IVY);
                    }
                    
                    living.setDotStacks(DotType.POISON, poisonStacks, player);
                    living.getAttributes().addModifier(modifierSource, defenseReductionDuration, player, modifier -> modifier.of(AttributeType.DEFENSE, ModifierType.MULTIPLICATIVE, defenseReduction));
                });
                
                // Fx
                final int points = 6;
                final double offset = Math.PI * 2 / points;
                
                for (int index = 0; index <= points; index++) {
                    final double x = Math.sin(theta + offset * index) * radius;
                    final double y = Math.sin(Math.toRadians(tick) * 5) * 0.2 + 0.5;
                    final double z = Math.cos(theta + offset * index) * radius;
                    
                    LocationHelper.offset(
                            location, x, y, z, () -> {
                                player.spawnWorldParticle(location, Particle.TINTED_LEAVES, 0, 1, 1, 1, 1, leavesColor);
                            }
                    );
                }
                
                theta += Math.PI / 32;
            }
        }.runTaskTimer(0, 1);
    }
}
