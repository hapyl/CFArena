package me.hapyl.fight.game.entity.event;

import org.bukkit.event.entity.EntityEvent;

import javax.annotation.Nonnull;

public interface EntityEventListener<T extends EntityEvent> {

    void handle(@Nonnull T event);

}
