package me.hapyl.fight.game.heroes.bloodfield;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.effect.effects.BleedEffect;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.bloodfield.impel.ImpelInstance;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.bloodfiend.BloodfiendPassive;
import me.hapyl.fight.util.CFUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class BloodfiendData implements Ticking {

    private final GamePlayer player;
    private final Map<LivingGameEntity, BiteData> succulence;
    private final BleedEffect bleedEffect;

    private ImpelInstance impelInstance;
    private int blood;

    public BloodfiendData(GamePlayer player) {
        this.player = player;
        this.succulence = Maps.newConcurrentMap();
        this.bleedEffect = (BleedEffect) Effects.BLEED.getEffect();
    }

    @Nullable
    public ImpelInstance getImpelInstance() {
        return impelInstance;
    }

    @Nonnull
    public ImpelInstance newImpelInstance(Bloodfiend instance) {
        if (impelInstance != null) {
            impelInstance.stop();
        }

        return impelInstance = new ImpelInstance(instance, player, CFUtils.fetchKeySet(succulence, GamePlayer.class));
    }

    public void reset() {
        if (impelInstance != null) {
            impelInstance.stop();
            impelInstance = null;
        }

        succulence.forEach((player, tick) -> {
            stopSucculence(player);
        });

        succulence.clear();
        blood = 0;
    }

    @Nonnull
    public BiteData getBiteData(LivingGameEntity entity) {
        return succulence.computeIfAbsent(entity, fn -> new BiteData(this.player, entity));
    }

    public void addSucculence(LivingGameEntity entity) {
        final BloodfiendPassive succulence = TalentRegistry.SUCCULENCE;
        final BiteData biteData = getBiteData(entity);

        biteData.bite(succulence.biteDuration);
    }

    public void stopSucculence(LivingGameEntity player) {
        final BiteData biteDara = this.succulence.remove(player);

        if (biteDara != null) {
            biteDara.remove();
        }
    }

    @Override
    public void tick() {
        succulence.forEach((player, data) -> {
            data.tick--;

            if (data.tick <= 0 || player.isDeadOrRespawning()) {
                stopSucculence(player);
            }
            else {
                // Fx
                bleedEffect.spawnParticle(player.getLocation().add(0, 0.5, 0));
                bleedEffect.spawnParticle(this.player.getLocation().add(0, 0.5, 0));
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

    @Nonnull
    public Set<GamePlayer> getSucculencePlayers() {
        return CFUtils.fetchKeySet(succulence, GamePlayer.class);
    }

    public int getSuckedCount() {
        return succulence.size();
    }

    public void clearSucculence() {
        succulence.clear();
    }

    public boolean isBitten(GamePlayer gamePlayer) {
        return succulence.containsKey(gamePlayer);
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
