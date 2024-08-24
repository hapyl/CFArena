package me.hapyl.fight.game.entity;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.Wrap;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

public class EntityTicker {

    /**
     * Number of ticks where effects cannot affect entity.
     */
    public final EntityTick noCCTicks;

    /**
     * Number of ticks where entity cannot be damaged.
     */
    public final EntityTick noDamageTicks;

    /**
     * The number of ticks the entity has been alive for.
     */
    public final EntityTick aliveTicks;

    /**
     * The number of ticks the entity has been in water for.
     */
    public final EntityTick inWaterTicks;

    /**
     * The numbers of ticks the player has been sneaking for.
     * <br>
     * Only applicable to {@link GamePlayer}.
     */
    public final EntityTick sneakTicks;

    private final LivingGameEntity entity;
    private final List<Tick> tickerList;

    EntityTicker(@Nonnull LivingGameEntity entity) {
        this.entity = entity;
        this.tickerList = Lists.newArrayList();

        // Add ticks
        this.noCCTicks = register("cc", TickDirection.DOWN);
        this.noDamageTicks = register("no_damage", TickDirection.DOWN);
        this.aliveTicks = register("alive", TickDirection.UP);
        this.inWaterTicks = register("in_water", TickDirection.UP, LivingGameEntity::isInWater);
        this.sneakTicks = register("sneak", TickDirection.UP, predicate -> predicate instanceof GamePlayer player && player.isSneaking());
    }

    @Override
    public String toString() {
        return CollectionUtils.wrapToString(tickerList, Wrap.DEFAULT);
    }

    protected void tick() {
        tickerList.forEach(Tick::tick);
    }

    private EntityTick register(String name, TickDirection direction, Predicate<LivingGameEntity> predicate) {
        final EntityTick tick = new EntityTick(entity, name, direction, predicate);

        tickerList.add(tick);
        return tick;
    }

    private EntityTick register(String name, TickDirection direction) {
        return register(name, direction, t -> true);
    }
}
