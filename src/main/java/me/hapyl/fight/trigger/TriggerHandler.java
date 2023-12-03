package me.hapyl.fight.trigger;

public interface TriggerHandler<T> {

    void handle(T t);

    default void unsubscribe(Subscribe<?> subscribe) {
        subscribe.getSubscribers().remove(this);
    }

}
