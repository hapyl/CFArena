package me.hapyl.fight.game.entity.event;

import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;

public final class EventType<E extends EntityEvent> {

    public static final EventType<EntityTeleportEvent> ENTITY_TELEPORT = new EventType<>(EntityTeleportEvent.class);
    public static final EventType<EntityTargetEvent> TARGET = new EventType<>(EntityTargetEvent.class);

    public final Class<E> event;

    private EventType(Class<E> event) {
        this.event = event;
    }
}
