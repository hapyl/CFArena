package me.hapyl.fight.game.entity;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.entity.event.EntityEventListener;
import me.hapyl.fight.game.entity.event.EventType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.spigotutils.module.hologram.GlobalHologram;
import me.hapyl.spigotutils.module.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.EntityEquipment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Named game entities are entities with a health bar above their head.
 * They're used for bosses and custom mobs.
 */
public class NamedGameEntity<T extends LivingEntity> extends LivingGameEntity {

    protected final GameEntityType<T> type;
    protected final T entity;
    private final Map<EventType<?>, EntityEventListener<?>> eventHandles;
    private final Hologram aboveHead;
    private final GameTask task;
    protected int tick;

    public NamedGameEntity(GameEntityType<T> type, T entity) {
        super(entity, type.getAttributes());

        this.eventHandles = Maps.newHashMap();
        this.entity = entity;
        this.type = type;
        this.aboveHead = new GlobalHologram();

        // Schedule tick
        task = new TickingGameTask() {
            @Override
            public void run(int tick) {
                NamedGameEntity.this.tick = tick;
                NamedGameEntity.this.tick();
            }
        };

        task.runTaskTimer(1, 1);

        this.aboveHead.create(getHologramLocation());
        this.aboveHead.showAll();

        onSpawn();
    }

    @Event
    public void onSpawn() {
    }

    @Event
    public void onTick() {
    }

    @Event
    public void onTick10() {
    }

    @Event
    public void onTick20() {
    }

    public final void tick() {
        if (entity.isDead()) {
            remove();
            return;
        }

        onTick();

        if (tick % 10 == 0) {
            onTick10();
        }

        if (tick % 20 == 0) {
            onTick20();
        }

        // Update above head hologram
        final String[] extraLines = getExtraHologramLines();
        aboveHead.clear();

        if (extraLines != null) {
            aboveHead.addLines(extraLines);
            //aboveHead.addLine("");
        }

        aboveHead.addLines(
                getName() + " " + getHealthFormatted()
        );

        aboveHead.updateLines();
        aboveHead.teleport(getHologramLocation());
    }

    public Location getHologramLocation() {
        return getLocation().add(0, entity.getEyeHeight() + type.getHologramOffset(), 0);
    }

    @Nullable
    public String[] getExtraHologramLines() {
        return null;
    }

    @Override
    public String getHealthFormatted() {
        return type.getType().formatHealth(this) + " &c❤";
    }

    @Nonnull
    public EntityEquipment getEquipment() {
        final EntityEquipment equipment = entity.getEquipment();

        if (equipment == null) {
            throw new IllegalArgumentException(this + " does not have equipment");
        }

        return equipment;
    }

    @Nonnull
    @Override
    public String getName() {
        return type.getNameFormatted();
    }

    @Override
    public void remove() {
        super.remove();

        task.cancel();
        aboveHead.destroy();
    }

    @Override
    public String toString() {
        return "NamedGameEntity{" + uuid + "}";
    }

    public int getTick() {
        return tick;
    }

    public void schedule(Runnable run, int delay) {
        GameTask.runLater(run, delay);
    }

    /**
     * Allows listening to event for this specific entity.
     *
     * @param type    - Event type.
     * @param handler - Handler for the event.
     */
    protected final <E extends EntityEvent> void listenTo(@Nonnull EventType<E> type, @Nonnull EntityEventListener<E> handler) {
        eventHandles.put(type, handler);
    }

    @SuppressWarnings("unchecked")
    public final <E extends EntityEvent> void callEvent(@Nonnull EventType<E> type, @Nonnull E event) {
        final EntityEventListener<E> listener = (EntityEventListener<E>) eventHandles.get(type);

        if (listener != null) {
            listener.handle(event);
        }
    }
}
