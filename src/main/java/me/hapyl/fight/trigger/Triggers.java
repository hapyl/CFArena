package me.hapyl.fight.trigger;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.trigger.subscribe.AbilityCooldownStartTrigger;
import me.hapyl.fight.trigger.subscribe.AttributeChangeTrigger;
import me.hapyl.fight.trigger.subscribe.GameChangeStateTrigger;

import javax.annotation.Nonnull;
import java.util.Map;

public final class Triggers {

    private static final Map<Class<?>, Subscribe<?>> HANDLERS = Maps.newHashMap();

    // subscribes
    public static final Subscribe<AttributeChangeTrigger> ATTRIBUTE_CHANGE = register(AttributeChangeTrigger.class);
    public static final Subscribe<GameChangeStateTrigger> GAME_CHANGE_STATE = register(GameChangeStateTrigger.class);
    public static final Subscribe<AbilityCooldownStartTrigger> ABILITY_COOLDOWN_START = register(AbilityCooldownStartTrigger.class);

    @SuppressWarnings("unchecked")
    public static <T extends Trigger> void call(@Nonnull T trigger) {
        final Subscribe<T> subscribe = (Subscribe<T>) HANDLERS.get(trigger.getClass());

        if (subscribe == null) {
            Debug.warn("Tried to call non-registered trigger " + trigger);
            return;
        }

        subscribe.getSubscribers().forEach(handler -> {
            handler.handle(trigger);
        });
    }

    private static <T extends Trigger> Subscribe<T> register(Class<T> clazz) {
        final Subscribe<T> subscribe = new Subscribe<>();

        HANDLERS.put(clazz, subscribe);
        return subscribe;
    }

}
