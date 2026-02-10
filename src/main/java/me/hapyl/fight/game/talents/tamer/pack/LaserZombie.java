package me.hapyl.fight.game.talents.tamer.pack;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Zombie;

import javax.annotation.Nonnull;

public class LaserZombie extends TamerPack {
    
    @DisplayField private final int laserPeriod = 60;
    @DisplayField(percentage = true) private final double laserBaseDefenseReduction = -0.2d;
    @DisplayField private final int laserDefenseReductionDuration = 100;
    @DisplayField private final int laserHitDelay = 10;
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("laser_zombie"));
    
    public LaserZombie() {
        super(
                "Laser Zombie", """
                                Lasers target enemy, reducing their %s.
                                """.formatted(AttributeType.DEFENSE), TalentType.IMPAIR
        );
        
        attributes.setMaxHealth(20);
        attributes.setSpeed(50);
        attributes.setKnockbackResistance(1.0d);
        
        setDurationSec(20);
    }
    
    @Override
    public void onSpawn(@Nonnull ActiveTamerPack pack, @Nonnull Location location) {
        pack.createEntity(location, Entities.ZOMBIE, entity -> new LaserZombieEntity(pack, entity));
    }
    
    private class LaserZombieEntity extends TamerEntity{
        
        private final LivingGameEntity guardian;
        
        public LaserZombieEntity(@Nonnull ActiveTamerPack pack, @Nonnull Zombie entity) {
            super(pack, entity);
            
            entity.setAdult();
            final Location location = entity.getLocation();
            
            guardian = pack.player.spawnAlliedEntity(
                    location, Entities.GUARDIAN, self -> new LivingGameEntity(self) {
                        @Override
                        public void remove() {
                            entity.remove();
                            super.remove();
                        }
                    }
            );
            
            guardian.setInvulnerable(true);
            guardian.setAI(false);
        }
        
        @Override
        public void tick(int index) {
            super.tick(index);
            
            final Location eyeLocation = entity.getEyeLocation();
            eyeLocation.subtract(0.0d, 0.25d, 0.d);
            
            guardian.teleport(eyeLocation);
            
            final LivingGameEntity target = getTargetEntity();
            
            if (target != null && guardian.hasLineOfSight(target) && tick > 0 && tick % laserPeriod == 0) {
                guardian.setTarget(target);
                guardian.as(Guardian.class, self -> {
                    self.setLaser(true);
                    self.setLaserTicks(80 - laserHitDelay);
                });
                
                player.schedule(
                        () -> {
                            guardian.setTarget(null);
                            guardian.as(Guardian.class, self -> self.setLaser(false));
                            
                            target.getAttributes().addModifier(
                                    modifierSource,
                                    laserDefenseReductionDuration,
                                    player,
                                    modifier -> modifier.of(AttributeType.DEFENSE, ModifierType.ADDITIVE, scaleUltimateEffectiveness(player, laserBaseDefenseReduction))
                            );
                            
                            // Fx
                            guardian.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.25f);
                        }, laserHitDelay
                );
            }
        }
        
        @Override
        public void remove() {
            super.remove();
            guardian.remove();
        }
        
    }
}
