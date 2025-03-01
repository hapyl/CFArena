package me.hapyl.fight.game.entity.named;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.hologram.GlobalHologram;
import me.hapyl.eterna.module.hologram.Hologram;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Named game entities are entities with a health bar above their head.
 * They're used for bosses and named mobs.
 */
public class NamedGameEntity<T extends LivingEntity> extends LivingGameEntity {

    protected final NamedEntityType type;
    protected final T entity;
    private final Hologram aboveHead;
    protected int tick;

    public NamedGameEntity(NamedEntityType type, T entity) {
        super(entity, type.getAttributes());

        this.entity = entity;
        this.type = type;
        this.aboveHead = new GlobalHologram();

        // Schedule tick
        this.aboveHead.create(getHologramLocation());
        this.aboveHead.showAll();

        onSpawn();
    }

    @EventLike
    public void onSpawn() {
    }

    @EventLike
    public void onTick(int tick) {
    }

    public final void tick() {
        super.tick();

        if (entity.isDead()) {
            kill();
            return;
        }

        onTick(tick);

        // Update above head hologram
        updateHologram();
    }

    private void updateHologram() {
        final String[] extraLines = getExtraHologramLines();
        aboveHead.clear();

        if (extraLines != null) {
            aboveHead.addLines(extraLines);
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
    public @Nonnull String getHealthFormatted() {
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
    @OverridingMethodsMustInvokeSuper
    public void kill() {
        super.kill();
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

}
