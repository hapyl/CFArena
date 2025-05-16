package me.hapyl.fight.game.heroes.vortex;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.vortex.*;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Vortex extends Hero implements UIComplexComponent, PlayerDataHandler<VortexData> {
    
    @DisplayField private final double damagePerDreamStack = 0.15d;
    @DisplayField private final double sotsDamage = 15.0d;
    
    private final PlayerDataMap<VortexData> playerData = PlayerMap.newDataMap(VortexData::new);
    
    private final int blinkIterations = 3;
    private final double blinkStep = 0.75d;
    
    public Vortex(@Nonnull Key key) {
        super(key, "Vortex");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.STRATEGY, Archetype.TALENT_DAMAGE, Archetype.MELEE, Archetype.SELF_SUSTAIN);
        profile.setGender(Gender.MALE);
        
        setDescription("""
                       A young boy with the power of speaking to stars...
                       """);
        setItem("2adc458dfabc20b8d587b0476280da2fb325fc616a5212784466a78b85fb7e4d");
        
        final HeroAttributes attributes = getAttributes();
        attributes.setMaxHealth(120);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(102, 51, 0);
        equipment.setLeggings(179, 89, 0);
        equipment.setBoots(255, 140, 26);
        
        setWeapon(new VortexWeapon());
        setUltimate(new VortexUltimate());
    }
    
    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new GameTask() {
            @Override
            public void run() {
                playerData.values().forEach(VortexData::tick);
            }
        }.runTaskTimer(0, 1);
    }
    
    @Override
    public VortexStarTalent getFirstTalent() {
        return TalentRegistry.VORTEX_STAR;
    }
    
    @Override
    public StarAligner getSecondTalent() {
        return TalentRegistry.STAR_ALIGNER;
    }
    
    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.LIKE_A_DREAM;
    }
    
    @Nullable
    @Override
    public List<String> getStrings(@Nonnull GamePlayer player) {
        final int starAmount = getFirstTalent().getStarAmount(player);
        final VortexData data = getPlayerData(player);
        final int dreamStacks = data.dreams;
        
        return List.of("&6⭐ &l" + starAmount, dreamStacks == 0 ? "" : "&e⚡ &l" + dreamStacks);
    }
    
    public void restoreHealth(@Nonnull GamePlayer player, @Nonnull AstralStar star) {
        if (star.isDead()) {
            return;
        }
        
        final VortexStarTalent talent = getFirstTalent();
        final double health = star.getHealth();
        
        // Return sacrificed health
        final EntityAttributes attributes = player.getAttributes();
        attributes.add(AttributeType.MAX_HEALTH, star.getMaxHealth());
        
        // Only heal for whatever the star's health is
        player.heal(health);
    }
    
    public double calculateAstralDamage(@Nonnull GamePlayer player, double damage) {
        final int stack = getPlayerData(player).dreams;
        
        return damage * (damagePerDreamStack * stack + 1);
    }
    
    public void performStarBlink(@Nonnull GamePlayer player, @Nonnull AstralStar targetStar) {
        // Calculate damage
        final VortexStarTalent talent = targetStar.getTalent();
        final double damage = calculateAstralDamage(player, sotsDamage);
        
        targetStar.setState(StarState.LINKING);
        
        final AstralStarList stars = talent.getStars(player);
        final Location location = player.getLocation();
        final Location starLocation = targetStar.getLocation();
        
        final double distance = location.distance(starLocation);
        final Vector vector = starLocation.toVector().subtract(location.toVector()).normalize().multiply(blinkStep);
        
        player.setGameMode(GameMode.SPECTATOR);
        
        new AstralTask(player, targetStar) {
            
            private double distanceTravelled = 0.0d;
            
            @Override
            public void onTaskStop() {
                player.setGameMode(GameMode.SURVIVAL);
            }
            
            @Override
            public void run(@Nonnull GamePlayer player, @Nonnull AstralStar star, int tick) {
                for (int i = 0; i < blinkIterations; i++) {
                    if (next()) {
                        return;
                    }
                }
            }
            
            private boolean next() {
                if (distanceTravelled >= distance) {
                    restoreHealth(player, targetStar);
                    stars.removeStar(targetStar);
                    
                    cancel();
                    return true;
                }
                
                // Travel
                location.add(vector);
                player.teleport(location);
                
                // Damage
                Collect.nearbyEntities(location, 1.5d).forEach(entity -> {
                    if (player.isSelfOrTeammate(entity)) {
                        return;
                    }
                    
                    entity.damageNoKnockback(damage, player, DamageCause.STAR_SLASH);
                });
                
                // Fx
                player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
                player.spawnWorldParticle(location, Particle.CRIT, 1, 0.25d, 0.25d, 0.25d, 0.05f);
                player.spawnWorldParticle(location, Particle.EFFECT, 3, 0.25d, 0.25d, 0.25d, 0.02f);
                
                player.playWorldSound(location, Sound.ITEM_FLINTANDSTEEL_USE, 0.75f);
                
                distanceTravelled += blinkStep;
                return false;
            }
        };
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<VortexData> getDataMap() {
        return playerData;
    }
    
    private class VortexUltimate extends UltimateTalent {
        
        @DisplayField private final double ultimateBaseDamage = 1d;
        @DisplayField private final double ultimateSpeed = 0.3d;
        @DisplayField private final int damagePeriod = 2;
        @DisplayField(scale = 500) private final double ultimateSpeedStuck = 0.05d;
        @DisplayField(percentage = true) private final double knockback = 0.3d;
        
        public VortexUltimate() {
            super(Vortex.this, "Arcana", 50);
            
            setDescription("""
                           Launch an &6Astral&7 energy in front of you that &nfollows&7 your crosshair and &brapidly&7 deals &cdamage&7 in small &cAoE&7.
                           """
            );
            
            setMaterial(Material.QUARTZ);
            setDurationSec(5);
            setCooldownSec(30);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            final double damage = calculateAstralDamage(player, ultimateBaseDamage);
            final Location location = player.getLocationInFrontFromEyes(0.75f);
            
            return builder()
                    .onExecute(() -> {
                    
                    })
                    .onTick(tick -> {
                        boolean isHit = false;
                        
                        if (tick % damagePeriod == 0) {
                            for (LivingGameEntity entity : Collect.nearbyEntities(location, 1.5d)) {
                                if (player.isSelfOrTeammate(entity)) {
                                    continue;
                                }
                                
                                isHit = true;
                                
                                entity.modifyKnockback(
                                        1 - knockback, self -> {
                                            self.damage(damage, player, DamageCause.SOTS);
                                        }
                                );
                            }
                        }
                        
                        final Vector vector = player.getEyeLocation().getDirection();
                        final Vector nextVector = vector.multiply(isHit ? ultimateSpeedStuck : ultimateSpeed);
                        
                        location.add(nextVector);
                        
                        if (!location.getBlock().isPassable()) {
                            location.subtract(nextVector);
                        }
                        
                        // Fx
                        player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0.0f);
                        
                        if (tick % 5 == 0) {
                            player.playWorldSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.25f);
                        }
                    });
        }
        
    }
}
