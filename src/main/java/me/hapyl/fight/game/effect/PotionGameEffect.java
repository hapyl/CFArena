package me.hapyl.fight.game.effect;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.entity.GameEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.Map;

public abstract class PotionGameEffect extends GameEffect {

    private final Map<PotionEffectType, Integer> dataMap;

    public PotionGameEffect(String name) {
        super(name);
        this.dataMap = Maps.newHashMap();
    }

    public void setPotionEffect(@Nonnull PotionEffectType type, int amplifier) {
        dataMap.put(type, amplifier);
    }

    // called on start after all effects are applied
    public abstract void onStartAfter(@Nonnull LivingEntity entity);

    // called on stop after all effects are removed
    public abstract void onStopAfter(@Nonnull LivingEntity entity);

    @Override
    public final void onStart(GameEntity entity) {
        dataMap.forEach((type, level) -> {
            entity.addPotionEffect(type.createEffect(999999/* Pretty sure Integer.MAX_VALUE is too big. */, level));
        });

        onStartAfter(entity.getEntity());
    }

    @Override
    public final void onStop(GameEntity entity) {
        dataMap.forEach((type, level) -> {
            entity.removePotionEffect(type);
        });

        onStopAfter(entity.getEntity());
    }

}
