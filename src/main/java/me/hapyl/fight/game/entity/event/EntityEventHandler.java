package me.hapyl.fight.game.entity.event;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.NamedGameEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;

import java.util.function.Consumer;

// Handles events for named entities
public final class EntityEventHandler implements Listener {

    @EventHandler()
    public void handleEntityTeleportEvent(EntityTeleportEvent ev) {
        forEachNamedEntity(entity -> {
            entity.callEvent(EventType.ENTITY_TELEPORT, ev);
        });
    }

    private void forEachNamedEntity(Consumer<NamedGameEntity<?>> consumer) {
        CF.getEntities().forEach(gameEntity -> {
            if (gameEntity instanceof NamedGameEntity<?> named) {
                consumer.accept(named);
            }
        });
    }

}
