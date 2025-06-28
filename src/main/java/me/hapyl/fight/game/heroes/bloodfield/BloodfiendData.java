package me.hapyl.fight.game.heroes.bloodfield;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.dot.DotType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.bloodfiend.BloodfiendPassive;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BloodfiendData extends PlayerData implements Ticking {
    
    protected final Bloodfiend bloodfiend;
    private final Map<LivingGameEntity, BiteData> succulence;
    
    private ImpelInstance impelInstance;
    private int blood;
    
    public BloodfiendData(@Nonnull Bloodfiend bloodfiend, @Nonnull GamePlayer player) {
        super(player);
        
        this.bloodfiend = bloodfiend;
        this.succulence = Maps.newConcurrentMap();
    }
    
    @Override
    public void remove() {
        if (impelInstance != null) {
            impelInstance.remove();
            impelInstance = null;
        }
        
        succulence.keySet().forEach(this::stopSucculence);
        succulence.clear();
        
        blood = 0;
    }
    
    @Nullable
    public ImpelInstance getImpelInstance() {
        return impelInstance;
    }
    
    @Nonnull
    public ImpelInstance newImpelInstance() {
        if (impelInstance != null) {
            impelInstance.remove();
        }
        
        return impelInstance = new ImpelInstance(
                this, player, succulence.keySet()
                                        .stream()
                                        .filter(GamePlayer.class::isInstance)
                                        .map(GamePlayer.class::cast)
                                        .collect(Collectors.toSet())
        );
    }
    
    @Nonnull
    public BiteData getBiteData(@Nonnull LivingGameEntity entity) {
        return succulence.computeIfAbsent(entity, fn -> new BiteData(this.player, entity));
    }
    
    public void addSucculence(@Nonnull LivingGameEntity entity) {
        final BloodfiendPassive succulence = TalentRegistry.SUCCULENCE;
        final BiteData biteData = getBiteData(entity);
        
        biteData.bite(succulence.biteDuration);
    }
    
    public void stopSucculence(@Nonnull LivingGameEntity entity) {
        final BiteData bite = this.succulence.remove(entity);
        
        if (bite != null) {
            bite.remove();
        }
    }
    
    @Override
    public void tick() {
        // Tick succulence
        succulence.forEach((player, data) -> {
            data.tick--;
            
            if (data.tick <= 0 || player.isDeadOrRespawning()) {
                stopSucculence(player);
            }
            else {
                // Fx
                DotType.BLEED.spawnParticle(player.getLocation().add(0, 0.5, 0));
                DotType.BLEED.spawnParticle(this.player.getLocation().add(0, 0.5, 0));
            }
        });
    }
    
    @Nonnull
    public Set<LivingGameEntity> getSuckedEntities() {
        return Sets.newHashSet(succulence.keySet());
    }
    
    public boolean isSuckedEntity(@Nonnull LivingGameEntity entity) {
        return succulence.containsKey(entity);
    }
    
    public int getSuckedCount() {
        return succulence.size();
    }
    
    public void clearBlood() {
        blood = 0;
    }
    
    public int getBlood() {
        return blood;
    }
    
    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }
}
