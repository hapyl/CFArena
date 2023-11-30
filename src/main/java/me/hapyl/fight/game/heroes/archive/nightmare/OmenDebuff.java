package me.hapyl.fight.game.heroes.archive.nightmare;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.Ticking;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.Map;

public class OmenDebuff implements Ticking {

    private final GamePlayer player;
    private final Map<LivingGameEntity, Integer> debuff;

    public OmenDebuff(GamePlayer player) {
        this.player = player;
        this.debuff = Maps.newConcurrentMap();
    }

    public void setOmen(LivingGameEntity entity, int tick) {
        // Don't set if the current value is greater, doesn't make any sense!
        if (debuff.getOrDefault(entity, 0) > tick) {
            return;
        }

        debuff.put(entity, tick);
        entity.addEffect(GameEffectType.PARANOIA, tick, true);
    }

    public boolean isAffected(LivingGameEntity entity) {
        return debuff.containsKey(entity);
    }

    public void clear() {
        debuff.clear();
    }

    @Override
    public void tick() {
        for (Map.Entry<LivingGameEntity, Integer> entry : debuff.entrySet()) {
            final LivingGameEntity entity = entry.getKey();
            final Integer tick = entry.getValue();

            if (entity == null || tick == null) {
                continue;
            }

            if (entity.isDeadOrRespawning()) {
                debuff.remove(entity);
                continue;
            }

            entry.setValue(tick - 1);

            if (entry.getValue() <= 0) {
                debuff.remove(entity);
            }

            // Fx
            final Location eyeLocation = entity.getEyeLocation();

            player.spawnParticle(eyeLocation, Particle.SPELL_WITCH, 1, 0.1d, 0.1d, 0.1d, 0.01f);
            player.spawnParticle(eyeLocation, Particle.SMOKE_LARGE, 2, 0.175d, 0.175d, 0.175d, 0.02f);
        }
    }

}
