package me.hapyl.fight.game.entity.cooldown;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class EntityCooldown {

    private final LivingGameEntity entity;
    private final Map<Cooldown, CooldownData> cooldownMap;

    public EntityCooldown(@Nonnull LivingGameEntity entity) {
        this.entity = entity;
        this.cooldownMap = Maps.newHashMap();
    }

    public LivingGameEntity getEntity() {
        return entity;
    }

    public void stopCooldowns() {
        cooldownMap.clear();
    }

    public void stopCooldown(@Nonnull Cooldown cooldown) {
        cooldownMap.remove(cooldown);
    }

    public boolean hasCooldown(@Nonnull Cooldown cooldown) {
        final CooldownData data = cooldownMap.get(cooldown);

        if (data == null) {
            return false;
        }

        if (data.isFinished()) {
            cooldownMap.remove(cooldown);
            return false;
        }

        return true;
    }

    public void startCooldown(@Nonnull Cooldown cooldown) {
        startCooldown(cooldown, cooldown.duration);
    }

    public void startCooldown(@Nonnull Cooldown cooldown, long durationMillis) {
        cooldownMap.put(cooldown, new CooldownData(cooldown, durationMillis));
    }

    @Nullable
    public CooldownData getData(@Nonnull Cooldown cooldown) {
        return cooldownMap.get(cooldown);
    }
}
