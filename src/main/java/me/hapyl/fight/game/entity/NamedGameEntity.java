package me.hapyl.fight.game.entity;

import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.spigotutils.module.hologram.GlobalHologram;
import me.hapyl.spigotutils.module.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Named game entities are entities with a health bar above their head.
 * They're used for bosses and custom mobs.
 */
public class NamedGameEntity<T extends LivingEntity> extends LivingGameEntity {

    protected final GameEntityType<T> type;
    private final Hologram aboveHead;
    private final GameTask task;
    protected int tick;
    protected final T entity;

    public NamedGameEntity(GameEntityType<T> type, T entity) {
        super(entity, type.getAttributes());

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

    public final void tick() {
        if (entity.isDead()) {
            remove();
            return;
        }

        onTick();

        // Update above head hologram
        final String[] extraLines = getExtraHologramLines();
        aboveHead.clear();

        if (extraLines != null) {
            aboveHead.addLines(extraLines);
            aboveHead.addLine("");
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
}
