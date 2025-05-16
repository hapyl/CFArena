package me.hapyl.fight.game.talents.frostbite;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;

import javax.annotation.Nonnull;
import java.util.Set;

public class IcyShardsPassive extends PassiveTalent {
    
    @DisplayField public final double chance = 0.25d;
    
    @DisplayField private final int impairDuration = Tick.fromSecond(6);
    @DisplayField private final short icicles = 8;
    @DisplayField private final double damage = 3.0d;
    @DisplayField private final double startDistance = 0.75f;
    @DisplayField private final double maxDistance = 3.0d;
    
    @DisplayField(percentage = true) private final double attackSpeedReduction = -0.5;
    @DisplayField(percentage = true) private final double attackReduction = -0.25;
    
    private final Particle.DustTransition transition = new Particle.DustTransition(
            Color.fromRGB(13, 70, 161),
            Color.fromRGB(74, 115, 181),
            1
    );
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("icy_shards"));
    
    public IcyShardsPassive(@Nonnull Key key) {
        super(key, "Icy Shards");
        
        setDescription("""
                       When &ntaking&7 &ndamage&7, there is a small chance to launch &bicicles&7 in all directions.
                       
                       Each &bicicle&7 deals &4damage&7 and decreases %s and %s.
                       """.formatted(AttributeType.ATTACK_SPEED, AttributeType.ATTACK)
        );
        
        setMaterial(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
        setType(TalentType.IMPAIR);
        
        setCooldownSec(2);
    }
    
    public void launchIcicles(@Nonnull GamePlayer player) {
        final Location location = player.getLocation().add(0, 1.6, 0);
        final double increment = Math.PI * 2 / icicles;
        
        new TickingGameTask() {
            private final Set<Integer> hitIcicles = Sets.newHashSet();
            private double distance = startDistance;
            
            @Override
            public void run(int tick) {
                if (hitIcicles.size() == icicles || distance >= maxDistance) {
                    cancel();
                    return;
                }
                
                int index = 0;
                
                for (int i = 0; i < 3; i++) {
                    for (double d = 0.0d; d < Math.PI * 2; d += increment, index++) {
                        if (hitIcicles.contains(index)) {
                            continue;
                        }
                        
                        final double x = Math.sin(d) * distance;
                        final double y = Math.sin(Math.toRadians(tick) * 8) * -1;
                        final double z = Math.cos(d) * distance;
                        
                        location.add(x, y, z);
                        
                        player.spawnWorldParticle(
                                location,
                                Particle.DUST_COLOR_TRANSITION,
                                1,
                                0,
                                0,
                                0,
                                0,
                                transition
                        );
                        
                        // Hit detection
                        for (LivingGameEntity entity : Collect.nearbyEntities(location, 1.0d)) {
                            if (player.isSelfOrTeammate(entity)) {
                                continue;
                            }
                            
                            entity.damage(damage, player, DamageCause.ICICLE);
                            entity.getAttributes().addModifier(
                                    modifierSource, impairDuration, player, modifier -> modifier
                                            .of(AttributeType.ATTACK_SPEED, ModifierType.MULTIPLICATIVE, attackSpeedReduction)
                                            .of(AttributeType.ATTACK, ModifierType.MULTIPLICATIVE, attackReduction)
                            );
                            
                            hitIcicles.add(index);
                        }
                        
                        location.subtract(x, y, z);
                    }
                    
                    distance += 0.5d / 3;
                }
                
            }
        }.runTaskTimer(1, 1);
        
        // Fx
        player.playWorldSound(Sound.BLOCK_GLASS_BREAK, 0.75f);
        player.playWorldSound(Sound.ENTITY_VILLAGER_HURT, 0.75f);
    }
    
    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
