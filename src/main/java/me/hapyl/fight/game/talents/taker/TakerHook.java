package me.hapyl.fight.game.talents.taker;

import com.google.common.collect.Lists;
import io.papermc.paper.math.Rotation;
import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;

public class TakerHook implements Removable {
    
    private static final DisplayData display = BDEngine.parse(
            "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:chain\",Properties:{axis:\"x\"}},transformation:[0f,0f,1f,-0.5f,0f,1f,0f,-0.375f,-1f,0f,0f,0.5f,0f,0f,0f,1f]}]}"
    );
    
    private static final ModifierSource modifierSource = new ModifierSource(Key.ofString("taker_hook"), true);
    
    private final DeathSwap talent;
    private final GamePlayer player;
    private final LinkedList<Display> chains;
    
    private GameTask taskExtend;
    private GameTask taskContract;
    
    @Nullable private LivingGameEntity target;
    
    public TakerHook(@Nonnull DeathSwap talent, @Nonnull GamePlayer player) {
        this.talent = talent;
        this.player = player;
        this.chains = Lists.newLinkedList();
        
        extend(player.getMidpointLocation());
    }
    
    @Override
    public void remove() {
        chains.forEach(Display::remove);
        chains.clear();
        
        if (taskExtend != null) {
            taskExtend.cancel();
        }
        
        if (taskContract != null) {
            taskContract.cancel();
        }
    }
    
    protected void breakChains() {
        final Location anchorLocation = getAnchorLocation();
        
        chains.forEach(display -> {
            final Location location = display.getLocation();
            final Vector vector = anchorLocation.toVector().subtract(location.toVector()).normalize();
            
            // Fx
            player.playWorldSound(location, Sound.BLOCK_CHAIN_BREAK, 0.5f);
            player.spawnWorldParticle(location, Particle.SMOKE, 0, vector.getX(), vector.getY(), vector.getZ(), 1.0f);
        });
        
        remove();
    }
    
    private void extend(Location location) {
        final Rotation rotation = Rotation.rotation(location.getYaw(), location.getPitch());
        final Vector vector = location.getDirection().normalize();
        
        this.taskExtend = new TickingGameTask() {
            private double distance;
            private boolean shouldContract;
            
            @Override
            public void run(int tick) {
                final double nextX = vector.getX() * distance;
                final double nextY = vector.getY() * distance;
                final double nextZ = vector.getZ() * distance;
                
                if (distance > talent.maxDistance || shouldContract) {
                    contract(location.add(nextX, nextY, nextZ));
                    cancel();
                    return;
                }
                
                LocationHelper.offset(
                        location, nextX, nextY, nextZ, () -> {
                            // Block collision detection
                            if (!location.getBlock().isPassable()) {
                                shouldContract = true;
                            }
                            
                            // Entity collision detection
                            final LivingGameEntity closest = Collect.nearestEntity(location, 1.5, player::isNotSelfOrTeammateOrHasEffectResistance);
                            
                            if (closest != null) {
                                target = closest;
                                shouldContract = true;
                                
                                // Affect entity
                                final double damage = target.getHealth() * talent.damagePercent;
                                
                                target.damage(damage, player, DamageCause.TAKER_HOOK);
                                target.triggerDebuff(player);
                                target.getAttributes().addModifier(
                                        modifierSource, talent.impairDuration, player, modifier -> modifier.of(AttributeType.SPEED, ModifierType.FLAT, talent.speedReduction)
                                );
                                
                                target.addEffect(EffectType.WITHER, talent.impairDuration);
                                
                                target.sendMessage("&4☠ &cOuch! %s hooked you, and you lost &e%.0f%%&c of your health!".formatted(
                                        player.getName(),
                                        talent.damagePercent * 100
                                ));
                                target.playSound(Sound.ENTITY_WITHER_HURT, 1.25f);
                                
                                // Reduce cooldown
                                talent.reduceCooldown(player);
                            }
                            
                            // Draw from current location to end location
                            drawChains(location, rotation);
                        }
                );
                
                distance += talent.step;
            }
        }.runTaskTimer(0, 1);
    }
    
    private void contract(Location location) {
        this.taskContract = new TickingGameTask() {
            @Override
            public void run(int tick) {
                final Location playerLocation = getAnchorLocation();
                final Vector direction = playerLocation.toVector().subtract(location.toVector()).normalize().multiply(talent.step);
                
                location.add(direction);
                
                if (location.distanceSquared(playerLocation) < 1) {
                    // This will "double cancel" the first task, but I don't care
                    TakerHook.this.remove();
                    return;
                }
                
                drawChains(location, Rotation.rotation(location.getYaw(), location.getPitch()));
                
                // Also sync entity
                if (target != null) {
                    final Location teleportLocation = BukkitUtils.newLocation(location);
                    teleportLocation.setYaw(location.getYaw() + 180);
                    
                    target.teleport(teleportLocation);
                }
            }
        }.runTaskTimer(0, 1);
    }
    
    private void drawChains(Location to, Rotation rotation) {
        final Location location = getAnchorLocation();
        location.setRotation(rotation);
        
        final double distance = to.distance(location);
        
        final double currentX = location.getX();
        final double currentY = location.getY();
        final double currentZ = location.getZ();
        
        final int chainSize = chains.size();
        final int difference = (int) (distance - chainSize);
        
        // If there are fewer chains than distance, create them
        if (difference > 0) {
            for (int i = 0; i < difference; i++) {
                chains.add(createChain(to));
            }
        }
        // Else remove last chains
        else {
            for (int i = 0; difference < i; i--) {
                final Display last = chains.pollLast();
                
                if (last != null) {
                    last.remove();
                }
            }
        }
        
        for (double d = 0.1; d < distance; d += 1) {
            final double progress = d / distance;
            
            final double x = currentX + (to.getX() - currentX) * progress;
            final double y = currentY + (to.getY() - currentY) * progress;
            final double z = currentZ + (to.getZ() - currentZ) * progress;
            
            location.set(x, y, z);
            
            // Sync chains
            int index = (int) d;
            final Display chain = chainAt(index);
            
            if (chain != null) {
                chain.teleport(location);
            }
        }
        
        // Fx
        player.playWorldSound(location, Sound.BLOCK_CHAIN_BREAK, 1.0f);
    }
    
    @Nullable
    private Display chainAt(int index) {
        return index < 0 || index >= chains.size() ? null : chains.get(index);
    }
    
    private Location getAnchorLocation() {
        final Location location = player.getLocation().add(0, 0.5, 0);
        
        return location.add(location.getDirection().setY(0).multiply(1.0));
    }
    
    private Display createChain(Location location) {
        return display.spawnInterpolated(location);
    }
    
}
