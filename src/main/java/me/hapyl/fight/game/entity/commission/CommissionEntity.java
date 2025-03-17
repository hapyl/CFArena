package me.hapyl.fight.game.entity.commission;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.fight.game.commission.Commission;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Named game entities are entities with a health bar above their head.
 * They're used for bosses and named mobs.
 */
public class CommissionEntity extends LivingGameEntity {

    protected final CommissionEntityType type;

    protected int tick;
    protected int level;

    public CommissionEntity(@Nonnull CommissionEntityType type, @Nonnull LivingEntity entity) {
        super(entity, type.getAttributes());

        this.type = type;

        // Set default equipment if not null
        if (type.equipment != null) {
            equipment(type.equipment);
        }

        // Changing the level fixes the entity health, etc.
        level(1);
        onSpawn();
    }

    @Nonnull
    public LivingEntity entity() {
        return entity;
    }

    @Nonnull
    public CommissionEntityType type() {
        return type;
    }

    @EventLike
    public void onSpawn() {
    }

    @EventLike
    public void onTick(int tick) {
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public final void tick() {
        super.tick();

        if (entity.isDead()) {
            this.remove();
            return;
        }

        onTick(tick++);

        // Update above head hologram
        updateHologram();
    }

    public boolean isBossOrMiniboss() {
        return type.getType() == EntityType.BOSS || type.getType() == EntityType.MINIBOSS;
    }

    public int level() {
        return level;
    }

    public void level(int level) {
        this.level = Math.clamp(level, 1, Commission.MAX_LEVEL);

        if (type.isAttributesScalable()) {
            Commission.scaleAttributes(this.attributes, this.level);
        }

        // We need to update the health as well
        this.health = getMaxHealth();
    }

    @Nonnull
    @Override
    public Location aboveHeadLocation() {
        return getLocation().add(0, entity.getEyeHeight() + type.getHologramOffset(), 0);
    }

    @Nullable
    public String[] getExtraHologramLines() {
        return null;
    }

    @Override
    @Nonnull
    public String getHealthFormatted() {
        return type.getType().formatHealth(this) + " &c❤";
    }

    @Nonnull
    public EntityEquipment getEquipment() {
        return Objects.requireNonNull(entity.getEquipment(), this + " does not have equipment.");
    }

    @Override
    public String toString() {
        return "CommissionEntity{%s}".formatted(type.getKey());
    }

    public int getTick() {
        return tick;
    }

    @Nonnull
    protected String nameFormatted() {
        return "&8[&7Lv%s&8] %s %s".formatted(level, type.getNameFormatted(), getHealthFormatted());
    }

    @Nonnull
    @Override
    public String getName() {
        return type.getName();
    }

    private void updateHologram() {
        final List<String> aboveHead = Lists.newArrayList();
        final String[] extraLines = getExtraHologramLines();

        if (extraLines != null) {
            aboveHead.addAll(Arrays.asList(extraLines));
        }

        aboveHead.add(nameFormatted());
        aboveHead(aboveHead.toArray(String[]::new));
    }

}
