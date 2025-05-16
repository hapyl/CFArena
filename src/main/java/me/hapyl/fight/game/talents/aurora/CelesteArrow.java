package me.hapyl.fight.game.talents.aurora;

import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.particle.ParticleBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Arrow;

import javax.annotation.Nonnull;

public class CelesteArrow extends AuroraArrowTalent {
    
    @DisplayField(percentage = true) private final double healing = 0.07;
    
    private final BlockData blockData = Material.GREEN_GLAZED_TERRACOTTA.createBlockData();
    private final ParticleBuilder fxParticle = ParticleBuilder.mobSpell(Color.fromRGB(36, 227, 71), false);
    
    public CelesteArrow(@Nonnull Key key) {
        super(key, "Celeste Arrows", ChatColor.GREEN, 4, 5.0d, 0.85d);
        
        setDescription("""
                       Equip {name} that &aheal&7 hit &ateammates&7 for &2{healing}&7 of their %s.
                       
                       &7&o;;Celeste arrows home towards nearby teammates.
                       """.formatted(AttributeType.MAX_HEALTH)
        );
        
        setType(TalentType.SUPPORT);
        setMaterial(Material.SMALL_DRIPLEAF);
        
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
        if (!player.isTeammate(entity)) {
            return;
        }
        
        entity.healRelativeToMaxHealth(healing, player);
        
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
                
                LocationHelper.offset(location, x, y, z, () -> fxParticle.display(location));
                LocationHelper.offset(location, z, y, x, () -> fxParticle.display(location));
                
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
