package me.hapyl.fight.trigger;

import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import java.util.Set;

// this class holds all the subscribers for this trigger
public class Subscribe<T extends Trigger> {

    private final Set<TriggerHandler<T>> subscribers;

    protected Subscribe() {
        this.subscribers = Sets.newHashSet();
    }

    @Nonnull
    public Set<TriggerHandler<T>> getSubscribers() {
        return subscribers;
    }

    public TriggerHandler<T> subscribe(@Nonnull TriggerHandler<T> handler) {
        subscribers.add(handler);
        return handler;
    }

}
