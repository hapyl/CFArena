package me.hapyl.fight.game.entity.cooldown;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class EntityCooldownHandler {

    private final LivingGameEntity entity;
    private final Map<EntityCooldown, CooldownData> cooldownMap;

    public EntityCooldownHandler(@Nonnull LivingGameEntity entity) {
        this.entity = entity;
        this.cooldownMap = Maps.newHashMap();
    }

    public LivingGameEntity getEntity() {
        return entity;
    }

    public void stopCooldowns() {
        cooldownMap.clear();
    }

    public void stopCooldown(@Nonnull EntityCooldown cooldown) {
        cooldownMap.remove(cooldown);
    }

    public boolean hasCooldown(@Nonnull EntityCooldown cooldown) {
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

    public void startCooldown(@Nonnull EntityCooldown cooldown) {
        startCooldown(cooldown, cooldown.durationInMilliseconds());
    }

    public void startCooldown(@Nonnull EntityCooldown cooldown, long durationMillis) {
        cooldownMap.put(cooldown, new CooldownData(cooldown, durationMillis));
    }

    @Nullable
    public CooldownData getData(@Nonnull EntityCooldown cooldown) {
        return cooldownMap.get(cooldown);
    }
}
