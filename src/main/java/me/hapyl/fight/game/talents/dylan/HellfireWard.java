package me.hapyl.fight.game.talents.dylan;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Callback;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.Shield;
import me.hapyl.fight.game.entity.cooldown.EntityCooldown;
import me.hapyl.fight.game.heroes.dylan.Dylan;
import me.hapyl.fight.game.heroes.dylan.DylanFamiliar;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class HellfireWard extends Talent {
    
    @DisplayField(percentage = true) private final double shieldStrength = 0.6;
    @DisplayField(percentage = true) private final float shieldCapacity = 0.6f;
    
    @DisplayField(percentage = true) private final double scorchingRingDamage = 0.3;
    @DisplayField private final int scorchingRingCooldown = 30;
    
    @DisplayField private final double maxLookupDistance = 20;
    
    private final EntityCooldown cooldown = EntityCooldown.of("hellfire_ward", scorchingRingCooldown * 50L);
    
    public HellfireWard(@Nonnull Key key) {
        super(key, "Hellfire Ward");
        
        setDescription("""
                       Cast a protective ward on a &ateammate &8(excluding &3%s&8)&7, applying &eHellfire Shield&7 for up to &b{duration}&7.
                       
                       &6Hellfire Shield
                        &8•&7 Absorbs &e{shieldStrength}&7 of the damage taken.
                        &8•&7 Triggers a &eScorching Ring&7 on damage, dealing &4{scorchingRingDamage}&7 of the &cdamage&7 taken to nearby &cenemies&7 and sets them on &efire&7.
                       """.formatted(Dylan.familiarName));
        
        setType(TalentType.DEFENSE);
        setMaterial(Material.HONEYCOMB);
        
        setDurationSec(12.5f);
        setCooldownSec(22);
    }
    
    @Nullable
    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final LivingGameEntity target = Collect.targetEntityRayCast(
                player, maxLookupDistance, 1.25, entity -> !(entity instanceof DylanFamiliar.FamiliarEntity) && player.isTeammate(entity)
        );
        
        if (target == null) {
            return Response.error("Not targeting a teammate!");
        }
        
        applyShield(player, target);
        return Response.OK;
    }
    
    public void applyShield(@Nonnull GamePlayer player, @Nonnull LivingGameEntity target) {
        target.setShield(new HellfireShield(
                player,
                target, target.getMaxHealth() * shieldCapacity, callback -> callback
                .duration(duration)
                .strength(shieldCapacity)
        ));
    }
    
    public class HellfireShield extends Shield {
        
        private static final DisplayData model = BDEngine.parse(
                "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:fire\",Properties:{east:\"false\",north:\"false\",south:\"false\",up:\"false\",west:\"true\"}},transformation:[0f,0f,0.5f,-0.25f,0f,0.5f,0f,0f,-1f,0f,0f,0f,0f,0f,0f,1f]}]}"
        );
        
        private static final double defaultRadius = 0.8;
        private static final int damageDuration = 10;
        
        private final GamePlayer player;
        private final List<DisplayEntity> models;
        
        private double radius;
        
        HellfireShield(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double maxCapacity, @Nonnull Callback<Builder> callback) {
            super(entity, maxCapacity, callback);
            
            this.player = player;
            this.models = createModels(3);
            this.radius = defaultRadius;
        }
        
        @Override
        public void onHit(double amount, @Nullable LivingGameEntity damager) {
            super.onHit(amount, damager);
            
            // Create a ring
            if (entity.hasCooldown(cooldown)) {
                return;
            }
            
            entity.startCooldown(cooldown);
            
            final double damage = amount * scorchingRingDamage;
            final Location location = entity.getMidpointLocation();
            
            // Affect
            new TickingGameTask() {
                private final Set<LivingGameEntity> tookDamage = Sets.newHashSet();
                
                @Override
                public void run(int tick) {
                    if (tick > damageDuration) {
                        radius = defaultRadius;
                        cancel();
                        return;
                    }
                    
                    // Calculate new radius
                    radius = defaultRadius + Math.sin(Math.PI * tick / damageDuration);
                    
                    // Go full circle
                    for (double d = 0; d < Math.PI * 2; d += Math.PI * 0.2 / radius * 0.7) {
                        final double x = Math.sin(d) * radius;
                        final double z = Math.cos(d) * radius;
                        
                        LocationHelper.offset(
                                location, x, 0, z, () -> {
                                    Collect.nearbyEntities(location, 0.5, entity -> !tookDamage.contains(entity) && HellfireShield.this.entity.isNotSelfOrTeammate(entity))
                                           .forEach(entity -> {
                                               entity.damageNoKnockback(damage, entity, DamageCause.SCORCHING_RING);
                                               entity.getEntityData().addAssistingPlayer(player);
                                               
                                               tookDamage.add(entity);
                                           });
                                    
                                    // temp fx
                                    entity.spawnWorldParticle(location, Particle.FLAME, 1);
                                }
                        );
                    }
                }
            }.runTaskTimer(0, 1);
        }
        
        @Override
        public void onRemove(@Nonnull Cause cause) {
            models.forEach(Entity::remove);
        }
        
        @Override
        public void tick() {
            super.tick();
            
            // Fx
            final double rad = Math.toRadians(duration * 6);
            final double offset = Math.PI * 2 / models.size();
            
            final Location location = entity.getLocation();
            
            for (int i = 0; i < models.size(); i++) {
                final double x = Math.sin(rad + offset * i) * radius;
                final double y = Math.sin(rad * i) * 0.1;
                final double z = Math.cos(rad + offset * i) * radius;
                
                final DisplayEntity display = models.get(i);
                
                LocationHelper.offset(
                        location, x, y + 1, z, () -> {
                            display.teleport(location);
                        }
                );
            }
        }
        
        private List<DisplayEntity> createModels(int amount) {
            final Location location = player.getLocation();
            final List<DisplayEntity> entities = Lists.newArrayList();
            
            for (int i = 0; i < amount; i++) {
                entities.add(model.spawn(
                        location, self -> {
                            self.setTeleportDuration(1);
                            self.setBillboard(Display.Billboard.CENTER);
                        }
                ));
            }
            
            return entities;
        }
    }
}
