package me.hapyl.fight.game.talents.nyx;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.Shield;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.display.StringDisplay;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class NyxPassive extends PassiveTalent {
    
    @DisplayField private final double healing = 10;
    @DisplayField private final double effectResIncrease = 25;
    @DisplayField private final double explosionRadius = 3.0;
    
    @DisplayField(percentage = true) private final double shieldCapacityScaling = 0.10d;
    @DisplayField(percentage = true) private final double damageScaling = 0.065d;
    
    @DisplayField private final int buffDuration = Tick.fromSeconds(6);
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("reverberation"));
    
    public NyxPassive(@Nonnull Key key) {
        super(key, "Reverberation");
        
        setDescription("""
                       Whenever &6Nyx&7 or her &aally&7 &eimpairs&7 an &cenemy&7, and she has a %s stack, Nyx will launch a &6&nfollowup&7 &6&nattack&7.
                       
                       Upon hit, it deals &cAoE damage&7 and grants a &9Void Shield&7 to the teammate who triggered this attack.
                       
                       &6Void Shied
                       Whenever the shield is broken or refreshed, &aheals&7 its target and increases %s.
                       """.formatted(Named.THE_CHAOS, AttributeType.EFFECT_RESISTANCE)
        );
        
        setType(TalentType.IMPAIR);
        setMaterial(Material.SHULKER_SHELL);
        setCooldownSec(2.5f);
    }
    
    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
    
    public void createShield(@Nonnull GamePlayer nyx, @Nonnull GamePlayer target) {
        final double health = nyx.getHealth();
        final double shieldCapacity = health * shieldCapacityScaling;
        
        if (shieldCapacity < 0) {
            return;
        }
        
        target.setShield(new VoidShield(nyx, target, shieldCapacity));
    }
    
    public void execute(@Nonnull GamePlayer nyx, @Nonnull GamePlayer player, @Nonnull LivingGameEntity target) {
        final Location location = target.getLocation();
        final double damage = nyx.scaleHealth(damageScaling);
        
        Collect.nearbyEntities(location, explosionRadius, entity -> !player.isSelfOrTeammate(entity))
               .forEach(entity -> {
                   // Add assist to nyx
                   entity.addAssistingPlayer(nyx);
                   
                   // Make the player who triggered the followup the damager,
                   // nyx gets the assist if entity dies from this attack
                   entity.setLastDamager(player);
                   entity.damage(damage, DamageCause.CHAOS);
               });
        
        startCooldown(nyx);
        createShield(nyx, player);
        
        location.add(0, target.getEyeHeight(), 0);
        
        // Fx
        new TickingGameTask() {
            
            private double t = 0;
            
            @Override
            public void run(int tick) {
                for (int i = 0; i < 8; i++) {
                    next();
                }
            }
            
            private void next() {
                if (t >= Math.PI * 1.25d) {
                    cancel();
                    return;
                }
                
                final double x = Math.sin(t) * 1.25d;
                final double y = Math.tan(Math.cos(t) * 0.5d) * 0.9d;
                final double z = Math.cos(t * 0.75d) * 0.9d;
                
                location.add(x, y, z);
                HeroRegistry.NYX.drawParticle(location);
                nyx.spawnWorldParticle(location, Particle.PORTAL, 1);
                location.subtract(x, y, z);
                
                t += Math.PI / 16;
            }
            
        }.runTaskTimer(0, 1);
        
        // Sfx
        nyx.playWorldSound(location, Sound.BLOCK_AMETHYST_BLOCK_BREAK, 0.0f);
        nyx.playWorldSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.75f);
    }
    
    private class VoidShield extends Shield {
        
        private final GamePlayer nyx;
        
        public VoidShield(@Nonnull GamePlayer nyx, @Nonnull GamePlayer player, double maxCapacity) {
            super(player, maxCapacity);
            
            this.nyx = nyx;
        }
        
        @Override
        public void display(double damage, @Nonnull Location location) {
            StringDisplay.ascend(entity.getEyeLocation(), "%1$s&l⚫ %1$s%2$.0f".formatted(Color.VOID, damage), 20);
        }
        
        @Override
        public void onRemove(@Nonnull Cause cause) {
            entity.heal(healing, nyx);
            entity.getAttributes().addModifier(modifierSource, buffDuration, modifier -> modifier.of(AttributeType.EFFECT_RESISTANCE, ModifierType.FLAT, effectResIncrease));
            
            // Fx
            entity.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.75f);
            entity.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.25f);
        }
    }
    
}
