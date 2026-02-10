package me.hapyl.fight.game.heroes.harbinger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.eterna.module.util.Vectors;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.harbinger.RiptidePassive;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class HarbingerData extends PlayerData implements Ticking {
    
    protected final Map<LivingGameEntity, Riptide> riptideMap;
    protected final LinkedList<HarbingerUltimate.ArrowTyphoon> typhoons;
    
    public StanceData stance;
    public long lastSlash;
    
    public HarbingerData(@Nonnull GamePlayer owner) {
        super(owner);
        
        this.riptideMap = Maps.newHashMap();
        this.typhoons = Lists.newLinkedList();
    }
    
    @Override
    public void remove() {
        riptideMap.clear();
        
        typhoons.forEach(HarbingerUltimate.ArrowTyphoon::cancel);
        typhoons.clear();
        
        if (stance != null) {
            stance.cancel();
            stance = null;
        }
    }
    
    @Override
    public void remove(@Nonnull LivingGameEntity entity) {
        riptideMap.remove(entity);
    }
    
    public void tick() {
        final Collection<Riptide> values = riptideMap.values();
        
        values.removeIf(Riptide::isExpired);
        values.forEach(Riptide::tick);
    }
    
    public void setRiptide(@Nonnull LivingGameEntity entity, int duration) {
        final Riptide riptide = riptideMap.computeIfAbsent(entity, e -> new Riptide(player, e));
        
        if (duration > riptide.remainingTicks) {
            riptide.remainingTicks = duration;
        }
        
        // Increment ultimate charge
        player.incrementEnergy(duration);
    }
    
    public void executeRiptideSlashIfPossible(@Nonnull LivingGameEntity entity) {
        if (!isValidForRiptideSlash(entity)) {
            return;
        }
        
        lastSlash = System.currentTimeMillis();
        
        // Mark cooldown
        riptideMap.computeIfAbsent(entity, e -> new Riptide(player, e)).lastHit = System.currentTimeMillis();
        final RiptidePassive passive = TalentRegistry.RIPTIDE;
        
        new GameTask() {
            private int hits;
            
            @Override
            public void run() {
                if (hits++ >= passive.numberOfHits || entity.isDeadOrRespawning()) {
                    cancel();
                    return;
                }
                
                entity.damageNoKnockback(passive.damage, player, DamageCause.RIPTIDE);
                entity.setVelocity(Vectors.random(passive.xzSpread, passive.ySpread));
                
                // Fx
                final Location location = entity.getEyeLocation();
                
                entity.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
                entity.playWorldSound(location, Sound.ITEM_BUCKET_FILL, 1.75f);
            }
        }.runTaskTimer(1, 2);
    }
    
    public boolean isOnCooldown(@Nonnull LivingGameEntity entity) {
        final Riptide riptide = riptideMap.get(entity);
        
        return riptide != null && riptide.isOnCooldown();
    }
    
    public boolean isAffected(@Nonnull LivingGameEntity entity) {
        return riptideMap.containsKey(entity);
    }
    
    private boolean isValidForRiptideSlash(@Nonnull LivingGameEntity entity) {
        return !entity.isDead() && isAffected(entity) && !isOnCooldown(entity);
    }
    
}
