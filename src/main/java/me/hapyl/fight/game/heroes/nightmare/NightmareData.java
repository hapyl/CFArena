package me.hapyl.fight.game.heroes.nightmare;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.PlayerData;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;

public class NightmareData extends PlayerData implements Ticking {
    
    private final Map<LivingGameEntity, Integer> debuffTicks;
    
    public NightmareData(@Nonnull GamePlayer player) {
        super(player);
        
        this.debuffTicks = Maps.newHashMap();
    }
    
    @Override
    public void remove() {
        debuffTicks.clear();
    }
    
    @Override
    public void tick() {
        final Iterator<Map.Entry<LivingGameEntity, Integer>> iterator = debuffTicks.entrySet().iterator();
        
        while (iterator.hasNext()) {
            final Map.Entry<LivingGameEntity, Integer> entry = iterator.next();
            final LivingGameEntity entity = entry.getKey();
            final int newValue = entry.getValue() - 1;
            
            entry.setValue(newValue);
            
            if (entity.isDeadOrRespawning() || newValue <= 0) {
                iterator.remove();
                continue;
            }
            
            // Only show the fx for nightmare
            final Location eyeLocation = entity.getEyeLocation().add(0, 0.65, 0);
            
            player.spawnParticle(eyeLocation, Particle.WITCH, 1, 0.1d, 0.1d, 0.1d, 0.01f);
            player.spawnParticle(eyeLocation, Particle.LARGE_SMOKE, 2, 0.175d, 0.175d, 0.175d, 0.02f);
        }
    }
    
    public void affect(@Nonnull LivingGameEntity entity, int duration) {
        debuffTicks.put(entity, duration);
    }
    
    public boolean isAffected(@Nonnull LivingGameEntity entity) {
        return debuffTicks.containsKey(entity);
    }
}
