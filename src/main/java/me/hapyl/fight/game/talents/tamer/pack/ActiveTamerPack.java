package me.hapyl.fight.game.talents.tamer.pack;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.eterna.module.util.Vectors;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a pack of entities that are tamed by a player.
 */
public class ActiveTamerPack implements Ticking, Removable {
    
    private static final double[][] RELATIVE_OFFSETS = Vectors.RELATIVE;
    
    protected final GamePlayer player;
    
    private final TamerPack pack;
    private final Set<TamerEntity> entities;
    private int duration;
    
    public ActiveTamerPack(TamerPack pack, GamePlayer player) {
        this.player = player;
        this.pack = pack;
        this.entities = Sets.newConcurrentHashSet();
        this.duration = pack.getDuration(player);
    }
    
    public int getDuration() {
        return duration;
    }
    
    public boolean isOver() {
        return duration <= 0 || entities.isEmpty();
    }
    
    public boolean isAlive() {
        return false;
    }
    
    public Set<TamerEntity> getEntities() {
        return entities;
    }
    
    @Nonnull
    public String getName() {
        return pack.getName();
    }
    
    public void spawn() {
        // Already spawned
        if (!entities.isEmpty()) {
            return;
        }
        
        final Location location = player.getLocation();
        
        for (int i = 0; i < pack.spawnAmount(); i++) {
            relative(location, relative -> pack.onSpawn(this, relative), i);
        }
    }
    
    public final void remove() {
        entities.forEach(GameEntity::remove);
        entities.clear();
    }
    
    public final void remove(LivingGameEntity entity) {
        if (!(entity instanceof TamerEntity tamerEntity)) {
            return;
        }
        
        entities.remove(tamerEntity);
        
        if (!entity.isDead()) {
            entity.remove();
        }
    }
    
    public final void recall() {
        entities.forEach(entity -> {
            final Location location = entity.getEyeLocation();
            
            // Fx
            entity.spawnWorldParticle(location, Particle.POOF, 15, 0.25d, 0.5d, 0.25d, 0.02f);
            entity.playWorldSound(location, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 0.2f);
            
            // Actually remove the entity
            entity.remove();
        });
        
        entities.clear();
    }
    
    @Override
    public final void tick() {
        duration--;
        
        entities.removeIf(TamerEntity::isDead);
        
        int index = 0;
        for (TamerEntity entity : entities) {
            entity.tick(index++);
        }
    }
    
    @Nonnull
    public TamerPack getPack() {
        return pack;
    }
    
    @Nullable
    public <T extends TamerEntity> T getFirstEntityOfType(@Nonnull Class<T> clazz) {
        for (TamerEntity entity : entities) {
            if (clazz.isInstance(entity)) {
                return clazz.cast(entity);
            }
        }
        
        return null;
    }
    
    @Nullable
    public TamerEntity getFirstEntity() {
        for (TamerEntity entity : entities) {
            return entity;
        }
        
        return null;
    }
    
    @Nonnull
    protected <T extends LivingEntity, E extends TamerEntity> E createEntity(Location location, Entities<T> type, Function<T, E> function) {
        final E entity = player.spawnAlliedEntity(location, type, function::apply);
        entities.add(entity);
        return entity;
    }
    
    private void relative(Location location, Consumer<Location> consumer, int index) {
        final double[] offsets = RELATIVE_OFFSETS[index % RELATIVE_OFFSETS.length];
        
        final double x = offsets[0];
        final double z = offsets[1];
        
        location.add(x, 0, z);
        consumer.accept(location);
        location.subtract(x, 0, z);
    }
}

